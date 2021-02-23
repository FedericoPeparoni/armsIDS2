package ca.ids.abms.plugins.cronos2.modules.service;

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
public class Cronos2Service extends AbstractPluginServiceProvider {

    private static final Logger LOG = LoggerFactory.getLogger(Cronos2Service.class);

    private final Cronos2Repository cronos2Repository;

    public Cronos2Service(final Cronos2Repository cronos2Repository) {
        super(PluginKey.CRONOS_2);
        this.cronos2Repository = cronos2Repository;
    }

    @Transactional(readOnly = true)
    public List<FplObject> findAll(final LocalDateTime minDate) {
        LOG.debug("CRONOS 2: get flight plans starting from {}", minDate);
        return this.cronos2Repository.findAllStartingFromDate(minDate);
    }
}
