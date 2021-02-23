package ca.ids.abms.modules.atcmovements;

import ca.ids.abms.util.billingcontext.BillingContextUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.config.error.RejectedException;
import ca.ids.abms.config.error.RejectedReasons;
import ca.ids.abms.modules.common.controllers.BulkLoaderComponent;
import ca.ids.abms.modules.common.dto.ItemLoaderObserver;
import ca.ids.abms.modules.common.dto.UploadReportViewModel;
import ca.ids.abms.modules.common.services.AbstractBulkLoader;
import ca.ids.abms.modules.common.services.EntityValidator;
import ca.ids.abms.modules.dataimport.RejectableCsvModel;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.rejected.RejectedItemService;
import ca.ids.abms.modules.rejected.RejectedItemType;
import ca.ids.abms.modules.uploadedfiles.UploadedFileService;

@Component(BulkLoaderComponent.ATC_LOG_LOADER)
public class AtcMovementLogBulkLoader extends AbstractBulkLoader {

    private static final Logger LOG = LoggerFactory.getLogger(AtcMovementLogBulkLoader.class);

    private static final String DISCARD_RECORD_MESSAGE = "Discarded record with flightId={} dateOfContact={} depTime={} depAd={} operatorIdentifier={}, because: {}";

    private AtcMovementLogMapper atcMovementLogMapper;

    private AtcMovementLogService atcMovementLogService;

    public AtcMovementLogBulkLoader(
        RejectedItemService rejectedItemService,
        EntityValidator validator,
        UploadedFileService uploadedFileService,
        AtcMovementLogMapper atcMovementLogMapper,
        AtcMovementLogService atcMovementLogService,
        BillingContextUtility billingContextUtility
    ) {
        super(rejectedItemService, validator, uploadedFileService, billingContextUtility);
        this.atcMovementLogMapper = atcMovementLogMapper;
        this.atcMovementLogService = atcMovementLogService;
    }

    @Override
    protected RejectedItemType resposibleForRecordType() {
        return RejectedItemType.ATS_MOVEMENTS_LOG;
    }

    @Override
    protected void importItem(final RejectableCsvModel csvModel, ItemLoaderObserver o) {
        assert csvModel != null;
        assert csvModel instanceof AtcMovementLogCsvViewModel;
        final AtcMovementLogCsvViewModel dto = (AtcMovementLogCsvViewModel)csvModel;
        AtcMovementLog itemEntity;
        try {
            itemEntity = atcMovementLogMapper.toModel(dto);
        } catch (Exception generic) {
            LOG.debug("Discarded record with flightId={} dateOfContact={} operatorIdentifier={}, because: {}",
                dto.getFlightId(), dto.getDateOfContact(),
                dto.getOperatorIdentifier(), generic.getMessage());
            throw new RejectedException(RejectedReasons.PARSE_ERROR, generic);
        }
        validator.validateItem(itemEntity);
        try {
            final AtcMovementLog persistedMovement = atcMovementLogService.createOrUpdate(itemEntity, o);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Updated record #{}: flightId={} dateOfContact={} operatorIdentifier={}",
                    persistedMovement.getId(), persistedMovement.getFlightId(), persistedMovement.getDateOfContact(),
                    persistedMovement.getOperatorIdentifier());
            }
        } catch (CustomParametrizedException cpe) {
            LOG.debug(DISCARD_RECORD_MESSAGE, itemEntity.getFlightId(), itemEntity.getDateOfContact(),
                itemEntity.getDepartureTime(), itemEntity.getDepartureAerodrome(), itemEntity.getOperatorIdentifier(),
                cpe.getLocalizedMessage());

            // if object is FlightMovement, set rejected reason to flight movement builder error
            // this will help the user know the message is referring to FlightMovement
            if (cpe.getObjectName() != null && cpe.getObjectName().equals(FlightMovement.class.getSimpleName())) {
                throw new RejectedException(RejectedReasons.FLIGHT_MOVEMENT_BUILDER_ERROR, cpe.getErrorDTO());
            } else {
                throw new RejectedException(cpe.getErrorDTO());
            }
        } catch (RejectedException rejected) {
            LOG.debug(DISCARD_RECORD_MESSAGE, itemEntity.getFlightId(), itemEntity.getDateOfContact(),
                itemEntity.getDepartureTime(), itemEntity.getDepartureAerodrome(), itemEntity.getOperatorIdentifier(),
                rejected.getMessage());
            throw rejected;
        } catch (Exception generic) {
            LOG.debug(DISCARD_RECORD_MESSAGE, itemEntity.getFlightId(), itemEntity.getDateOfContact(),
                itemEntity.getDepartureTime(), itemEntity.getDepartureAerodrome(), itemEntity.getOperatorIdentifier(),
                generic.getClass().getSimpleName(), generic.getMessage());
            throw new RejectedException(ExceptionFactory.resolveManagedErrors(generic));

        }
    }

    @Override
    protected UploadReportViewModel checkItem(RejectableCsvModel aCsvModel) {
        return null;
    }
}
