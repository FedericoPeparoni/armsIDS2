package ca.ids.abms.modules.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SystemDataTypeService {

    private final Logger log = LoggerFactory.getLogger(SystemDataTypeService.class);
    private final SystemDataTypeRepository systemDataTypeRepository;

    public SystemDataTypeService(SystemDataTypeRepository aSystemDataTypeRepository) {
        systemDataTypeRepository = aSystemDataTypeRepository;
    }

    @Transactional(readOnly = true)
    public Page<SystemDataType> findAll(Pageable pageable) {
        log.debug("Request to get system data types");
        return systemDataTypeRepository.findAll(pageable);
    }

}
