package ca.ids.abms.plugins.kcaa.aatis.modules.invoicepermit;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class InvoicePermitMapper implements RowMapper<InvoicePermit> {

    @Override
    public InvoicePermit mapRow(ResultSet rs, int rowNum) throws SQLException {
        InvoicePermit aIP = new InvoicePermit();

        aIP.setAdhocFeePaid(rs.getString("adhoc_adhoc_fee_paid"));
        aIP.setAdhocFee(rs.getDouble("adhoc_fee"));
        aIP.setAdhocStatusId(rs.getInt("adhoc_status_id"));
        aIP.setAdhocPermitNumber(rs.getString("adhoc_permit_no"));

        Date invoicePermitDate = rs.getDate("adhoc_adhoc_fee_payment_date");
        aIP.setAdhocAdhocFeePaymentDate(
            invoicePermitDate != null
                ? invoicePermitDate.toLocalDate()
                : null
        );

        aIP.setAdhocAdhocFeePaymentBy(rs.getString("adhoc_adhoc_fee_payment_by"));
        aIP.setAdhocAdhocFeePaymentNo(rs.getString("adhoc_adhoc_fee_payment_no"));
        aIP.setAdhocAdhocFeePaymentAmount(rs.getDouble("adhoc_adhoc_fee_payment_amount"));
        aIP.setAdhocAdhocFeePaymentComments(rs.getString("adhoc_adhoc_fee_payment_comments"));
        aIP.setAdhocTotalFeePaymentAmount(rs.getDouble("adhoc_total_fee_payment_amount"));

        return aIP;
    }

}


