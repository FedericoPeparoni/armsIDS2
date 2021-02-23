--drop currency columns
alter table aerodromecategories drop column if exists international_fees_currency;
alter table aerodromecategories drop column if exists domestic_fees_currency;