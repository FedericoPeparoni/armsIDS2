CREATE FUNCTION system_data_type_id(column_name varchar) RETURNS int AS $$
DECLARE identifier int;
BEGIN
   SELECT  id INTO identifier
   FROM    system_data_types
   WHERE   name = column_name;

   RETURN identifier;
END;
$$ LANGUAGE plpgsql;

CREATE FUNCTION system_item_type_id(column_name varchar) RETURNS int AS $$
DECLARE identifier int;
BEGIN
   SELECT  id INTO identifier
   FROM    system_item_types
   WHERE   name = column_name;

   RETURN identifier;
END;
$$ LANGUAGE plpgsql;

do $$
declare
    v_query varchar;
begin
	v_query := 'INSERT INTO system_configurations (item_name, item_class, data_type, units, range, default_value, current_value,created_by) VALUES ($1,$2,$3,$4,$5,$6,$7,$8)';
	
    execute format(v_query) using 'Log file name',                                                   system_item_type_id('logging'),  system_data_type_id('filename'), NULL,      NULL,                                           'logs/abms.log', NULL, 'system';
    execute format(v_query) using 'Logging level',                                                   system_item_type_id('logging'),  system_data_type_id('int'),      NULL,      '0-2',                                          '3',             NULL, 'system';
    execute format(v_query) using 'Require confirmation on update',                                  system_item_type_id('system'),   system_data_type_id('boolean'),  NULL,      't/f',                                          'T',             NULL, 'system';
    execute format(v_query) using 'Require confirmation on delete',                                  system_item_type_id('system'),   system_data_type_id('boolean'),  NULL,      't/f',                                          'T',             NULL, 'system';
    execute format(v_query) using 'Date format',                                                     system_item_type_id('system'),   system_data_type_id('string'),   NULL,      'yyyy-mm-dd yyyy-mon-dd dd-mm-yyyy mm-dd-yyyy', 'yyyy-mon-dd',   NULL, 'system';
    execute format(v_query) using 'Flight movement retention',                                       system_item_type_id('deletion'), system_data_type_id('int'),      'days',    '365-maxint',                                   '3650',          NULL, 'system';
    execute format(v_query) using 'Line item retention',                                             system_item_type_id('deletion'), system_data_type_id('int'),      'days',    '365-maxint',                                   '3650',          NULL, 'system';
    execute format(v_query) using 'ATC movement logs retention',                                     system_item_type_id('deletion'), system_data_type_id('int'),      'days',    '365-maxint',                                   '3650',          NULL, 'system';
    execute format(v_query) using 'User event log retention',                                        system_item_type_id('deletion'), system_data_type_id('int'),      'days',    '365-maxint',                                   '3650',          NULL, 'system';
    
    execute format(v_query) using 'Email server name',                                               system_item_type_id('email'),    system_data_type_id('string'),   NULL,      NULL,                                           NULL,            NULL, 'system';
    execute format(v_query) using 'Email server port',                                               system_item_type_id('email'),    system_data_type_id('int'),      'port',    NULL,                                           NULL,            NULL, 'system';
    execute format(v_query) using 'Email username',                                                  system_item_type_id('email'),    system_data_type_id('string'),   NULL,      NULL,                                           NULL,            NULL, 'system';
    execute format(v_query) using 'Email password',                                                  system_item_type_id('email'),    system_data_type_id('string'),   NULL,      NULL,                                           NULL,            NULL, 'system';
    execute format(v_query) using 'Email SSL indicator',                                             system_item_type_id('email'),    system_data_type_id('boolean'),  NULL,      't/f',                                          'T',             NULL, 'system';
    execute format(v_query) using 'Email from address',                                              system_item_type_id('email'),    system_data_type_id('string'),   'address', NULL,                                           NULL,            NULL, 'system';
    execute format(v_query) using 'SMS server name',                                                 system_item_type_id('sms'),      system_data_type_id('string'),   NULL,      NULL,                                           NULL,            NULL, 'system';
    execute format(v_query) using 'SMS server port',                                                 system_item_type_id('sms'),      system_data_type_id('int'),      'port',    NULL,                                           NULL,            NULL, 'system';
    execute format(v_query) using 'SMS username',                                                    system_item_type_id('sms'),      system_data_type_id('string'),   NULL,      NULL,                                           NULL,            NULL, 'system';
    
    execute format(v_query) using 'SMS password',                                                    system_item_type_id('sms'),      system_data_type_id('string'),   NULL,      NULL,                                           NULL,            NULL, 'system';
    execute format(v_query) using 'SMS from number',                                                 system_item_type_id('sms'),      system_data_type_id('string'),   'address', NULL,                                           NULL,            NULL, 'system';
    execute format(v_query) using 'Password minimum length',                                         system_item_type_id('password'), system_data_type_id('boolean'),  'chars',   '0-32',                                         '4',             NULL, 'system';
    execute format(v_query) using 'Password uppercase required',                                     system_item_type_id('password'), system_data_type_id('boolean'),  NULL,      't/f',                                          'T',             NULL, 'system';
    execute format(v_query) using 'Password lowercase required',                                     system_item_type_id('password'), system_data_type_id('boolean'),  NULL,      't/f',                                          'T',             NULL, 'system';
    execute format(v_query) using 'Password numeric required',                                       system_item_type_id('password'), system_data_type_id('boolean'),  NULL,      't/f',                                          'T',             NULL, 'system';
    execute format(v_query) using 'Password special character required',                             system_item_type_id('password'), system_data_type_id('boolean'),  NULL,      't/f',                                          'T',             NULL, 'system';
    execute format(v_query) using 'Flight plan is required',                                         system_item_type_id('data'),     system_data_type_id('boolean'),  NULL,      't/f',                                          'T',             NULL, 'system';
    execute format(v_query) using 'ATC log is required',                                             system_item_type_id('data'),     system_data_type_id('boolean'),  NULL,      't/f',                                          'T',             NULL, 'system';
    
    execute format(v_query) using 'Tower aircraft/passenger movement log is required',               system_item_type_id('data'),     system_data_type_id('boolean'),  NULL,      't/f',                                          'F',             NULL, 'system';
    execute format(v_query) using 'Passenger service charge return is required',                     system_item_type_id('data'),     system_data_type_id('boolean'),  NULL,      't/f',                                          'F',             NULL, 'system';
    execute format(v_query) using 'Passenger manifest is required',                                  system_item_type_id('data'),     system_data_type_id('boolean'),  NULL,      't/f',                                          'F',             NULL, 'system';
    execute format(v_query) using 'Radar summary is required',                                       system_item_type_id('data'),     system_data_type_id('boolean'),  NULL,      't/f',                                          'F',             NULL, 'system';
    execute format(v_query) using 'Crossing distance precedence',                                    system_item_type_id('crossing'), system_data_type_id('string'),   NULL,      'smallest largest scheduled,radar,nominal',     'scheduled',     NULL, 'system';
    execute format(v_query) using 'Exempt flights shorter than distance',                            system_item_type_id('crossing'), system_data_type_id('float'),    'km',      NULL,                                           NULL,            NULL, 'system';
    execute format(v_query) using 'Minimum domestic crossing distance',                              system_item_type_id('crossing'), system_data_type_id('float'),    'km',      NULL,                                           NULL,            NULL, 'system';
    execute format(v_query) using 'Maximum domestic crossing distance',                              system_item_type_id('crossing'), system_data_type_id('float'),    'km',      NULL,                                           NULL,            NULL, 'system';
    execute format(v_query) using 'Minimum regional crossing distance',                              system_item_type_id('crossing'), system_data_type_id('float'),    'km',      NULL,                                           NULL,            NULL, 'system';
    
    execute format(v_query) using 'Maximum regional crossing distance',                              system_item_type_id('crossing'), system_data_type_id('float'),    'km',      NULL,                                           NULL,            NULL, 'system';
    execute format(v_query) using 'Minimum international crossing distance',                         system_item_type_id('crossing'), system_data_type_id('float'),    'km',      NULL,                                           NULL,            NULL, 'system';
    execute format(v_query) using 'Maximum international crossing distance',                         system_item_type_id('crossing'), system_data_type_id('float'),    'km',      NULL,                                           NULL,            NULL, 'system';
    execute format(v_query) using 'Entry/exit point rounding distance',                              system_item_type_id('crossing'), system_data_type_id('float'),    'km',      '0-20',                                         '0',             NULL, 'system';
    execute format(v_query) using 'Flight leg decimal places',                                       system_item_type_id('crossing'), system_data_type_id('int'),      NULL,      '0-5',                                          '2',             NULL, 'system';
    execute format(v_query) using 'Calculate crossing distance based on speed and duration',         system_item_type_id('crossing'), system_data_type_id('boolean'),  NULL,      't/f',                                          'f',             NULL, 'system';
    execute format(v_query) using 'Organisation name.  Used to determine site-specific processing.', system_item_type_id('ansp'),     system_data_type_id('string'),   NULL,      'CAAB, ZACL, KCAA, DC-ANSP.',                   NULL,            NULL, 'system';
    execute format(v_query) using 'Mailing adddress',                                                system_item_type_id('ansp'),     system_data_type_id('string'),   NULL,      NULL,                                           NULL,            NULL, 'system';
    execute format(v_query) using 'Contact information',                                             system_item_type_id('ansp'),     system_data_type_id('string'),   NULL,      NULL,                                           NULL,            NULL, 'system';
    
    execute format(v_query) using 'Banking information',                                             system_item_type_id('ansp'),     system_data_type_id('string'),   NULL,      NULL,                                           NULL,            NULL, 'system';
    execute format(v_query) using 'Logo file path',                                                  system_item_type_id('ansp'),     system_data_type_id('string'),   NULL,      NULL,                                           NULL,            NULL, 'system';
    execute format(v_query) using 'Default account currency',                                        system_item_type_id('default'),  system_data_type_id('string'),   NULL,      NULL,                                           NULL,            NULL, 'system';
    execute format(v_query) using 'Default account credit limit',                                    system_item_type_id('default'),  system_data_type_id('float'),    NULL,      NULL,                                           NULL,            NULL, 'system';
    execute format(v_query) using 'Default account maximum credit note amount',                      system_item_type_id('default'),  system_data_type_id('float'),    NULL,      NULL,                                           NULL,            NULL, 'system';
    execute format(v_query) using 'Default account minimum credit note amount',                      system_item_type_id('default'),  system_data_type_id('float'),    NULL,      NULL,                                           NULL,            NULL, 'system';
    execute format(v_query) using 'Default account payment terms',                                   system_item_type_id('default'),  system_data_type_id('int'),      NULL,      NULL,                                           NULL,            NULL, 'system';
    execute format(v_query) using 'Default account monthly penalty rate',                            system_item_type_id('default'),  system_data_type_id('float'),    NULL,      NULL,                                           NULL,            NULL, 'system';
    execute format(v_query) using 'Default account parking exemption',                               system_item_type_id('default'),  system_data_type_id('float'),    'hours',   NULL,                                           NULL,            NULL, 'system';
end $$;