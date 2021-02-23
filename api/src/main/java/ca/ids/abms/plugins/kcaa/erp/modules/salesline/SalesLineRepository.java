package ca.ids.abms.plugins.kcaa.erp.modules.salesline;

import ca.ids.abms.modules.common.services.AbstractPluginJdbcRepository;
import ca.ids.abms.plugins.kcaa.erp.config.KcaaErpDatabaseConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SalesLineRepository extends AbstractPluginJdbcRepository<SalesLine, String> {

    private final SalesLineUtility salesLineUtility;

    public SalesLineRepository(@Qualifier(KcaaErpDatabaseConfig.NAMED_PARAMETER_JDBC_TEMPLATE)
                               NamedParameterJdbcTemplate kcaaErpNamedParameterJdbcTemplate,
                               SalesLineUtility salesLineUtility) {
        super(kcaaErpNamedParameterJdbcTemplate, salesLineUtility);
        this.salesLineUtility = salesLineUtility;
    }

    /**
     * Select query expected to return one result.
     *
     * @param documentNo sales header no
     * @param lineNo sales line no
     * @return `T` type
     */
    public SalesLine findOne(String documentNo, Integer lineNo) {
        return selectOne(salesLineUtility.findOneStatement(documentNo, lineNo));
    }

    /**
     * Persist and return new sales line.
     *
     * @param salesLine to insert
     * @return inserted sales line
     */
    @Override
    public SalesLine insert(SalesLine salesLine) {

        // object cannot be null to insert
        if (salesLine == null)
            throw new IllegalArgumentException("`salesLine` argument must NOT be null");

        // insert object into database
        save(salesLineUtility.insertStatement(salesLine));

        // return newly created sales line
        return salesLine;
    }

    /**
     * Persist and return updated sales line.
     *
     * @param salesLine to update
     * @return updated sales line
     */
    @Override
    public SalesLine update(SalesLine salesLine) {

        // object cannot be null to update
        if (salesLine == null)
            throw new IllegalArgumentException("`salesLine` argument must NOT be null");

        // find existing entry by sales line's identifiers
        SalesLine existingSalesLine = findOne(salesLine.getDocumentNo(), salesLine.getLineNo());

        // existing entry cannot be null
        if (existingSalesLine == null)
            throw new IllegalStateException("Cannot update " + salesLineUtility.getResourceName() +
                " as existing entry could not be found with document no of " + salesLine.getDocumentNo() +
                " and line no of" + salesLine.getLineNo());

        // merge only non default values into exiting sales line
        salesLineUtility.mergeOnly(salesLine, existingSalesLine);

        // update existing sales line in database
        save(salesLineUtility.updateStatement(existingSalesLine));

        // find and return newly updated sales line by identifiers
        return findOne(salesLine.getDocumentNo(), salesLine.getLineNo());
    }
}
