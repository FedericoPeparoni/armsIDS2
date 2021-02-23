package ca.ids.abms.plugins.kcaa.erp.modules.aircraftregistrations;

import ca.ids.abms.modules.common.services.AbstractPluginJdbcRepository;
import ca.ids.abms.plugins.kcaa.erp.config.KcaaErpDatabaseConfig;
import ca.ids.abms.util.jdbc.PluginSqlStatement;
import ca.ids.abms.util.jdbc.annotations.HandleEmptyResult;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
public class KcaaErpAircraftRegistrationRepository extends AbstractPluginJdbcRepository<KcaaErpAircraftRegistration, Integer> {

    private final KcaaErpAircraftRegistrationUtility kcaaErpAircraftRegistrationUtility;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public KcaaErpAircraftRegistrationRepository(
        @Qualifier(KcaaErpDatabaseConfig.NAMED_PARAMETER_JDBC_TEMPLATE)
        final NamedParameterJdbcTemplate kcaaErpNamedParameterJdbcTemplate,
        final KcaaErpAircraftRegistrationUtility kcaaErpAircraftRegistrationUtility
    ) {
        super(kcaaErpNamedParameterJdbcTemplate, kcaaErpAircraftRegistrationUtility);
        this.kcaaErpAircraftRegistrationUtility = kcaaErpAircraftRegistrationUtility;
        this.namedParameterJdbcTemplate = kcaaErpNamedParameterJdbcTemplate;
    }

    /**
     * Select sales invoice lines by timestamp between parameters.
     *
     * @param timestampFrom timestamp from value
     * @param timestampTo timestamp to value
     * @return list of sales invoice lines
     */
    List<KcaaErpAircraftRegistration> findByTimestampBetween(byte[] timestampFrom, byte[] timestampTo) {
        try {
            return select(kcaaErpAircraftRegistrationUtility.findByTimestampBetweenStatement(timestampFrom, timestampTo));
        } catch(EmptyResultDataAccessException e) {
            return Collections.emptyList();
        }
    }

    /**
     * Select sales invoice lines latest timestamp.
     *
     * @return latest timestamp of sales invoice lines
     */
    @HandleEmptyResult
    byte[] findLatestTimestamp() {
        PluginSqlStatement sqlStatement = kcaaErpAircraftRegistrationUtility.findLatestTimestampStatement();
        return namedParameterJdbcTemplate.queryForObject(sqlStatement.getStatement(), sqlStatement.getParams(),
            (rs, rowNum) -> rs.getBytes(1));
    }
}
