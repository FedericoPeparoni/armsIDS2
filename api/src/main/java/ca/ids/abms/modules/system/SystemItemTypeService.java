package ca.ids.abms.modules.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SystemItemTypeService {

    private final Logger log = LoggerFactory.getLogger(SystemItemTypeService.class);
    private final SystemItemTypeRepository systemItemTypeRepository;

    public SystemItemTypeService(SystemItemTypeRepository aSystemItemTypeRepository) {
        systemItemTypeRepository = aSystemItemTypeRepository;
    }

    @Transactional(readOnly = true)
    public Page<SystemItemType> findAll(Pageable pageable) {
        log.debug("Request to get system item types");
        return systemItemTypeRepository.findAll(pageable);
    }

}
