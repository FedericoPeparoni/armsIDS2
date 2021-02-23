-- add EANA organization name to system configuration options
UPDATE system_configurations SET
    range = 'CAAB,DC-ANSP,EANA,INAC,KCAA,ZACL'
WHERE item_name = 'Organisation name.  Used to determine site-specific processing.';
