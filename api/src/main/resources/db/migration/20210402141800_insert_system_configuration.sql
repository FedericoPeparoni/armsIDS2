do $$
declare
    system_item_types_id integer;
begin
    system_item_types_id := nextval('abms.system_item_types_id_seq');


INSERT INTO abms.system_item_types(
    id, name)
VALUES (system_item_types_id, 'UnifiedTax');


INSERT INTO abms.system_configurations(
    id, item_name, item_class, data_type, units, range, created_by)
    
    
VALUES (DEFAULT, 'Agricultural Discount (%)', system_item_types_id, 1, 'percent', '0,100', 'system' );
       


INSERT INTO abms.system_configurations(
    id, item_name, item_class, data_type, units, range, created_by)
 
VALUES (DEFAULT, 'Flight School Discount (%)', system_item_types_id, 1, 'percent', '0,100', 'system');
  
 end $$ LANGUAGE plpgsql;