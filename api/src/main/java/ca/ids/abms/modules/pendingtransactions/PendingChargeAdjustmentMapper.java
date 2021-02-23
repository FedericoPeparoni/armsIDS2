package ca.ids.abms.modules.pendingtransactions;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface PendingChargeAdjustmentMapper {

    PendingChargeAdjustmentCsvExportModel toCsvModel(PendingChargeAdjustment pendingChargeAdjustment);

    List<PendingChargeAdjustmentCsvExportModel> toCsvModel(Iterable<PendingChargeAdjustment> pendingChargeAdjustments);
}
