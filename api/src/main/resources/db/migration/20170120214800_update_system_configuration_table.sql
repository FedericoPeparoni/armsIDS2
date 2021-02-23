-- Incorrect values in database, should be comma separated.  Some were comma, some were space separated
UPDATE system_configurations SET
    range = 'smallest,largest,scheduled,radar,nominal'
WHERE item_name = 'Crossing distance precedence';

UPDATE system_configurations SET
    range = 'CAAB,ZACL,KCAA,DC-ANSP'
WHERE item_name = 'Organisation name.  Used to determine site-specific processing.';

UPDATE system_configurations SET
    range = 'yyyy-MM-dd,yyyy-MMM-dd,dd-MM-yyyy,MM-dd-yyyy'
WHERE item_name = 'Date format';

-- Update all empty values with default value
UPDATE system_configurations SET current_value = default_value WHERE current_value IS NULL;

-- Make boolean values all lowercase
UPDATE system_configurations SET current_value = 't', default_value = 't' WHERE current_value = 'T';
UPDATE system_configurations SET current_value = 'f', default_value = 'f' WHERE current_value = 'F';
