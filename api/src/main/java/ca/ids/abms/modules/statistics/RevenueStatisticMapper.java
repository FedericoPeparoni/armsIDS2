package ca.ids.abms.modules.statistics;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface RevenueStatisticMapper {

    List<RevenueStatisticViewModel> toViewModel(Iterable<RevenueStatistic> items);

    RevenueStatisticViewModel toViewModel(RevenueStatistic item);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    RevenueStatistic toModel(RevenueStatisticViewModel dto);
}
