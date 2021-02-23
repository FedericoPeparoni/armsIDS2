-- add columns for the local aircraft
ALTER TABLE aircraft_registrations
    ADD COLUMN aircraft_service_date timestamp with time zone;
ALTER TABLE aircraft_registrations
    ADD COLUMN coa_issue_date timestamp with time zone;
ALTER TABLE aircraft_registrations
    ADD COLUMN coa_expiry_date timestamp with time zone;
ALTER TABLE aircraft_registrations
    ADD COLUMN is_local boolean default false;
    
UPDATE aircraft_registrations SET is_local = true where country_of_registration = (select c.id from system_configurations s,  countries c where s.item_name='Country code' and c.country_code = s.current_value);
