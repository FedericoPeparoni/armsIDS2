-- create bank accounts table, this will be populated via user interface
CREATE TABLE IF NOT EXISTS bank_accounts (
    id                               serial PRIMARY KEY,
    name                             character varying(60) NOT NULL,
    number                           character varying(30) NOT NULL,
    created_at                       timestamp with time zone NOT NULL DEFAULT now(),
    created_by                       character varying(50) NOT NULL DEFAULT 'system',
    updated_at                       timestamp with time zone,
    updated_by                       character varying(50),
    version                          bigint NOT NULL DEFAULT 0
);
