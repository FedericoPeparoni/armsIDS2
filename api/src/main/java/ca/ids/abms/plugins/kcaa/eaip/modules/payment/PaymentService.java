package ca.ids.abms.plugins.kcaa.eaip.modules.payment;

import ca.ids.abms.modules.common.services.AbstractPluginServiceProvider;
import ca.ids.abms.modules.transactions.TransactionPayment;
import ca.ids.abms.plugins.PluginKey;
import ca.ids.abms.plugins.kcaa.eaip.config.KcaaEaipDatabaseConfig;
import ca.ids.abms.plugins.kcaa.eaip.modules.payment.utility.PaymentUtility;
import ca.ids.abms.plugins.kcaa.eaip.modules.requisitionnumbers.KcaaEaipRequisitionNumber;
import ca.ids.abms.util.jdbc.PluginSqlStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@SuppressWarnings("unused")
public class PaymentService extends AbstractPluginServiceProvider {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final PaymentUtility paymentUtility;

    public PaymentService(
        final PaymentRepository paymentRepository,
        final PaymentUtility paymentUtility
    ){
        super(PluginKey.KCAA_EAIP);
        this.paymentRepository = paymentRepository;
        this.paymentUtility = paymentUtility;
    }

    @Transactional(readOnly = true, value = KcaaEaipDatabaseConfig.TRANSACTION_MANAGER)
    public List<Payment> findAllPayments() {
        return paymentRepository.findAll();
    }

    @Transactional(readOnly = true, value = KcaaEaipDatabaseConfig.TRANSACTION_MANAGER)
    @SuppressWarnings("WeakerAccess")
    public Payment findByReqId(String reqId) {
        return paymentRepository.findByReqId(reqId);
    }

    @Transactional(value = KcaaEaipDatabaseConfig.TRANSACTION_MANAGER)
    public void create(
        final TransactionPayment transactionPayment, final KcaaEaipRequisitionNumber requisitionNumber
    ) {
        LOG.debug("Insert requisition payment from transaction payment and requisition number : {}, {}",
            transactionPayment, requisitionNumber);
        paymentRepository.create(
            requisitionNumber.getReqCountryId(),
            requisitionNumber.getReqId(),
            requisitionNumber.getReqArId(),
            requisitionNumber.getReqManinfoId(),
            transactionPayment.getTransaction().getId(),
            requisitionNumber.getCreatedBy(),
            transactionPayment.getTransaction().getReceiptNumber(),
            transactionPayment.getTransaction().getDescription(),
            requisitionNumber.getReqTotalAmountConverted(),
            transactionPayment.getTransaction().getCurrency().getCurrencyCode(),
            transactionPayment.getTransaction().getCreatedBy(),
            transactionPayment.getTransaction().getDescription());
    }

    public PluginSqlStatement createStatement(
        final TransactionPayment transactionPayment, final KcaaEaipRequisitionNumber requisitionNumber
    ) {
        return paymentUtility.create(
            requisitionNumber.getReqCountryId(),
            requisitionNumber.getReqId(),
            requisitionNumber.getReqArId(),
            requisitionNumber.getReqManinfoId(),
            transactionPayment.getTransaction().getId(),
            requisitionNumber.getCreatedBy(),
            transactionPayment.getTransaction().getReceiptNumber(),
            transactionPayment.getTransaction().getDescription(),
            requisitionNumber.getReqTotalAmountConverted(),
            transactionPayment.getTransaction().getCurrency().getCurrencyCode(),
            transactionPayment.getTransaction().getCreatedBy(),
            transactionPayment.getTransaction().getDescription());
    }
}
