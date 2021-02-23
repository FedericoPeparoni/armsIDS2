package ca.ids.abms.modules.waketurbulence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WakeTurbulenceCategoryService {

    private final Logger log = LoggerFactory.getLogger(WakeTurbulenceCategoryService.class);

    private WakeTurbulenceCategoryRepository wakeTurbulenceCategoryRepository;

    public WakeTurbulenceCategoryService(WakeTurbulenceCategoryRepository aWakeTurbulenceCategoryRepository) {
        wakeTurbulenceCategoryRepository = aWakeTurbulenceCategoryRepository;
    }

    @Transactional(readOnly = true)
    public Page<WakeTurbulenceCategory> findAll(Pageable pageable) {
        log.debug("Request to get wakeTurbulenceCategories");
        return wakeTurbulenceCategoryRepository.findAll(pageable);
    }
}
