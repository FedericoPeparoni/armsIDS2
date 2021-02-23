package ca.ids.abms.modules.common.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.accounts.AccountService;
import ca.ids.abms.modules.aircraft.AircraftType;
import ca.ids.abms.modules.aircraft.AircraftTypeService;
import ca.ids.abms.modules.common.mappers.AccountResolver;
import ca.ids.abms.modules.common.mappers.AircraftTypeResolver;
import ca.ids.abms.modules.common.mappers.CountryResolver;
import ca.ids.abms.modules.common.mappers.LogTranslations;
import ca.ids.abms.modules.countries.Country;
import ca.ids.abms.modules.countries.CountryService;

@LogTranslations
@Component
public class EntityResolver {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AircraftTypeService aircraftTypeService;

    @Autowired
    private CountryService countryService;

    @AccountResolver
    public Account resolveAccountByAccountName(final String accountName) {
        final Account account = accountService.findAccountByNameOrAliasOrOperator(accountName);
        if (account == null) {
            throw new CustomParametrizedException(
                "Cannot retrieve the account due to unknown account name" + " " + accountName, accountName,
                    EntityResolver.class, "accountName");
        }
        return account;
    }

    @AircraftTypeResolver
    public AircraftType resolveAircraftTypeByAircraftType(final String aircraftType) {
        final AircraftType at = aircraftTypeService.findByAircraftType(aircraftType);
        if (at == null) {
            throw new CustomParametrizedException(
                "Cannot retrieve the aircraft type due to unknown aircraft type" + " " + aircraftType, aircraftType,
                    EntityResolver.class, "aircraftType");
        }
        return at;
    }

    @CountryResolver
    public Country resolveCountryByPrefix(final String prefix) {
        final Country c = countryService.findCountryByPrefix(prefix);
        if (c == null) {
            throw new CustomParametrizedException("Cannot retrieve the country due to unknown prefix" + " " + prefix, prefix,
                    EntityResolver.class, "country");
        }
        return c;
    }

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    public void setAircraftTypeService(AircraftTypeService aAircraftTypeService) {
        aircraftTypeService = aAircraftTypeService;
    }

    public void setCountryService(CountryService aCountryService) {
        countryService = aCountryService;
    }
}
