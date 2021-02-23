-- add LEONARDO for Radar flight strip format to system configuration options
UPDATE system_configurations SET
    range = 'RAYTHEON-A,EUROCAT-A,INDRA-REC,LEONARDO'
WHERE item_name = 'Radar flight strip format';
