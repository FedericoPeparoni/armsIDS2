package ca.ids.abms.modules.currencies;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Sort;

public interface CurrencyExchangeRateRepository extends JpaRepository<CurrencyExchangeRate, Integer> {

    @Query("select cer from CurrencyExchangeRate cer where ( cer.currency.id = ?1) order by cer.exchangeRateValidFromDate")
    List<CurrencyExchangeRate> getAllSortedByDate(Integer id);

    Page<CurrencyExchangeRate> getAllCurrencyExchangeRateByCurrencyId(Integer id, Pageable pageable);
    
    List<CurrencyExchangeRate> getAllCurrencyExchangeRateByCurrencyId(Integer id, Sort sort);

    /**
     * Get applicable currency exchange rate from source currency to target currency by currency ids on a particular date.
     */
    @Query("select cer from CurrencyExchangeRate cer where cer.currency.id = ?1 and cer.targetCurrency.id = ?2 " +
        "and cer.exchangeRateValidFromDate <= ?3 and cer.exchangeRateValidToDate >= ?3")
    List<CurrencyExchangeRate> getApplicableCurrencyExchangeRate(Integer sourceCurrencyId, Integer targetCurrencyId, LocalDateTime date);

    /**
     * Get applicable currency exchange rate to USD by currency id on a particular date.
     */
    @Query("select cer from CurrencyExchangeRate cer where cer.currency.id = ?1 and cer.targetCurrency.currencyCode = 'USD' " +
        "and cer.exchangeRateValidFromDate <= ?2 and cer.exchangeRateValidToDate >= ?2")
    List<CurrencyExchangeRate> getApplicableCurrencyExchangeRateToUsd(Integer currencyId, LocalDateTime date);

    @Query("select cer from CurrencyExchangeRate cer " +
            "where ( ( ?1 >= cer.exchangeRateValidFromDate and ?1 <= cer.exchangeRateValidToDate ) or " +
            "( ?2 >= cer.exchangeRateValidFromDate and ?2 <= cer.exchangeRateValidToDate ) or " +
            "( ?1 < cer.exchangeRateValidFromDate and ?2 > cer.exchangeRateValidFromDate ))")
    List<CurrencyExchangeRate> getAllCurrencyExchangeRateDoNotOverlap(LocalDateTime fromDate, LocalDateTime toDate);

    @Query("select cer from CurrencyExchangeRate cer " +
            "where ( cer.currency.id = ?1 and (( ?2 >= cer.exchangeRateValidFromDate and ?2 <= cer.exchangeRateValidToDate ) or " +
            "( ?3 >= cer.exchangeRateValidFromDate and ?3 <= cer.exchangeRateValidToDate ) or " +
            "( ?2 < cer.exchangeRateValidFromDate and ?3 > cer.exchangeRateValidFromDate )))")
    List<CurrencyExchangeRate> getAllCurrencyExchangeRateDoNotOverlap(Integer currencyId, LocalDateTime fromDate, LocalDateTime toDate);

    @Query("select cer from CurrencyExchangeRate cer " +
            "where ( cer.currency.id = ?1 and cer.exchangeRateValidFromDate <= ?2 and cer.exchangeRateValidToDate >= ?3 )")
    CurrencyExchangeRate getCurrencyExchangeRateByCurrencyIdAndPeriod(Integer currencyId, LocalDateTime fromDate, LocalDateTime toDate);

    /**
     * Get applicable currency exchange rate to USD by currency code for current date.
     */
    @Query("select cer.exchangeRate from CurrencyExchangeRate cer where cer.currency.currencyCode = :currencyCode " +
        "and cer.targetCurrency.currencyCode = 'USD' " +
        "and NOW() between cer.exchangeRateValidFromDate and cer.exchangeRateValidToDate")
    Double getCurrentExchangeRatesByCurrencyCode(@Param("currencyCode") String currencyCode);
}
