package ca.ids.abms.plugins.kcaa.eaip.modules.requisition;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class RequisitionMapper implements RowMapper<Requisition> {

    @Override
    public ca.ids.abms.plugins.kcaa.eaip.modules.requisition.Requisition mapRow(ResultSet rs, int rowNum) throws SQLException {
        ca.ids.abms.plugins.kcaa.eaip.modules.requisition.Requisition requisition = new Requisition();

        requisition.setReqNo(rs.getString("req_no"));
        requisition.setReqStatusId(rs.getInt("req_status_id"));
        requisition.setReqTotalAmount(rs.getDouble("req_total_amount"));
        requisition.setReqCurrency(rs.getString("req_currency"));
        requisition.setReqCountryId(rs.getInt("req_country_id"));
        requisition.setReqId(rs.getInt("req_id"));
        requisition.setReqArId(rs.getInt("req_ar_id"));
        requisition.setReqManinfoId(rs.getInt("req_maninfo_id"));
        return requisition;
    }

}


