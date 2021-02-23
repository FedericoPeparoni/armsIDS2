-- Based on (renamed/deleted/refactored) file 20180608080000_insert_vtu_currency.sql
CREATE OR REPLACE FUNCTION currency_id(column_name varchar) RETURNS int AS $$
DECLARE identifier int;
BEGIN
   SELECT  id INTO identifier
   FROM    currencies
   WHERE   currency_code = column_name;

   RETURN identifier;
END;
$$ LANGUAGE plpgsql;
