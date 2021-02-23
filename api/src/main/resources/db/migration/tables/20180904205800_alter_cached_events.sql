-- add exceptions inverted cached event property, default false
ALTER TABLE cached_events
    ADD COLUMN exceptions_inverted boolean NOT NULL DEFAULT false;
