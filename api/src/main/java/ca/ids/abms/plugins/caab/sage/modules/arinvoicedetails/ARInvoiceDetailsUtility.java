package ca.ids.abms.plugins.caab.sage.modules.arinvoicedetails;

import ca.ids.abms.modules.common.services.PluginJdbcUtility;
import ca.ids.abms.util.jdbc.AutoRowMapper;
import org.springframework.stereotype.Component;

@Component
public class ARInvoiceDetailsUtility extends PluginJdbcUtility<ARInvoiceDetails, String> {

    public ARInvoiceDetailsUtility() {
        super(ARInvoiceDetails.class, new AutoRowMapper<>(ARInvoiceDetails.class));
    }
}
