-- migrate caab sage bank codes to global bank codes table, no longer a caab sage plugin specific table
INSERT INTO bank_codes(code, description, billing_center_id, currency_id)
    SELECT code, description, billing_center_id, currency_id FROM caab_sage_bank_codes;

-- drop caab sage bank codes specific table as no longer used
DROP TABLE IF EXISTS caab_sage_bank_codes;
