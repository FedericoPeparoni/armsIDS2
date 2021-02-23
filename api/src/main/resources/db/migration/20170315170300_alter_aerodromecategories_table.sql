--add new columns
alter table aerodromecategories add column international_fees_currency integer references currencies (id);
alter table aerodromecategories add column domestic_fees_currency integer references currencies (id);

--update existing records
update aerodromecategories set international_fees_currency=(select id from currencies where currency_code='USD');
update aerodromecategories set domestic_fees_currency=(select id from currencies where currency_code='USD');

--make not null
alter table aerodromecategories alter column international_fees_currency set not null;
alter table aerodromecategories alter column domestic_fees_currency set not null;