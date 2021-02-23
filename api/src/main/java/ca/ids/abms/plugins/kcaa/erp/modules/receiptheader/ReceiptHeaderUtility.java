package ca.ids.abms.plugins.kcaa.erp.modules.receiptheader;

import ca.ids.abms.modules.plugins.enumerators.PluginSqlAction;
import ca.ids.abms.plugins.kcaa.common.services.KcaaPluginJdbcUtility;
import ca.ids.abms.plugins.kcaa.erp.modules.receiptline.ReceiptLine;
import ca.ids.abms.plugins.kcaa.erp.modules.receiptline.ReceiptLineUtility;
import ca.ids.abms.plugins.kcaa.erp.utilities.jdbc.ErpRowMapper;
import ca.ids.abms.util.jdbc.PluginSqlStatement;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;

@Component
public class ReceiptHeaderUtility extends KcaaPluginJdbcUtility<ReceiptHeader, String> {

    private static final String ACCOUNT_TYPE_PARAM_NAME = "accountType";

    private static final String APP_STATUS_PARAM_NAME = "appStatus";

    private static final String APPLIES_TO_DOC_TYPE_PARAM_NAME = "appliesToDocType";

    private static final String TIMESTAMP_FROM_PARAM_NAME = "from";

    private static final String TIMESTAMP_TO_PARAM_NAME = "to";

    private final ReceiptLineUtility receiptLineUtility;

    private String statementSelectByTimestampBetween;

    private String statementSelectLatestTimestamp;

    public ReceiptHeaderUtility(ReceiptLineUtility receiptLineUtility) {
        super(ReceiptHeader.class, new ErpRowMapper<>(ReceiptHeader.class));
        this.receiptLineUtility = receiptLineUtility;

        setStatementSelectByTimestampBetween();
        setStatementSelectLatestTimestamp();
    }

    /**
     * Plugin Sql Statement for select by timestamp between query.
     *
     * @param timestampFrom timestamp from value
     * @param timestampTo timestamp to value
     * @return Select Plugin Sql Statement
     */
    PluginSqlStatement findByTimestampBetweenStatement(byte[] timestampFrom, byte[] timestampTo) {

        // create plugin sql statement and return
        PluginSqlStatement statement = new PluginSqlStatement();

        statement.setAction(PluginSqlAction.SELECT);
        statement.setStatement(statementSelectByTimestampBetween);
        statement.setResource(getResourceName());
        statement.setParams(new MapSqlParameterSource()
            .addValue(TIMESTAMP_FROM_PARAM_NAME, timestampFrom)
            .addValue(TIMESTAMP_TO_PARAM_NAME, timestampTo)
            .addValue(APP_STATUS_PARAM_NAME, 2) // represents `Posted` in Kcaa Erp System
            .addValue(APPLIES_TO_DOC_TYPE_PARAM_NAME, 2) // represents `invoice` in Kcaa Erp System
            .addValue(ACCOUNT_TYPE_PARAM_NAME, 1)); // represents `Customer` in Kcaa Erp System

        return statement;
    }

    /**
     * Plugin SQL Statement for select latest timestamp query.
     *
     * @return Select Plugin Sql Statement
     */
    PluginSqlStatement findLatestTimestampStatement() {

        // create plugin sql statement and return
        PluginSqlStatement statement = new PluginSqlStatement();

        statement.setAction(PluginSqlAction.SELECT);
        statement.setStatement(statementSelectLatestTimestamp);
        statement.setResource(getResourceName());

        statement.setParams(new MapSqlParameterSource()
            .addValue(APP_STATUS_PARAM_NAME, 2) // represents `Posted` in Kcaa Erp System
            .addValue(APPLIES_TO_DOC_TYPE_PARAM_NAME, 2) // represents `invoice` in Kcaa Erp System
            .addValue(ACCOUNT_TYPE_PARAM_NAME, 1)); // represents `Customer` in Kcaa Erp System

        return statement;
    }

    /**
     * Set select by timestamp between statement from resource name.
     * ONLY supports MSSQL.
     */
    private void setStatementSelectByTimestampBetween() {
        statementSelectByTimestampBetween = "SELECT DISTINCT rH.* FROM [" + getResourceName() + "] AS rH"
            + " INNER JOIN [" + receiptLineUtility.getResourceName() + "] AS rL"
            + " ON rH.[" + getIdentifierName() + "] = rL.[" + ReceiptLine.NO_COLUMN_NAME + "]"
            + " WHERE rH.[" + ReceiptHeader.TIMESTAMP_COLUMN_NAME + "] > :" + TIMESTAMP_FROM_PARAM_NAME
            + " AND rH.[" + ReceiptHeader.TIMESTAMP_COLUMN_NAME + "] <= :" + TIMESTAMP_TO_PARAM_NAME
            + " AND rL.[" + ReceiptLine.APP_STATUS_COLUMN_NAME + "] = :" + APP_STATUS_PARAM_NAME
            + " AND rL.[" + ReceiptLine.APPLIES_TO_DOC_TYPE_COLUMN_NAME + "] = :" + APPLIES_TO_DOC_TYPE_PARAM_NAME
            + " AND rL.[" + ReceiptLine.ACCOUNT_TYPE_COLUMN_NAME + "] = :" + ACCOUNT_TYPE_PARAM_NAME;
    }

    /**
     * Set select latest timestamp statement from resource name.
     * ONLY supports MSSQL.
     */
    private void setStatementSelectLatestTimestamp() {
        statementSelectLatestTimestamp = "SELECT TOP (1) rH.[" + ReceiptHeader.TIMESTAMP_COLUMN_NAME + "]"
            + " FROM [" + getResourceName() + "] AS rH"
            + " INNER JOIN [" + receiptLineUtility.getResourceName() + "] AS rL"
            + " ON rH.[" + getIdentifierName() + "] = rL.[" + ReceiptLine.NO_COLUMN_NAME + "]"
            + " WHERE rL.[" + ReceiptLine.APP_STATUS_COLUMN_NAME + "] = :" + APP_STATUS_PARAM_NAME
            + " AND rL.[" + ReceiptLine.APPLIES_TO_DOC_TYPE_COLUMN_NAME + "] = :" + APPLIES_TO_DOC_TYPE_PARAM_NAME
            + " AND rL.[" + ReceiptLine.ACCOUNT_TYPE_COLUMN_NAME + "] = :" + ACCOUNT_TYPE_PARAM_NAME
            + " ORDER BY [" + ReceiptHeader.TIMESTAMP_COLUMN_NAME + "] DESC";
    }
}
