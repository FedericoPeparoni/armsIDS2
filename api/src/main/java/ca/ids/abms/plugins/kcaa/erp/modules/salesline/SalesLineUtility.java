package ca.ids.abms.plugins.kcaa.erp.modules.salesline;

import ca.ids.abms.modules.plugins.enumerators.PluginSqlAction;
import ca.ids.abms.plugins.kcaa.common.services.KcaaPluginJdbcUtility;
import ca.ids.abms.plugins.kcaa.erp.utilities.jdbc.ErpRowMapper;
import ca.ids.abms.util.jdbc.PluginSqlStatement;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;

@Component
public class SalesLineUtility extends KcaaPluginJdbcUtility<SalesLine, String> {

    private static final String DOCUMENT_NO_PARAM_NAME = "documentNo";
    private static final String LINE_NO_PARAM_NAME = "lineNo";

    private String statementSelectOne;

    public SalesLineUtility() {
        super(SalesLine.class, new ErpRowMapper<>(SalesLine.class));
        setStatementSelectOne();
    }

    /**
     * Plugin Sql Statement for select one query by document no and line no.
     *
     * @param documentNo of sales line to fine
     * @param lineNo of sales line to fine
     * @return Select Plugin Sql Statement
     */
    PluginSqlStatement findOneStatement(String documentNo, Integer lineNo) {

        // create plugin sql statement and return
        PluginSqlStatement statement = new PluginSqlStatement();

        statement.setAction(PluginSqlAction.SELECT);
        statement.setStatement(statementSelectOne);
        statement.setResource(getResourceName());
        statement.setParams(new MapSqlParameterSource()
            .addValue(DOCUMENT_NO_PARAM_NAME, documentNo)
            .addValue(LINE_NO_PARAM_NAME, lineNo));

        return statement;
    }

    /**
     * Set select one statement from resource name.
     * ONLY supports MSSQL.
     */
    private void setStatementSelectOne() {
        statementSelectOne = "SELECT * FROM [" + getResourceName()
            + "] WHERE [" + SalesLine.DOCUMENT_NO_COLUMN_NAME + "] = :" + DOCUMENT_NO_PARAM_NAME
            + " AND [" + SalesLine.LINE_NO_COLUMN_NAME + "] = :" + LINE_NO_PARAM_NAME;
    }
}
