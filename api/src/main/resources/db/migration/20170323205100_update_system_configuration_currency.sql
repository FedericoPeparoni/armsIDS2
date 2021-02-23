-- Update `default account currency` to have better naming as well as be classed under `ansp`
update system_configurations SET
    item_name = 'ANSP currency',
    item_class = (SELECT id from system_item_types where name = 'ansp')
WHERE item_name = 'Default account currency';
