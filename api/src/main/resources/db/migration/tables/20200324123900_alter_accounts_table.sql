ALTER TABLE accounts
    ADD COLUMN whitelist_last_activity_date_time timestamp,
    ADD COLUMN whitelist_inactivity_notice_sent_flag boolean DEFAULT false,
    ADD COLUMN whitelist_expiry_notice_sent_flag boolean DEFAULT false,
    ADD COLUMN whitelist_state varchar(15) DEFAULT 'ACTIVE';

UPDATE accounts
    SET whitelist_last_activity_date_time = (CASE WHEN updated_at IS NOT NULL THEN updated_at ELSE created_at END)
    WHERE whitelist_last_activity_date_time IS NULL;

ALTER TABLE accounts
    ALTER COLUMN whitelist_last_activity_date_time SET NOT NULL,
    ALTER COLUMN whitelist_last_activity_date_time SET DEFAULT now(),
    ALTER COLUMN whitelist_inactivity_notice_sent_flag SET NOT NULL,
    ALTER COLUMN whitelist_expiry_notice_sent_flag SET NOT NULL,
    ALTER COLUMN whitelist_state SET NOT NULL;
