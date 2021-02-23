ALTER TABLE system_configurations
    ADD COLUMN client_storage_forbidden boolean NOT NULL DEFAULT false;

ALTER TABLE system_configurations
    ADD COLUMN system_validation_type character varying;
