package ca.ids.abms.amhs;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ca.ids.abms.modules.amhsconfiguration.AmhsAccount;
import ca.ids.abms.modules.amhsconfiguration.AmhsAccountRepository;
import ca.ids.abms.modules.amhsconfiguration.AmhsConfiguration;
import ca.ids.abms.modules.amhsconfiguration.AmhsConfigurationRepository;

@Component
class AmhsAgentDatabaseHelper {
    
    public AmhsAgentDatabaseHelper (
            final AmhsConfigurationRepository amhsConfigurationRepository,
            final AmhsAccountRepository amhsAccountRepository) {
        this.amhsConfigurationRepository = amhsConfigurationRepository;
        this.amhsAccountRepository = amhsAccountRepository;
    }
    
    @Transactional (readOnly = true)
    public AmhsConfiguration getActiveConfiguration() {
        return amhsConfigurationRepository.getActive();
    }
    
    @Transactional (readOnly = true)
    public List <AmhsAccount> getAllAccounts() {
        return amhsAccountRepository.findAll();
    }
    
    private final AmhsConfigurationRepository amhsConfigurationRepository;
    private final AmhsAccountRepository amhsAccountRepository;

}
