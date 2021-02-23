ALTER table radar_summaries ADD COLUMN segment integer default 1;

UPDATE radar_summaries SET segment =1;

/*
 * Add an additional field "segment" to the unique constraint:
 * 
 * OLD: (flight_identifier, day_of_flight, dep_time, dep_ad)
 * NEW: (flight_identifier, day_of_flight, dep_time, dep_ad, segment)
 * 
 * This is safe because the new constraint more specific than the old
 * one, so there will be no records that violate it.
 */
alter table radar_summaries drop constraint unique_radar_summary;
alter table radar_summaries add constraint unique_radar_summary
    UNIQUE (flight_identifier, day_of_flight, dep_time, dep_ad, segment);

ALTER table radar_summaries ADD COLUMN entry_coordinate varchar(20);
ALTER table radar_summaries ADD COLUMN exit_coordinate varchar(20);