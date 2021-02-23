alter table countries alter column country_name type character varying(50);
alter table currencies alter column symbol type character varying(10);
alter table currencies alter column currency_name type character varying(50);
alter table currencies alter column decimal_places set default (2);
