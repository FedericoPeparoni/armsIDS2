ALTER TABLE accounts DROP CONSTRAINT accounts_iata_code_key;
ALTER TABLE accounts DROP CONSTRAINT accounts_icao_code_key;
ALTER TABLE accounts DROP CONSTRAINT accounts_name_key;
ALTER TABLE accounts ADD CONSTRAINT unique_account UNIQUE (name, iata_code, icao_code);
