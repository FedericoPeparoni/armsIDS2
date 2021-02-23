package ca.ids.abms.plugins.kcaa.erp.modules.salesheader;

import ca.ids.abms.modules.common.services.AbstractPluginJdbcRepository;
import ca.ids.abms.plugins.kcaa.erp.config.KcaaErpDatabaseConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SalesHeaderRepository extends AbstractPluginJdbcRepository<SalesHeader, String> {

    private final SalesHeaderUtility salesHeaderUtility;

    public SalesHeaderRepository(@Qualifier(KcaaErpDatabaseConfig.NAMED_PARAMETER_JDBC_TEMPLATE)
                                 NamedParameterJdbcTemplate kcaaErpNamedParameterJdbcTemplate,
                                 SalesHeaderUtility salesHeaderUtility) {
        super(kcaaErpNamedParameterJdbcTemplate, salesHeaderUtility);
        this.salesHeaderUtility = salesHeaderUtility;
    }

    /**
     * Persist and return new sales header.
     *
     * @param salesHeader to insert
     * @return inserted sales header
     */
    @Override
    public SalesHeader insert(SalesHeader salesHeader) {

        // object cannot be null to insert
        if (salesHeader == null)
            throw new IllegalArgumentException("`salesHeader` argument must NOT be null");

        // insert object into database
        save(salesHeaderUtility.insertStatement(salesHeader));

        // return newly created object
        return salesHeader;
    }

    /**
     * Persist and return updated sales header.
     *
     * @param salesHeader to update
     * @return updated sales header
     */
    @Override
    public SalesHeader update(SalesHeader salesHeader) {

        // object cannot be null to update
        if (salesHeader == null)
            throw new IllegalArgumentException("`salesHeader` argument must NOT be null");

        // find existing entry by sales header's identifier
        String identifier = salesHeaderUtility.identifierValue(salesHeader);
        SalesHeader existingSalesHeader = findOne(identifier);

        // existing entry cannot be null
        if (existingSalesHeader == null)
            throw new IllegalStateException("Cannot update " + salesHeaderUtility.getResourceName() +
                " as existing entry could not be found with identifier value of " + identifier);

        // merge only non default values into exiting sales header
        salesHeaderUtility.mergeOnly(salesHeader, existingSalesHeader);

        // update existing sales header in database
        save(salesHeaderUtility.updateStatement(existingSalesHeader));

        // find and return newly updated sales header by identifier
        return findOne(identifier);
    }
}
