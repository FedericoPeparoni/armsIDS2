------------------------------------------------------------------------------------------------------------------------
-- dpanech 2020-01-14: this file was refactored from an older migration script called
--
--          db/migration/tables/20190415144100_alter_kcaa_aatis_eaip_tables.sql
--
-- which caused a conflict in some cases and has since been removed.
--
-- This script is executed conditionally only if table kcaa_aatis_transactions exists and kcaa_aatis_permit_numbers
-- does not (see changelog.yml)
--
------------------------------------------------------------------------------------------------------------------------

------------------------------------------------------------------------------------------------------------------------
-- Change KCAA AATIS transactions table to permit numbers and requisition numbers tables respectively with            --
-- foreign key mapped to billing ledgers instead of transactions.                                                     --
--                                                                                                                    --
-- Prior to this data structure change, records only existed from point-of-sale generate and pay action. This allows  --
-- us to assume that the transaction_id reference will link to exactly one transaction payment record and thus        --
-- allowing us to map transaction id to billing ledger id appropriately for existing data                             --
------------------------------------------------------------------------------------------------------------------------

-- alter existing KCAA AATIS transactions table
ALTER TABLE kcaa_aatis_transactions
    RENAME TO kcaa_aatis_permit_numbers;

-- drop transactions foreign key constraint before modifying the column values
ALTER TABLE kcaa_aatis_permit_numbers
    DROP CONSTRAINT kcaa_aatis_transactions_transactions_fkey;

-- change all transaction id references to billing ledger id references and
-- rename transaction id column to billing ledger id
ALTER TABLE kcaa_aatis_permit_numbers
    RENAME transction_id TO billing_ledger_id;

UPDATE kcaa_aatis_permit_numbers pn
	SET billing_ledger_id = tp.billing_ledger_id
	FROM transaction_payments tp
	WHERE pn.billing_ledger_id = tp.transaction_id;

-- add billing ledgers foreign key constraint now that values are defined
ALTER TABLE kcaa_aatis_permit_numbers
    ADD CONSTRAINT kcaa_aatis_permit_numbers_billing_ledgers_fkey FOREIGN KEY (billing_ledger_id)
    REFERENCES billing_ledgers (id) MATCH SIMPLE
    ON UPDATE CASCADE
    ON DELETE CASCADE;
