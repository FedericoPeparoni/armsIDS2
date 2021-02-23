-- Alter table `accounts`, drop column `type`
-- `type` column is no longer used because `account_type` is used (ABMSTechSpec-02Feb2017)
alter table accounts drop column if exists type;

-- Account Type (M,string,’Airline’,’GeneralAviation’,’NonAviation’, ‘Tenant’, ‘Commercial’,’Other’)

insert into account_types (name) values ('Tenant');
insert into account_types (name) values ('Commercial');
insert into account_types (name) values ('Other');

drop table types;