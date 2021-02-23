package ca.ids.abms.plugins.kcaa.aatis.modules.invoicepermit.utility;

import ca.ids.abms.modules.plugins.enumerators.PluginSqlAction;
import ca.ids.abms.plugins.kcaa.aatis.modules.invoicepermit.InvoicePermit;
import ca.ids.abms.plugins.kcaa.aatis.modules.invoicepermit.InvoicePermitMapper;
import ca.ids.abms.plugins.kcaa.common.services.KcaaPluginJdbcUtility;
import ca.ids.abms.util.jdbc.PluginSqlStatement;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public class InvoicePermitUtility extends KcaaPluginJdbcUtility<InvoicePermit, Integer> {

    public InvoicePermitUtility(InvoicePermitMapper invoicePermitMapper) {
        super(InvoicePermit.class, invoicePermitMapper);
    }

    private static final String QUERY_FOR_ONE_PERMIT_INVOICE = (
        "SELECT * FROM adhoc_clearances " +
            "WHERE adhoc_adhoc_fee_paid IS NULL " +
            "AND adhoc_status_id = 8 " +
            "AND adhoc_permit_no = :valueSuppliedByOperator"
    );

    private static final String QUERY_FOR_ALL_PERMIT_INVOICES = (
        "SELECT * FROM adhoc_clearances AND adhoc_status_id = 8"
    );

    private static final String QUERY_FOR_ALL_UNPAID_PERMIT_INVOICES = (
        "SELECT * FROM adhoc_clearances " +
            "WHERE adhoc_adhoc_fee_paid IS NULL " +
            "AND adhoc_status_id = 8"
    );

    private static final String QUERY_FOR_ALL_PAID_PERMIT_INVOICES = (
        "SELECT * FROM adhoc_clearances " +
            "WHERE adhoc_adhoc_fee_paid = 'TRUE' " +
            "AND adhoc_status_id = 8"
    );

    private static final String UPDATE_PERMIT_INVOICE = (
        "UPDATE adhoc_clearances " +
            "SET adhoc_adhoc_fee_paid = 'TRUE', " +
            "adhoc_adhoc_fee_payment_date = :paymentDate, " +
            "adhoc_adhoc_fee_payment_by = :billingOperatorName, " +
            "adhoc_adhoc_fee_payment_no = :receiptNumber, " +
            "adhoc_adhoc_fee_payment_amount = :amountPaidExcludingAdminFee, " +
            "adhoc_total_fee_payment_amount = :totalAmountPaid, " +
            "adhoc_adhoc_fee_payment_comments = :comments " +
            "WHERE adhoc_status_id = 8 " +
            "AND adhoc_permit_no = :valueSuppliedByOperator" );

    public PluginSqlStatement findAll() {
        return getStatement(QUERY_FOR_ALL_PERMIT_INVOICES, PluginSqlAction.SELECT);
    }

    public PluginSqlStatement findAllPaid() {
        return getStatement(QUERY_FOR_ALL_PAID_PERMIT_INVOICES, PluginSqlAction.SELECT);
    }

    public PluginSqlStatement findAllUnpaid() {
        return getStatement(QUERY_FOR_ALL_UNPAID_PERMIT_INVOICES, PluginSqlAction.SELECT);
    }

    public PluginSqlStatement findByAdhocPermitNumber(String adhocPermitNumber) {
        final MapSqlParameterSource namedParameters = new MapSqlParameterSource();

        namedParameters.addValue("valueSuppliedByOperator", adhocPermitNumber);

        return getStatement(QUERY_FOR_ONE_PERMIT_INVOICE, PluginSqlAction.SELECT, namedParameters);
    }

    public PluginSqlStatement updateAsPaid(
        String adhocPermitNumber,
        String billingOperatorName,
        String receiptNumber,
        Double amountPaidExcludingAdminFee,
        Double totalAmountPaid,
        String comments
    ) {
        final MapSqlParameterSource namedParameters = new MapSqlParameterSource();

        namedParameters.addValue("valueSuppliedByOperator", adhocPermitNumber);
        namedParameters.addValue("billingOperatorName", billingOperatorName);
        namedParameters.addValue("receiptNumber", receiptNumber);
        namedParameters.addValue("amountPaidExcludingAdminFee", (- amountPaidExcludingAdminFee));
        namedParameters.addValue("totalAmountPaid", (- totalAmountPaid));
        namedParameters.addValue("comments", comments);
        namedParameters.addValue("paymentDate", new Date());

        return getStatement(UPDATE_PERMIT_INVOICE, PluginSqlAction.UPDATE, namedParameters);
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

