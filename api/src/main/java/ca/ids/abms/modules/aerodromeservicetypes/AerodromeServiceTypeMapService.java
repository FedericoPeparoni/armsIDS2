package ca.ids.abms.modules.aerodromeservicetypes;

import ca.ids.abms.config.db.Filter;
import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.config.db.JoinFilter;
import ca.ids.abms.modules.aerodromeserviceoutages.AerodromeServiceOutageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AerodromeServiceTypeMapService {
    private AerodromeServiceTypeMapRepository aerodromeServiceTypeMapRepository;

    private static final Logger LOG = LoggerFactory.getLogger(AerodromeServiceOutageService.class);

    public AerodromeServiceTypeMapService(AerodromeServiceTypeMapRepository aerodromeServiceTypeMapRepository) {
        this.aerodromeServiceTypeMapRepository = aerodromeServiceTypeMapRepository;
    }

    @Transactional(readOnly = true)
    public Page<AerodromeServiceTypeMap> getAllAerodromeServiceTypeMapByFilter(String textSearch,
                                                                               Pageable pageable,
                                                                               String serviceType,
                                                                               String aerodromeStatus,
                                                                               String aerodromeName,
                                                                               LocalDateTime startDateTime,
                                                                               LocalDateTime endDateTime) {
        LOG.debug("Request to get all Aerodrome Service Type Map");

        final FiltersSpecification.Builder filterBuilder = new FiltersSpecification.Builder().lookFor(textSearch);

        if (serviceType != null) {
            filterBuilder.restrictOn(JoinFilter.equal("aerodromeServiceType", "serviceName", serviceType));
        }

        if (aerodromeStatus != null && aerodromeStatus.equals("withOutagesOnly")) {
            filterBuilder.restrictOn(Filter.notEmpty("aerodromeServiceOutages"));
        }

        if (aerodromeName != null) {
            filterBuilder.restrictOn(JoinFilter.equal("aerodrome", "aerodromeName", aerodromeName));
        }

        if (startDateTime != null) {
            filterBuilder.restrictOn(JoinFilter.greaterThanOrEqualTo("aerodromeServiceOutages","startDateTime", startDateTime));
        }

        if (endDateTime != null) {
            filterBuilder.restrictOn(JoinFilter.lessThanOrEqualTo("aerodromeServiceOutages","endDateTime", endDateTime));
        }

        return aerodromeServiceTypeMapRepository.findAll(filterBuilder.build(), pageable);
    }

    public long countAll() {
        return aerodromeServiceTypeMapRepository.count();
    }
}
