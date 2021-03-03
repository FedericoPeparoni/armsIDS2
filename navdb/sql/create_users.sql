-- vim:ts=2:sts=2:sw=2:et

\i config.sql

-- create a user or role with the specified password (optional)
create or replace function navdb_setup__create_role (p_role in text, p_password in text = null) returns void language plpgsql as $$
declare
  v_count integer;
begin
  select count (*) into v_count from pg_authid where rolname = p_role;
  if v_count <= 0 then
    raise notice 'creating role %', p_role;
    execute 'create role '|| p_role;
    if p_password is not null then
      execute 'alter role ' || p_role || ' password ''' || p_password || '''';
    end if;
  end if;
end
$$
;

-- grant the specified role to the specified user if necessary
create or replace function navdb_setup__grant_role (p_user text, p_role text) returns void language plpgsql as $$
begin
  if not pg_has_role (p_user, p_role, 'MEMBER') then
    raise notice 'granting role % to %', p_role, p_user;
    execute 'grant ' || p_role || ' to ' || p_user;
  end if;
end
$$
;

-- roles
select navdb_setup__create_role (:'navdb_ro_role');
select navdb_setup__create_role (:'navdb_rw_role');

-- owner user: "navdb" password "aftn"
select navdb_setup__create_role (:'navdb_owner',   'aftn');
alter user :navdb_owner login;

-- user "navdb_ro" password "aftn"
select navdb_setup__create_role (:'navdb_ro_user', 'aftn');
select navdb_setup__grant_role  (:'navdb_ro_user', :'navdb_ro_role');
alter user :navdb_ro_user login;

-- user "navdb_rw" password "aftn"
select navdb_setup__create_role (:'navdb_rw_user', 'aftn');
select navdb_setup__grant_role  (:'navdb_rw_role', :'navdb_ro_role');
select navdb_setup__grant_role  (:'navdb_rw_user', :'navdb_rw_role');
alter user :navdb_rw_user login;

-- drop temporary functions
drop function navdb_setup__create_role (text, text);
drop function navdb_setup__grant_role (text, text);

