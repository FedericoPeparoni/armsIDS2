package ca.ids.abms.plugins.kcaa.eaip.modules.requisition;

import ca.ids.abms.modules.common.services.AbstractPluginServiceProvider;
import ca.ids.abms.modules.transactions.TransactionPayment;
import ca.ids.abms.plugins.PluginKey;
import ca.ids.abms.plugins.kcaa.eaip.config.KcaaEaipDatabaseConfig;
import ca.ids.abms.plugins.kcaa.eaip.modules.requisition.utility.RequisitionUtility;
import ca.ids.abms.plugins.kcaa.eaip.modules.requisitionnumbers.KcaaEaipRequisitionNumber;
import ca.ids.abms.util.jdbc.PluginSqlStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@SuppressWarnings("unused")
public class RequisitionService extends AbstractPluginServiceProvider {

    private static final Logger LOG = LoggerFactory.getLogger(RequisitionService.class);

    private final RequisitionRepository requisitionRepository;
    private final RequisitionUtility requisitionUtility;

    public RequisitionService(
        final RequisitionRepository requisitionRepository,
        final RequisitionUtility requisitionUtility
    ){
        super(PluginKey.KCAA_EAIP);
        this.requisitionRepository = requisitionRepository;
        this.requisitionUtility = requisitionUtility;
    }

    @Transactional(readOnly = true, value = KcaaEaipDatabaseConfig.TRANSACTION_MANAGER)
    public List<Requisition> findAllRequisitions() {
        return requisitionRepository.findAll();
    }

    @Transactional(readOnly = true, value = KcaaEaipDatabaseConfig.TRANSACTION_MANAGER)
    public List<Requisition> findAllPaid() {
        return requisitionRepository.findAllPaid();
    }

    @Transactional(readOnly = true, value = KcaaEaipDatabaseConfig.TRANSACTION_MANAGER)
    public List<Requisition> findAllUnpaid() {
        return requisitionRepository.findAllUnpaid();
    }

    @Transactional(readOnly = true, value = KcaaEaipDatabaseConfig.TRANSACTION_MANAGER)
    @SuppressWarnings("WeakerAccess")
    public Requisition findByReqNumber(String reqNumber) {
        return requisitionRepository.findByAdhocPermitNumber(reqNumber);
    }

    @Transactional(value = KcaaEaipDatabaseConfig.TRANSACTION_MANAGER)
    public void updateAsPaid(
        final TransactionPayment transactionPayment, final KcaaEaipRequisitionNumber requisitionNumber
    ) {
        LOG.debug("Update requisition number '{}' from transaction payment : {}",
            requisitionNumber.getReqNumber(), transactionPayment);
        requisitionRepository.updateAsPaid(
            requisitionNumber.getReqNumber(),
            transactionPayment.getTransaction().getReceiptNumber(),
            transactionPayment.getTransaction().getTransactionDateTime(),
            transactionPayment.getTransaction().getCreatedBy(),
            transactionPayment.getTransaction().getDescription(),
            (- transactionPayment.getTransaction().getPaymentAmount()));
    }

    public PluginSqlStatement updateAsPaidStatement(
        final TransactionPayment transactionPayment, final KcaaEaipRequisitionNumber requisitionNumber
    ) {
        return requisitionUtility.updateAsPaid(
            requisitionNumber.getReqNumber(),
            transactionPayment.getTransaction().getReceiptNumber(),
            transactionPayment.getTransaction().getTransactionDateTime(),
            transactionPayment.getTransaction().getCreatedBy(),
            transactionPayment.getTransaction().getDescription(),
            (- transactionPayment.getTransaction().getPaymentAmount()));
    }
}
