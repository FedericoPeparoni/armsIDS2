-- fix LEONARDO and INTELCAN-A formats for Radar flight strip after merging develop-1.4 to develop-1.5
UPDATE system_configurations SET
    range = 'RAYTHEON-A,EUROCAT-A,INDRA-REC,INTELCAN-A,LEONARDO'
WHERE item_name = 'Radar flight strip format';
