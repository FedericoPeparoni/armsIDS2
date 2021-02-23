-- create cached event metadata table and index
CREATE TABLE cached_event_metadata
(
    cached_event_id integer NOT NULL,
    type character varying NOT NULL,
    action character varying NOT NULL,
    resource character varying,
    statement character varying,
    sequence integer NOT NULL,
    CONSTRAINT cached_event_metadata_cached_events_fkey FOREIGN KEY (cached_event_id)
        REFERENCES cached_events (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
CREATE INDEX fki_cached_event_metadata_cached_events_fkey
    ON cached_event_metadata USING btree
    (cached_event_id);
