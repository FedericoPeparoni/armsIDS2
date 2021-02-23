UPDATE system_configurations SET
    item_name = 'Email TLS indicator'
WHERE item_name = 'Email SSL indicator';

UPDATE languages SET
    token = 'Email TLS indicator', val = 'Email TLS indicator'
WHERE token = 'Email SSL indicator' and code = 'en';

UPDATE languages SET
    token = 'Email TLS indicator', val = 'Indicador TLS de Correo Electrónico'
WHERE token = 'Email SSL indicator' and code = 'es';
