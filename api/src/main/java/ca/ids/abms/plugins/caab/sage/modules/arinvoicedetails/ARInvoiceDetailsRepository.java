package ca.ids.abms.plugins.caab.sage.modules.arinvoicedetails;

import ca.ids.abms.modules.common.services.AbstractPluginJdbcRepository;
import ca.ids.abms.plugins.caab.sage.config.CaabSageDatabaseConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ARInvoiceDetailsRepository extends AbstractPluginJdbcRepository<ARInvoiceDetails, String> {

    private final ARInvoiceDetailsUtility utility;

    public ARInvoiceDetailsRepository(
        @Qualifier(CaabSageDatabaseConfig.NAMED_PARAMETER_JDBC_TEMPLATE)
            final NamedParameterJdbcTemplate namedParameterJdbcTemplate,
        final ARInvoiceDetailsUtility utility
    ) {
        super(namedParameterJdbcTemplate, utility);
        this.utility = utility;
    }

    /**
     * Persist and return new invoice details.
     *
     * @param arInvoiceDetails to insert
     * @return inserted invoice details
     */
    @Override
    public ARInvoiceDetails insert(ARInvoiceDetails arInvoiceDetails) {

        // object cannot be null to insert
        if (arInvoiceDetails == null)
            throw new IllegalArgumentException("`arInvoiceDetails` argument must NOT be null");

        // insert object into database
        save(utility.insertStatement(arInvoiceDetails));

        // return newly created invoice details
        return arInvoiceDetails;
    }
}
