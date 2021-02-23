package ca.ids.abms.modules.amhsconfiguration;

import ca.ids.abms.amhs.AmhsAgentStatus;
import ca.ids.abms.modules.common.controllers.MediaDocumentComponent;
import ca.ids.abms.modules.reports2.common.ReportDocument;
import ca.ids.abms.modules.reports2.common.ReportDocumentCreator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping(AmhsConfigurationController.ENDPOINT)
public class AmhsConfigurationController extends MediaDocumentComponent {

    static final String ENDPOINT = "/api/amhs-configurations";

    private static final Logger LOG = LoggerFactory.getLogger(AmhsConfigurationController.class);

    private final AmhsConfigurationService amhsConfigurationService;
    private final ReportDocumentCreator reportDocumentCreator;

    public AmhsConfigurationController(
            final AmhsConfigurationService amhsConfigurationService,
            final ReportDocumentCreator reportDocumentCreator) {
        this.amhsConfigurationService = amhsConfigurationService;
        this.reportDocumentCreator = reportDocumentCreator;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('amhs_config_view')")
    public ResponseEntity<?> getPage(
            @RequestParam(name = "searchFilter", required = false) final String searchFilter,
            @SortDefault.SortDefaults({@SortDefault(sort = { "remoteHostname", "id" })}) final Pageable pageable,
            @RequestParam(name = "csvExport", required = false) Boolean csvExport) {
        LOG.debug("REST request to get all AMHS configurations with search '{}' for page '{}'", searchFilter, pageable);
        
        Page <AmhsConfiguration> page = amhsConfigurationService.findAll (searchFilter, pageable);

        if (csvExport != null && csvExport) {
            ReportDocument report = reportDocumentCreator.createCsvDocument (
                    "amhs_configurations", page.getContent(), AmhsConfiguration.class, true);
            return doCreateBinaryResponse(report);
        }
        return ResponseEntity.ok().body(page);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('amhs_config_view')")
    public ResponseEntity<AmhsConfiguration> getOne (@PathVariable final Integer id) {
        LOG.debug("REST request to get AMHS Configuration with id '{}'", id);
        return ResponseEntity.ok().body(amhsConfigurationService.findOne(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('amhs_config_modify')")
    public ResponseEntity<AmhsConfiguration> create (@Valid @RequestBody final AmhsConfiguration model) throws URISyntaxException {
        LOG.debug("REST request to create AMHS Configuration : {}", model);
        final AmhsConfiguration entity = amhsConfigurationService.create (model);
        return ResponseEntity.created(new URI(ENDPOINT + "/" + entity.getId()))
                .body(entity);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('amhs_config_modify')")
    public ResponseEntity<AmhsConfiguration> update(
            @PathVariable final Integer id,
            @Valid @RequestBody final AmhsConfiguration model) {
        LOG.debug("REST request to update AMHS Configuration with id '{}' : {}", id, model);
        final AmhsConfiguration entity = amhsConfigurationService.update (id, model);
        return ResponseEntity.ok().body (entity);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('amhs_config_modify')")
    public ResponseEntity<?> delete(@PathVariable final Integer id) {
        LOG.debug("REST request to delete AMHS Configuration with id '{}'", id);
        amhsConfigurationService.remove (id);
        return ResponseEntity.ok().body (null);
    }

    @GetMapping(path = "/list")
    @PreAuthorize("hasAuthority('amhs_config_view')")
    public ResponseEntity<List<AmhsConfiguration>> list() {
        LOG.debug("REST request to get list of AMHS Configurations");
        return ResponseEntity.ok(amhsConfigurationService.findAll());
    }

    @PostMapping("/agent/start")
    @PreAuthorize("hasAuthority('amhs_config_modify')")
    public ResponseEntity<AmhsAgentStatus> startAgent() {
        final AmhsAgentStatus x = amhsConfigurationService.startAgent();
        return ResponseEntity.ok().body(x);
    }
    
    @PostMapping("/agent/stop")
    @PreAuthorize("hasAuthority('amhs_config_modify')")
    public ResponseEntity<AmhsAgentStatus> stopAgent() {
        final AmhsAgentStatus x = amhsConfigurationService.stopAgent();
        return ResponseEntity.ok().body(x);
    }
    
    @PostMapping("/agent/restart")
    @PreAuthorize("hasAuthority('amhs_config_modify')")
    public ResponseEntity<AmhsAgentStatus> restartAgent() {
        final AmhsAgentStatus x = amhsConfigurationService.restartAgent();
        return ResponseEntity.ok().body(x);
    }
    
    @GetMapping("/agent/status")
    @PreAuthorize("hasAuthority('amhs_config_view')")
    public ResponseEntity<AmhsAgentStatus> agentStatus() {
        final AmhsAgentStatus x = amhsConfigurationService.agentStatus();
        return ResponseEntity.ok().body(x);
    }
}
