-- create sequence for cached event ids
CREATE SEQUENCE cached_events_id_seq
    CYCLE
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 2147483647;

CREATE FUNCTION nextval_unique(_sequence regclass, _table text, _column text)
    returns bigint as
$$
DECLARE
    _nextval bigint;
    _done bool;
BEGIN

    -- set initial value of _done to false to begin loop
    _done := false;

    -- loop until done, until unique nextval found
    WHILE NOT _done LOOP

        -- get next value from sequence
        _nextval := nextval(_sequence);

        -- check if next value exists in specified table for the specified column
        EXECUTE
            format('SELECT NOT EXISTS (SELECT 1 FROM %I WHERE %I = $1)', _table, _column)
        USING
            _nextval
        INTO
            _done;

    END LOOP;

    -- return nextval as the next unique value
    RETURN _nextval;

END;
$$ LANGUAGE 'plpgsql';

-- create cached events table
CREATE TABLE cached_events
(
    id integer NOT NULL DEFAULT nextval_unique('cached_events_id_seq'::regclass, 'cached_events'::text, 'id'::text),
    target character varying NOT NULL,
    method_name character varying NOT NULL,
    created_at timestamp with time zone NOT NULL DEFAULT now(),
    created_by character varying(50) NOT NULL,
    updated_at timestamp with time zone,
    updated_by character varying(50),
    CONSTRAINT cached_events_pkey PRIMARY KEY (id)
);

-- create cached event parameter types table and index
CREATE TABLE cached_event_param_types
(
    cached_event_id integer NOT NULL,
    type character varying NOT NULL,
    sequence integer NOT NULL,
    CONSTRAINT cached_event_params_cached_events_fkey FOREIGN KEY (cached_event_id)
        REFERENCES cached_events (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
CREATE INDEX fki_cached_event_params_cached_events_fkey
    ON cached_event_param_types USING btree
    (cached_event_id);

-- create cached event exceptions table and index
CREATE TABLE cached_event_exceptions
(
    cached_event_id integer NOT NULL,
    exception character varying NOT NULL,
    CONSTRAINT cached_event_exceptions_cached_events_fkey FOREIGN KEY (cached_event_id)
        REFERENCES cached_events (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
CREATE INDEX fki_cached_event_exceptions_cached_events_fkey
    ON cached_event_exceptions USING btree
    (cached_event_id);

-- create cached event caches table and index
CREATE TABLE cached_event_caches
(
    cached_event_id integer NOT NULL,
    cache_name character varying NOT NULL,
    CONSTRAINT cached_event_caches_cached_events_fkey FOREIGN KEY (cached_event_id)
        REFERENCES cached_events (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
CREATE INDEX fki_cached_event_caches_cached_events_fkey
    ON cached_event_caches USING btree
    (cached_event_id);

-- create cached event arguments table and index
CREATE TABLE cached_event_arguments
(
    cached_event_id integer NOT NULL,
    argument bytea NOT NULL,
    sequence integer NOT NULL,
    CONSTRAINT cached_event_arguments_cached_events_fkey FOREIGN KEY (cached_event_id)
        REFERENCES cached_events (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
CREATE INDEX fki_cached_event_arguments_cached_events_fkey
    ON cached_event_arguments USING btree
    (cached_event_id);

-- create cached event result table and index
CREATE TABLE cached_event_results
(
    cached_event_id integer NOT NULL,
    class character varying,
    result character varying,
    thrown boolean NOT NULL,
    sequence integer NOT NULL,
    created_at timestamp with time zone NOT NULL DEFAULT now(),
    created_by character varying(50) NOT NULL DEFAULT 'system',
    CONSTRAINT cached_event_results_cached_events_fkey FOREIGN KEY (cached_event_id)
        REFERENCES cached_events (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
CREATE INDEX fki_cached_event_results_cached_events_fkey
    ON cached_event_results USING btree
    (cached_event_id);
