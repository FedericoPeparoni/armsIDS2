package ca.ids.abms.modules.currencies;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)

public abstract class CurrencyExchangeRateMapper {
    public abstract CurrencyExchangeRateViewModel toViewModel(CurrencyExchangeRate amhsAccount);

    public abstract CurrencyExchangeRate toModel(CurrencyExchangeRateViewModel amhsAccount);
}
