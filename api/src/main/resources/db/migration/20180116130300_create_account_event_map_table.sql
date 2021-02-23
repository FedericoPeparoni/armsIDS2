CREATE TABLE account_event_map
(
     id serial primary key,
     account_id integer NOT NULL references accounts (id),
     notification_event_type_id integer NOT NULL references notification_event_types (id),
     notification_email boolean NOT NULL,
     notification_sms boolean NOT NULL,
     version bigint NOT NULL DEFAULT 0,
     created_at timestamp NOT NULL DEFAULT now(),
     created_by varchar(50) NOT NULL,
     updated_at timestamp,
     updated_by varchar(50),
     CONSTRAINT account_notification_event_type_unique UNIQUE (account_id, notification_event_type_id)
  )
