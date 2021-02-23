package ca.ids.abms.plugins.kcaa.eaip.modules.requisition.utility;

import ca.ids.abms.modules.plugins.enumerators.PluginSqlAction;
import ca.ids.abms.plugins.kcaa.common.services.KcaaPluginJdbcUtility;
import ca.ids.abms.plugins.kcaa.eaip.modules.requisition.Requisition;
import ca.ids.abms.plugins.kcaa.eaip.modules.requisition.RequisitionMapper;
import ca.ids.abms.util.jdbc.PluginSqlStatement;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
@SuppressWarnings("FieldCanBeLocal")
public class RequisitionUtility extends KcaaPluginJdbcUtility<Requisition, Integer> {

    public RequisitionUtility(RequisitionMapper requisitionMapper) {
        super(Requisition.class, requisitionMapper);
    }

    private static final String QUERY_FOR_ONE_REQUISITION = (
        "SELECT * FROM requisitions " +
            "WHERE req_status_id = 6 " +
            "AND req_no = :valueSuppliedByOperator"
    );

    private static final String QUERY_FOR_ALL_REQUISITIONS = (
        "SELECT * FROM requisitions"
    );

    private static final String QUERY_FOR_ALL_UNPAID_REQUISITIONS = (
        "SELECT * FROM requisitions " +
            "WHERE req_status_id = 6"
    );

    private static final String QUERY_FOR_ALL_PAID_REQUISITIONS = (
        "SELECT * FROM requisitions " +
            "WHERE req_status_id = 7"
    );

    private static final String UPDATE_REQUISITION = (
        "Update requisitions " +
            "SET req_status_id = 7, " +
            "req_invoice_no = :reqInvoiceNumber, " +
            "req_paid = 'TRUE', " +
            "req_paid_date = :reqPaidDate, " +
            "req_paid_by = :reqPaidBy, " +
            "req_paid_by_comments = :reqPaidByComments, " +
            "req_paid_amount = :reqPaidAmount " +
            "WHERE req_no = :valueSuppliedByOperator"
    );

    public PluginSqlStatement findAll() {
        return getStatement(QUERY_FOR_ALL_REQUISITIONS, PluginSqlAction.SELECT);
    }

    public PluginSqlStatement findAllPaid() {
        return getStatement(QUERY_FOR_ALL_PAID_REQUISITIONS, PluginSqlAction.SELECT);
    }

    public PluginSqlStatement findAllUnpaid() {
        return getStatement(QUERY_FOR_ALL_UNPAID_REQUISITIONS, PluginSqlAction.SELECT);
    }

    public PluginSqlStatement findByReqNumber(String reqNumber) {
        final MapSqlParameterSource namedParameters = new MapSqlParameterSource();

        namedParameters.addValue("valueSuppliedByOperator", reqNumber);

        return getStatement(QUERY_FOR_ONE_REQUISITION, PluginSqlAction.SELECT, namedParameters);
    }

    public PluginSqlStatement updateAsPaid(
        String reqNumber,
        String reqInvoiceNumber,
        LocalDateTime reqPaidDate,
        String reqPaidBy,
        String reqPaidByComments,
        Double reqPaidAmount
    ) {
        final MapSqlParameterSource namedParameters = new MapSqlParameterSource();

        namedParameters.addValue("valueSuppliedByOperator", reqNumber);
        namedParameters.addValue("reqInvoiceNumber", reqInvoiceNumber);
        namedParameters.addValue("reqPaidDate", reqPaidDate);
        namedParameters.addValue("reqPaidBy", reqPaidBy);
        namedParameters.addValue("reqPaidByComments", reqPaidByComments);
        namedParameters.addValue("reqPaidAmount", reqPaidAmount);

        return getStatement(UPDATE_REQUISITION, PluginSqlAction.UPDATE, namedParameters);
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

