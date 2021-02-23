CREATE TABLE IF NOT EXISTS self_care_portal_inactivity_expiry_notices (
    id                               serial PRIMARY KEY,
    account_id                       integer NOT NULL references accounts (id),
    notice_type                      varchar(50) NOT NULL,
    date_time                        timestamp with time zone NOT NULL DEFAULT now(),
    message_text                     varchar(500) NOT NULL,
    created_at                       timestamp with time zone NOT NULL DEFAULT now(),
    created_by                       character varying(50) NOT NULL DEFAULT 'system',
    updated_at                       timestamp with time zone,
    updated_by                       character varying(50)
);
