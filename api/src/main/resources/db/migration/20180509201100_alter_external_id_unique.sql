ALTER TABLE billing_centers
    ADD CONSTRAINT billing_centers_external_accounting_system_identifier_uniq UNIQUE (external_accounting_system_identifier);

ALTER TABLE currencies
    ADD CONSTRAINT currencies_external_accounting_system_identifier_uniq UNIQUE (external_accounting_system_identifier);
