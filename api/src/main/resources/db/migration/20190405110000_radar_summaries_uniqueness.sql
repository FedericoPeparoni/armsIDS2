/*
 * Add an additional field "dep_ad" to the unique constraint:
 * 
 * OLD: (flight_identifier, day_of_flight, dep_time)
 * NEW: (flight_identifier, day_of_flight, dep_time, dep_ad)
 * 
 * This is safe because the new constraint more specific than the old
 * one, so there will be no records that violate it.
 */
alter table radar_summaries drop constraint unique_radar_summary;
alter table radar_summaries add constraint unique_radar_summary
    UNIQUE (flight_identifier, day_of_flight, dep_time, dep_ad);
