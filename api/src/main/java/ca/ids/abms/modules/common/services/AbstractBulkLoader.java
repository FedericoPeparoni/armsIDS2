package ca.ids.abms.modules.common.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import ca.ids.abms.config.ContentTypes;
import ca.ids.abms.config.error.RejectedReasons;
import ca.ids.abms.modules.charges.PassengerServiceChargeReturnCsvViewModel;
import ca.ids.abms.util.billingcontext.BillingContextKey;
import ca.ids.abms.util.billingcontext.BillingContextUtility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import ca.ids.abms.config.error.NonRejectedException;
import ca.ids.abms.config.error.RejectedException;
import ca.ids.abms.modules.aircraft.AircraftRegistrationCsvViewModel;
import ca.ids.abms.modules.common.dto.BulkLoaderSummary;
import ca.ids.abms.modules.common.dto.ItemLoaderObserver;
import ca.ids.abms.modules.common.dto.UploadReportViewModel;
import ca.ids.abms.modules.dataimport.RejectableCsvModel;
import ca.ids.abms.modules.flight.FlightScheduleCsvViewModel;
import ca.ids.abms.modules.localaircraftregistry.LocalAircraftRegistryCsvViewModel;
import ca.ids.abms.modules.rejected.RejectedItemService;
import ca.ids.abms.modules.rejected.RejectedItemType;
import ca.ids.abms.modules.uploadedfiles.UploadedFileService;
import ca.ids.abms.modules.uploadedfiles.enumerate.UploadedFileRecordType;

