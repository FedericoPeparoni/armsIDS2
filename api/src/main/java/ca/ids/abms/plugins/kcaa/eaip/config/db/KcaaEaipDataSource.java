package ca.ids.abms.plugins.kcaa.eaip.config.db;

import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.plugins.kcaa.eaip.modules.system.KcaaEaipConfigurationItemName;
import ca.ids.abms.util.jdbc.MutableDataSource;
import org.springframework.stereotype.Component;

@Component
public class KcaaEaipDataSource extends MutableDataSource {

    private final SystemConfigurationService systemConfigurationService;

    public KcaaEaipDataSource(final SystemConfigurationService systemConfigurationService) {
        super("app.plugins.kcaa.eaip.db");
        this.systemConfigurationService = systemConfigurationService;
    }

    @Override
    protected String getUrl() {
        return systemConfigurationService.getValue(KcaaEaipConfigurationItemName.DATABASE_CONNECTION_URL);
    }
}
