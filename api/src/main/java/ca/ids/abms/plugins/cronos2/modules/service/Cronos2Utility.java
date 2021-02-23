package ca.ids.abms.plugins.cronos2.modules.service;

import ca.ids.abms.modules.common.services.PluginJdbcUtility;
import ca.ids.abms.modules.plugins.enumerators.PluginSqlAction;
import ca.ids.abms.modules.spatiareader.entity.FplObject;
import ca.ids.abms.util.converter.JSR310DateConverters;
import ca.ids.abms.util.jdbc.PluginSqlStatement;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public class Cronos2Utility extends PluginJdbcUtility<FplObject, Integer> {

    public Cronos2Utility() {
        super(FplObject.class, new BeanPropertyRowMapper(FplObject.class));
    }

    protected static final String QUERY_FOR_ALL_FPL_OBJECTS = (
        "SELECT * FROM ( \n" +
            "    SELECT \n" +
            "            catalogue_fpl_object_id ,catalogue_date , catalogue_status, catalogue_tx_status , \n" +
            "            catalogue_prc_status,  catalogue_expiry_date,  com_priority ,  com_originator ,  com_dst_addr_list ,  com_optional_field ,  flight_id ,  ssr_mode ,  ssr_code , \n" +
            "            flight_rules ,  flight_type ,  aircraft_number,  aircraft_type ,  wake_turb,  equipment ,  departure_ad,  departure_time ,  msg_departure_ad,  msg_departure_time , \n" +
            "            speed ,  flight_level ,  route ,  destination_ad ,  total_eet ,  alternate_ad ,  alternate_ad_2 ,  arrival_ad ,  arrival_time,  arr_aerodrome_17 ,  other_info , \n" +
            "            departure_date ,  arrival_date,  cancelation_date,  last_change_date,  delay_date ,  day_of_flight ,  gufi, \n" +
            "            user_id, max(child_catalogue_date) as child_message_max_catalogue_date, raw_fpl \n" +
            "    FROM ( \n" +
            "            select \n" +
            "                    fpl_object.catalogue_fpl_object_id, fpl_object.catalogue_date, fpl_object.catalogue_status, fpl_object.catalogue_tx_status, \n" +
            "                    fpl_object.catalogue_prc_status, fpl_object.catalogue_expiry_date, fpl_object.com_priority, fpl_object.com_originator, \n" +
            "                    fpl_object.com_dst_addr_list, fpl_object.com_optional_field, fpl_object.flight_id, fpl_object.ssr_mode, fpl_object.ssr_code, \n" +
            "                    fpl_object.flight_rules, fpl_object.flight_type, fpl_object.aircraft_number, fpl_object.aircraft_type, fpl_object.wake_turb, \n" +
            "                    fpl_object.equipment, fpl_object.departure_ad, fpl_object.departure_time, fpl_object.msg_departure_ad, fpl_object.msg_departure_time, \n" +
            "                    fpl_object.speed, fpl_object.flight_level, fpl_object.route, fpl_object.destination_ad, fpl_object.total_eet, fpl_object.alternate_ad, \n" +
            "                    fpl_object.alternate_ad_2, fpl_object.arrival_ad, fpl_object.arrival_time, fpl_object.arr_aerodrome_17, fpl_object.other_info, \n" +
            "                    fpl_object.departure_date, fpl_object.arrival_date, fpl_object.cancelation_date, fpl_object.last_change_date, fpl_object.delay_date, \n" +
            "                    fpl_object.day_of_flight, fpl_object.user_id, fpl_object.gufi, \n" +
            "                    fpl_message.catalogue_date AS child_catalogue_date, fpl_message.raw_fpl AS raw_fpl \n" +
            "                FROM ais.fpl_object fpl_object, ais.processed_fpl fpl_message \n" +
            "                WHERE (fpl_message.catalogue_date > :min_date) AND fpl_message.fpl_object_id = fpl_object.catalogue_fpl_object_id \n" +
            "            UNION \n" +
            "            select \n" +
            "                    fpl_object.catalogue_fpl_object_id, fpl_object.catalogue_date, fpl_object.catalogue_status, fpl_object.catalogue_tx_status, \n" +
            "                    fpl_object.catalogue_prc_status, fpl_object.catalogue_expiry_date, fpl_object.com_priority, fpl_object.com_originator, \n" +
            "                    fpl_object.com_dst_addr_list, fpl_object.com_optional_field, fpl_object.flight_id, fpl_object.ssr_mode, fpl_object.ssr_code, \n" +
            "                    fpl_object.flight_rules, fpl_object.flight_type, fpl_object.aircraft_number, fpl_object.aircraft_type, fpl_object.wake_turb, \n" +
            "                    fpl_object.equipment, fpl_object.departure_ad, fpl_object.departure_time, fpl_object.msg_departure_ad, fpl_object.msg_departure_time, \n" +
            "                    fpl_object.speed, fpl_object.flight_level, fpl_object.route, fpl_object.destination_ad, fpl_object.total_eet, fpl_object.alternate_ad, \n" +
            "                    fpl_object.alternate_ad_2, fpl_object.arrival_ad, fpl_object.arrival_time, fpl_object.arr_aerodrome_17, fpl_object.other_info, \n" +
            "                    fpl_object.departure_date, fpl_object.arrival_date, fpl_object.cancelation_date, fpl_object.last_change_date, fpl_object.delay_date, \n" +
            "                    fpl_object.day_of_flight, fpl_object.user_id, fpl_object.gufi, \n" +
            "                    ats_message.catalogue_date AS child_catalogue_date, fpl_message.raw_fpl AS raw_fpl \n" +
            "                FROM ais.processed_fpl fpl_message,ais.fpl_object fpl_object \n" +
            "                left outer join ais.processed_ats ats_message on fpl_object.catalogue_fpl_object_id = ats_message.fpl_object_id \n" +
            "                WHERE (ats_message.catalogue_date > :min_date) AND fpl_message.fpl_object_id = fpl_object.catalogue_fpl_object_id \n" +
            "        ) a group by catalogue_fpl_object_id ,catalogue_date , catalogue_status, catalogue_tx_status , catalogue_prc_status, catalogue_expiry_date, com_priority , com_originator , \n" +
            "          com_dst_addr_list , com_optional_field , flight_id ,ssr_mode ,ssr_code ,flight_rules ,flight_type ,aircraft_number,aircraft_type ,wake_turb,equipment ,departure_ad, \n" +
            "          departure_time ,msg_departure_ad,msg_departure_time ,speed ,flight_level ,route ,destination_ad ,total_eet ,alternate_ad ,alternate_ad_2 ,arrival_ad ,arrival_time, \n" +
            "          arr_aerodrome_17 ,other_info ,departure_date ,arrival_date,cancelation_date,last_change_date,delay_date ,day_of_flight ,gufi,user_id, raw_fpl \n" +
            " ) top_most \n" +
            "order by child_message_max_catalogue_date \n" +
            "limit :capacity"
        );

    PluginSqlStatement findAllStartingFromDate (final LocalDateTime minDate, final Integer capacity) {
        final MapSqlParameterSource namedParameters = new MapSqlParameterSource();

        namedParameters.addValue("min_date", JSR310DateConverters.convertLocalDateTimeToDate(minDate));
        namedParameters.addValue("capacity", capacity);

        return getStatement(QUERY_FOR_ALL_FPL_OBJECTS, PluginSqlAction.SELECT, namedParameters);

    }

    private PluginSqlStatement getStatement(String statement, PluginSqlAction action, MapSqlParameterSource params) {
        PluginSqlStatement result = new PluginSqlStatement();

        result.setAction(action);
        result.setParams(params);
        result.setResource(getResourceName());
        result.setStatement(statement);

        return result;
    }
}
