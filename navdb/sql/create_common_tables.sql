-- vim:ts=2:sts=2:sw=2:et

-------------------------------------------
-- navdb_admin
-------------------------------------------
create table navdb_admin (
  dm_version varchar(10) not null,
  last_updated timestamp without time zone not null default current_timestamp
);

insert into navdb_admin (dm_version) values (:'navdb_version');

create function navdb_admin__trf_last_updated() returns trigger language plpgsql as $$
begin
  NEW.last_updated := current_timestamp;
  return NEW;
end;
$$
;
create trigger navdb_admin__trg_last_updated
               before update on navdb_admin
               for each row
               when (OLD.* is distinct from NEW.*)
               execute procedure navdb_admin__trf_last_updated()
;


-------------------------------------------
-- FUNCTION:
--   navdb__get_workspaces();
--
-- DESCRIPTION:
--   Returns a list rowset with one column containing all available workspaces
--
-- EXAMPLE:
--   selelect navdb__get_workspaces();   -- returns 'live', 'staging'
-------------------------------------------
create or replace function navdb__get_workspaces() returns setof text language sql immutable as $$
select unnest (array ['live', 'staging']);
$$
;

-------------------------------------------
-- FUNCTION:
--   navdb__set_workspace (workspace);
--
-- DESCRIPTION:
--   Sets current aero tables workspace by manipulating the search path.
--   The "workspace" parameter must be a string 'live' or 'staging'.
--
-- EXAMPLE:
--   select navdb__set_workspace ('live');     -- 'navdb,navdb_common,public'
--   select navdb__set_workspace ('staging');  -- 'navdb_staging,navdb_common,public'
-------------------------------------------
create or replace function navdb__set_workspace (p_workspace in text) returns void language plpgsql as $$
declare
  v_live_schema           text := 'navdb';
  v_staging_schema        text := 'navdb_staging';
  v_target_schema         text;
  v_old_search_path       text[];
  v_new_search_path       text[];
  v_part                  text;
begin

  -- determine target schema name
  if p_workspace is null then
    raise 'p_workspace must not be null';
  elsif p_workspace = 'live' or p_workspace = v_live_schema then
    v_target_schema := v_live_schema;
  elsif p_workspace = 'staging' or p_workspace = v_staging_schema then
    v_target_schema := v_staging_schema;
  else
    raise 'p_workspace must be ''live'' or ''staging''';
  end if;

  -- determine current search path
  select current_schemas (false) into v_old_search_path;

  -- remove target schema from search path
  v_new_search_path := array[]::text[];
  foreach v_part in array v_old_search_path loop
    if v_part is not null and v_part <> v_live_schema and v_part <> v_staging_schema then
      v_new_search_path := v_new_search_path || v_part;
    end if;
  end loop;
  
  -- prepend target schema to search path
  v_new_search_path := v_target_schema || v_new_search_path;
  
  -- save the search path
  execute 'set search_path to ' || array_to_string (v_new_search_path, ',');
end
$$
;

----------------------------------------------------------------------------------
-- FUNCTION:
--   navdb__set_effective_date() -- set effective date used by feature views
--
-- DESCRIPTION:
--   Sets the effective date (TIMESTAMP) for feature instances returned by various
--   feature views. The effective date can be set in each session.
--
-- ARGUMENTS:
--   p_date in timestamp with time zone -- the new effective date
--
-- RETURNS:
--   timestamp with time zone -- the new effective date (same as the parameter above)
--
----------------------------------------------------------------------------------
create or replace function navdb__set_effective_date (
  p_date in timestamp with time zone
)
returns timestamp with time zone
language plpgsql
as $$
begin
  if p_date is null then
    perform set_config('temporality.effective_date', '', false);
    return null;
  else
    perform set_config('temporality.effective_date', to_char (p_date, 'YYYY-MM-DD HH24:MI:SS'), false);
    return p_date;
  end if;
end
$$
;

----------------------------------------------------------------------------------
-- FUNCTION:
--   navdb__get_effective_date() -- get effective date used by feature views
--
-- DESCRIPTION:
--   Returns the effective date (TIMESTAMP) for feature instances returned by various
--   feature views. The effective date can be set in each session;
--   the default (i.e., when not set for current session) is the start time of the
--   current transaction.
--
-- RETURNS:
--   TIMESTAMP WITHOUT TIMEZONE
--
----------------------------------------------------------------------------------
create or replace function navdb__get_effective_date()
returns timestamp with time zone
language plpgsql
stable
cost 1000
as $$
  declare
    effective_date timestamp with time zone;
  begin
    select to_timestamp (current_setting('temporality.effective_date'), 'YYYY-MM-DD HH24:MI:SS') into effective_date;
    return effective_date;
  exception
    when undefined_object or invalid_datetime_format then
      return transaction_timestamp();
  end;
$$
;


-- timeslices

CREATE FUNCTION navdb__keep_data_by_effective_date(
    workspace text,
    first_effective_date timestamp without time zone)
  RETURNS void AS
$BODY$
	DECLARE
		r record;
	BEGIN
		PERFORM navdb__set_workspace(workspace);
		FOR r IN SELECT db_name FROM sm_fea where fea_pk > 0 order by db_name ASC
			LOOP
				EXECUTE 'DELETE FROM ' || r.db_name::regclass || ' WHERE validFrom < ''' || first_effective_date || ''''  ;
			END LOOP;
	END;
$BODY$
  LANGUAGE plpgsql VOLATILE;
  
CREATE FUNCTION navdb__keep_last_timeslices(
    workspace text,
    max_timeslices integer)
  RETURNS integer AS
$BODY$
	DECLARE
		r record;
		currentMax int;
		mindate timestamp without time zone;
		toReturn int;
	BEGIN
		PERFORM navdb__set_workspace(workspace);
		SELECT count(*) INTO currentMax FROm sm_rolldate;
		IF (currentMax > max_timeslices AND max_timeslices > 1) THEN
			SELECT min(lastrolldate) INTO mindate FROM (SELECT * FROM sm_rolldate ORDER BY id1 OFFSET max_timeslices) a;
			PERFORM navdb__keep_data_by_effective_date(workspace, mindate);
			DELETE FROM sm_rolldate where id1 IN (SELECT id1 FROM sm_rolldate OFFSET max_timeslices);
			toReturn = currentMax - max_timeslices;
		ELSE
			toReturn = 0;
		END IF;
		
		RAISE NOTICE 'DELETE TIMESLICES#: %', toReturn;
		return toReturn;
	END;
$BODY$
  LANGUAGE plpgsql VOLATILE;

-- GIS functions
\i ids__st_intersection.sql
\i create_geometry_functions.sql
