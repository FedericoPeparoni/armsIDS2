package ca.ids.abms.modules.currencies;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public class CurrencyServiceTest {
    private CurrencyRepository currencyRepository;
    private CurrencyService currencyService;

    @Test
    public void createCurrency() throws Exception {
        Currency currency = new Currency();
        currency.setCurrencyName("name");

        when(currencyRepository.save(any(Currency.class)))
        .thenReturn(currency);

        Currency result = currencyService.create(currency);
        assertThat(result.getCurrencyName()).isEqualTo(currency.getCurrencyName());
    }

    @Test
    public void deleteCurrency() throws Exception {
        currencyService.delete(1);
        verify(currencyRepository).delete(any(Integer.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getAllCurrencys() throws Exception {
        List<Currency> currencies = Collections.singletonList(new Currency());

        when(currencyRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(new PageImpl<>(currencies));

        Page<Currency> results = currencyService.findAll(false, null, mock(Pageable.class));

        assertThat(results.getTotalElements()).isEqualTo(currencies.size());
    }

    @Test
    public void getCurrencyById() throws Exception {
        Currency currency = new Currency();
        currency.setId(1);

        when(currencyRepository.getOne(any()))
        .thenReturn(currency);

        Currency result = currencyService.getOne(1);
        assertThat(result).isEqualTo(currency);
    }

    @Before
    public void setup() {
        currencyRepository = mock(CurrencyRepository.class);
        currencyService = new CurrencyService(currencyRepository);
    }

    @Test
    public void getANSPCurrencyAndUSD() {

        Currency currencyUSD = new Currency();
        currencyUSD.setCurrencyCode("USD");

        Currency currencyCAD = new Currency();
        currencyCAD.setCurrencyCode("CAD");

        List<Currency> currencyList = new ArrayList<>(2);
        currencyList.add(currencyUSD);
        currencyList.add(currencyCAD);

        when(currencyRepository.findByCurrencyCode("USD"))
            .thenReturn(currencyUSD);
        when(currencyRepository.getANSPCurrency())
            .thenReturn(currencyCAD);

        Page<Currency> list = currencyService.getANSPCurrencyAndUSD();

        assertThat(list.getTotalElements()).isEqualTo(2);
        assertThat(list.getContent()).isEqualTo(currencyList);
    }

    @Test
    public void updateCurrency() throws Exception {
        Currency existingCurrency = new Currency();
        existingCurrency.setCurrencyName("name");

        Currency currency = new Currency();
        currency.setCurrencyName("new name");

        when(currencyRepository.getOne(any()))
        .thenReturn(existingCurrency);

        when(currencyRepository.save(any(Currency.class)))
        .thenReturn(existingCurrency);

        Currency result = currencyService.update(1, currency);

        assertThat(result.getCurrencyName()).isEqualTo("new name");
    }
}
