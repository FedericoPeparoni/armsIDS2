package ca.ids.abms.plugins.caab.sage.modules.arpaymentdetails;

import ca.ids.abms.modules.common.services.AbstractPluginJdbcRepository;
import ca.ids.abms.plugins.caab.sage.config.CaabSageDatabaseConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ARPaymentDetailsRepository extends AbstractPluginJdbcRepository<ARPaymentDetails, String> {

    private final ARPaymentDetailsUtility utility;

    public ARPaymentDetailsRepository(
        @Qualifier(CaabSageDatabaseConfig.NAMED_PARAMETER_JDBC_TEMPLATE)
            final NamedParameterJdbcTemplate namedParameterJdbcTemplate,
        final ARPaymentDetailsUtility utility
    ) {
        super(namedParameterJdbcTemplate, utility);
        this.utility = utility;
    }

    /**
     * Persist and return new payment details.
     *
     * @param arPaymentDetails to insert
     * @return inserted payment details
     */
    @Override
    public ARPaymentDetails insert(ARPaymentDetails arPaymentDetails) {

        // object cannot be null to insert
        if (arPaymentDetails == null)
            throw new IllegalArgumentException("`arPaymentDetails` argument must NOT be null");

        // insert object into database
        save(utility.insertStatement(arPaymentDetails));

        // return newly created payment details
        return arPaymentDetails;
    }
}
