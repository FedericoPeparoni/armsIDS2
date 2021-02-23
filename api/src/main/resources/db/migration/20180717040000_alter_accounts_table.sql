alter table accounts add column nationality varchar(2) check (nationality in ('NA','FO'));

update accounts set nationality = 'NA';

alter table accounts alter column nationality set not null;
