-- add CADSUR organization name to system configuration options
UPDATE system_configurations SET
    range = 'CAAB,DC-ANSP,EANA,INAC,KCAA,ZACL,TTCAA,CASAS,CADSUR'
WHERE item_name = 'Organisation name.  Used to determine site-specific processing.';
