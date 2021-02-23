delete from system_configurations where item_name = 'Default country';

update system_configurations set system_validation_type = 'INVOICE_SPEC' where item_name = 'Invoice currency for enroute charges';
