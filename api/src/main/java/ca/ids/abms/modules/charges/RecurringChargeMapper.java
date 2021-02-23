package ca.ids.abms.modules.charges;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import ca.ids.abms.modules.accounts.AccountMapper;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { ServiceChargeCatalogueMapper.class, AccountMapper.class } )
public interface RecurringChargeMapper {
    
    public List <RecurringChargeViewModel> toViewModel (Iterable <RecurringCharge> entityList);
    public RecurringChargeViewModel toViewModel (RecurringCharge entity);
    
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    RecurringCharge toModel (final RecurringChargeViewModel dto);
    List <RecurringCharge> toModel (final List <RecurringChargeViewModel> dtoList);

    @Mapping(target = "serviceChargeCatalogue", source = "serviceChargeCatalogue.description")
    @Mapping(target = "account", source = "account.name")
    RecurringChargeCsvExportModel toCsvModel(RecurringCharge item);

    List<RecurringChargeCsvExportModel> toCsvModel(Iterable<RecurringCharge> items);
}
