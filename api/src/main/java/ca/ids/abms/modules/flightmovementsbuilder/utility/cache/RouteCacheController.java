package ca.ids.abms.modules.flightmovementsbuilder.utility.cache;

import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/route-caching")
public class RouteCacheController {

    private final Logger log = LoggerFactory.getLogger(RouteCacheController.class);
    private final RouteCacheService routeCacheService;

    public RouteCacheController(RouteCacheService aRouteCacheService) {
        routeCacheService = aRouteCacheService;
    }

    @PreAuthorize("hasAuthority('route_cache_modify')")
    @RequestMapping(value = "/clear-cache", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteRouteCache() {

        Long count = routeCacheService.getNumberOfRouteCacheRetention();
        log.debug("REST request to delete {} cache entries", count);

        RouteCacheSummaryViewModel rc = routeCacheService.getCount();
        Long actualRowEntries = rc.getCount();
        log.debug("The number of entries currently in the cache {}", actualRowEntries);
        if (actualRowEntries > count) {
            Long limit = actualRowEntries - count;
            log.debug("Deleting  {} oldest route cache items", limit);
            routeCacheService.deleteOldestRouteCache(limit);
        }

        return ResponseEntity.ok().build();
    }

    @RequestMapping(method = RequestMethod.GET)
    public RouteCacheSummaryViewModel getNumberEntriesCache() throws URISyntaxException {
        log.debug("REST request to get the number of entries currently in the cache");
        RouteCacheSummaryViewModel rc = routeCacheService.getCount();
        return rc;
    }

    @RequestMapping(value = "/retention", method = RequestMethod.GET)
    public RouteCacheSummaryViewModel getNumberRetention() throws URISyntaxException {
        log.debug("REST request to get the number of retention");
        RouteCacheSummaryViewModel rc = routeCacheService.getNumberOfRetention();
        return rc;
    }

    @PreAuthorize("hasAuthority('route_cache_modify')")
    @RequestMapping(value = "/reset", method = RequestMethod.DELETE)
    public ResponseEntity<Void> resetCache() {

        log.debug("REST request to reset cache entries");

        routeCacheService.deleteAll();

        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('route_cache_modify')")
    @RequestMapping(value = "/retention/{newValue}", method = RequestMethod.PUT)
    public ResponseEntity<RouteCacheSummaryViewModel> updateAerodromeCategory(@PathVariable Integer newValue) throws URISyntaxException {
        log.debug("REST request to update number of retention : {}", newValue);

        RouteCacheSummaryViewModel result = routeCacheService.updateNumberOfRetention(newValue);

        return ResponseEntity.ok().body(result);
    }
}
