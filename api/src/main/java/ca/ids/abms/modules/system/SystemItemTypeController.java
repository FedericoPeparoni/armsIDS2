package ca.ids.abms.modules.system;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/system-item-types")
public class SystemItemTypeController {

    private final Logger log = LoggerFactory.getLogger(SystemItemTypeController.class);
    private final SystemItemTypeService systemItemTypeService;

    public SystemItemTypeController(SystemItemTypeService aSystemItemTypeService) {
        systemItemTypeService = aSystemItemTypeService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Page<SystemItemType> getAllSystemItemClasses(Pageable pageable) {
        log.debug("REST request to get all system item type");
        return systemItemTypeService.findAll(pageable);
    }
}
