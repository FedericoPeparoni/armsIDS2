package ca.ids.abms.plugins.kcaa.aatis.modules.invoicepermit;

import ca.ids.abms.modules.common.services.AbstractPluginJdbcRepository;
import ca.ids.abms.plugins.kcaa.aatis.config.KcaaAatisDatabaseConfig;
import ca.ids.abms.plugins.kcaa.aatis.modules.invoicepermit.utility.InvoicePermitUtility;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class InvoicePermitRepository extends AbstractPluginJdbcRepository<InvoicePermit, Integer> {

    private final InvoicePermitUtility invoicePermitUtility;

    public InvoicePermitRepository(
        @Qualifier(KcaaAatisDatabaseConfig.NAMED_PARAMETER_JDBC_TEMPLATE)
        final NamedParameterJdbcTemplate namedParameterJdbcTemplate,
        final InvoicePermitUtility invoicePermitUtility
    ) {
        super(namedParameterJdbcTemplate, invoicePermitUtility);
        this.invoicePermitUtility = invoicePermitUtility;
    }

    @Override
    public List<InvoicePermit> findAll() {
        return select(invoicePermitUtility.findAll());
    }

    List<InvoicePermit> findAllPaid() {
        return select(invoicePermitUtility.findAllPaid());
    }

    List<InvoicePermit> findAllUnpaid() {
        return select(invoicePermitUtility.findAllUnpaid());
    }

    InvoicePermit findByAdhocPermitNumber(String adhocPermitNumber) {
        try {
            return selectOne(invoicePermitUtility.findByAdhocPermitNumber(adhocPermitNumber));
        } catch(EmptyResultDataAccessException e) {
            return null;
        }
    }

    void updateAsPaid(
        String adhocPermitNumber,
        String billingOperatorName,
        String receiptNumber,
        Double amountPaidExcludingAdminFee,
        Double totalAmountPaid,
        String comments

    ) {
        // update permit invoice
        super.save(
            invoicePermitUtility.updateAsPaid(
                adhocPermitNumber,
                billingOperatorName,
                receiptNumber,
                amountPaidExcludingAdminFee,
                totalAmountPaid,
                comments

            )
        );
    }
}

