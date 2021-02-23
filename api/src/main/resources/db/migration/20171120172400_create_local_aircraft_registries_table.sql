create table local_aircraft_registries
  (
     id serial primary key,
     registration_number varchar(50) not null unique,
     owner_name varchar(50) not null,
     analysis_type varchar(100) not null,
     mtow_weight double precision not null,
     coa_date_of_renewal timestamp with time zone not null,
     coa_date_of_expiry timestamp with time zone not null,
     version bigint NOT NULL DEFAULT 0,
     created_at timestamp     not null default now(),
     created_by varchar(50)   not null,
     updated_at timestamp,
     updated_by varchar(50)
  );
