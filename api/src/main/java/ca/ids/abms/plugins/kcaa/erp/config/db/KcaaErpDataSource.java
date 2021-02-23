package ca.ids.abms.plugins.kcaa.erp.config.db;

import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.plugins.kcaa.erp.modules.system.KcaaErpConfigurationItemName;
import ca.ids.abms.util.jdbc.MutableDataSource;
import org.springframework.stereotype.Component;

@Component
public class KcaaErpDataSource extends MutableDataSource {

    private final SystemConfigurationService systemConfigurationService;

    public KcaaErpDataSource(final SystemConfigurationService systemConfigurationService) {
        super("app.plugins.kcaa.erp.db");
        this.systemConfigurationService = systemConfigurationService;
    }

    @Override
    protected String getUrl() {
        return systemConfigurationService.getValue(KcaaErpConfigurationItemName.DATABASE_CONNECTION_URL);
    }
}
