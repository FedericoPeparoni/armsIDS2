package ca.ids.abms.plugins.caab.sage.modules.arpaymentheader;

import ca.ids.abms.modules.common.services.PluginJdbcUtility;
import ca.ids.abms.util.jdbc.AutoRowMapper;
import org.springframework.stereotype.Component;

@Component
public class ARPaymentHeaderUtility extends PluginJdbcUtility<ARPaymentHeader, String> {

    public ARPaymentHeaderUtility() {
        super(ARPaymentHeader.class, new AutoRowMapper<>(ARPaymentHeader.class));
    }
}
