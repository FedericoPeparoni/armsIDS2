DROP FUNCTION IF EXISTS plugin_id(varchar);

CREATE FUNCTION plugin_id(plugin_key varchar) RETURNS int AS $$
DECLARE identifier int;
BEGIN
   SELECT  id INTO identifier
   FROM    plugins
   WHERE   key = plugin_key;

   RETURN identifier;
END;
$$ LANGUAGE plpgsql;
