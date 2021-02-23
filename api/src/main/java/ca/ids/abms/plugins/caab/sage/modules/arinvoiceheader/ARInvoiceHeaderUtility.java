package ca.ids.abms.plugins.caab.sage.modules.arinvoiceheader;

import ca.ids.abms.modules.common.services.PluginJdbcUtility;
import ca.ids.abms.util.jdbc.AutoRowMapper;
import org.springframework.stereotype.Component;

@Component
public class ARInvoiceHeaderUtility extends PluginJdbcUtility<ARInvoiceHeader, String> {

    public ARInvoiceHeaderUtility() {
        super(ARInvoiceHeader.class, new AutoRowMapper<>(ARInvoiceHeader.class));
    }
}
