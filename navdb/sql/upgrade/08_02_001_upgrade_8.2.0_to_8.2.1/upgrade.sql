-- vim:ts=2:sts=2:sw=2:et

-- include the local copy of config.sql
\i upgrade_config.sql

begin;

set search_path to navdb_common,navdb,public,pg_catalog;

CREATE FUNCTION navdb_common.navdb__keep_data_by_effective_date(
    workspace text,
    first_effective_date timestamp without time zone)
  RETURNS void AS
$BODY$
	DECLARE
		r record;
	BEGIN
		PERFORM navdb_common.navdb__set_workspace(workspace);
		FOR r IN SELECT db_name FROM sm_fea where fea_pk > 0 order by db_name ASC
			LOOP
				EXECUTE 'DELETE FROM ' || r.db_name::regclass || ' WHERE validFrom < ''' || first_effective_date || ''''  ;
			END LOOP;
	END;
$BODY$
  LANGUAGE plpgsql VOLATILE;
  
CREATE FUNCTION navdb_common.navdb__keep_last_timeslices(
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
		PERFORM navdb_common.navdb__set_workspace(workspace);
		SELECT count(*) INTO currentMax FROm sm_rolldate;
		IF (currentMax > max_timeslices AND max_timeslices > 1) THEN
			SELECT min(lastrolldate) INTO mindate FROM (SELECT * FROM sm_rolldate ORDER BY id1 OFFSET max_timeslices) a;
			PERFORM navdb_common.navdb__keep_data_by_effective_date(workspace, mindate);
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

-- set data model version
update navdb_common.navdb_admin set dm_version = :'navdb_version';

commit;

