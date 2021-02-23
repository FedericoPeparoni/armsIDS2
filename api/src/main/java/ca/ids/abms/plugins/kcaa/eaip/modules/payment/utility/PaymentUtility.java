package ca.ids.abms.plugins.kcaa.eaip.modules.payment.utility;

import ca.ids.abms.modules.plugins.enumerators.PluginSqlAction;
import ca.ids.abms.plugins.kcaa.common.services.KcaaPluginJdbcUtility;
import ca.ids.abms.plugins.kcaa.eaip.modules.payment.Payment;
import ca.ids.abms.plugins.kcaa.eaip.modules.payment.PaymentMapper;
import ca.ids.abms.util.jdbc.PluginSqlStatement;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("FieldCanBeLocal")
public class PaymentUtility extends KcaaPluginJdbcUtility<Payment, Integer> {

    public PaymentUtility(PaymentMapper paymentMapper) {
        super(Payment.class, paymentMapper);
    }

    private static final String QUERY_FOR_ONE_PAYMENT = (
        "SELECT * FROM payments " +
            "WHERE payment_req_id = :reqId"
    );

    private static final String QUERY_FOR_ALL_PAYMENTS = (
        "SELECT * FROM payments"
    );

    private static final String INSERT_PAYMENT = (
        "INSERT INTO payments " +
            "(" +
            "payment_status_id, payment_country_id, payment_req_id, payment_ar_id, payment_maninfo_id, payment_invoice_id, payment_maninfo_names, " +
            "payment_document_no, payment_desc, payment_total_amount, payment_currency, payment_created_by, payment_created_comments" +
            ")" +
            "VALUES " +
            "(" +
            "7, :paymentCountryId, :paymentReqId, :paymentArId, :paymentManinfoId, :paymentInvoiceId, :paymentManinfoNames, " +
            ":paymentDocumentNo, :paymentDesc, :paymentTotalAmount, :paymentCurrency, :paymentCreatedBy, :paymentCreatedComments" +
            ")"
    );

    public PluginSqlStatement findAll() {
        return getStatement(QUERY_FOR_ALL_PAYMENTS, PluginSqlAction.SELECT);
    }

    public PluginSqlStatement findByReqId(String reqId) {
        final MapSqlParameterSource namedParameters = new MapSqlParameterSource();

        namedParameters.addValue("reqId", reqId);

        return getStatement(QUERY_FOR_ONE_PAYMENT, PluginSqlAction.SELECT, namedParameters);
    }

    public PluginSqlStatement create(
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
        final MapSqlParameterSource namedParameters = new MapSqlParameterSource();

        namedParameters.addValue("paymentCountryId", paymentCountryId);
        namedParameters.addValue("paymentReqId", paymentReqId);
        namedParameters.addValue("paymentArId", paymentArId);
        namedParameters.addValue("paymentManinfoId", paymentManinfoId);
        namedParameters.addValue("paymentInvoiceId", paymentInvoiceId);
        namedParameters.addValue("paymentManinfoNames", paymentManinfoNames);
        namedParameters.addValue("paymentDocumentNo", paymentDocumentNo);
        namedParameters.addValue("paymentDesc", paymentDesc);
        namedParameters.addValue("paymentTotalAmount", paymentTotalAmount);
        namedParameters.addValue("paymentCurrency", paymentCurrency);
        namedParameters.addValue("paymentCreatedBy", paymentCreatedBy);
        namedParameters.addValue("paymentCreatedComments", paymentCreatedComments);

        return getStatement(INSERT_PAYMENT, PluginSqlAction.INSERT, namedParameters);
    }

    private PluginSqlStatement getStatement(String statement, PluginSqlAction action, MapSqlParameterSource params) {
        PluginSqlStatement result = new PluginSqlStatement();

        result.setAction(action);
        result.setParams(params);
        result.setResource(getResourceName());
        result.setStatement(statement);

        return result;
    }

    private PluginSqlStatement getStatement(String statement, PluginSqlAction action) {
        PluginSqlStatement result = new PluginSqlStatement();

        result.setAction(action);
        result.setResource(getResourceName());
        result.setStatement(statement);

        return result;
    }

}

