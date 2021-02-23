package ca.ids.abms.modules.mtow;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface AverageMtowFactorMapper {

    List<AverageMtowFactorViewModel> toViewModel(Iterable<AverageMtowFactor> items);

    AverageMtowFactorViewModel toViewModel(AverageMtowFactor item);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    AverageMtowFactor toModel(AverageMtowFactorViewModel dto);

    AverageMtowFactorCsvExportModel toCsvModel(AverageMtowFactor item);

    List<AverageMtowFactorCsvExportModel> toCsvModel(Iterable<AverageMtowFactor> items);

}
