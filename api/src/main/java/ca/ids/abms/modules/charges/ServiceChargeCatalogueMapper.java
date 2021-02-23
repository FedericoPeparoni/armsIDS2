package ca.ids.abms.modules.charges;

import ca.ids.abms.modules.common.mappers.ABMSMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public abstract class ServiceChargeCatalogueMapper extends ABMSMapper {

    public abstract List<ServiceChargeCatalogueViewModel> toViewModel(Iterable<ServiceChargeCatalogue> items);

    public abstract ServiceChargeCatalogueViewModel toViewModel(ServiceChargeCatalogue item);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    public abstract ServiceChargeCatalogue toModel(ServiceChargeCatalogueViewModel dto);

    @Mapping(target = "currency", source = "currency.currencyCode")
    public abstract ServiceChargeCatalogueCsvExportModel toCsvModel(ServiceChargeCatalogue item);

    public abstract List<ServiceChargeCatalogueCsvExportModel> toCsvModel(Iterable<ServiceChargeCatalogue> items);
}
