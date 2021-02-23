package ca.ids.abms.modules.chargesadjustment;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface ChargesAdjustmentMapper {

    List<ChargesAdjustmentViewModel> toViewModel(Iterable<ChargesAdjustment> items);

    @Mapping(target = "transactionId", source = "transaction.id")
    @Mapping(target = "billingLedgerId", source = "billingLedger.id")
    ChargesAdjustmentViewModel toViewModel(ChargesAdjustment item);

    @Mapping(target = "transaction", ignore = true)
    @Mapping(target = "billingLedger", ignore = true)
    ChargesAdjustment toModel(ChargesAdjustmentViewModel dto);
}
