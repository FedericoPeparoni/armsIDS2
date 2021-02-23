package ca.ids.abms.plugins.caab.sage.config.db;

import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.plugins.caab.sage.modules.system.CaabSageConfigurationItemName;
import ca.ids.abms.util.jdbc.MutableDataSource;
import org.springframework.stereotype.Component;

@Component
public class CaabSageDataSource extends MutableDataSource {

    private final SystemConfigurationService systemConfigurationService;

    public CaabSageDataSource(final SystemConfigurationService systemConfigurationService) {
        super("app.plugins.caab.sage.db");
        this.systemConfigurationService = systemConfigurationService;
    }

    @Override
    protected String getUrl() {
        return systemConfigurationService.getValue(CaabSageConfigurationItemName.DATABASE_CONNECTION_URL);
    }
}
