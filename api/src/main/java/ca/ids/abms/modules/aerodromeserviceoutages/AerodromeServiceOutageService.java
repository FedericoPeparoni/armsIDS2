package ca.ids.abms.modules.aerodromeserviceoutages;

import ca.ids.abms.config.db.Filter;
import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.config.db.JoinFilter;
import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.modules.aerodromes.Aerodrome;
import ca.ids.abms.modules.aerodromeservicetypes.AerodromeServiceType;
import ca.ids.abms.modules.aerodromeservicetypes.DiscountType;
import ca.ids.abms.modules.util.models.ModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AerodromeServiceOutageService {

    private AerodromeServiceOutageRepository aerodromeServiceOutageRepository;
    private static final Logger LOG = LoggerFactory.getLogger(AerodromeServiceOutageService.class);

    public AerodromeServiceOutageService(AerodromeServiceOutageRepository aerodromeServiceOutageRepository) {
        this.aerodromeServiceOutageRepository = aerodromeServiceOutageRepository;
    }

    @Transactional(readOnly = true)
    public List<AerodromeServiceOutage> findAll() {
        LOG.debug("Request to get all Aerodrome Service Outages");
        return aerodromeServiceOutageRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<AerodromeServiceOutage> findAll(Pageable pageable) {
        LOG.debug("Request to get all Aerodrome Service Outages");
        return aerodromeServiceOutageRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<AerodromeServiceOutage> findAll(String textSearch,
                                                Pageable pageable,
                                                String serviceType,
                                                String aerodromeName,
                                                LocalDateTime startDateTime,
                                                LocalDateTime endDateTime) {
        LOG.debug("Request to get Aerodrome Service Outages by text search. Search: {}", textSearch);
        final FiltersSpecification.Builder filterBuilder = new FiltersSpecification.Builder().lookFor(textSearch);

        if (serviceType != null) {
            filterBuilder.restrictOn(JoinFilter.equal("aerodromeServiceType", "serviceName", serviceType));
        }

        if (aerodromeName != null) {
            filterBuilder.restrictOn(JoinFilter.equal("aerodrome", "aerodromeName", aerodromeName));
        }

        if (startDateTime != null) {
            filterBuilder.restrictOn(Filter.greaterThanOrEqualTo("startDateTime", startDateTime));
        }

        if (endDateTime != null) {
            filterBuilder.restrictOn(Filter.lessThanOrEqualTo("endDateTime", endDateTime));
        }
        return aerodromeServiceOutageRepository.findAll(filterBuilder.build(), pageable);
    }

    @Transactional(readOnly = true)
    public AerodromeServiceOutage getOne(Integer id) {
        LOG.debug("Request to get Aerodrome Service Outage : {}", id);
        return aerodromeServiceOutageRepository.getOne(id);
    }

    public AerodromeServiceOutage save(AerodromeServiceOutage aerodromeServiceOutage) {
        LOG.debug("Request to save Aerodrome Service Outage : {}", aerodromeServiceOutage);
        validate(aerodromeServiceOutage, null);
        return aerodromeServiceOutageRepository.save(aerodromeServiceOutage);
    }

    public void delete(Integer id) {
        LOG.debug("Request to delete Aerodrome Service Outage : {}", id);
        aerodromeServiceOutageRepository.delete(id);
    }

    public AerodromeServiceOutage update(Integer id, AerodromeServiceOutage aerodromeServiceOutage) {
        LOG.debug("Request to update Aerodrome Service Outage : {}", aerodromeServiceOutage);
        validate(aerodromeServiceOutage, id);
        try {
            AerodromeServiceOutage existingAerodromeServiceOutage = aerodromeServiceOutageRepository.getOne(id);
            ModelUtils.merge(aerodromeServiceOutage, existingAerodromeServiceOutage);
            return aerodromeServiceOutageRepository.save(existingAerodromeServiceOutage);
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e, ErrorConstants.ERR_UPDATE_NO_LONGER_EXISTS);
        }
    }

    private void validate(AerodromeServiceOutage aerodromeServiceOutage, Integer id) {

        // validate dates/times
        LocalDateTime sd = aerodromeServiceOutage.getStartDateTime();
        LocalDateTime ed = aerodromeServiceOutage.getEndDateTime();
        Aerodrome aerodrome = aerodromeServiceOutage.getAerodromeServiceTypeMap().getId().getAerodrome();
        AerodromeServiceType aerodromeServiceType = aerodromeServiceOutage.getAerodromeServiceTypeMap().getId().getAerodromeServiceType();

        if (ed.isAfter(sd)) {
            List<AerodromeServiceOutage> overlaps = id == null
                ? aerodromeServiceOutageRepository.getOverlapsDates(sd, ed, aerodrome.getId(), aerodromeServiceType.getId())
                : aerodromeServiceOutageRepository.getOverlapsDates(sd, ed, aerodrome.getId(), aerodromeServiceType.getId(), id);
            if (overlaps != null && !overlaps.isEmpty()) {
                LOG.debug("Bad request: overlapped dates/times for aerodrome: {} and service type: {}",
                    aerodrome.getAerodromeName(), aerodromeServiceType.getServiceName());
                final String details = "Overlapped dates/times for the selected aerodrome and service type";
                throw new CustomParametrizedException(ErrorConstants.ERR_UNIQUENESS_VIOLATION, new Exception(details));
            }
        } else if (sd.equals(ed)) {
            LOG.debug("Bad request: start date/time is the same as end date/time");
            final String details = "Start date/time is the same as end date/time";
            throw new CustomParametrizedException(ErrorConstants.ERR_START_END_DATE, new Exception(details));
        } else {
            LOG.debug("Bad request: start date is greater than end date");
            final String details = "Start date/time is not before the end date/time";
            throw new CustomParametrizedException(ErrorConstants.ERR_START_END_DATE, new Exception(details));
        }

        // validate discount amount
        if (aerodromeServiceOutage.getAerodromeDiscountAmount() < 0 || aerodromeServiceOutage.getApproachDiscountAmount() < 0) {
            LOG.debug("Bad request: Discount Amount is less than 0");
            final String details = "Discount Amount should be more than 0";
            throw new CustomParametrizedException(ErrorConstants.DEF_ERR_VALIDATION, new Exception(details));
        }

        if (aerodromeServiceOutage.getAerodromeDiscountType().equals(DiscountType.percentage) &&
            aerodromeServiceOutage.getAerodromeDiscountAmount() > 100) {
            LOG.debug("Bad request: Aerodrome Discount Amount is not within 0-100% range");
            final String details = "Aerodrome Discount Amount should be should within 0-100%";
            throw new CustomParametrizedException(ErrorConstants.DEF_ERR_VALIDATION, new Exception(details));
        }

        if (aerodromeServiceOutage.getApproachDiscountType().equals(DiscountType.percentage) &&
            aerodromeServiceOutage.getApproachDiscountAmount() > 100) {
            LOG.debug("Bad request: Approach Discount Amount is not within 0-100% range");
            final String details = "Approach Discount Amount should be should within 0-100%";
            throw new CustomParametrizedException(ErrorConstants.DEF_ERR_VALIDATION, new Exception(details));
        }
    }

    public List<AerodromeServiceOutage> getAerodromeServiceOutagesByAerodromeAndDateOfFlight(String aerodromeName, LocalDateTime dateTime) {
        return aerodromeServiceOutageRepository.getAerodromeServiceOutagesByAerodromeAndDateOfFlight(aerodromeName, dateTime);
    }

    public long countAll() {
        return aerodromeServiceOutageRepository.count();
    }
}
