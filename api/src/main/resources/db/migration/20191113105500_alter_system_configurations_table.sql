-- add TTCAA and CASAS organization names to system configuration options
UPDATE system_configurations SET
    range = 'CAAB,DC-ANSP,EANA,INAC,KCAA,ZACL,TTCAA,CASAS'
WHERE item_name = 'Organisation name.  Used to determine site-specific processing.';
