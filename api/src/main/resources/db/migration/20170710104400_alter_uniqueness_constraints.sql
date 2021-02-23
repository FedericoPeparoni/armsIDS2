ALTER TABLE certificate_templates ADD CONSTRAINT unique_certificate_template_name UNIQUE (certificate_template_name);
ALTER TABLE radar_summaries ADD CONSTRAINT unique_radar_summary UNIQUE (flight_identifier, day_of_flight, dep_time);
