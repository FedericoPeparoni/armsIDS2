alter table account_exemptions add approach_fees_exempt bool not null default(false);
alter table account_exemptions alter column approach_fees_exempt drop default;
alter table account_exemptions add aerodrome_fees_exempt bool not null default(false);
alter table account_exemptions alter column aerodrome_fees_exempt drop default;

update permissions set name='exempt_account_view' where name='account_exemptions_view';
update permissions set name='exempt_account_modify' where name='account_exemptions_modify';
