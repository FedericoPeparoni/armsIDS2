update system_configurations set data_type = (select id from system_data_types where name = 'int') where item_name = 'Password minimum length';
update system_configurations set item_name = 'Mailing address' where item_name = 'Mailing adddress';
