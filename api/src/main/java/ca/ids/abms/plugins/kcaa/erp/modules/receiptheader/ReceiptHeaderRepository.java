package ca.ids.abms.plugins.kcaa.erp.modules.receiptheader;

import ca.ids.abms.modules.common.services.AbstractPluginJdbcRepository;
import ca.ids.abms.plugins.kcaa.erp.config.KcaaErpDatabaseConfig;
import ca.ids.abms.util.jdbc.PluginSqlStatement;
import ca.ids.abms.util.jdbc.annotations.HandleEmptyResult;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ReceiptHeaderRepository extends AbstractPluginJdbcRepository<ReceiptHeader, String> {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final ReceiptHeaderUtility receiptHeaderUtility;

    public ReceiptHeaderRepository(@Qualifier(KcaaErpDatabaseConfig.NAMED_PARAMETER_JDBC_TEMPLATE)
                                   NamedParameterJdbcTemplate kcaaErpNamedParameterJdbcTemplate,
                                   ReceiptHeaderUtility receiptHeaderUtility) {
        super(kcaaErpNamedParameterJdbcTemplate, receiptHeaderUtility);
        this.namedParameterJdbcTemplate = kcaaErpNamedParameterJdbcTemplate;
        this.receiptHeaderUtility = receiptHeaderUtility;
    }

    /**
     * Select reciept headers by timestamp between parameters.
     *
     * @param timestampFrom timestamp from value
     * @param timestampTo timestamp to value
     * @return list of receipt headers
     */
    List<ReceiptHeader> findByTimestampBetween(byte[] timestampFrom, byte[] timestampTo) {
        return select(receiptHeaderUtility.findByTimestampBetweenStatement(timestampFrom, timestampTo));
    }

    /**
     * Select receipt headers latest timestamp.
     *
     * @return latest timestamp of receipt headers
     */
    @HandleEmptyResult
    byte[] findLatestTimestamp() {
        PluginSqlStatement sqlStatement = receiptHeaderUtility.findLatestTimestampStatement();
        return namedParameterJdbcTemplate.queryForObject(sqlStatement.getStatement(), sqlStatement.getParams(),
            (rs, rowNum) -> rs.getBytes(1));
    }
}
