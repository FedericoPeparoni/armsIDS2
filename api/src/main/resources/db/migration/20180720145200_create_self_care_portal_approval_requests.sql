create table self_care_portal_approval_requests
  (
     id serial primary key,
     account_id integer references accounts (id),
     user_id integer NOT NULL references users (id),
     request_type varchar(25) NOT NULL,
     request_dataset varchar(50) NOT NULL,
     object_id integer,
     request_text varchar NOT NULL,
     status varchar(25) NOT NULL,
     responders_name varchar(100),
     response_date timestamp with time zone,
     response_text varchar(100),
     version bigint NOT NULL DEFAULT 0,
     created_at timestamp NOT NULL default now(),
     created_by varchar(50) NOT NULL,
     updated_at timestamp,
     updated_by varchar(50)
  );
