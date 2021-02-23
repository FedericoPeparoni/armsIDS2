package ca.ids.abms.modules.statistics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/air-traffic-statistics")
public class AirTrafficStatisticController {

    private final Logger log = LoggerFactory.getLogger(AirTrafficStatisticController.class);
    private final AirTrafficStatisticService airTrafficStatisticService;
    private final AirTrafficStatisticMapper airTrafficStatisticMapper;

    public AirTrafficStatisticController(AirTrafficStatisticService airTrafficStatisticService, AirTrafficStatisticMapper airTrafficStatisticMapper) {
        this.airTrafficStatisticService = airTrafficStatisticService;
        this.airTrafficStatisticMapper = airTrafficStatisticMapper;
    }

    @PreAuthorize("hasAuthority('statistics_generate')")
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<AirTrafficStatistic> create(@Valid @RequestBody AirTrafficStatistic airTrafficStatistic) throws URISyntaxException, UnsupportedEncodingException {
        log.debug("REST request to save template for airTrafficStatistics : {}", airTrafficStatistic.getName());

        AirTrafficStatistic result = airTrafficStatisticService.save(airTrafficStatistic);

        return ResponseEntity.created(new URI("/api/air-traffic-statistics/" + URLEncoder.encode(result.getName(), "UTF-8")))
            .body(result);
    }

    @PreAuthorize("hasAuthority('statistics_generate')")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<AirTrafficStatistic> update(@RequestBody AirTrafficStatistic airTrafficStatistic, @PathVariable Integer id) throws URISyntaxException {
        log.debug("REST request to update template for airTrafficStatistics : {}", airTrafficStatistic);

        AirTrafficStatistic result = airTrafficStatisticService.update(id, airTrafficStatistic);

        return ResponseEntity.ok().body(result);
    }

    @PreAuthorize("hasAuthority('statistics_generate')")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        log.debug("REST request to delete template for airTrafficStatistics : {}", id);

        airTrafficStatisticService.delete(id);

        return ResponseEntity.ok().build();
    }

    // Returns all names
    @RequestMapping(value = "/getNames", method = RequestMethod.GET)
    public ResponseEntity<List<String>> getAllNames() {
        log.debug("REST request to get all names from AirTrafficStatistic");
        List<String> result = airTrafficStatisticService.getAllNames();
        return ResponseEntity.ok().body(result);
    }

    @RequestMapping(value = "/template-name/{name}", method = RequestMethod.GET)
    public ResponseEntity<AirTrafficStatisticViewModel> getAirTrafficStatisticByName(@PathVariable String name) {
        log.debug("REST request to get AirTrafficStatistic by name: {}", name);

        AirTrafficStatistic airTrafficStatistic = airTrafficStatisticService.getOneByName(name);

        return Optional.ofNullable(airTrafficStatistic)
            .map(result -> new ResponseEntity<>(airTrafficStatisticMapper.toViewModel(result), HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
