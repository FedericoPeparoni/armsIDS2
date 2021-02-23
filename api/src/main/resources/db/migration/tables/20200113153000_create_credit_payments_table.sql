-- create credit payments table, this will be populated by credit processor transactions
CREATE TABLE IF NOT EXISTS credit_payments (
    id                               serial PRIMARY KEY,
    transaction_time                 timestamp with time zone NOT NULL,
    account_id                       integer references accounts (id),
    requestor_ip                     varchar(50) NOT NULL,
    request                          varchar(500) NOT NULL,
    response                         varchar(700) NOT NULL,
    response_status                  varchar(20) NOT NULL,
    response_description             varchar(250),
    created_at                       timestamp with time zone NOT NULL DEFAULT now(),
    created_by                       character varying(50) NOT NULL DEFAULT 'system',
    updated_at                       timestamp with time zone,
    updated_by                       character varying(50),
    version                          bigint NOT NULL DEFAULT 0
);
