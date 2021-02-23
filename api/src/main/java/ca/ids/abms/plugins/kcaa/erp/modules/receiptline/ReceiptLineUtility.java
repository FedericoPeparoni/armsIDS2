package ca.ids.abms.plugins.kcaa.erp.modules.receiptline;

import ca.ids.abms.modules.plugins.enumerators.PluginSqlAction;
import ca.ids.abms.plugins.kcaa.common.services.KcaaPluginJdbcUtility;
import ca.ids.abms.plugins.kcaa.erp.utilities.jdbc.ErpRowMapper;
import ca.ids.abms.util.jdbc.PluginSqlStatement;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;

@Component
public class ReceiptLineUtility extends KcaaPluginJdbcUtility<ReceiptLine, String> {

    private static final String NO_PARAM_NAME = "no";

    private String statementSelectByReceiptHeaderNo;

    public ReceiptLineUtility() {
        super(ReceiptLine.class, new ErpRowMapper<>(ReceiptLine.class));
        setStatementSelectByReceiptHeaderNo();
    }

    /**
     * Plugin Sql Statement for select by receipt header no.
     *
     * @param receiptHeaderNo to find
     * @return Select Plugin Sql Statement
     */
    PluginSqlStatement findByReceiptHeaderNo(String receiptHeaderNo) {

        // create plugin sql statement and return
        PluginSqlStatement statement = new PluginSqlStatement();

        statement.setAction(PluginSqlAction.SELECT);
        statement.setStatement(statementSelectByReceiptHeaderNo);
        statement.setResource(getResourceName());
        statement.setParams(new MapSqlParameterSource(NO_PARAM_NAME, receiptHeaderNo));

        return statement;
    }

    /**
     * Set select by receipt header no statement from resource name.
     * ONLY supports MSSQL.
     */
    private void setStatementSelectByReceiptHeaderNo() {
        statementSelectByReceiptHeaderNo = "SELECT * FROM [" + getResourceName()
            + "] WHERE [" + ReceiptLine.NO_COLUMN_NAME + "] = :" + NO_PARAM_NAME;
    }
}
