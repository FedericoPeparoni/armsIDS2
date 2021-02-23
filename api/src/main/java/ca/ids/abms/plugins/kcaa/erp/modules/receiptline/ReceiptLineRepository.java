package ca.ids.abms.plugins.kcaa.erp.modules.receiptline;

import ca.ids.abms.modules.common.services.AbstractPluginJdbcRepository;
import ca.ids.abms.plugins.kcaa.erp.config.KcaaErpDatabaseConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ReceiptLineRepository extends AbstractPluginJdbcRepository<ReceiptLine, String> {

    private final ReceiptLineUtility receiptLineUtility;

    public ReceiptLineRepository(@Qualifier(KcaaErpDatabaseConfig.NAMED_PARAMETER_JDBC_TEMPLATE)
                                 NamedParameterJdbcTemplate kcaaErpNamedParameterJdbcTemplate,
                                 ReceiptLineUtility receiptLineUtility) {
        super(kcaaErpNamedParameterJdbcTemplate, receiptLineUtility);
        this.receiptLineUtility = receiptLineUtility;
    }

    /**
     * Select query expected to return result list.
     *
     * @param receiptHeaderNo of `T` type entity
     * @return `T` type
     */
    List<ReceiptLine> findByReceiptHeaderNo(String receiptHeaderNo) {
        return select(receiptLineUtility.findByReceiptHeaderNo(receiptHeaderNo));
    }
}
