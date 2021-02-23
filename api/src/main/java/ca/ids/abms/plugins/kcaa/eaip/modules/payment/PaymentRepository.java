package ca.ids.abms.plugins.kcaa.eaip.modules.payment;

import ca.ids.abms.modules.common.services.AbstractPluginJdbcRepository;
import ca.ids.abms.plugins.kcaa.eaip.config.KcaaEaipDatabaseConfig;
import ca.ids.abms.plugins.kcaa.eaip.modules.payment.utility.PaymentUtility;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PaymentRepository extends AbstractPluginJdbcRepository<Payment, Integer> {

    private final PaymentUtility paymentUtility;

    public PaymentRepository(
        @Qualifier(KcaaEaipDatabaseConfig.NAMED_PARAMETER_JDBC_TEMPLATE)
        final NamedParameterJdbcTemplate namedParameterJdbcTemplate,
        final PaymentUtility paymentUtility
    ) {
        super(namedParameterJdbcTemplate, paymentUtility);
        this.paymentUtility = paymentUtility;
    }

    @Override
    public List<Payment> findAll() {
        return select(paymentUtility.findAll());
    }

    Payment findByReqId(String reqId) {
        try {
            return selectOne(paymentUtility.findByReqId(reqId));
        } catch(EmptyResultDataAccessException e) {
            return null;
        }
    }

    @SuppressWarnings("squid:S00107")
    public void create(
        Integer paymentCountryId,
        Integer paymentReqId,
        Integer paymentArId,
        Integer paymentManinfoId,
        Integer paymentInvoiceId,
        String paymentManinfoNames,
        String paymentDocumentNo,
        String paymentDesc,
        Double paymentTotalAmount,
        String paymentCurrency,
        String paymentCreatedBy,
        String paymentCreatedComments
    ) {
        super.save(
            paymentUtility.create(
                paymentCountryId,
                paymentReqId,
                paymentArId,
                paymentManinfoId,
                paymentInvoiceId,
                paymentManinfoNames,
                paymentDocumentNo,
                paymentDesc,
                paymentTotalAmount,
                paymentCurrency,
                paymentCreatedBy,
                paymentCreatedComments
            )
        );
    }
}
