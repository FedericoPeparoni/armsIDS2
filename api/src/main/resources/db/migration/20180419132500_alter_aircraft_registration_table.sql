ALTER TABLE aircraft_registrations
ADD COLUMN created_by_self_care boolean not null default false;