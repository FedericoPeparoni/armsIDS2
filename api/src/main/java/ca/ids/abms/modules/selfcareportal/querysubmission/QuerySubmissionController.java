package ca.ids.abms.modules.selfcareportal.querysubmission;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/query-submission")
public class QuerySubmissionController {

    private static final Logger LOG = LoggerFactory.getLogger(QuerySubmissionController.class);
    private final QuerySubmissionService querySubmissionService;

    public QuerySubmissionController(QuerySubmissionService querySubmissionService) {
        this.querySubmissionService = querySubmissionService;
    }

    @PostMapping
    public ResponseEntity create(@Valid @RequestBody QuerySubmission querySubmission) {
        LOG.debug("REST request to send a query submission : {}", querySubmission);
        if (querySubmissionService.send(querySubmission.getSenderEmail(), querySubmission.getSubject(), querySubmission.getMessage(), null, true))
            return new ResponseEntity(HttpStatus.OK);
        else
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
}
