package ca.ids.abms.modules.system;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/system-data-types")
public class SystemDataTypeController {

    private final Logger log = LoggerFactory.getLogger(SystemDataTypeController.class);
    private final SystemDataTypeService systemDataTypeService;

    public SystemDataTypeController(SystemDataTypeService aSystemDataTypeService) {
        systemDataTypeService = aSystemDataTypeService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Page<SystemDataType> getAllSystemDataTypes(Pageable pageable) {
        log.debug("REST request to get all system data types");
        return systemDataTypeService.findAll(pageable);
    }
}
