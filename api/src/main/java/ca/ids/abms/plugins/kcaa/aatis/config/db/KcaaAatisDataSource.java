package ca.ids.abms.plugins.kcaa.aatis.config.db;

import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.plugins.kcaa.aatis.modules.system.KcaaAatisConfigurationItemName;
import ca.ids.abms.util.jdbc.MutableDataSource;
import org.springframework.stereotype.Component;

@Component
public class KcaaAatisDataSource extends MutableDataSource {

    private final SystemConfigurationService systemConfigurationService;

    public KcaaAatisDataSource(final SystemConfigurationService systemConfigurationService) {
        super("app.plugins.kcaa.aatis.db");
        this.systemConfigurationService = systemConfigurationService;
    }

    @Override
    protected String getUrl() {
        return systemConfigurationService.getValue(KcaaAatisConfigurationItemName.DATABASE_CONNECTION_URL);
    }
}