public abstract class AbstractBulkLoader<T extends RejectableCsvModel> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractBulkLoader.class);

    private RejectedItemService rejectedItemService;

    private UploadedFileService uploadedFileService;

    protected EntityValidator validator;

    private BillingContextUtility billingContextUtility;

    public AbstractBulkLoader (
        RejectedItemService rejectedItemService,
        EntityValidator validator,
        UploadedFileService uploadedFileService,
        BillingContextUtility billingContextUtility
    ) {
        this.rejectedItemService = rejectedItemService;
        this.validator = validator;
        this.uploadedFileService = uploadedFileService;
        this.billingContextUtility = billingContextUtility;
    }

    /**
     * Bulk load csv model, file contents will be saved to history table.
     *
     * @param csvModels list of csv models to load
     * @param file file containing csv models, simply saved to history table
     * @return summary of bulk load
     */
    public BulkLoaderSummary bulkLoad(final List<T> csvModels, final File file) {
        byte[] bytes;
        String contentType;
        LocalDateTime lastModifiedLDT = uploadedFileService.formatFileLastModifiedDate(file.lastModified());
        try {
            bytes = Files.readAllBytes(file.toPath());
            contentType = Files.probeContentType(file.toPath());
        } catch (IOException ee) {
            bytes = null;
            contentType = null;
        }

        // ensure content type is defined to OCTET_STREAM if null
        if (contentType == null) {
            contentType = ContentTypes.OCTET_STREAM.toValue();
        }

        // US100976: merge waypoints by default
        return this.bulkLoad(
            csvModels, file.getName(), contentType, bytes, false, lastModifiedLDT, Boolean.TRUE
        );
    }

    /**
     * Bulk load csv model, multipart file contents will be saved to history table.
     *
     * @param csvModels list of csv models to load
     * @param file multipart file containing csv models, simply saved to history table
     * @return summary of bulk load
     */
    public BulkLoaderSummary bulkLoad(final List<T> csvModels, final MultipartFile file) {
        // US100976: merge waypoints by default
        return this.bulkLoad(csvModels, file, Boolean.TRUE);
    }

    /**
     * Bulk load csv model, multipart file contents will be saved to history table.
     *
     * @param csvModels list of csv models to load
     * @param file multipart file containing csv models, simply saved to history table
     * @param mergeWaypoints flag to indicate if merging with existing waypoint data
     * @return summary of bulk load
     */
    public BulkLoaderSummary bulkLoad(final List<T> csvModels, final MultipartFile file, final Boolean mergeWaypoints) {
        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException ex) {
            bytes = null;
        }
        return this.bulkLoad(csvModels, file.getOriginalFilename(), file.getContentType(), bytes, true, null, mergeWaypoints);
    }

    public List<UploadReportViewModel> reportBulkUpload(final List<T> csvModels, final MultipartFile file) {
        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException ex) {
            bytes = null;
        }
        return this.reportBulkLoad(csvModels, file.getOriginalFilename(),  null);
    }

    protected abstract void importItem(final T csvModel, final ItemLoaderObserver o);

    protected abstract UploadReportViewModel checkItem(final T csvModel);

    protected abstract RejectedItemType resposibleForRecordType();

    private BulkLoaderSummary bulkLoad(
        final List<T> csvModels, final String fileName, final String fileType, final byte[] fileContent,
        final Boolean manualUpload, final LocalDateTime lastModified, final Boolean mergeWaypoints
    ) {
        final BulkLoaderSummary summary = new BulkLoaderSummary();
        if (CollectionUtils.isNotEmpty(csvModels)) {
            LocalDateTime timeBefore = LocalDateTime.now();
            LOG.info("Starting to import {} records", csvModels.size());

            final String header = RejectableCsvModel.getHeader(csvModels.get(0).getClass());

            // create billing context to use when bulk loading
            Map<BillingContextKey, Object> billingContext = new EnumMap<>(BillingContextKey.class);
            billingContext.put(BillingContextKey.ANSP_CURRENCY, null);
            billingContext.put(BillingContextKey.BILLABLE_AIRSPACE, null);
            billingContext.put(BillingContextKey.MERGE_WAYPOINTS, mergeWaypoints);
            billingContext.put(BillingContextKey.BULK_LOADER, true);

            // pass billingContextUtility the billing context
            billingContextUtility.perform(billingContext, () -> processCsvModels(csvModels, summary, fileName, header));

            LOG.info("Importing finished {}", summary);
            LOG.info("Import took: {} seconds", (Duration.between(timeBefore, LocalDateTime.now()).getSeconds()));

            // create a file upload history record
            UploadedFileRecordType recordType = uploadedFileService.getUploadedFileRecordType(this.resposibleForRecordType());

            uploadedFileService.createUploadedFileRecordFromBulkLoaderSummary(summary, recordType, fileName, fileType, fileContent, manualUpload, lastModified);
        } else {
            LOG.info("Nothing to import");
        }

        return summary;
    }

    private void processCsvModels(final List<T> csvModels, final BulkLoaderSummary summary, final String fileName, final String header) {
        for (final T csvModel : csvModels) {
            try {
                bulkLoad(csvModel, summary);
                summary.incrementProcessed();
            } catch (RejectedException re) {
                summary.incrementRejected();

                // skip creating a rejected item if flagged as SHOULD DISCARD
                if (!re.getReason().equalsIgnoreCase(RejectedReasons.ALREADY_EXISTS_SHOULD_DISCARD.toValue())) {
                    rejectItem(csvModel, re, fileName, header);
                }
            }
        }
    }

    private void bulkLoad(final T csvModel) {
        assert csvModel.getRawText() != null;

        if (csvModel.isParsed()) {
            this.importItem(csvModel, null);
        } else {
            throw new RejectedException(csvModel.getErrorMessage());
        }
    }

    private void bulkLoad(final T csvModel, final ItemLoaderObserver o) {
        /* If this assert fails, there is a bug for sure; maybe you're using a wrong CSV reader
         *  The RejectableRowCsvImportService will insert always a row_text (at least empty).
        */

        assert csvModel.getRawText() != null;

        if (csvModel.isParsed()) {
            this.importItem(csvModel, o);
        } else {
            throw new RejectedException(csvModel.getErrorMessage());
        }
    }

    private void rejectItem(final T csvModel, final RejectedException re, final String fileName,
                                    final String header) {
        final ObjectMapper objectMapper = new ObjectMapper();
        byte[] jsonText = null;
        try {
            jsonText = objectMapper.writeValueAsBytes(csvModel);
        } catch (JsonProcessingException e) {
            // Obtaining a jsonText is a plus
            LOG.debug("Cannot create a JSON object from {}", csvModel);
        }
        rejectedItemService.create(this.resposibleForRecordType(), re, null,
            fileName, csvModel.getRawText(), header, jsonText);
    }

    private List<UploadReportViewModel> reportBulkLoad(final List<T> csvModels, final String fileName,LocalDateTime lastModified) {
        final List<UploadReportViewModel> rejectedItemList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(csvModels)) {
            LOG.info("Starting to import {} records", csvModels.size());

            for (final T csvModel : csvModels) {
                try {
                    bulkLoad(csvModel);
                } catch (RejectedException re) {
                    UploadReportViewModel rejected = reportRejectItem(csvModel, re, fileName);
                    rejectedItemList.add(rejected);
                } catch (NonRejectedException ex) {
                    LOG.warn("Item was not importable and dropped due to '{}' ", ex.getMessage());
                }
            }
            LOG.info("Importing finished, {} item(s) rejected", rejectedItemList.size());
        } else {
            LOG.info("Nothing to import");
        }

        return rejectedItemList;
    }

    private UploadReportViewModel reportRejectItem(final T csvModel, final RejectedException re, final String fileName) {
        UploadReportViewModel rejected = new UploadReportViewModel();
        rejected.setFilename(fileName);
        rejected.setRawText(csvModel.getRawText());
        if (csvModel instanceof FlightScheduleCsvViewModel) {
            FlightScheduleCsvViewModel sch = (FlightScheduleCsvViewModel)csvModel;
            rejected.setId(sch.getFlightServiceNumber());
            if(re.getLocalizedMessage().contains("Aerodrome not valid")) {
                rejected.setErrorMessages(re.getReason());
                rejected.setDetails(re.getLocalizedMessage());
            }
            else {
                rejected.setErrorMessages(re.getLocalizedMessage());
                rejected.setDetails(re.getErrorDto().getErrorDescription());
            }
        }
        if (csvModel instanceof LocalAircraftRegistryCsvViewModel) {
            LocalAircraftRegistryCsvViewModel lar = (LocalAircraftRegistryCsvViewModel)csvModel;
            rejected.setId(lar.getRegistrationNumber());
            rejected.setErrorMessages(re.getLocalizedMessage());
            rejected.setDetails(re.getErrorDto().getErrorDescription());
        }
        if (csvModel instanceof AircraftRegistrationCsvViewModel) {
        	AircraftRegistrationCsvViewModel ar = (AircraftRegistrationCsvViewModel)csvModel;
            rejected.setId(ar.getRegistrationNumber());
            rejected.setErrorMessages(re.getLocalizedMessage());
            rejected.setDetails(re.getErrorDto().getErrorDescription());
        }
        if (csvModel instanceof PassengerServiceChargeReturnCsvViewModel) {
            PassengerServiceChargeReturnCsvViewModel pscr = (PassengerServiceChargeReturnCsvViewModel)csvModel;
            rejected.setId(pscr.getFlightId());
            rejected.setErrorMessages(re.getLocalizedMessage());
            rejected.setDetails(re.getErrorDto().getErrorDescription());
        }
        return rejected;
    }

    public List<UploadReportViewModel> checkBulkUpload(List<T> csvModels) {
        final List<UploadReportViewModel> summary = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(csvModels)) {
            LOG.info("Starting to check {} records", csvModels.size());

            for (final T csvModel : csvModels) {
                UploadReportViewModel checked = checkBulkLoad(csvModel);
                if (checked != null) {
                    summary.add(checked);
                }
            }
            LOG.info("Checking finished {}", summary);
        } else {
            LOG.info("Nothing to check");
        }

        return summary;
    }

    private UploadReportViewModel checkBulkLoad(T csvModel) {
        assert csvModel.getRawText() != null;
        UploadReportViewModel viewModel = null;
        if (csvModel.isParsed()) {
            viewModel = this.checkItem(csvModel);
        }
        return viewModel;
    }
}
