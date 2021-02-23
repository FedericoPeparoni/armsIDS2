package ca.ids.abms.modules.selfcareportal.creditpayment;

import ca.ids.abms.modules.common.services.AbmsCrudService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@SuppressWarnings("WeakerAccess")
public class CreditPaymentTransactionService extends AbmsCrudService<CreditPaymentTransaction, Integer> {

    private final CreditPaymentTransactionRepository creditPaymentTransactionRepository;

    CreditPaymentTransactionService(final CreditPaymentTransactionRepository creditPaymentTransactionRepository) {
        super(creditPaymentTransactionRepository);
        this.creditPaymentTransactionRepository = creditPaymentTransactionRepository;
    }

    /**
     * Find all CreditPaymentTransaction records.
     */
    @Transactional(readOnly = true)
    public List<CreditPaymentTransaction> findAll() {
        return creditPaymentTransactionRepository.findAll();
    }

    /**
     * Credit payment transactions are supported if one or more configured
     * in the system.
     *
     * @return true if credit payment transactions are supported
     */
    @Transactional(readOnly = true)
    public Boolean isSupported() {
        return creditPaymentTransactionRepository.count() > 0;
    }
}
