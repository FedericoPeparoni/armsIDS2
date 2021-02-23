--
-- Based on the following (deleted) files from 1.3 branch
--    20180426121800_fix_langtag.sql
--    20180509151500_add_sysconfig_item_with_lang.sql
--    20180604101802_venezuela_configs.sql
--    20180606134700_ven_sysconfig.sql
--

INSERT INTO system_data_types (name) SELECT 'psql' WHERE NOT EXISTS (SELECT id FROM system_data_types WHERE name = 'psql');

-- from 20180426121800_fix_langtag.sql
update system_configurations
   set item_name = 'Calculate circular flight distance based on speed and duration'
where item_name = 'Calculate crossing distance based on speed and duration';

do $$
declare
    v_query varchar;
begin
	
    v_query := 'INSERT INTO system_configurations (item_name, item_class, data_type, units, range, default_value, current_value, created_by) ' ||
               'SELECT $1,$2,$3,$4,$5,$6,$7,$8 WHERE NOT EXISTS (select id from system_configurations where item_name = $1)';

    -- from 20180509151500_add_sysconfig_item_with_lang.sql
    execute format(v_query) using 'Require aerodrome external system id', system_item_type_id('external'), system_data_type_id('boolean'), null, 't/f', 'f', 'f', 'system';
    execute format(v_query) using 'Require account external system id', system_item_type_id('external'), system_data_type_id('boolean'), null, 't/f', 'f', 'f', 'system';
    execute format(v_query) using 'Require billing centre external system id', system_item_type_id('external'), system_data_type_id('boolean'), null, 't/f', 'f', 'f', 'system';
    execute format(v_query) using 'Require currency external system id', system_item_type_id('external'), system_data_type_id('boolean'), null, 't/f', 'f', 'f', 'system';
    execute format(v_query) using 'Require service charge catalogue external system id', system_item_type_id('external'), system_data_type_id('boolean'), null, 't/f', 'f', 'f', 'system';

    -- 20180604101802_venezuela_configs.sql
    execute format(v_query) using 'Aviation credit invoice currency conversion date', system_item_type_id('workflow'), system_data_type_id('string'),
         null, 'day of flight,invoice date,current date', 'day of flight', 'day of flight', 'system';

    execute format(v_query) using 'Aviation cash invoice currency conversion datee', system_item_type_id('workflow'), system_data_type_id('string'),
         null, 'day of flight,invoice date,current date', 'day of flight', 'day of flight', 'system';

    execute format(v_query) using 'Invoice by flight movement category', system_item_type_id('workflow'), system_data_type_id('boolean'),
         null, 't/f', 't', 't', 'system';

    execute format(v_query) using 'Invoice currency for enroute charges', system_item_type_id('workflow'), system_data_type_id('string'),
         null, 'account,flight movement category', 'account', 'account', 'system';

    execute format(v_query) using 'IATA invoicing support', system_item_type_id('workflow'), system_data_type_id('boolean'),
         null, 't/f', 't', 't', 'system';

    execute format(v_query) using 'Country code', system_item_type_id('ansp'), system_data_type_id('psql'),
         null, null, null, null, 'system';

end $$;

-- Refactored from 20180606134700_ven_sysconfig.sql
-- Copy country code from the old "Default country" to the new "Country code" option
update system_configurations new
   set current_value = old.current_value
  from system_configurations old
 where new.item_name = 'Country code'
   and old.item_name = 'Default country'
   and length (trim (both from coalesce (new.current_value, ''))) = 0
   and length (trim (both from coalesce (old.current_value, ''))) > 0
;
delete from system_configurations where item_name = 'Default country';
