alter table account_users_map
    drop constraint if exists account_users_map_pkey,
    add constraint account_users_map_pkey PRIMARY KEY (account_id, user_id),
    drop column if exists id,
    drop column if exists created_at,
    drop column if exists created_by,
    drop column if exists updated_at,
    drop column if exists updated_by,
    drop column if exists version
;

delete from account_users_map;
insert into account_users_map (account_id, user_id)
	select id, self_care_user_id from accounts where self_care_user_id is not null;

alter table accounts drop column if exists self_care_user_id;
