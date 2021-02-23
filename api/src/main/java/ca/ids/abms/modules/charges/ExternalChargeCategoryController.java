package ca.ids.abms.modules.charges;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
@RequestMapping("/api/external-charge-categories")
public class ExternalChargeCategoryController {

    private static final Logger LOG = LoggerFactory.getLogger(ExternalChargeCategoryController.class);

    private final ExternalChargeCategoryService externalChargeCategoryService;

    public ExternalChargeCategoryController(final ExternalChargeCategoryService externalChargeCategoryService) {
        this.externalChargeCategoryService = externalChargeCategoryService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ExternalChargeCategory>> get() {
        LOG.debug("Request to get all external charge categories.");
        return ResponseEntity.ok(externalChargeCategoryService.findAll());
    }

    @RequestMapping(value = "/non-aviation", method = RequestMethod.GET)
    public ResponseEntity<List<ExternalChargeCategory>> nonAviation() {
        LOG.debug("Request to get all external charge categories.");
        return ResponseEntity.ok(externalChargeCategoryService.findAllNonAviation());
    }
}
