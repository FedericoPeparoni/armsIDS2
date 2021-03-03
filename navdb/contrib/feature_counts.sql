create or replace function pg_temp.x_count(p_table varchar) returns integer language plpgsql as $$
declare
	x integer;
begin
	execute format ('select count(*) from %s', p_table) into x;
	return x;
end
$$;

select table_name, pg_temp.x_count (table_name) from information_schema.tables where table_schema = 'navdb' and table_type = 'BASE TABLE' order by table_name;

drop function  pg_temp.x_count(varchar);

