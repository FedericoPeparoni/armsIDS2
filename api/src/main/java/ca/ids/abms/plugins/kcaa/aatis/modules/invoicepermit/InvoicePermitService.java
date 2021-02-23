package ca.ids.abms.plugins.kcaa.aatis.modules.invoicepermit;

import ca.ids.abms.modules.common.services.AbstractPluginServiceProvider;
import ca.ids.abms.modules.transactions.TransactionPayment;
import ca.ids.abms.plugins.PluginKey;
import ca.ids.abms.plugins.kcaa.aatis.config.KcaaAatisDatabaseConfig;
import ca.ids.abms.plugins.kcaa.aatis.modules.invoicepermit.utility.InvoicePermitUtility;
import ca.ids.abms.plugins.kcaa.aatis.modules.permitnumber.KcaaAatisPermitNumber;
import ca.ids.abms.util.jdbc.PluginSqlStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@SuppressWarnings("unused")
public class InvoicePermitService extends AbstractPluginServiceProvider {

    private static final Logger LOG = LoggerFactory.getLogger(InvoicePermitService.class);

    private final InvoicePermitRepository invoicePermitRepository;
    private final InvoicePermitUtility invoicePermitUtility;

    public InvoicePermitService(
        final InvoicePermitRepository invoicePermitRepository,
        final InvoicePermitUtility invoicePermitUtility
    ){
        super(PluginKey.KCAA_AATIS);
        this.invoicePermitRepository = invoicePermitRepository;
        this.invoicePermitUtility = invoicePermitUtility;
    }

    @Transactional(readOnly = true, value = KcaaAatisDatabaseConfig.TRANSACTION_MANAGER)
    public List<InvoicePermit> findAllPermitInvoices() {
        return invoicePermitRepository.findAll();
    }

    @Transactional(readOnly = true, value = KcaaAatisDatabaseConfig.TRANSACTION_MANAGER)
    public List<InvoicePermit> findAllPaid() {
        return invoicePermitRepository.findAllPaid();
    }

    @Transactional(readOnly = true, value = KcaaAatisDatabaseConfig.TRANSACTION_MANAGER)
    public List<InvoicePermit> findAllUnpaid() {
        return invoicePermitRepository.findAllUnpaid();
    }

    @Transactional(readOnly = true, value = KcaaAatisDatabaseConfig.TRANSACTION_MANAGER)
    @SuppressWarnings("WeakerAccess")
    public InvoicePermit findByAdhocPermitNumber(String adhocPermitNumber) {
        return invoicePermitRepository.findByAdhocPermitNumber(adhocPermitNumber);
    }

    @Transactional(value = KcaaAatisDatabaseConfig.TRANSACTION_MANAGER)
    public void updateAsPaid(
        final TransactionPayment transactionPayment, final KcaaAatisPermitNumber permitNumber
    ) {
        LOG.debug("Update invoice permit '{}' for amount '{}' from transaction payment : {}",
            permitNumber.getInvoicePermitNumber(), permitNumber.getAdhocTotalFeePaymentAmount(), transactionPayment);
        invoicePermitRepository.updateAsPaid(
            permitNumber.getInvoicePermitNumber(),
            transactionPayment.getTransaction().getCreatedBy(),
            transactionPayment.getTransaction().getReceiptNumber(),
            (- permitNumber.getAdhocTotalFeePaymentAmount()),
            (- permitNumber.getAdhocTotalFeePaymentAmount()),
            transactionPayment.getTransaction().getDescription());
    }

    public PluginSqlStatement updateAsPaidStatement(
        final TransactionPayment transactionPayment, final KcaaAatisPermitNumber permitNumber
    ) {
        return invoicePermitUtility.updateAsPaid(
            permitNumber.getInvoicePermitNumber(),
            transactionPayment.getTransaction().getCreatedBy(),
            transactionPayment.getTransaction().getReceiptNumber(),
            (- permitNumber.getAdhocTotalFeePaymentAmount()),
            (- permitNumber.getAdhocTotalFeePaymentAmount()),
            transactionPayment.getTransaction().getDescription());
    }
}
