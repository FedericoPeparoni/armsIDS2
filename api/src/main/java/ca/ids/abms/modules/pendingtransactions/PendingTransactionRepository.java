package ca.ids.abms.modules.pendingtransactions;

import ca.ids.abms.modules.roles.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;

public interface PendingTransactionRepository extends JpaRepository<PendingTransaction, Integer>, JpaSpecificationExecutor {

    Page<PendingTransaction> findAllByCurrentApprovalLevelId(Pageable pageable, Integer id);

    Page<PendingTransaction> findAllByCurrentApprovalLevelApprovalGroupIsIn(Collection<Role> roles, Pageable pageable );
}
