package ca.ids.abms.plugins.prototype.modules.transactions;

import ca.ids.abms.modules.plugins.enumerators.PluginSqlAction;
import ca.ids.abms.modules.common.services.PluginJdbcUtility;
import ca.ids.abms.util.jdbc.PluginSqlStatement;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;

@Component
public class PrototypeTransactionUtility extends PluginJdbcUtility<PrototypeTransaction, Integer> {

    @SuppressWarnings("FieldCanBeLocal")
    private static final String INSERT_STATEMENT = "INSERT INTO :prototypeTransaction (" +
        "amount, description, date, type_id, account_id, currency_id) " +
        "VALUES (:amount, :description, :date, :typeId, :accountId, :currencyId);";

    public PrototypeTransactionUtility() {
        super(PrototypeTransaction.class);
    }

    public PluginSqlStatement insertStatement(PrototypeTransaction transaction) {
        return new PluginSqlStatement(PluginSqlAction.INSERT, getResourceName(), INSERT_STATEMENT, getParams(transaction));
    }

    private MapSqlParameterSource getParams(PrototypeTransaction transaction) {
        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("prototypeTransaction", getResourceName());
        params.addValue("id", transaction.getId());
        params.addValue("amount", transaction.getAmount());
        params.addValue("description", transaction.getDescription());
        params.addValue("date", transaction.getDate());
        params.addValue("typeId", transaction.getTypeId());
        params.addValue("accountId", transaction.getAccountId());
        params.addValue("currencyId", transaction.getCurrencyId());

        return params;
    }
}
