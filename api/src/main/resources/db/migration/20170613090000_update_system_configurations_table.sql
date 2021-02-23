-- Add user specified crossing distance
UPDATE system_configurations SET
    range = 'smallest,largest,scheduled,radar,nominal,atc log,tower log,user'
WHERE item_name = 'Crossing distance precedence';

