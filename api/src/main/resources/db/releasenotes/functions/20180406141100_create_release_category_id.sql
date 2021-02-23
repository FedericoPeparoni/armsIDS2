DROP FUNCTION IF EXISTS release_category_id(varchar);

CREATE FUNCTION release_category_id(category_key varchar) RETURNS int AS $$
DECLARE identifier int;
BEGIN
   SELECT  id INTO identifier
   FROM    release_categories
   WHERE   key = category_key;

   RETURN identifier;
END;
$$ LANGUAGE plpgsql;
