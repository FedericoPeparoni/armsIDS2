package ca.ids.abms.plugins.prototype.modules.transactionlog;

import ca.ids.abms.modules.plugins.enumerators.PluginSqlAction;
import ca.ids.abms.modules.common.services.PluginJdbcUtility;
import ca.ids.abms.util.jdbc.PluginSqlStatement;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;

@Component
public class PrototypeTransactionLogUtility extends PluginJdbcUtility<PrototypeTransactionLog, Integer> {

    @SuppressWarnings("FieldCanBeLocal")
    private static final String INSERT_STATEMENT = "INSERT INTO :prototypeTransactionLog (transaction_id, user_name) " +
        "VALUES (:transactionId, :userName);";

    public PrototypeTransactionLogUtility() {
        super(PrototypeTransactionLog.class);
    }

    @Override
    public PluginSqlStatement insertStatement(PrototypeTransactionLog transactionLog) {
        return new PluginSqlStatement(PluginSqlAction.INSERT, getResourceName(), INSERT_STATEMENT, getParams(transactionLog));
    }

    private MapSqlParameterSource getParams(PrototypeTransactionLog transactionLog) {
        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("prototypeTransactionLog", getResourceName());
        params.addValue("id", transactionLog.getId());
        params.addValue("transactionId", transactionLog.getTransactionId());
        params.addValue("userName", transactionLog.getUserName());

        return params;
    }
}
