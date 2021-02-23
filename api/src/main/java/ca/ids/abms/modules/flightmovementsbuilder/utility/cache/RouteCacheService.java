package ca.ids.abms.modules.flightmovementsbuilder.utility.cache;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ca.ids.abms.modules.system.SystemConfiguration;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;

@Service
@Transactional(propagation=Propagation.REQUIRES_NEW)
public class RouteCacheService {

    private final Logger log = LoggerFactory.getLogger(RouteCacheService.class);

    private RouteCacheRepository routeCacheRepository;

    private SystemConfigurationService systemConfigurationService;

    public RouteCacheService(RouteCacheRepository aRouteCacheRepository, SystemConfigurationService aSystemConfigurationService) {
        routeCacheRepository = aRouteCacheRepository;
        systemConfigurationService = aSystemConfigurationService;
    }

    public void delete(Integer id) {
        log.debug("Request to delete RouteCache : {}", id);
        routeCacheRepository.delete(id);
    }

    public void deleteAll() {
        log.debug("Request to delete all RouteCache");
        routeCacheRepository.deleteAll();
    }

    public void deleteOldestRouteCache(Long limit) {
        log.debug("Request to delete the {} oldest RouteCache", limit);
        routeCacheRepository.deleteOldestRouteCache(limit);
    }

    @Transactional(propagation=Propagation.REQUIRES_NEW, readOnly=true)
    public RouteCache findByKey(String depAd, String route, String destAd){
        RouteCache routeCache=null;
        if(StringUtils.isNotBlank(depAd) && StringUtils.isNotBlank(route) && StringUtils.isNotBlank(destAd)){
            routeCache=routeCacheRepository.findTop1ByDepartureAerodromeAndRouteTextAndDestinationAerodrome(depAd, route, destAd);
        }
        return routeCache;
    }

    @Transactional(propagation=Propagation.REQUIRES_NEW, readOnly=true)
    public RouteCache findByKey(String depAd, String route, String destAd, int speed, int estimatedElapsed){
        RouteCache routeCache=null;
        if(StringUtils.isNotBlank(depAd) && StringUtils.isNotBlank(route) && StringUtils.isNotBlank(destAd)){
            routeCache=routeCacheRepository.findByDepartureAerodromeAndRouteTextAndDestinationAerodromeAndSpeedAndEstimatedElapsed(depAd, route, destAd, speed, estimatedElapsed);
        }
        return routeCache;
    }
    
    @Transactional(propagation=Propagation.REQUIRES_NEW, readOnly=true)
    public RouteCache findByKey(String depAd, String route, String destAd, Double flightLevel){
        RouteCache routeCache=null;
        if(flightLevel == null) {
            return findByKey(depAd, route, destAd);
        }
        if(StringUtils.isNotBlank(depAd) && StringUtils.isNotBlank(route) && StringUtils.isNotBlank(destAd)){
            routeCache=routeCacheRepository.findTop1ByDepartureAerodromeAndRouteTextAndDestinationAerodromeAndFlightLevel(depAd, route, destAd, flightLevel);
        }
        return routeCache;
    }
    
    @Transactional(propagation=Propagation.REQUIRES_NEW, readOnly=true)
    public RouteCache findByKey(String depAd, String route, String destAd, int speed, int estimatedElapsed, Double flightLevel){
        RouteCache routeCache=null;
        if(flightLevel == null) {
            return findByKey(depAd, route, destAd, speed, estimatedElapsed);
        }
        if(StringUtils.isNotBlank(depAd) && StringUtils.isNotBlank(route) && StringUtils.isNotBlank(destAd)){
            routeCache=routeCacheRepository.findByDepartureAerodromeAndRouteTextAndDestinationAerodromeAndSpeedAndEstimatedElapsedAndFlightLevel(depAd, route, destAd, speed, estimatedElapsed, flightLevel);        
        }
        return routeCache;
    }

    public RouteCacheSummaryViewModel getCount() {
        log.trace("Request to count RouteCache");
        RouteCacheSummaryViewModel rc = new RouteCacheSummaryViewModel();
        Long count = routeCacheRepository.count();
        rc.setCount(count);
        return rc;
    }

    public RouteCacheSummaryViewModel getNumberOfRetention() {
		Long retention = getNumberOfRouteCacheRetention();
		RouteCacheSummaryViewModel rc = new RouteCacheSummaryViewModel();
        rc.setNumberOfRetention(retention);
        return rc;
	}

	public Long getNumberOfRouteCacheRetention(){
        Long count = 0l;
        SystemConfiguration systemConfiguration=systemConfigurationService.getOneByItemName(SystemConfigurationItemName.ROUTE_CACHE_COUNT);
        if(systemConfiguration!=null){
            try {
                count = Long.parseLong(systemConfiguration.getCurrentValue());
            }catch(NumberFormatException e){
                log.error("Error on parse the value for the property : "+ SystemConfigurationItemName.ROUTE_CACHE_COUNT);
            }
        }
        return count;
    }

	public RouteCache save(RouteCache routeCache) {
        log.debug("Request to save RouteCache : {}", routeCache);
        return routeCacheRepository.save(routeCache);
    }

	public RouteCacheSummaryViewModel updateNumberOfRetention(Integer value) {
		SystemConfiguration item=systemConfigurationService.getOneByItemName(SystemConfigurationItemName.ROUTE_CACHE_COUNT);
		item.setCurrentValue(value.toString());
		systemConfigurationService.update(item);
		RouteCacheSummaryViewModel rc = new RouteCacheSummaryViewModel();
        rc.setNumberOfRetention(value.longValue());
        return rc;
	}
	
	public Double getSegmentLength(String segment) {
	    Double len = routeCacheRepository.getSegmentLength(segment);
	    if(len != null) {
	        len= len/1000;
	    }
	    return len;
	}
}
