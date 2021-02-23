create table ldp_billing_formulas (
    aerodromecategory_id            int not null references aerodromecategories (id),
    charges_type                    varchar(10) not null,
    charges_spreadsheet             bytea,
    spreadsheet_content_type        varchar(128) not null,
    spreadsheet_file_name           varchar(128) not null,
    created_at                      timestamp    not null default now(),
    created_by                      varchar(50)  not null,
    updated_at                      timestamp,
    updated_by                      varchar(50),
    PRIMARY KEY  (aerodromecategory_id, charges_type)
);
-- charges_type := ("landing" | "departure" | "parking"); the values has been hardcoded (so without a lookup table).
