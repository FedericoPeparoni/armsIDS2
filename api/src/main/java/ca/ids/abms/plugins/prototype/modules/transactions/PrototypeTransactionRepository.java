package ca.ids.abms.plugins.prototype.modules.transactions;

import ca.ids.abms.modules.common.services.AbstractPluginJdbcRepository;
import ca.ids.abms.util.jdbc.PluginKeyHolder;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PrototypeTransactionRepository extends AbstractPluginJdbcRepository<PrototypeTransaction, Integer> {

    private final PrototypeTransactionUtility prototypeTransactionUtility;

    public PrototypeTransactionRepository(NamedParameterJdbcTemplate primaryNamedParameterJdbcTemplate,
                                          PrototypeTransactionUtility prototypeTransactionUtility) {
        super(primaryNamedParameterJdbcTemplate, prototypeTransactionUtility);
        this.prototypeTransactionUtility = prototypeTransactionUtility;
    }

    public PrototypeTransaction insert(PrototypeTransaction transaction) {

        // insert transaction
        PluginKeyHolder key = super.save(prototypeTransactionUtility.insertStatement(transaction));

        // find and return newly created transaction
        return findOne(key.getKey().intValue());

    }
}
