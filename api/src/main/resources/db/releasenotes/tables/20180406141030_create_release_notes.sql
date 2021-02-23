-- create release notes
CREATE TABLE IF NOT EXISTS release_notes
(
    id serial PRIMARY KEY,
    title character varying(255) NOT NULL,
    "number" character varying(16) NOT NULL,
    reopened boolean NOT NULL DEFAULT false,
    release_category_id integer NOT NULL,
    release_version character varying(16) NOT NULL,
    created_at timestamp with time zone NOT NULL DEFAULT now(),
    created_by character varying(50) NOT NULL DEFAULT 'system',
    updated_at timestamp with time zone,
    updated_by character varying(50),
	version bigint NOT NULL DEFAULT 0,
    CONSTRAINT release_notes_release_categories_fkey FOREIGN KEY (release_category_id)
        REFERENCES release_categories (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
