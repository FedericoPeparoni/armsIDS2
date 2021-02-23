package ca.ids.abms.plugins.prototype.modules.transactionlog;

import ca.ids.abms.modules.common.services.AbstractPluginJdbcRepository;
import ca.ids.abms.util.jdbc.PluginKeyHolder;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PrototypeTransactionLogRepository extends AbstractPluginJdbcRepository<PrototypeTransactionLog, Integer> {

    private final PrototypeTransactionLogUtility prototypeTransactionLogUtility;

    public PrototypeTransactionLogRepository(NamedParameterJdbcTemplate primaryNamedParameterJdbcTemplate,
                                             PrototypeTransactionLogUtility prototypeTransactionLogUtility) {
        super(primaryNamedParameterJdbcTemplate, prototypeTransactionLogUtility);
        this.prototypeTransactionLogUtility = prototypeTransactionLogUtility;
    }

    public PrototypeTransactionLog insert(PrototypeTransactionLog transactionLog) {

        // insert transaction log
        PluginKeyHolder key = super.save(prototypeTransactionLogUtility.insertStatement(transactionLog));

        // find and return newly created transaction log
        return findOne(key.getKey().intValue());
    }
}
