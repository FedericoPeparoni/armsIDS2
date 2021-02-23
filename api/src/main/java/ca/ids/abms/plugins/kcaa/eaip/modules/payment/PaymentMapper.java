package ca.ids.abms.plugins.kcaa.eaip.modules.payment;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class PaymentMapper implements RowMapper<Payment> {

    @Override
    public Payment mapRow(ResultSet rs, int rowNum) throws SQLException {
        Payment payment = new Payment();

        payment.setPaymentId(rs.getInt("payment_id"));
        payment.setPaymentCountryId(rs.getInt("payment_country_id"));
        payment.setPaymentStatusId(rs.getInt("payment_status_id"));
        payment.setPaymentReqId(rs.getInt("payment_req_id"));
        payment.setPaymentArId(rs.getInt("payment_ar_id"));
        payment.setPaymentManinfoId(rs.getInt("payment_maninfo_id"));
        payment.setPaymentInvoiceId(rs.getInt("payment_invoice_id"));
        payment.setPaymentManinfoNames(rs.getString("payment_maninfo_names"));
        payment.setPaymentDocumentNo(rs.getString("payment_document_no"));
        payment.setPaymentDesc(rs.getString("payment_desc"));
        payment.setPaymentTotalAmount(rs.getDouble("payment_total_amount"));
        payment.setPaymentCurrency(rs.getString("payment_currency"));
        payment.setPaymentCreatedBy(rs.getString("payment_created_by"));
        payment.setPaymentCreatedComments(rs.getString("payment_created_comments"));

        return payment;
    }

}


