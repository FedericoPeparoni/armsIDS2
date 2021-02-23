package ca.ids.abms.modules.selfcareportal.creditpayment;

import ca.ids.abms.config.db.ABMSRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditPaymentTransactionRepository extends ABMSRepository<CreditPaymentTransaction, Integer> {
}
