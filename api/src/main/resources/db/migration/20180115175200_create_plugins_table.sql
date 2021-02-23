-- create plugins table
CREATE TABLE plugins
(
    id serial primary key,
    name character varying NOT NULL,
    description character varying,
    enabled boolean NOT NULL,
    key character varying NOT NULL,
    created_at timestamp with time zone NOT NULL DEFAULT now(),
    created_by character varying(50) NOT NULL DEFAULT 'system',
    updated_at timestamp with time zone,
    updated_by character varying(50)
);

do $$
declare
    v_query varchar;
begin

    v_query := 'INSERT INTO plugins (name, description, enabled, key) VALUES ($1,$2,$3,$4)';
    execute format(v_query) using 'ABMS Plugin Prototype', 'This ABMS plugin prototype is for development only.', false, 'prototype';

end $$;
