package ca.ids.abms.modules.accounts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountExternalChargeCategoryRepository extends JpaRepository<AccountExternalChargeCategory, Integer> {

    List<AccountExternalChargeCategory> findByAccountId(
        final Integer accountId);

    List<AccountExternalChargeCategory> findByAccountIdAndExternalChargeCategoryId(
        final Integer accountId,
        final Integer externalChargeCategoryId);

    AccountExternalChargeCategory findOneByExternalSystemIdentifier(
        final String externalSystemIdentifier);
}
