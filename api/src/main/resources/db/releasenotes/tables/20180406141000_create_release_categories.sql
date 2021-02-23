-- create release categories
CREATE TABLE IF NOT EXISTS release_categories
(
    id serial PRIMARY KEY,
    key character varying(16) NOT NULL UNIQUE,
    title character varying(50) NOT NULL,
    sort_order integer NOT NULL,
    created_at timestamp with time zone NOT NULL DEFAULT now(),
    created_by character varying(50) NOT NULL DEFAULT 'system',
    updated_at timestamp with time zone,
    updated_by character varying(50),
	version bigint NOT NULL DEFAULT 0
);
