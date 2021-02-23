package ca.ids.abms.plugins.kcaa.erp.modules.salesheader;

import ca.ids.abms.plugins.kcaa.common.services.KcaaPluginJdbcUtility;
import ca.ids.abms.plugins.kcaa.erp.utilities.jdbc.ErpRowMapper;
import org.springframework.stereotype.Component;

@Component
public class SalesHeaderUtility extends KcaaPluginJdbcUtility<SalesHeader, String> {

    public SalesHeaderUtility() {
        super(SalesHeader.class, new ErpRowMapper<>(SalesHeader.class));
    }
}
