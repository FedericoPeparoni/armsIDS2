package ca.ids.abms.plugins.prototype.config;

import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.plugins.Plugin;
import ca.ids.abms.modules.plugins.PluginRepository;
import ca.ids.abms.modules.transactions.Transaction;
import ca.ids.abms.modules.transactions.TransactionType;
import ca.ids.abms.plugins.PluginKey;
import ca.ids.abms.plugins.prototype.modules.transactions.PrototypeTransactionServiceProvider;
import ca.ids.spring.cache.managers.CacheUpdateManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import ca.ids.abms.modules.translation.Translation;
import java.time.LocalDateTime;
import java.util.Random;

// TODO: remove, cache update manager only here for prototype plugin exception mocking

@Configuration(value = "CacheUpdateConfig.Prototype")
public class CacheUpdateConfig {

    final private Logger LOG = LoggerFactory.getLogger(CacheUpdateConfig.class);

    public CacheUpdateConfig(CacheUpdateManager cacheUpdateManager,
                             PrototypeTransactionServiceProvider prototypeTransactionServiceProvider,
                             PluginRepository pluginRepository) {
        LOG.info("Registering class '" + prototypeTransactionServiceProvider.getClass().getName() +
            "' method 'save(new Transaction())' with CacheUpdateManager.");

        // only register methods if plugin prototype enabled
        Plugin plugin = pluginRepository.findOneByKey(PluginKey.PROTOTYPE);
        if (plugin != null && plugin.getEnabled()) {

            // used as random id generator
            Random random = new Random();

            // mock account object
            Account account = new Account();
            account.setId(random.nextInt(900) + 100);

            // mock currency object
            Currency currency = new Currency();
            currency.setId(random.nextInt(90) + 10);

            // mock transaction type object
            TransactionType transactionType = new TransactionType();
            transactionType.setId(random.nextInt(9) + 1);

            // mock random amount of 2 decimals
            double amount = ((double) Math.round((100 + (10000 - 100) * random.nextDouble()) * 100)) / 100;

            // mock transaction object
            Transaction transaction = new Transaction();
            transaction.setAccount(account);
            transaction.setTransactionDateTime(LocalDateTime.now());
            transaction.setDescription(Translation.getLangByToken("Mock prototype transaction that is generated at 1 minute intervals."));
            transaction.setAmount(amount);
            transaction.setCurrency(currency);
            transaction.setTransactionType(transactionType);

            // register save for prototype retry testing purposes only (to mimic a user using the system)
            // wouldn't normally register a retry annotated method
            cacheUpdateManager.registerMethod(() -> prototypeTransactionServiceProvider.save(transaction));
        }
    }
}
