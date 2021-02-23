package ca.ids.abms.plugins.cronos1.modules.service;

import ca.ids.abms.modules.common.services.AbstractPluginServiceProvider;
import ca.ids.abms.modules.spatiareader.entity.FplObject;
import ca.ids.abms.plugins.PluginKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;

@Service
public class FplObjectService extends AbstractPluginServiceProvider {

    private static final Logger LOG = LoggerFactory.getLogger(FplObjectService.class);

    private final FplObjectRepository fplObjectRepository;

    public FplObjectService(final FplObjectRepository fplObjectRepository) {
        super(PluginKey.CRONOS_1);
        this.fplObjectRepository = fplObjectRepository;
    }

    @Transactional(readOnly = true)
    public List<FplObject> findAll(final LocalDateTime minDate) {
        LOG.debug("CRONOS 1: get flight plans starting from {}", minDate);
        return this.fplObjectRepository.findAllStartingFromDate(minDate);
    }
}
