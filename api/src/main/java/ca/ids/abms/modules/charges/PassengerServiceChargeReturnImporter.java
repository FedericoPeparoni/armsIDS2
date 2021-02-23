package ca.ids.abms.modules.charges;

import static ca.ids.abms.modules.common.controllers.BulkLoaderComponent.DATA_IMPORT_SERVICE;
import static ca.ids.abms.modules.common.controllers.BulkLoaderComponent.PASSENGER_CHARGE_RETURN_LOADER;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import ca.ids.abms.modules.common.dto.BulkLoaderSummary;
import ca.ids.abms.modules.common.services.AbstractDataImporter;
import ca.ids.abms.modules.dataimport.CsvImportServiceImp;
import ca.ids.abms.modules.dataimport.DataImportService;
import ca.ids.abms.modules.dataimport.DataImportType;
import ca.ids.abms.modules.system.SystemConfiguration;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.uploadedfiles.UploadedFileService;

@Component(DataImportType.Values.PASSENGER)
public class PassengerServiceChargeReturnImporter extends AbstractDataImporter {

    public static final DataImportType DATA_IMPORT_TYPE = DataImportType.PASSENGER;

    public static final String PASSENGER_UPLOAD_LOCATION = "Passenger service charge return file location";

    private final Logger LOG = LoggerFactory.getLogger(PassengerServiceChargeReturnImporter.class);

    private SystemConfigurationService systemConfigurationService;

    @Autowired
    @Qualifier(DATA_IMPORT_SERVICE)
    private CsvImportServiceImp<PassengerServiceChargeReturnCsvViewModel> dataImportService;

    @Qualifier(PASSENGER_CHARGE_RETURN_LOADER)
    private final PassengerServiceChargeReturnLoader bulkLoader;

    public PassengerServiceChargeReturnImporter(final UploadedFileService uploadedFileService,
                                                final SystemConfigurationService systemConfigurationService,
                                                final CsvImportServiceImp<PassengerServiceChargeReturnCsvViewModel> dataImportService,
                                                final PassengerServiceChargeReturnLoader bulkLoader) {
        super(uploadedFileService, DATA_IMPORT_TYPE);
        this.systemConfigurationService = systemConfigurationService;
        this.dataImportService = dataImportService;
        this.bulkLoader = bulkLoader;
    }

    /**
     * Get passenger service charge return location string from system configuration.
     *
     * @return passenger service charge return location string
     */
    @Override
    public String getLocation() {
        SystemConfiguration item = systemConfigurationService.getOneByItemName(PASSENGER_UPLOAD_LOCATION);
        if (item != null)
            return item.getCurrentValue();
        else
            return null;
    }

    /**
     * Parse file as passenger service charge return modal and load into passenger service charge return bulk loader.
     *
     * @param file file to bulk upload
     * @return bulk loader summary
     */
    @Override
    public BulkLoaderSummary upload(File file) {
        List<PassengerServiceChargeReturnCsvViewModel> result;
        try {
            result = dataImportService.parseFromFile(file,
                DataImportService.STRATEGY.BIND_BY_POSITION, PassengerServiceChargeReturnCsvViewModel.class);
        } catch (Exception ex) {
            LOG.error("Cannot read the file {} because: {}", file.getName(), ex.getMessage());
            return null;
        }
        return bulkLoader.bulkLoad(result, file);
    }
}
