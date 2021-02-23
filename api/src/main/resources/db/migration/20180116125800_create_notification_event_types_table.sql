CREATE TABLE notification_event_types
  (
     id serial primary key,
     event_type  varchar(60) not null unique,
     user_notification_indicator boolean not null,
     customer_notification_indicator boolean not null,
     created_at timestamp not null default now(),
     created_by varchar(50) not null,
     updated_at timestamp,
     updated_by varchar(50)
  );


INSERT INTO notification_event_types (event_type, user_notification_indicator, customer_notification_indicator, created_at, created_by)
VALUES ('Recurring charge end warning',true, false, now(), 'system');

INSERT INTO notification_event_types (event_type, user_notification_indicator, customer_notification_indicator, created_at, created_by)
VALUES ('Recurring charge end notification',true, false, now(), 'system');

INSERT INTO notification_event_types (event_type, user_notification_indicator, customer_notification_indicator, created_at, created_by)
VALUES ('Invoice publication',true, true, now(), 'system');

INSERT INTO notification_event_types (event_type, user_notification_indicator, customer_notification_indicator, created_at, created_by)
VALUES ('Invoice overdue',true, true, now(), 'system');

INSERT INTO notification_event_types (event_type, user_notification_indicator, customer_notification_indicator, created_at, created_by)
VALUES ('Expired certificate warning',true, true, now(), 'system');

INSERT INTO notification_event_types (event_type, user_notification_indicator, customer_notification_indicator, created_at, created_by)
VALUES ('Expired certificate',true, true, now(), 'system');

INSERT INTO notification_event_types (event_type, user_notification_indicator, customer_notification_indicator, created_at, created_by)
VALUES ('Flight plan reception',true, false, now(), 'system');

INSERT INTO notification_event_types (event_type, user_notification_indicator, customer_notification_indicator, created_at, created_by)
VALUES ('Flight plan reception for blacklisted account',true, false, now(), 'system');
