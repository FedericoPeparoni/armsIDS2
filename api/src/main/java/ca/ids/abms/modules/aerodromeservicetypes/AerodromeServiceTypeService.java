package ca.ids.abms.modules.aerodromeservicetypes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AerodromeServiceTypeService {

    private AerodromeServiceTypeRepository aerodromeServiceTypeRepository;
    private static final Logger LOG = LoggerFactory.getLogger(AerodromeServiceTypeService.class);

    public AerodromeServiceTypeService(AerodromeServiceTypeRepository aerodromeServiceTypeRepository) {
        this.aerodromeServiceTypeRepository = aerodromeServiceTypeRepository;
    }

    @Transactional(readOnly = true)
    public List<AerodromeServiceType> findAll() {
        LOG.debug("Request to get all Aerodrome Service Types");
        return aerodromeServiceTypeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public AerodromeServiceType getOne(Integer id) {
        LOG.debug("Request to get Aerodrome Service Types : {}", id);
        return aerodromeServiceTypeRepository.getOne(id);
    }

    @Transactional(readOnly = true)
    public AerodromeServiceType findByServiceName(String name) {
        LOG.debug("Request to get Aerodrome Service Types by name: {}", name);
        return aerodromeServiceTypeRepository.findByServiceName(name);
    }
}
