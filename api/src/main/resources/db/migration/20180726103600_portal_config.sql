INSERT INTO system_item_types (id, name) VALUES (DEFAULT, 'self-care');

do $$
declare
    v_query varchar;
begin
    v_query := 'INSERT INTO system_configurations (item_name, item_class, data_type, units, range, default_value, current_value, created_by) VALUES ($1,$2,$3,$4,$5,$6,$7,$8)';
    execute format(v_query) using 'Maximum web users per account', system_item_type_id('self-care'), system_data_type_id('int'), null, '1,100', '1', '1', 'system';
    execute format(v_query) using 'Maximum accounts per web user', system_item_type_id('self-care'), system_data_type_id('int'), null, '1,100', '1', '1', 'system';
end $$;

CREATE TABLE account_users_map (
    id serial primary key,
    account_id integer NOT NULL references accounts (id),
    user_id integer NOT NULL references users (id),
    created_at timestamp NOT NULL DEFAULT now(),
    created_by varchar(50) NOT NULL,
    updated_at timestamp,
    updated_by varchar(50),
    version bigint NOT NULL DEFAULT 0
);
