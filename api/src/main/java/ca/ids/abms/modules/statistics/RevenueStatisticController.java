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
@RequestMapping("/api/revenue-statistics")
public class RevenueStatisticController {

    private final Logger log = LoggerFactory.getLogger(RevenueStatisticController.class);
    private final RevenueStatisticService revenueStatisticService;
    private final RevenueStatisticMapper revenueStatisticMapper;

    public RevenueStatisticController(RevenueStatisticService revenueStatisticService, RevenueStatisticMapper revenueStatisticMapper) {
        this.revenueStatisticService = revenueStatisticService;
        this.revenueStatisticMapper = revenueStatisticMapper;
    }

    @PreAuthorize("hasAuthority('statistics_generate')")
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<RevenueStatisticViewModel> create(@Valid @RequestBody RevenueStatisticViewModel revenueDto) throws URISyntaxException, UnsupportedEncodingException {
        log.debug("REST request to save template for RevenueStatisticViewModel : {}", revenueDto.getName());

        final RevenueStatistic revenueStatistic = revenueStatisticMapper.toModel(revenueDto);
        final RevenueStatisticViewModel result = revenueStatisticMapper.toViewModel(revenueStatisticService.save(revenueStatistic));

        return ResponseEntity.created(new URI("/api/revenue-statistics/" + URLEncoder.encode(result.getName(), "UTF-8")))
    		.body(result);
    }

    @PreAuthorize("hasAuthority('statistics_generate')")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<RevenueStatisticViewModel> update(@RequestBody RevenueStatisticViewModel revenueDto, @PathVariable Integer id) {
        log.debug("REST request to update template for RevenueStatisticViewModel : {}", revenueDto);

        final RevenueStatistic revenueStatistic = revenueStatisticMapper.toModel(revenueDto);
        final RevenueStatisticViewModel result = revenueStatisticMapper.toViewModel(revenueStatisticService.update(id, revenueStatistic));

        return ResponseEntity.ok().body(result);
    }

    @PreAuthorize("hasAuthority('statistics_generate')")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        log.debug("REST request to delete template for RevenueStatisticViewModel : {}", id);

        revenueStatisticService.delete(id);

        return ResponseEntity.ok().build();
    }

    // Returns all names
    @RequestMapping(value = "/getNames", method = RequestMethod.GET)
    public ResponseEntity<List<String>> getAllNames() {
        log.debug("REST request to get all names from RevenueStatistic");
        List<String> result = revenueStatisticService.getAllNames();
        return ResponseEntity.ok().body(result);
    }

    @RequestMapping(value = "/template-name/{name}", method = RequestMethod.GET)
    public ResponseEntity<RevenueStatisticViewModel> getRevenueStatisticByName(@PathVariable String name) {
        log.debug("REST request to get RevenueStatistic by name: {}", name);

        RevenueStatistic revenueStatistic = revenueStatisticService.getOneByName(name);

        return Optional.ofNullable(revenueStatistic)
            .map(result -> new ResponseEntity<>(revenueStatisticMapper.toViewModel(result), HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
