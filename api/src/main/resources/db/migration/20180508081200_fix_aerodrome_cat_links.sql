ALTER TABLE ldp_billing_formulas DROP CONSTRAINT IF EXISTS ldp_billing_formulas_pkey;
ALTER TABLE ldp_billing_formulas DROP CONSTRAINT IF EXISTS ldp_billing_formulas_aerodromecategory_id_fkey;

ALTER TABLE ldp_billing_formulas RENAME COLUMN aerodromecategory_id TO aerodrome_category_id;

ALTER TABLE ldp_billing_formulas ADD CONSTRAINT ldp_billing_formulas_pkey
PRIMARY KEY (aerodrome_category_id, charges_type);
ALTER TABLE ldp_billing_formulas ADD CONSTRAINT ldp_billing_formulas_aerodrome_category_id_fkey
FOREIGN KEY (aerodrome_category_id) REFERENCES aerodrome_categories (id);
