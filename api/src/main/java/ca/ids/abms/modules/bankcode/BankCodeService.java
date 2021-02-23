package ca.ids.abms.modules.bankcode;

import ca.ids.abms.modules.billingcenters.BillingCenter;
import ca.ids.abms.modules.currencies.Currency;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BankCodeService {

    private final BankCodeRepository bankCodeRepository;

    public BankCodeService(final BankCodeRepository bankCodeRepository) {
        this.bankCodeRepository = bankCodeRepository;
    }

    @Transactional(readOnly = true)
    public List<BankCode> findAllBy(final BillingCenter billingCenter) {
        return bankCodeRepository.findAllByBillingCenter(billingCenter);
    }

    @Transactional(readOnly = true)
    public BankCode findOne(final BillingCenter billingCenter, final Currency currency) {
        return bankCodeRepository.findByBillingCenterAndCurrency(billingCenter, currency);
    }
}
