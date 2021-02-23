package ca.ids.abms.plugins.caab.sage.modules.arpaymentheader;

import ca.ids.abms.modules.common.services.AbstractPluginJdbcRepository;
import ca.ids.abms.plugins.caab.sage.config.CaabSageDatabaseConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ARPaymentHeaderRepository extends AbstractPluginJdbcRepository<ARPaymentHeader, String> {

    private final ARPaymentHeaderUtility utility;

    public ARPaymentHeaderRepository(
        @Qualifier(CaabSageDatabaseConfig.NAMED_PARAMETER_JDBC_TEMPLATE)
            final NamedParameterJdbcTemplate namedParameterJdbcTemplate,
        final ARPaymentHeaderUtility utility
    ) {
        super(namedParameterJdbcTemplate, utility);
        this.utility = utility;
    }

    /**
     * Persist and return new payment header.
     *
     * @param arPaymentHeader to insert
     * @return inserted payment header
     */
    @Override
    public ARPaymentHeader insert(ARPaymentHeader arPaymentHeader) {

        // object cannot be null to insert
        if (arPaymentHeader == null)
            throw new IllegalArgumentException("`arPaymentHeader` argument must NOT be null");

        // insert object into database
        save(utility.insertStatement(arPaymentHeader));

        // return newly created payment header
        return arPaymentHeader;
    }
}
