package ca.ids.abms.modules.localaircraftregistry;

import ca.ids.abms.util.billingcontext.BillingContextUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.config.error.RejectedException;
import ca.ids.abms.config.error.RejectedReasons;
import ca.ids.abms.modules.common.dto.ItemLoaderObserver;
import ca.ids.abms.modules.common.dto.UploadReportViewModel;
import ca.ids.abms.modules.common.services.AbstractBulkLoader;
import ca.ids.abms.modules.common.services.EntityValidator;
import ca.ids.abms.modules.dataimport.RejectableCsvModel;
import ca.ids.abms.modules.rejected.RejectedItemService;
import ca.ids.abms.modules.rejected.RejectedItemType;
import ca.ids.abms.modules.uploadedfiles.UploadedFileService;

@Component
public class LocalAircraftRegistryLoader extends AbstractBulkLoader {

    private final Logger log = LoggerFactory.getLogger(LocalAircraftRegistryLoader.class);

    private LocalAircraftRegistryMapper localAircraftRegistryMapper;

    private LocalAircraftRegistryService localAircraftRegistryService;

    public LocalAircraftRegistryLoader(
        RejectedItemService rejectedItemService,
        EntityValidator validator,
        UploadedFileService uploadedFileService,
        LocalAircraftRegistryMapper localAircraftRegistryMapper,
        LocalAircraftRegistryService localAircraftRegistryService,
        BillingContextUtility billingContextUtility
    ) {

        super(rejectedItemService, validator, uploadedFileService, billingContextUtility);
        this.localAircraftRegistryMapper = localAircraftRegistryMapper;
        this.localAircraftRegistryService = localAircraftRegistryService;
    }

    @Override
    protected RejectedItemType resposibleForRecordType() {
        return RejectedItemType.LOCAL_AIRCRAFT_REGISTRY;
    }

    @Override
    protected void importItem(final RejectableCsvModel csvModel, ItemLoaderObserver o) throws RejectedException {
        assert csvModel != null;
        assert csvModel instanceof LocalAircraftRegistryCsvViewModel;
        final LocalAircraftRegistryCsvViewModel dto = (LocalAircraftRegistryCsvViewModel)csvModel;
        LocalAircraftRegistry itemEntity;
        try {
            itemEntity = localAircraftRegistryMapper.toModel(dto);
        } catch (Exception generic) {
            String errorDTC = "";
            if (dto.getRegistrationNumber() == null) {
                errorDTC = "RegistrationNumber: may not be null;";
            } else if (dto.getOwnerName() == null) {
                errorDTC = "OwnerName: may not be null;";
            } else if (dto.getMtowWeight() == null) {
                errorDTC = "MtowWeight: may not be null;";
            } else if (dto.getCoaDateOfRenewal() == null) {
                errorDTC = "CoaDateOfRenewal: may not be null;";
            } else if (dto.getCoaDateOfExpiry() == null) {
                errorDTC = "CoaDateOfExpiry: may not be null;";
            } else {
                errorDTC = generic.getMessage();
            }
            log.debug("Discarded record with registration number={}, because: {}",
                dto.getRegistrationNumber(), errorDTC);
            throw new RejectedException(RejectedReasons.PARSE_ERROR, "Data not valid", errorDTC);
        }
        validator.validateItem(localAircraftRegistryMapper.toModel(dto));
        try {
            final LocalAircraftRegistry persistedItem = localAircraftRegistryService.createOrUpdate(itemEntity);
            if (log.isDebugEnabled()) {
                log.debug("Updated record #{}: registration number={}",
                    persistedItem.getId(), persistedItem.getRegistrationNumber());
            }
        } catch (RejectedException rejected) {
            log.debug("Discarded record with registration number={}, because: {}",
                dto.getRegistrationNumber(),rejected.getMessage());
            throw rejected;
        } catch (Exception generic) {
            log.debug("Discarded record with registration number={}, because: {}:{}",
                dto.getRegistrationNumber(), generic.getClass().getSimpleName(), generic.getMessage());
            throw new RejectedException(ExceptionFactory.resolveManagedErrors(generic));
        }
    }

    @Override
    protected UploadReportViewModel checkItem(RejectableCsvModel csvModel) {
        return null;
    }
}
