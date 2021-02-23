package ca.ids.abms.modules.aerodromes.cluster;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface RepositioningAerodromeClusterMapper {

    default Collection<String> mapAerodromeIdentifiers(
            Collection<RepositioningAssignedAerodromeCluster> aerodromeIdentifiers) {
        return aerodromeIdentifiers.stream().map(RepositioningAssignedAerodromeCluster::getAerodromeIdentifier)
                .collect(Collectors.toList());
    }

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "aerodromeIdentifiers", ignore = true)
    RepositioningAerodromeCluster toModel(RepositioningAerodromeClusterViewModel dto);

    List<RepositioningAerodromeClusterViewModel> toViewModel(
            Iterable<RepositioningAerodromeCluster> repositioningAerodromeClusters);

    RepositioningAerodromeClusterViewModel toViewModel(RepositioningAerodromeCluster repositioningAerodromeCluster);

    RepositioningAerodromeClusterCsvExportModel toCsvModel(RepositioningAerodromeCluster item);

    List<RepositioningAerodromeClusterCsvExportModel> toCsvModel(Iterable<RepositioningAerodromeCluster> items);
}
