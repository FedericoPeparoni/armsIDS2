package ca.ids.abms.modules.currencies;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.modules.util.models.Calculation;
import ca.ids.oxr.client.dto.LatestExchangeRates;
import ca.ids.oxr.client.service.OpenExchangeRatesService;

public class CurrencyExchangeRateServiceTest {
    private CurrencyRepository currencyRepository;
    private CurrencyExchangeRateService currencyExchangeRateService;
    private CurrencyExchangeRateRepository currencyExchangeRateRepository;
    private OpenExchangeRatesService openExchangeRatesService;

    @Before
    public void setup() {
        currencyRepository = mock(CurrencyRepository.class);
        openExchangeRatesService = mock(OpenExchangeRatesService.class);
        currencyExchangeRateRepository = mock(CurrencyExchangeRateRepository.class);
        currencyExchangeRateService = new CurrencyExchangeRateService(currencyRepository, currencyExchangeRateRepository, openExchangeRatesService);
    }

    @Test
    public void createSuccessfully() throws Exception {
        CurrencyExchangeRate exRate = getUSDExchangeRate();

        when(currencyExchangeRateRepository.getAllCurrencyExchangeRateDoNotOverlap(exRate.getCurrency().getId(), startOfFirstDay, endOfFirstDay))
            .thenReturn(null);

        when(currencyExchangeRateRepository.save(any(CurrencyExchangeRate.class)))
            .thenReturn(exRate);

        CurrencyExchangeRate result = currencyExchangeRateService.create(exRate);
        assertThat(result.getExchangeRate()).isEqualTo(exRate.getExchangeRate());
    }

    @Test(expected=CustomParametrizedException.class)
    public void createOverlappingAnExistingRate() throws Exception {
        CurrencyExchangeRate exRate = getUSDExchangeRate();

        when(currencyExchangeRateRepository.getAllCurrencyExchangeRateDoNotOverlap(exRate.getCurrency().getId(), startOfFirstDay, endOfFirstDay))
            .thenReturn(Collections.singletonList(new CurrencyExchangeRate()));

        when(currencyExchangeRateRepository.save(any(CurrencyExchangeRate.class)))
            .thenReturn(exRate);

        CurrencyExchangeRate result = currencyExchangeRateService.create(exRate);
        assertThat(result.getExchangeRate()).isEqualTo(exRate.getExchangeRate());
    }

    @Test
    public void updateSuccessfully() throws Exception {
        CurrencyExchangeRate existingItem = getUSDExchangeRate();
        CurrencyExchangeRate updatedItem = getUSDExchangeRate();
        updatedItem.setExchangeRate(2.0);

        when(currencyExchangeRateRepository.getOne(any())).thenReturn(existingItem);

        when(currencyExchangeRateRepository.getAllCurrencyExchangeRateDoNotOverlap(existingItem.getCurrency().getId(), startOfFirstDay, endOfFirstDay))
            .thenReturn(Collections.singletonList(existingItem));

        when(currencyExchangeRateRepository.save(any(CurrencyExchangeRate.class)))
            .thenReturn(updatedItem);

        CurrencyExchangeRate result = currencyExchangeRateService.update(1, updatedItem);
        assertThat(result.getExchangeRate()).isEqualTo(2.0);
    }

    @Test(expected=CustomParametrizedException.class)
    public void updateOverlapping() throws Exception {
        CurrencyExchangeRate existingItem = getUSDExchangeRate();
        CurrencyExchangeRate updatedItem = getUSDExchangeRate2();
        CurrencyExchangeRate overlappedItem = getUSDExchangeRateToOverlap();

        when(currencyExchangeRateRepository.getOne(any())).thenReturn(existingItem);

        when(currencyExchangeRateRepository.getAllCurrencyExchangeRateDoNotOverlap(existingItem.getCurrency().getId(),
                startOfFirstDay, endOfSecondDay))
                .thenReturn(Collections.singletonList(overlappedItem));

        CurrencyExchangeRate result = currencyExchangeRateService.update(1, updatedItem);
    }

    @Test
    public void delete() throws Exception {
        currencyExchangeRateService.delete(1);
        verify(currencyExchangeRateRepository).delete(any(Integer.class));
    }

