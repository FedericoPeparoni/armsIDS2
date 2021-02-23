-- unique constraint shouldn't included column val
-- delete duplicate records that are oldest
-- this is prep for removing val from unique constraint
DELETE FROM languages WHERE id IN (
	SELECT l.id FROM languages l
	JOIN languages d ON l.token = d.token AND l.code = d.code AND l.part = d.part
	WHERE l.val <> d.val AND l.created_at < d.created_at);

-- change existing unique constraint to drop val
ALTER TABLE languages DROP CONSTRAINT IF EXISTS distinct_language_values;
ALTER TABLE languages ADD CONSTRAINT distinct_language_values UNIQUE (token, code, part);

-- create or replace add lang by json function
CREATE OR REPLACE FUNCTION add_languages_by_json(IN langs character varying)
    RETURNS void
    LANGUAGE 'plpgsql'

AS $BODY$
BEGIN

-- use supplied lang string as json and insert records into languages table
-- if conflict, update language value and audit columns
WITH data_json (doc) AS (VALUES (langs::json))
INSERT INTO languages (token, code, val, created_by, part)
SELECT p.token, p.code, p.val, p.created_by, p.part
FROM data_json l CROSS JOIN LATERAL json_populate_recordset(null::languages, doc) as p
ON CONFLICT (token, code, part) DO UPDATE SET
	val = EXCLUDED.val,
	updated_at = now(),
	updated_by = EXCLUDED.created_by;

END;
$BODY$;
