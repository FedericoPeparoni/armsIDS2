package ca.ids.abms.modules.chargesadjustment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChargesAdjustmentRepository extends JpaRepository<ChargesAdjustment, Integer> {
    List<ChargesAdjustment> findAllByTransactionId(Integer id);
}