    @Test
    public void getAll() throws Exception {
        List<CurrencyExchangeRate> rates = Collections.singletonList(getUSDExchangeRate());

        when(currencyExchangeRateRepository.getAllCurrencyExchangeRateByCurrencyId(any(), any(Pageable.class)))
        .thenReturn(new PageImpl<>(rates));

        Page<CurrencyExchangeRate> results = currencyExchangeRateService.getAll(1, mock(Pageable.class));

        assertThat(results.getContent().size()).isEqualTo(1);
        assertThat(results.getContent().get(0).getId()).isEqualTo(rates.get(0).getId());
    }

    @Test
    public void getApplicableRate() throws Exception {
        CurrencyExchangeRate item = getUSDExchangeRate();
        List<CurrencyExchangeRate> items = new ArrayList<>(1);
        items.add(item);
        when(currencyExchangeRateRepository.getApplicableCurrencyExchangeRateToUsd(any(), anyObject()))
                .thenReturn(items);
        Currency currency = new Currency();
        currency.setId(1);
        currency.setCurrencyCode("USD");
        CurrencyExchangeRate result = currencyExchangeRateService.getApplicableRateToUsd(currency, aMomentInTheMiddle);
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getExchangeRate()).isEqualTo(1.0);
        assertThat(result.getExchangeRateValidFromDate()).isEqualTo(startOfFirstDay);
        assertThat(result.getExchangeRateValidToDate()).isEqualTo(endOfFirstDay);
    }

    @Test
    public void updateAllSuccessfully() throws Exception {
        LatestExchangeRates ratesFromAPI = getLatestExchangeRates();

        when(openExchangeRatesService.getLatestExchangeRates(anyObject()))
                .thenReturn(ratesFromAPI);

        when(currencyRepository.findAll()).thenReturn(Collections.singletonList(getUSDCurrency()));

        when(currencyExchangeRateRepository.getCurrencyExchangeRateByCurrencyIdAndPeriod(anyInt(),
                anyObject(), anyObject()))
                .thenReturn(getCurrentUSDExchangeRate());

        currencyExchangeRateService.updateAll();

        verify(currencyExchangeRateRepository).save(any(CurrencyExchangeRate.class));
    }

    @Test
    public void updateAllSuccessfullyWithCreation() throws Exception {
        LatestExchangeRates ratesFromAPI = getLatestExchangeRates();

        when(openExchangeRatesService.getLatestExchangeRates(anyObject()))
                .thenReturn(ratesFromAPI);

        when(currencyRepository.findAll()).thenReturn(Collections.singletonList(getUSDCurrency()));

        when(currencyExchangeRateRepository.getAllCurrencyExchangeRateDoNotOverlap(anyObject(), anyObject()))
                .thenReturn(null);

        when(currencyExchangeRateRepository.getCurrencyExchangeRateByCurrencyIdAndPeriod(anyInt(),
                anyObject(), anyObject()))
                .thenReturn(null);

        currencyExchangeRateService.updateAll();

        verify(currencyExchangeRateRepository).save(any(CurrencyExchangeRate.class));
    }

    @Test
    public void updateAllDiscarding() throws Exception {
        LatestExchangeRates ratesFromAPI = getLatestExchangeRates();

        when(openExchangeRatesService.getLatestExchangeRates(anyObject()))
                .thenReturn(ratesFromAPI);

        when(currencyRepository.findAll()).thenReturn(Collections.singletonList(getUSDCurrency()));

        when(currencyExchangeRateRepository.getAllCurrencyExchangeRateDoNotOverlap(anyObject(), anyObject()))
                .thenReturn(Collections.singletonList(getUSDExchangeRateToOverlap()));

        when(currencyExchangeRateRepository.getCurrencyExchangeRateByCurrencyIdAndPeriod(anyInt(),
                anyObject(), anyObject()))
                .thenReturn(null);

        currencyExchangeRateService.updateAll();

        verify(currencyExchangeRateRepository, times(0)).save(any(CurrencyExchangeRate.class));
    }

    @Test
    public void getExchangeAmount() {

        // assert that system is multiplying exchange rate when no inverse value supplied
        double result = currencyExchangeRateService.getExchangeAmount(0.5, 100.25, 2);
        assertThat(result).isEqualTo(Calculation.truncate(100.25 * 0.5, 2));

        // assert that system is multiplying exchange rate when inverse false
        result = currencyExchangeRateService.getExchangeAmount(0.5, 100.25, 2, false);
        assertThat(result).isEqualTo(Calculation.truncate(100.25 * 0.5, 2));

        // assert that system is dividing exchange rate when inverse true
        result = currencyExchangeRateService.getExchangeAmount(0.5, 100.25, 2, true);
        assertThat(result).isEqualTo(Calculation.truncate(100.25 / 0.5, 2));
    }

    static LatestExchangeRates getLatestExchangeRates() {
        Map<String, Double> rates = new HashMap<>();
        rates.put("CAD", 1.1);
        LatestExchangeRates ratesFromAPI = new LatestExchangeRates();
        ratesFromAPI.setBase("USD");
        ratesFromAPI.setTimestamp(LocalDateTime.now());
        ratesFromAPI.setRates(rates);
        return ratesFromAPI;
    }

    static Currency getUSDCurrency () {
        final Currency currency = new Currency();
        currency.setId(1);
        currency.setActive(true);
        currency.setCurrencyCode("CAD");
        currency.setDecimalPlaces(2);
        currency.setSymbol("$");
        currency.setAllowUpdatedFromWeb(true);
        return currency;
    }

    final static LocalDateTime startOfFirstDay = LocalDateTime.of(2017, 01, 01, 0, 0);
    final static LocalDateTime endOfFirstDay = LocalDateTime.of(2017, 01, 01, 23, 59);
    final static LocalDateTime startOfSecondDay = LocalDateTime.of(2017, 01, 02, 0, 0);
    final static LocalDateTime endOfSecondDay = LocalDateTime.of(2017, 01, 02,  23, 59);
    final static LocalDateTime startOfThirdDay = LocalDateTime.of(2017, 01, 03, 0, 0);
    final static LocalDateTime endOfThirdDay = LocalDateTime.of(2017, 01, 03,  23, 59);
    final static LocalDateTime aMomentInTheMiddle = LocalDateTime.of(2017, 01, 01, 12, 00);


    static CurrencyExchangeRate getUSDExchangeRate() {
        final CurrencyExchangeRate currencyExchangeRate = new CurrencyExchangeRate();
        currencyExchangeRate.setId(1);
        currencyExchangeRate.setExchangeRate(1.0);
        currencyExchangeRate.setExchangeRateValidFromDate(startOfFirstDay);
        currencyExchangeRate.setExchangeRateValidToDate(endOfFirstDay);
        currencyExchangeRate.setCurrency(getUSDCurrency());
        return currencyExchangeRate;
    }

    static CurrencyExchangeRate getUSDExchangeRate2() {
        final CurrencyExchangeRate currencyExchangeRate = new CurrencyExchangeRate();
        currencyExchangeRate.setId(2);
        currencyExchangeRate.setExchangeRate(1.0);
        currencyExchangeRate.setExchangeRateValidFromDate(startOfFirstDay);
        currencyExchangeRate.setExchangeRateValidToDate(endOfSecondDay);
        currencyExchangeRate.setCurrency(getUSDCurrency());
        return currencyExchangeRate;
    }

    static CurrencyExchangeRate getUSDExchangeRateToOverlap() {
        final CurrencyExchangeRate currencyExchangeRate = new CurrencyExchangeRate();
        currencyExchangeRate.setId(3);
        currencyExchangeRate.setExchangeRate(1.0);
        currencyExchangeRate.setExchangeRateValidFromDate(startOfFirstDay);
        currencyExchangeRate.setExchangeRateValidToDate(endOfSecondDay);
        currencyExchangeRate.setCurrency(getUSDCurrency());
        return currencyExchangeRate;
    }

    static CurrencyExchangeRate getCurrentUSDExchangeRate() {
        final CurrencyExchangeRate currencyExchangeRate = new CurrencyExchangeRate();
        currencyExchangeRate.setId(3);
        currencyExchangeRate.setExchangeRate(1.0);
        final LocalDate now = LocalDate.now();
        final LocalDateTime startAt = now.atStartOfDay();
        final LocalDateTime endAt = now.atTime(LocalTime.MAX);
        currencyExchangeRate.setExchangeRateValidFromDate(startAt);
        currencyExchangeRate.setExchangeRateValidToDate(endAt);
        currencyExchangeRate.setCurrency(getUSDCurrency());
        return currencyExchangeRate;
    }
}
