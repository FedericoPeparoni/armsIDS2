package ca.ids.abms.modules.towermovements;

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

@Component(BulkLoaderComponent.TOWER_LOG_LOADER)
public class TowerMovementLogBulkLoader extends AbstractBulkLoader {

    private final Logger log = LoggerFactory.getLogger(TowerMovementLogBulkLoader.class);

    private TowerMovementLogMapper towerMovementLogMapper;

    private TowerMovementLogService towerMovementLogService;

    public TowerMovementLogBulkLoader(
        RejectedItemService rejectedItemService,
        EntityValidator validator,
        UploadedFileService uploadedFileService,
        TowerMovementLogMapper towerMovementLogMapper,
        TowerMovementLogService towerMovementLogService,
        BillingContextUtility billingContextUtility
    ) {
        super(rejectedItemService, validator, uploadedFileService, billingContextUtility);
        this.towerMovementLogMapper = towerMovementLogMapper;
        this.towerMovementLogService = towerMovementLogService;
    }

    @Override
    protected RejectedItemType resposibleForRecordType() {
        return RejectedItemType.TOWER_AIRCRAFT_PASSENGER_MOVEMENTS_LOG;
    }

    @Override
    protected void importItem(final RejectableCsvModel csvModel, ItemLoaderObserver o) throws RejectedException {
        assert csvModel != null;
        assert csvModel instanceof TowerMovementLogCsvViewModel;
        final TowerMovementLogCsvViewModel dto = (TowerMovementLogCsvViewModel)csvModel;
        TowerMovementLog itemEntity;
        try {
            itemEntity = towerMovementLogMapper.toModel(dto);
        } catch (Exception generic) {
            log.debug("Discarded record with flightId={} dateOfContact={} operatorName={}, because: {}",
                dto.getFlightId(), dto.getDateOfContact(),
                dto.getOperatorName(), generic.getMessage());
            throw new RejectedException(RejectedReasons.PARSE_ERROR, generic);
        }
        validator.validateItem(itemEntity);
        try {
            final TowerMovementLog persistedMovement = towerMovementLogService.createOrUpdateByFlightId(itemEntity, o);
            if (log.isDebugEnabled()) {
                log.debug("Updated record #{}: flightId={} dateOfContact={} operatorName={}",
                    persistedMovement.getId(), persistedMovement.getFlightId(), persistedMovement.getDateOfContact(),
                    persistedMovement.getOperatorName());
            }
        } catch (CustomParametrizedException cpe) {
            log.debug("Discarded record with flightId={} dateOfContact={} depTime={} depAd={} operatorName={}, because: {}",
                itemEntity.getFlightId(), itemEntity.getDateOfContact(), itemEntity.getDepartureTime(), itemEntity.getDepartureAerodrome(),
                itemEntity.getOperatorName(), cpe.getLocalizedMessage());

            // if object is FlightMovement, set rejected reason to flight movement builder error
            // this will help the user know the message is referring to FlightMovement
            if (cpe.getObjectName() != null && cpe.getObjectName().equals(FlightMovement.class.getSimpleName())) {
                throw new RejectedException(RejectedReasons.FLIGHT_MOVEMENT_BUILDER_ERROR, cpe.getErrorDTO());
            } else {
                throw new RejectedException(cpe.getErrorDTO());
            }
        } catch (RejectedException rejected) {
            log.debug("Discarded record with flightId={} dateOfContact={} depTime={} depAd={} operatorName={}, because: {}",
                itemEntity.getFlightId(), itemEntity.getDateOfContact(), itemEntity.getDepartureTime(), itemEntity.getDepartureAerodrome(),
                itemEntity.getOperatorName(), rejected.getMessage());
            throw rejected;
        } catch (Exception generic) {
            log.debug("Discarded record with flightId={} dateOfContact={} depTime={} depAd={} operatorName={}, because: {}",
                itemEntity.getFlightId(), itemEntity.getDateOfContact(), itemEntity.getDepartureTime(), itemEntity.getDepartureAerodrome(),
                itemEntity.getOperatorName(), generic.getClass().getSimpleName(), generic.getMessage());
            throw new RejectedException(ExceptionFactory.resolveManagedErrors(generic));
        }
    }

    @Override
    protected UploadReportViewModel checkItem(RejectableCsvModel aCsvModel) {
        // TODO Auto-generated method stub
        return null;
    }
}
