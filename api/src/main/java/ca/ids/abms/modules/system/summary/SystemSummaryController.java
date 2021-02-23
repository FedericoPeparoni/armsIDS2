package ca.ids.abms.modules.system.summary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/system-summary")
public class SystemSummaryController {

    private final Logger log = LoggerFactory.getLogger(SystemSummaryController.class);
    private final SystemSummaryService systemSummaryService;
    
    public SystemSummaryController(SystemSummaryService aSystemSummaryService) {
        systemSummaryService = aSystemSummaryService;
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public SystemSummaryViewModel getSystemSummary() {
        log.debug("REST request to get system summary");
        final SystemSummaryViewModel systemSummary = systemSummaryService.getSystemSummary();
        return systemSummary;
    }
}
