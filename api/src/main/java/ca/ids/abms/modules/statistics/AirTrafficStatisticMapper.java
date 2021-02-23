package ca.ids.abms.modules.statistics;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface AirTrafficStatisticMapper {

    List<AirTrafficStatisticViewModel> toViewModel(Iterable<AirTrafficStatistic> items);

    AirTrafficStatisticViewModel toViewModel(AirTrafficStatistic item);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    AirTrafficStatistic toModel(AirTrafficStatisticViewModel dto);

}
