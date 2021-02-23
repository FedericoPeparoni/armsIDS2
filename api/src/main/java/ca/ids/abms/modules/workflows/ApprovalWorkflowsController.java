package ca.ids.abms.modules.workflows;

import ca.ids.abms.modules.common.controllers.MediaDocumentComponent;
import ca.ids.abms.modules.reports2.common.ReportDocument;
import ca.ids.abms.modules.reports2.common.ReportDocumentCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(ApprovalWorkflowsController.REQUEST_MAPPING)
@SuppressWarnings({"unused", "squid:S1452"})
public class ApprovalWorkflowsController extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(ApprovalWorkflowsController.class);
    static final String REQUEST_MAPPING = "/api/approval-workflow";

    private final ApprovalWorkflowService approvalWorkflowService;
    private final ApprovalWorkflowMapper workflowMapper;
    private final ReportDocumentCreator reportDocumentCreator;

    public ApprovalWorkflowsController(final ApprovalWorkflowService approvalWorkflowService,
                                       final ApprovalWorkflowMapper workflowMapper,
                                       final ReportDocumentCreator reportDocumentCreator) {
        this.approvalWorkflowService = approvalWorkflowService;
        this.workflowMapper = workflowMapper;
        this.reportDocumentCreator = reportDocumentCreator;
    }

    @PreAuthorize("hasAuthority('approval_workflow_view') or hasAuthority('approval_workflow_modify')")
    @GetMapping(value = "/{level}")
    public ResponseEntity<ApprovalWorkflowViewModel> getOne(@PathVariable Integer level) {
        LOG.debug("REST request to get ApprovalWorkflow ID: {}", level);

        final ApprovalWorkflow entity = approvalWorkflowService.getOne(level);

        return Optional.ofNullable(entity)
            .map(result -> new ResponseEntity<>(workflowMapper.toViewModel(entity), HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAuthority('approval_workflow_view') or hasAuthority('transaction_pending_view') " +
        "or hasAuthority('approval_workflow_modify') or hasAuthority('transaction_pending_modify')")
    @GetMapping
    public ResponseEntity<?> getAll(@SortDefault(sort = {"level"}, direction = Sort.Direction.ASC) final Pageable pageable,
                                    @RequestParam(name = "csvExport", required = false) Boolean csvExport) {
        LOG.debug("REST request to get all ApprovalWorkflow ");

        final Page<ApprovalWorkflow> entities = approvalWorkflowService.findAll(pageable);

        if (csvExport != null && csvExport) {
            final List<ApprovalWorkflow> list = entities.getContent();
            final List<ApprovalWorkflowCsvExportModel> csvExportModel = workflowMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Transaction_Approval_Workflow", csvExportModel,
                ApprovalWorkflowCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            return Optional.ofNullable(entities)
                .map(result -> new ResponseEntity<Page<ApprovalWorkflowViewModel>>(
                    new PageImpl<>(workflowMapper.toViewModel(entities), pageable, entities.getTotalElements()),
                    HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }
    }

    @PreAuthorize("hasAuthority('approval_workflow_modify')")
    @PostMapping(value = "/{level}")
    public ResponseEntity<ApprovalWorkflowViewModel> create(@PathVariable final Integer level,
                                                            @Valid @RequestBody final ApprovalWorkflowViewModel dto) throws URISyntaxException {
        LOG.debug("REST request to create ApprovalWorkflow with level {}: {}", level, dto);

        final ApprovalWorkflow entity = workflowMapper.toModel(dto);
        final ApprovalWorkflow newEntity = approvalWorkflowService.create(level, entity);
        final ApprovalWorkflowViewModel result = workflowMapper.toViewModel(newEntity);

        return ResponseEntity.created(requestMappingEndpoint("/" + result.getLevel()))
            .body(result);
    }

    @PreAuthorize("hasAuthority('approval_workflow_modify')")
    @PostMapping
    public ResponseEntity<Void> createAll(@Valid @RequestBody final List<ApprovalWorkflowViewModel> dtoList) throws URISyntaxException {
        LOG.debug("REST request to create {} ApprovalWorkflow items", dtoList.size());

        final List<ApprovalWorkflow> entities = workflowMapper.toModel(dtoList);
        approvalWorkflowService.create(entities);

        return ResponseEntity.created(requestMappingEndpoint("/")).build();
    }

    @PreAuthorize("hasAuthority('approval_workflow_modify')")
    @PutMapping(value = "/{level}")
    public ResponseEntity<ApprovalWorkflowViewModel> update(@Valid @RequestBody final ApprovalWorkflowViewModel dto,
                                                            @PathVariable final Integer level) {
        LOG.debug("REST request to update ApprovalWorkflow level {}: {}", level, dto);

        if (level == null) {
            return ResponseEntity.badRequest().body(null);
        }

        final ApprovalWorkflow entity = workflowMapper.toModel(dto);
        final ApprovalWorkflow newEntity = approvalWorkflowService.update(level, entity);
        final ApprovalWorkflowViewModel result = workflowMapper.toViewModel(newEntity);

        return ResponseEntity.ok().body(result);
    }

    @PreAuthorize("hasAuthority('approval_workflow_modify')")
    @DeleteMapping(value = "/{level}")
    public ResponseEntity<Void> delete(@PathVariable final Integer level) {
        LOG.debug("REST request to delete ApprovalWorkflow level : {}", level);
        if (level == null) {
            return ResponseEntity.badRequest().body(null);
        }
        approvalWorkflowService.delete(level);
        return ResponseEntity.ok().build();
    }

    /**
     * Get request mapping endpoint.
     *
     * @param endpoint of request
     * @return URI representation of endpoint
     * @throws URISyntaxException if endpoint contains invalid characters
     */
    private URI requestMappingEndpoint(String endpoint) throws URISyntaxException {
        return new URI(REQUEST_MAPPING + endpoint);
    }

}
