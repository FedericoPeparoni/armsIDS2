package ca.ids.abms.modules.interestrates;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface InterestRateMapper {

    List<InterestRateViewModel> toViewModel(Iterable<InterestRate> interestRates);

    InterestRateViewModel toViewModel(InterestRate interestRate);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    InterestRate toModel(InterestRateViewModel dto);

    InterestRateCsvExportModel toCsvModel(InterestRate item);

    List<InterestRateCsvExportModel> toCsvModel(Iterable<InterestRate> items);
}
