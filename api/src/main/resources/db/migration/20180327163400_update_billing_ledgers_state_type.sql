-- update billing_ledger and set all invoice_state_type values' capitalization to uppercase
UPDATE
    billing_ledgers
SET
    invoice_state_type = UPPER(invoice_state_type)
