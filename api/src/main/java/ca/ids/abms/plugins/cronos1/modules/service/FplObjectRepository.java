package ca.ids.abms.plugins.cronos1.modules.service;

import ca.ids.abms.modules.spatiareader.entity.FplObject;
import ca.ids.abms.plugins.cronos1.config.Cronos1DatabaseConfig;
import ca.ids.abms.util.converter.JSR310DateConverters;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class FplObjectRepository {

    private final Integer bufferCapacity;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final RowMapper<FplObject> rowMapper;

    public FplObjectRepository(
        @Value("${app.spatiadb.fplImport.bufferCapacity}") final Integer bufferCapacity,
        @Qualifier(Cronos1DatabaseConfig.NAMED_PARAMETER_JDBC_TEMPLATE) final NamedParameterJdbcTemplate namedParameterJdbcTemplate
    ) {
        this.bufferCapacity = bufferCapacity;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.rowMapper = new BeanPropertyRowMapper<>(FplObject.class);
    }

    private static final String QUERY_FOR_ALL_FPL_OBJECTS = (
        "SELECT * FROM (SELECT catalogue_fpl_object_id ,catalogue_date , catalogue_status, catalogue_tx_status ,  catalogue_prc_status,  catalogue_expiry_date,  com_priority , " +
            "  com_originator ,  com_dst_addr_list ,  com_optional_field ,  flight_id ,  ssr_mode ,  ssr_code ,  flight_rules ,  flight_type ,  aircraft_number,  aircraft_type , " +
            "  wake_turb,  equipment ,  departure_ad,  departure_time ,  msg_departure_ad,  msg_departure_time ,  speed ,  flight_level ,  route ,  destination_ad ,  total_eet , " +
            "  alternate_ad ,  alternate_ad_2 ,  arrival_ad ,  arrival_time,  arr_aerodrome_17 ,  other_info ,  departure_date ,  arrival_date,  cancelation_date,  last_change_date, " +
            "  delay_date ,  day_of_flight ,  gufi,  user_id, max(child_catalogue_date) as child_message_max_catalogue_date, raw_fpl FROM ( " +
            "select fpl_object.*, fpl_message.catalogue_date AS child_catalogue_date, fpl_message.raw_fpl AS raw_fpl " +
            "FROM ais.fpl_object fpl_object, ais.processed_fpl fpl_message  WHERE (fpl_message.catalogue_date > :min_date) AND fpl_message.fpl_object_id = fpl_object.catalogue_fpl_object_id " +
            "UNION DISTINCT select fpl_object.*, ats_message.catalogue_date AS child_catalogue_date, fpl_message.raw_fpl AS raw_fpl " +
            "FROM ais.processed_fpl fpl_message,ais.fpl_object fpl_object left outer join ais.processed_ats ats_message on fpl_object.catalogue_fpl_object_id = ats_message.fpl_object_id  " +
            "WHERE (ats_message.catalogue_date > :min_date) AND fpl_message.fpl_object_id = fpl_object.catalogue_fpl_object_id " +
            ") a group by catalogue_fpl_object_id ,catalogue_date , catalogue_status, catalogue_tx_status ,  catalogue_prc_status,  catalogue_expiry_date,  com_priority ,  com_originator , " +
            "  com_dst_addr_list ,  com_optional_field ,  flight_id ,  ssr_mode ,  ssr_code ,  flight_rules ,  flight_type ,  aircraft_number,  aircraft_type ,  wake_turb,  equipment , " +
            "  departure_ad,  departure_time ,  msg_departure_ad,  msg_departure_time ,  speed ,  flight_level ,  route ,  destination_ad ,  total_eet ,  alternate_ad ,  alternate_ad_2 ,  arrival_ad , " +
            "  arrival_time,  arr_aerodrome_17 ,  other_info ,  departure_date ,  arrival_date,  cancelation_date,  last_change_date,  delay_date ,  day_of_flight ,  gufi,  user_id,  raw_fpl " +
            "  ) top_most  order by child_message_max_catalogue_date  limit :capacity"
    );
    List<FplObject> findAllStartingFromDate(final LocalDateTime minDate) {

        final MapSqlParameterSource namedParameters = new MapSqlParameterSource();

        namedParameters.addValue("min_date", JSR310DateConverters.convertLocalDateTimeToDate(minDate));
        namedParameters.addValue("capacity", bufferCapacity);

        return namedParameterJdbcTemplate.query(QUERY_FOR_ALL_FPL_OBJECTS, namedParameters, rowMapper);
    }
}
