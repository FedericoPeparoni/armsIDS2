-- change type from serial to integer (no auto-increment)
alter table flightmovement_categories
    alter column id drop default
;
drop sequence flightmovement_categories_id_seq;

-- set currencies on any existing records to USD
update flightmovement_categories
   set enroute_result_currency_id = currency_id ('USD'),
       enroute_invoice_currency_id = currency_id('USD')
;

-- add the "not null" constraint to currenciy columns
alter table flightmovement_categories
    alter column enroute_result_currency_id set not null,
    alter column enroute_invoice_currency_id set not null
;

