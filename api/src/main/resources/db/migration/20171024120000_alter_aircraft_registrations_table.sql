-- Add column `country_override`, represents if a country was assigned during creation
ALTER TABLE aircraft_registrations
    ADD country_override bool;
