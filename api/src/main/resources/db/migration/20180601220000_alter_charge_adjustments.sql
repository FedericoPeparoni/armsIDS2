ALTER TABLE charges_adjustments
    ADD COLUMN external_accounting_system_identifier character varying(20);

ALTER TABLE pending_charge_adjustments
    ADD COLUMN external_accounting_system_identifier character varying(20);
