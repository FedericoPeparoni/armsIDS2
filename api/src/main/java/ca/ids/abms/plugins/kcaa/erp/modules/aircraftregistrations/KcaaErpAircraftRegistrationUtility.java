package ca.ids.abms.plugins.kcaa.erp.modules.aircraftregistrations;

import ca.ids.abms.modules.plugins.enumerators.PluginSqlAction;
import ca.ids.abms.plugins.kcaa.common.services.KcaaPluginJdbcUtility;
import ca.ids.abms.util.jdbc.PluginSqlStatement;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class KcaaErpAircraftRegistrationUtility extends KcaaPluginJdbcUtility<KcaaErpAircraftRegistration, Integer> {

    private static final String ANNUAL_ANS_CHARGE_PARAM_NAME = "annualAnsCharge";

    private static final String TIMESTAMP_FROM_PARAM_NAME = "from";

    private static final String TIMESTAMP_TO_PARAM_NAME = "to";

    private String statementSelectByTimestampBetween;

    private String statementSelectLatestTimestamp;

    public KcaaErpAircraftRegistrationUtility(
        final KcaaErpAircraftRegistrationMapper kcaaErpAircraftRegistrationMapper) {
        super(KcaaErpAircraftRegistration.class, kcaaErpAircraftRegistrationMapper);

        setStatementSelectByTimestampBetween();
        setStatementSelectLatestTimestamp();
    }

    PluginSqlStatement findByTimestampBetweenStatement(byte[] timestampFrom, byte[] timestampTo) {

        // create plugin sql statement and return
        PluginSqlStatement statement = new PluginSqlStatement();

        statement.setAction(PluginSqlAction.SELECT);
        statement.setStatement(statementSelectByTimestampBetween);
        statement.setResource(getResourceName());
        statement.setParams(new MapSqlParameterSource()
            .addValue(TIMESTAMP_FROM_PARAM_NAME, timestampFrom)
            .addValue(TIMESTAMP_TO_PARAM_NAME, timestampTo)
            .addValue(ANNUAL_ANS_CHARGE_PARAM_NAME, 1));

        return statement;
    }

    PluginSqlStatement findLatestTimestampStatement() {

        // create plugin sql statement and return
        PluginSqlStatement statement = new PluginSqlStatement();

        statement.setAction(PluginSqlAction.SELECT);
        statement.setStatement(statementSelectLatestTimestamp);
        statement.setResource(getResourceName());
        statement.setParams(new MapSqlParameterSource()
            .addValue(ANNUAL_ANS_CHARGE_PARAM_NAME, 1));

        return statement;
    }

    private void setStatementSelectByTimestampBetween() {
        statementSelectByTimestampBetween = "SELECT * FROM [" + getResourceName() + "]"
            + " WHERE [" + KcaaErpAircraftRegistration.TIMESTAMP_COLUMN_NAME + "] > :" + TIMESTAMP_FROM_PARAM_NAME
            + " AND [" + KcaaErpAircraftRegistration.TIMESTAMP_COLUMN_NAME + "] <= :" + TIMESTAMP_TO_PARAM_NAME
            + " AND [" + KcaaErpAircraftRegistration.ANNUAL_ANS_CHARGE_COLUMN_NAME + "] = :" + ANNUAL_ANS_CHARGE_PARAM_NAME
            + " AND [" + KcaaErpAircraftRegistration.REG_MARK_COLUMN_NAME + "] != ''";
    }

    private void setStatementSelectLatestTimestamp() {
        statementSelectLatestTimestamp = "SELECT TOP (1) [" + KcaaErpAircraftRegistration.TIMESTAMP_COLUMN_NAME + "]"
            + " FROM [" + getResourceName() + "]"
            + " WHERE [" + KcaaErpAircraftRegistration.ANNUAL_ANS_CHARGE_COLUMN_NAME + "] = :" + ANNUAL_ANS_CHARGE_PARAM_NAME
            + " AND [" + KcaaErpAircraftRegistration.REG_MARK_COLUMN_NAME + "] != ''"
            + " ORDER BY [" + KcaaErpAircraftRegistration.TIMESTAMP_COLUMN_NAME + "] DESC";
    }
}
