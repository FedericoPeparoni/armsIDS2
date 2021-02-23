package ca.ids.abms.plugins.amhs.fpl;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CronosProcessedFpl {

    public Long catalogue_fpl_id;
    public LocalDateTime com_filing_datetime;
    public String raw_fpl;
    public String msg_num_ref_data;
    public String flight_id;
    public String ssr_mode;
    public String flight_rules;
    public String flight_type;
    public String aircraft_number;
    public String aircraft_type;
    public String wake_turb;
    public String equipment;
    public String departure_ad;
    public String departure_time;
    public LocalDateTime day_of_flight;
    public String speed;
    public String flight_level;
    public String route;
    public String destination_ad;
    public String alternate_ad;
    public String alternate_ad_2;
    public String total_eet;
    public String other_info;

    public LocalDate getDayOfFlight() {
        return day_of_flight == null ? null : day_of_flight.toLocalDate();
    }

}
