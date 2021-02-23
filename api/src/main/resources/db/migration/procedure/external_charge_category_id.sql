DROP FUNCTION IF EXISTS external_charge_category_id(varchar);

CREATE FUNCTION external_charge_category_id(external_charge_category_name varchar) RETURNS int AS $$
DECLARE identifier int;
BEGIN
   SELECT  id INTO identifier
   FROM    external_charge_categories
   WHERE   name = external_charge_category_name;

   RETURN identifier;
END;
$$ LANGUAGE plpgsql;
