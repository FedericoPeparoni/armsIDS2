package ca.ids.abms.modules.waketurbulence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wake-turbulence-categories")
public class WakeTurbulenceCategoryController {

    private final Logger log = LoggerFactory.getLogger(WakeTurbulenceCategoryController.class);
    private final WakeTurbulenceCategoryService wakeTurbulenceCategoriesService;

    public WakeTurbulenceCategoryController(WakeTurbulenceCategoryService aWakeTurbulenceCategoryService) {
        wakeTurbulenceCategoriesService = aWakeTurbulenceCategoryService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Page<WakeTurbulenceCategory> getAllWakeTurbulenceCategories(Pageable pageable) {
        log.debug("REST request to get all Wake Turbulence Categories");
        return wakeTurbulenceCategoriesService.findAll(pageable);
    }
}
