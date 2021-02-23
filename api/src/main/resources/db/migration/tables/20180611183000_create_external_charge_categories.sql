-- create ExternalChargeCategory data table
CREATE TABLE IF NOT EXISTS external_charge_categories
(
    "id" serial NOT NULL PRIMARY KEY,
    "name" character varying(50) NOT NULL,
    "unique" boolean NOT NULL DEFAULT true,
    "created_at" timestamp with time zone NOT NULL DEFAULT now(),
    "created_by" character varying(50) NOT NULL DEFAULT 'system',
    "updated_at" timestamp with time zone,
    "updated_by" character varying(50)
);

-- create Account many-to-many ExternalChargeCateogry link table
CREATE TABLE IF NOT EXISTS account_external_charge_categories
(
    "id" serial NOT NULL PRIMARY KEY,
    "account_id" integer NOT NULL,
    "external_charge_category_id" integer NOT NULL,
    "external_system_identifier" character varying(25) NOT NULL,
    "version" integer NOT NULL DEFAULT 0,
    "created_at" timestamp with time zone NOT NULL DEFAULT now(),
    "created_by" character varying(50) NOT NULL DEFAULT 'system',
    "updated_at" timestamp with time zone,
    "updated_by" character varying(50),
    CONSTRAINT account_external_charge_category_fkey FOREIGN KEY (account_id)
        REFERENCES accounts (id) MATCH SIMPLE
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    CONSTRAINT external_charge_category_fkey FOREIGN KEY (external_charge_category_id)
        REFERENCES external_charge_categories (id) MATCH SIMPLE
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

-- add ServiceChargeCatalogue many-to-one ExternalChargeCategory foreign column
ALTER TABLE service_charge_catalogues
    ADD COLUMN external_charge_category_id integer;
