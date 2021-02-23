package ca.ids.abms.plugins.amhs;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import ca.ids.abms.modules.plugins.PluginService;
import ca.ids.abms.modules.system.SystemConfiguration;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.plugins.PluginKey;

@Component
public class AmhsPluginConfigLoader {
    
    public AmhsPluginConfigLoader (final PluginService pluginService, final SystemConfigurationService systemConfigurationService) {
        this.pluginService = pluginService;
        this.systemConfigurationService = systemConfigurationService;
        this.amhsPluginConfigCache = createAmhsPluginConfigCache();
    }
    
    public void clearCache() {
        this.amhsPluginConfigCache.invalidateAll();
    }
    
    public AmhsPluginConfig getConfig() {
        
        // enabled
        final boolean enabled = Optional.of(pluginService.isEnabled(PluginKey.AMHS)).orElse(false);
        
        // workDir
        String messageDir = null;
        final SystemConfiguration item = systemConfigurationService.getOneByItemName(CFG_MSG_DIR);
        if (item != null) {
            messageDir = StringUtils.trimToNull (item.getCurrentValue());
        }
        
        // done
        return new AmhsPluginConfig (enabled, messageDir);
    }
    
    public AmhsPluginConfig getCachedConfig() {
        return amhsPluginConfigCache.getUnchecked (true);
    }
    
    private LoadingCache <Boolean, AmhsPluginConfig> createAmhsPluginConfigCache() {
        final CacheLoader <Boolean, AmhsPluginConfig> loader = new CacheLoader <Boolean, AmhsPluginConfig>() {
            @Override
            public AmhsPluginConfig load(Boolean key) throws Exception {
                return getConfig();
            }
        };
        return CacheBuilder.newBuilder().expireAfterWrite (15, TimeUnit.SECONDS).build (loader);
    }

    private static final String CFG_MSG_DIR = "AMHS: incoming message directory";
    private final PluginService pluginService;
    private final SystemConfigurationService systemConfigurationService;
    private final LoadingCache <Boolean, AmhsPluginConfig> amhsPluginConfigCache;
}
