
-- drop invalid old constraint
alter table accounts drop constraint unique_account;


-- Fix duplicate values

-- name
update accounts set name = format ('%s (%s)', name, id)
 where name in (
  select name from accounts group by name having count (name) > 1);

-- iata_code
update accounts set iata_code = null
 where iata_code in (
  select iata_code from accounts group by iata_code having count (iata_code) > 1);

-- icao_code
update accounts set icao_code = null
 where icao_code in (
  select icao_code from accounts group by icao_code having count (icao_code) > 1);

-- alias
update accounts set alias = null
 where alias in (
  select alias from accounts group by alias having count (alias) > 1);

-- opr_identifier
update accounts set opr_identifier = null
 where opr_identifier in (
  select opr_identifier from accounts group by opr_identifier having count (opr_identifier) > 1);

-- external_accounting_system_identifier
update accounts set external_accounting_system_identifier = null
 where external_accounting_system_identifier in (
  select external_accounting_system_identifier from accounts group by external_accounting_system_identifier having count (external_accounting_system_identifier) > 1);
;


-- add new constraints
alter table accounts add constraint account_uniq_name unique (name);
alter table accounts add constraint account_uniq_iata_code unique (iata_code);
alter table accounts add constraint account_uniq_icao_code unique (icao_code);
alter table accounts add constraint account_uniq_alias unique (alias);
alter table accounts add constraint account_uniq_opr_identifier unique (opr_identifier);
alter table accounts add constraint account_uniq_external_accounting_system_identifier unique (external_accounting_system_identifier);
