package ca.ids.abms.modules.manifests;

import ca.ids.abms.util.billingcontext.BillingContextUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.config.error.RejectedException;
import ca.ids.abms.config.error.RejectedReasons;
import ca.ids.abms.modules.common.controllers.BulkLoaderComponent;
import ca.ids.abms.modules.common.dto.ItemLoaderObserver;
import ca.ids.abms.modules.common.dto.UploadReportViewModel;
import ca.ids.abms.modules.common.services.AbstractBulkLoader;
import ca.ids.abms.modules.common.services.EntityValidator;
import ca.ids.abms.modules.dataimport.RejectableCsvModel;
import ca.ids.abms.modules.rejected.RejectedItemService;
import ca.ids.abms.modules.rejected.RejectedItemType;
import ca.ids.abms.modules.uploadedfiles.UploadedFileService;

@Component(BulkLoaderComponent.PASSENGER_MANIFEST_LOADER)
public class PassengerManifestLoader extends AbstractBulkLoader {

    private final Logger log = LoggerFactory.getLogger(PassengerManifestLoader.class);

    private PassengerManifestMapper mapper;

    private PassengerManifestService service;

    public PassengerManifestLoader(
        RejectedItemService rejectedItemService,
        EntityValidator validator,
        UploadedFileService uploadedFileService,
        PassengerManifestMapper mapper,
        PassengerManifestService service,
        BillingContextUtility billingContextUtility
    ) {
        super(rejectedItemService, validator, uploadedFileService, billingContextUtility);
        this.mapper = mapper;
        this.service = service;
    }

    @Override
    protected RejectedItemType resposibleForRecordType() {
        return RejectedItemType.PASSENGER_MANIFESTS;
    }

    @Override
    protected void importItem(final RejectableCsvModel csvModel, ItemLoaderObserver o) throws RejectedException {
        assert csvModel != null;
        assert csvModel instanceof PassengerManifestCsvViewModel;
        final PassengerManifestCsvViewModel dto = (PassengerManifestCsvViewModel)csvModel;
        PassengerManifest itemEntity;
        try {
            itemEntity = mapper.toModel(dto);
        } catch (Exception generic) {
            log.debug("Discarded record with flightId={}, because: {}",
                dto.getFlightId(), generic.getMessage());
            throw new RejectedException(RejectedReasons.PARSE_ERROR, generic);
        }
        validator.validateItem(itemEntity);
        try {
            final PassengerManifest persistedItem = service.create(itemEntity); // TODO missing CreateOrUpdate
            if (log.isDebugEnabled()) {
                log.debug("Updated record #{}: flightId={};",
                    persistedItem.getDocumentNumber(), persistedItem.getFlightId());
            }
        } catch (Exception generic) {
            log.debug("Discarded record with flightId={}, because: {}",
                itemEntity.getFlightId(), generic.getMessage());
            throw new RejectedException(ExceptionFactory.resolveManagedErrors(generic));
        }
    }

    @Override
    protected UploadReportViewModel checkItem(RejectableCsvModel aCsvModel) {
        // TODO Auto-generated method stub
        return null;
    }
}
