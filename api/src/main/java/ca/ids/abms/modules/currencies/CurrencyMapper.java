package ca.ids.abms.modules.currencies;

import ca.ids.abms.util.csv.annotations.CsvProperty;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Mapper
public interface CurrencyMapper {

    List<CurrencyViewModel> toViewModel(Iterable<Currency> items);

    CurrencyViewModel toViewModel(Currency item);

    Currency toModel(CurrencyViewModel dto);

    @Mapping(target = "countryName", source = "countryCode.countryName")
    @Mapping(target = "exchangeRate", ignore = true)
    CurrencyCsvExportModel toCsvModel(Currency item);

    List<CurrencyCsvExportModel> toCsvModel(Iterable<Currency> items);

    @AfterMapping
    default void resolveCsvExportModel(final Currency source,
                                       @MappingTarget final CurrencyCsvExportModel target) {

        List<ExchangeRate> list = new ArrayList<>();
        List<CurrencyExchangeRate> currencyExchangeRate = source.getCurrencyExchangeRate();
        if (currencyExchangeRate.isEmpty()) {
            list.add(new ExchangeRate());
        } else {
            currencyExchangeRate.sort(Comparator.comparing(CurrencyExchangeRate::getExchangeRateValidFromDate).reversed());
            source.getCurrencyExchangeRate().forEach(e -> {
                ExchangeRate rate = new ExchangeRate();
                rate.rate = e.getExchangeRate();
                rate.from = e.getExchangeRateValidFromDate();
                rate.to = e.getExchangeRateValidToDate();
                list.add(rate);
            });
        }
        target.setExchangeRate(list);
    }

    class ExchangeRate {
        @CsvProperty(value = "Exchange Rate", precision = 5, inverse = true)
        private Double rate;

        @CsvProperty(date = true)
        private LocalDateTime from;

        @CsvProperty(date = true)
        private LocalDateTime to;
    }
}
