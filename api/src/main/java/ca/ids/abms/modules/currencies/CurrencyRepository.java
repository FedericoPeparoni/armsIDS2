package ca.ids.abms.modules.currencies;

import ca.ids.abms.config.db.ABMSRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CurrencyRepository extends ABMSRepository<Currency, Integer> {

    Page<Currency> findByActive(Boolean active, Pageable pageable);

    Currency findByCurrencyCode(String currencyCode);

    Currency findByExternalAccountingSystemIdentifier(String externalAccountingSystemIdentifier);

    @Query(value="select * from currencies where currency_code = (select current_value from system_configurations where item_name = 'ANSP currency')", nativeQuery = true)
    Currency getANSPCurrency();
    
    @Query (nativeQuery = true,
            value = "select count(1) > 0 from currencies where exchange_rate_target_currency = :currencyId and id <> :currencyId and active")
    boolean isUsedAsExchangeTargetByAnotherActiveCurrency (@Param("currencyId") final int currencyId);

    @Query ("SELECT c FROM Currency c WHERE c.active is TRUE")
    List<Currency> getActiveCurrency();
}
