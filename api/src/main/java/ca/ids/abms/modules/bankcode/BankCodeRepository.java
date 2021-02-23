package ca.ids.abms.modules.bankcode;

import ca.ids.abms.modules.billingcenters.BillingCenter;
import ca.ids.abms.modules.currencies.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankCodeRepository extends JpaRepository<BankCode, Integer> {

    BankCode findByBillingCenterAndCurrency(final BillingCenter billingCenter, final Currency currency);

    List<BankCode> findAllByBillingCenter(final BillingCenter billingCenter);
}
