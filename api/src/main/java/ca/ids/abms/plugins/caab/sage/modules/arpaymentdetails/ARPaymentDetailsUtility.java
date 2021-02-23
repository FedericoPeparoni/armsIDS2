package ca.ids.abms.plugins.caab.sage.modules.arpaymentdetails;

import ca.ids.abms.modules.common.services.PluginJdbcUtility;
import ca.ids.abms.util.jdbc.AutoRowMapper;
import org.springframework.stereotype.Component;

@Component
public class ARPaymentDetailsUtility extends PluginJdbcUtility<ARPaymentDetails, String> {

    public ARPaymentDetailsUtility() {
        super(ARPaymentDetails.class, new AutoRowMapper<>(ARPaymentDetails.class));
    }
}
