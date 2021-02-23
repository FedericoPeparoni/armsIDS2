package ca.ids.abms.modules.pendingtransactions;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PendingChargeAdjustmentRepository extends JpaRepository<PendingChargeAdjustment, Integer> {

    List<PendingChargeAdjustment> findAllByPendingTransactionId(Integer id);

}
