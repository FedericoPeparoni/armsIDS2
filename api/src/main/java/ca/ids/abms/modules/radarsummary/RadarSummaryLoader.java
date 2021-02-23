package ca.ids.abms.modules.radarsummary;

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
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.rejected.RejectedItemService;
import ca.ids.abms.modules.rejected.RejectedItemType;
import ca.ids.abms.modules.uploadedfiles.UploadedFileService;

@Component(BulkLoaderComponent.RADAR_SUMMARY_LOADER)
public class RadarSummaryLoader extends AbstractBulkLoader<RadarSummaryCsvViewModel> {

    private static final Logger LOG = LoggerFactory.getLogger(RadarSummaryLoader.class);

    private RadarSummaryMapper mapper;

    private RadarSummaryService service;
    
    private final RadarSummaryDepartureEstimator radarSummaryDepartureEstimator;

    public RadarSummaryLoader(
        RejectedItemService rejectedItemService,
        EntityValidator validator,
        UploadedFileService uploadedFileService,
        RadarSummaryMapper mapper,
        RadarSummaryService service,
        BillingContextUtility billingContextUtility,
        RadarSummaryDepartureEstimator radarSummaryDepartureEstimator
    ) {
        super(rejectedItemService, validator, uploadedFileService, billingContextUtility);
        this.mapper = mapper;
        this.service = service;
        this.radarSummaryDepartureEstimator = radarSummaryDepartureEstimator;
    }

    @Override
    protected RejectedItemType resposibleForRecordType() {
        return RejectedItemType.RADAR_SUMMARIES;
    }

    @Override
    protected void importItem(final RadarSummaryCsvViewModel csvModel, ItemLoaderObserver o) {
        assert csvModel != null;
        RadarSummary itemEntity;
        try {
            itemEntity = mapper.toModel(csvModel);
        } catch (Exception generic) {
            LOG.debug("Discarded record with registration={}, because: {}",
                csvModel.getRegistration(), generic.getMessage());
            LOG.trace("Record discarded due to Exception: {}", generic);
            throw new RejectedException(RejectedReasons.PARSE_ERROR, generic);
        }
        radarSummaryDepartureEstimator.resolveMissingDepartureTime(itemEntity);
        validator.validateItem(itemEntity);
        try {
            final RadarSummary persistedItem = service.createOrUpdate(itemEntity, o);

            if (LOG.isDebugEnabled()) {
                LOG.debug("Updated record #{}: registration={};",
                    persistedItem.getId(), persistedItem.getRegistration());
            }
        } catch (CustomParametrizedException cpe) {
            LOG.debug("Discarded record with flightId={} dayOfFlight={} depTime={} depAd={}, because: {}",
                itemEntity.getFlightIdentifier(), itemEntity.getDayOfFlight(), itemEntity.getDepartureTime(),
                itemEntity.getDepartureAeroDrome(),cpe.getLocalizedMessage());
            LOG.trace("Record discarded due to CustomParametrizedException: {}", cpe);

            // if object is FlightMovement, set rejected reason to flight movement builder error
            // this will help the user know the message is referring to FlightMovement
            if (cpe.getObjectName() != null && cpe.getObjectName().equals(FlightMovement.class.getSimpleName())) {
                throw new RejectedException(RejectedReasons.FLIGHT_MOVEMENT_BUILDER_ERROR, cpe.getErrorDTO());
            } else {
                throw new RejectedException(cpe.getErrorDTO());
            }
        } catch (RejectedException rejected) {
            LOG.debug("Discarded record with flightId={} dayOfFlight={} depTime={} depAd={}, because: {}, reason={}",
                itemEntity.getFlightIdentifier(), itemEntity.getDayOfFlight(), itemEntity.getDepartureTime(),
                itemEntity.getDepartureAeroDrome(),rejected.getMessage(),rejected.getReasonCode());
            LOG.trace("Record discarded due to RejectedException: {}", rejected);
            throw rejected;
        } catch (Exception generic) {
            LOG.debug("Discarded record with flightId={} dayOfFlight={} depTime={} depAd={}, because: {}:{}",
                itemEntity.getFlightIdentifier(), itemEntity.getDayOfFlight(), itemEntity.getDepartureTime(),
                itemEntity.getDepartureAeroDrome(), generic.getClass().getSimpleName(), generic.getMessage());
            LOG.trace("Record discarded due to Exception: {}", generic);
            throw new RejectedException(ExceptionFactory.resolveManagedErrors(generic));
        }
    }
    
    @Override
    protected UploadReportViewModel checkItem(RadarSummaryCsvViewModel aCsvModel) {
        return null;
    }
}
