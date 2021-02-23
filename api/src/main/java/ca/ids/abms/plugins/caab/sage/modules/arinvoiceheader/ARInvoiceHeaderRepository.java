package ca.ids.abms.plugins.caab.sage.modules.arinvoiceheader;

import ca.ids.abms.modules.common.services.AbstractPluginJdbcRepository;
import ca.ids.abms.plugins.caab.sage.config.CaabSageDatabaseConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ARInvoiceHeaderRepository extends AbstractPluginJdbcRepository<ARInvoiceHeader, String> {

    private final ARInvoiceHeaderUtility utility;

    public ARInvoiceHeaderRepository(
        @Qualifier(CaabSageDatabaseConfig.NAMED_PARAMETER_JDBC_TEMPLATE)
            final NamedParameterJdbcTemplate namedParameterJdbcTemplate,
        final ARInvoiceHeaderUtility utility
    ) {
        super(namedParameterJdbcTemplate, utility);
        this.utility = utility;
    }

    /**
     * Persist and return new invoice header.
     *
     * @param arInvoiceHeader to insert
     * @return inserted invoice header
     */
    @Override
    public ARInvoiceHeader insert(ARInvoiceHeader arInvoiceHeader) {

        // object cannot be null to insert
        if (arInvoiceHeader == null)
            throw new IllegalArgumentException("`arInvoiceHeader` argument must NOT be null");

        // insert object into database
        save(utility.insertStatement(arInvoiceHeader));

        // return newly created invoice header
        return arInvoiceHeader;
    }
}
