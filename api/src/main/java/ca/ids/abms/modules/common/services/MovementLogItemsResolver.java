package ca.ids.abms.modules.common.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.accounts.AccountService;
import ca.ids.abms.modules.common.mappers.IcaoCodeResolver;
import ca.ids.abms.modules.common.mappers.LogTranslations;

@LogTranslations
@Component
public class MovementLogItemsResolver {

    @Autowired
    private AccountService accountService;

    @IcaoCodeResolver
    public String resolveIcaoCodeByOperatorName(final String operatorName) {
        final Account account = accountService.findAccountByNameOrAliasOrOperator(operatorName);
        if (account == null) {
            throw new CustomParametrizedException(
                "Cannot retrieve the ICAO code due to unknown operator" + " " + operatorName,
                "operatorIdentifier");
        }
        return account.getIcaoCode();
    }

    void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }
}
