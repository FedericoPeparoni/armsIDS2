package ca.ids.abms.modules.rejected;

import ca.ids.abms.modules.common.mappers.DateTimeMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public abstract class RejectedItemMapper extends DateTimeMapper {

    abstract List<RejectedItemViewModel> toViewModel(Iterable<RejectedItem> items);
    abstract RejectedItemViewModel toViewModel(RejectedItem item);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    abstract RejectedItem toModel(RejectedItemViewModel dto);

    public abstract RejectedItemCsvExportModel toCsvModel(RejectedItem rejectedItem);

    abstract List<RejectedItemCsvExportModel> toCsvModel(Iterable<RejectedItem> rejectedItem);

    RejectedItemType mapRejectedItemType(final String rejectedItemType) {
        return RejectedItemType.forValue(rejectedItemType);
    }

    String mapRejectedItemType(final RejectedItemType rejectedItemType) {
        return rejectedItemType != null ? rejectedItemType.toValue() : null;
    }

    RejectedItemStatus mapRejectedItemStatus(final String rejectedItemStatus) {
        return RejectedItemStatus.forValue(rejectedItemStatus);
    }

    String mapRejectedItemStatus(final RejectedItemStatus rejectedItemStatus) {
        return rejectedItemStatus != null ? rejectedItemStatus.toValue() : null;
    }
}
