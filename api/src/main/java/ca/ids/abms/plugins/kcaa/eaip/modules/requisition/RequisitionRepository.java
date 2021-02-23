package ca.ids.abms.plugins.kcaa.eaip.modules.requisition;

import ca.ids.abms.modules.common.services.AbstractPluginJdbcRepository;
import ca.ids.abms.plugins.kcaa.eaip.config.KcaaEaipDatabaseConfig;
import ca.ids.abms.plugins.kcaa.eaip.modules.requisition.utility.RequisitionUtility;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class RequisitionRepository extends AbstractPluginJdbcRepository<ca.ids.abms.plugins.kcaa.eaip.modules.requisition.Requisition, Integer> {

    private final RequisitionUtility requisitionUtility;

    public RequisitionRepository(
        @Qualifier(KcaaEaipDatabaseConfig.NAMED_PARAMETER_JDBC_TEMPLATE)
        final NamedParameterJdbcTemplate namedParameterJdbcTemplate,
        final RequisitionUtility requisitionUtility
    ) {
        super(namedParameterJdbcTemplate, requisitionUtility);
        this.requisitionUtility = requisitionUtility;
    }

    @Override
    public List<Requisition> findAll() {
        return select(requisitionUtility.findAll());
    }

    List<Requisition> findAllPaid() {
        return select(requisitionUtility.findAllPaid());
    }

    List<Requisition> findAllUnpaid() {
        return select(requisitionUtility.findAllUnpaid());
    }

    Requisition findByAdhocPermitNumber(String reqNumber) {
        try {
            return selectOne(requisitionUtility.findByReqNumber(reqNumber));
        } catch(EmptyResultDataAccessException e) {
            return null;
        }
    }

    void updateAsPaid(
        String reqNumber,
        String reqInvoiceNumber,
        LocalDateTime reqPaidDate,
        String reqPaidBy,
        String reqPaidByComments,
        Double reqPaidAmount
    ) {
        // update permit invoice
        save(
            requisitionUtility.updateAsPaid(
                reqNumber,
                reqInvoiceNumber,
                reqPaidDate,
                reqPaidBy,
                reqPaidByComments,
                reqPaidAmount
            )
        );
    }
}

