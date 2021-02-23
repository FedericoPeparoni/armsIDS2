INSERT INTO system_item_types (id, name) VALUES (DEFAULT, 'whitelist');
INSERT INTO system_item_types (id, name) VALUES (DEFAULT, 'creditcard');
INSERT INTO system_data_types (id, name) VALUES (DEFAULT, 'date');

do $$
declare
    v_query varchar;
begin
    v_query := 'INSERT INTO system_configurations (item_name, item_class, data_type, units, range, default_value, current_value, created_by) VALUES ($1,$2,$3,$4,$5,$6,$7,$8)';
    execute format(v_query) using 'Whitelisting enabled', system_item_type_id('whitelist'), system_data_type_id('boolean'), null, 't/f', 'f', 'f', 'system';
    execute format(v_query) using 'Whitelisting start date', system_item_type_id('whitelist'), system_data_type_id('date'), null, null, null, null, 'system';
    execute format(v_query) using 'Inactivity period', system_item_type_id('whitelist'), system_data_type_id('int'), 'months', '1,60', '6', '6', 'system';
    execute format(v_query) using 'Expiry period', system_item_type_id('whitelist'), system_data_type_id('int'), 'months', '1,60', '6', '6', 'system';
    execute format(v_query) using 'Inactivity notice text', system_item_type_id('whitelist'), system_data_type_id('string'), null, null, null, null, 'system';
    execute format(v_query) using 'Expiry notice text', system_item_type_id('whitelist'), system_data_type_id('string'), null, null, null, null, 'system';
    execute format(v_query) using 'Aircraft registration notice text', system_item_type_id('whitelist'), system_data_type_id('string'), null, null, null, null, 'system';
    execute format(v_query) using 'Prepayment notice text', system_item_type_id('whitelist'), system_data_type_id('string'), null, null, null, null, 'system';
    execute format(v_query) using 'Accepted flight notice text', system_item_type_id('whitelist'), system_data_type_id('string'), null, null, null, null, 'system';
    execute format(v_query) using 'Declined flight notice text', system_item_type_id('whitelist'), system_data_type_id('string'), null, null, null, null, 'system';
    execute format(v_query) using 'Originator AFTN address', system_item_type_id('whitelist'), system_data_type_id('string'), null, null, null, null, 'system';
    execute format(v_query) using 'Accepted flight notice addresses', system_item_type_id('whitelist'), system_data_type_id('string'), null, null, null, null, 'system';
    execute format(v_query) using 'Declined flight notice addresses', system_item_type_id('whitelist'), system_data_type_id('string'), null, null, null, null, 'system';
    execute format(v_query) using 'Flight notice priority', system_item_type_id('whitelist'), system_data_type_id('string'), null, 'urgent,non-urgent,normal', 'normal', 'normal', 'system';
    execute format(v_query) using 'Credit card processor configured', system_item_type_id('creditcard'), system_data_type_id('boolean'), null, 't/f', 'f', 'f', 'system';
    execute format(v_query) using 'Credit card processor', system_item_type_id('creditcard'), system_data_type_id('string'), null, 'paypal,cxpay', 'paypal', 'paypal', 'system';
    execute format(v_query) using 'Credit card processor URL', system_item_type_id('creditcard'), system_data_type_id('string'), null, null, null, null, 'system';
    execute format(v_query) using 'Credit card processor private key', system_item_type_id('creditcard'), system_data_type_id('string'), null, null, null, null, 'system';
    execute format(v_query) using 'Credit card processor public key', system_item_type_id('creditcard'), system_data_type_id('string'), null, null, null, null, 'system';
end $$;

