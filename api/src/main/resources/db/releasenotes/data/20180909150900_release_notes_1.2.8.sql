do $$
declare
    v_query varchar;
begin

    -- remove existing release notes with release_version 1.2.8 if any
    -- when adding more release notes for this version, rename file and update below accordingly
    DELETE FROM release_notes WHERE release_version = '1.2.8';

    -- update existing release notes with same number to reopened and insert new release notes
    v_query := 'UPDATE release_notes SET reopened = true, updated_at = now(), updated_by = ''system'' WHERE number = $2;
                INSERT INTO release_notes (title, number, release_category_id, release_version) VALUES ($1,$2,$3,''1.2.8'');';

    -- features
    execute format(v_query) using 'Include flight departure time on aviation invoices', '90115', release_category_id('feature');
    execute format(v_query) using 'Allow override for zero length billable track if it is not required for invoicing', '90104', release_category_id('feature');
    execute format(v_query) using 'Flight movements - add a button to reset filters', '90103', release_category_id('feature');
    execute format(v_query) using 'Left menu. Move currencies and aircraft types to from management to operations', '90215', release_category_id('feature');
    execute format(v_query) using 'Passenger charges should not be shown on invoices', '90114', release_category_id('feature');

    -- changes
    execute format(v_query) using 'Airport and unknown locations latitude/longitude data entry', '76970', release_category_id('change');
    execute format(v_query) using 'unspecified locations and unspecified aircraft types - predefined filter for unresolved location/aircraft', '76969', release_category_id('change');
    execute format(v_query) using 'User management should allow admin to force user to change his password', '70122', release_category_id('change');

    -- bugs
    execute format(v_query) using 'Transactions - debit/credit note documents are incorrect', '89967', release_category_id('bug');
    execute format(v_query) using 'Problems with account statements', '78304', release_category_id('bug');
    execute format(v_query) using 'Aviation invoice PAX not showing', '90069', release_category_id('bug');
    execute format(v_query) using 'Aviation billing - untrapped server error HTTP 414 "Request-URI Too Long"', '89931', release_category_id('bug');
    execute format(v_query) using 'Cannot generate IATA invoice - Transaction marked as rollbackOnly', '89916', release_category_id('bug');
    execute format(v_query) using 'South African Express invoice SSKIA001119 for local aviation charges (landing etc) invoice was in USD', '84531', release_category_id('bug');
    execute format(v_query) using 'Downloaded invoice has incorrect date in the file name', '84537', release_category_id('bug');
    execute format(v_query) using 'Radar upload - flights are rejected, but a meaningless error message is displayed in the reject interface', '89686', release_category_id('bug');
    execute format(v_query) using 'Radar upload fails - update count returns 0 records when 1 was expected', '89681', release_category_id('bug');
    execute format(v_query) using 'Cannot get JDBC connection', '89535', release_category_id('bug');
    execute format(v_query) using 'Aviation billing invoices - exchange rate is used for current date, not end of billing period date', '86820', release_category_id('bug');
    execute format(v_query) using 'Timeout: Pool empty. Unable to fetch a connection in 30 seconds, none available', '90038', release_category_id('bug');
    execute format(v_query) using 'Aviation billing - progress bar for reconcile and recalculate no longer works properly', '71957', release_category_id('bug');
    execute format(v_query) using 'Selfcare user registration issues (3 items)', '88947', release_category_id('bug');
    execute format(v_query) using 'Pending transactions - transactions is spelled incorrectly', '89948', release_category_id('bug');
    execute format(v_query) using 'Flight schedule editing problems (three items)', '89259', release_category_id('bug');
    execute format(v_query) using 'Selfcare aircraft registration - 5Y is not identified as a Kenya registration prefix', '89988', release_category_id('bug');
    execute format(v_query) using 'Resolution of flight to account is done by flight id / icao code first, and registration number second', '90137', release_category_id('bug');
    execute format(v_query) using 'Radar upload fails - duplicate key', '89683', release_category_id('bug');
    execute format(v_query) using 'Radar upload fails - value too long for type', '89680', release_category_id('bug');
    execute format(v_query) using 'All file upload interfaces should indicate the type of file being uploaded', '87786', release_category_id('bug');
    execute format(v_query) using 'Implement flags to disable requirements for internet access for web portal', '90037', release_category_id('bug');
    execute format(v_query) using 'Flight movements - when loading a flight movement into the data editing form any invalid characters need to be removed from the registration number and flight id', '89951', release_category_id('bug');
    execute format(v_query) using 'Approach fees as ADAP label implementation', '86597', release_category_id('bug');
    execute format(v_query) using 'Display inverse of exchange rate to USD based on system configuration', '87783', release_category_id('bug');
    execute format(v_query) using 'Invoice issues - new templates', '88864', release_category_id('bug');
    execute format(v_query) using 'NullPointer Exception from Transaction Form', '90251', release_category_id('bug');
    execute format(v_query) using 'Air navigation schedules - when a schedule is selected and copied to the edit form the aerodrome class is not copied', '84037', release_category_id('bug');
    execute format(v_query) using 'MTOW maximum value', '77531', release_category_id('bug');
    execute format(v_query) using 'Add system configuration setting for default map bounding box', '77271', release_category_id('bug');
    execute format(v_query) using '"Edit a User" form details has an overlap between a button and nearest label', '77138', release_category_id('bug');
    execute format(v_query) using 'When user modifies a group and updates, the form is closed. It should remain open', '74537', release_category_id('bug');
    execute format(v_query) using 'Flight movement editing. Account should be free text', '72739', release_category_id('bug');
    execute format(v_query) using 'user management - admin cannot force user password change', '72409', release_category_id('bug');
    execute format(v_query) using 'Group management - privileges are not listed in alphabetical order', '72036', release_category_id('bug');
    execute format(v_query) using 'Chrome browser asks if password should be saved when a new account is created', '71211', release_category_id('bug');
    execute format(v_query) using 'System summary - time field has been removed from latest flight information', '74597', release_category_id('bug');
    execute format(v_query) using 'Throughout application - column specifications for uniqueness have not been implemented in the database or in the user interface', '70357', release_category_id('bug');
    execute format(v_query) using 'system configuration parameters - update with current settings from tech specification', '70130', release_category_id('bug');
    execute format(v_query) using 'Protection against "double clicks"', '68906', release_category_id('bug');
    execute format(v_query) using 'All datagrids - text filters work inconsistently', '67762', release_category_id('bug');
    execute format(v_query) using 'All data grids - text and other filters are only applied to the displayed page (except flight movements)', '68014', release_category_id('bug');
    execute format(v_query) using 'Currency - ANSP Currency exchange', '66227', release_category_id('bug');
    execute format(v_query) using 'Flight movement display - reconcile feedback box displays internal indentifiers', '70431', release_category_id('bug');
    execute format(v_query) using 'Login - prevent a user from starting concurrent sessions', '70295', release_category_id('bug');
    execute format(v_query) using 'Point of sale - aviation invoice - create flight - aircraft types is not populated', '90119', release_category_id('bug');
    execute format(v_query) using 'Pending transactions form issues', '89969', release_category_id('bug');
    execute format(v_query) using 'Rejects form is non-standard', '90105', release_category_id('bug');
    execute format(v_query) using 'KRA clerk name and KRA receipt number are mandatory for cash receipts', '87794', release_category_id('bug');
    execute format(v_query) using 'ADAP Charges are applied on arrival', '90106', release_category_id('bug');
    execute format(v_query) using 'Selfcare login errors after lost password recovery', '88950', release_category_id('bug');
    execute format(v_query) using 'Selfcare aircraft registration - No handler found for POST', '89987', release_category_id('bug');
    execute format(v_query) using 'Flight movement builder - international flights from Sudan (HSSS-HKJK) are classed as regional', '90144', release_category_id('bug');
    execute format(v_query) using 'Route parser exception - THRU plan Index out of bounds', '89990', release_category_id('bug');
    execute format(v_query) using 'Self care flight cost calculation - speed and eet are reported as mandatory/missing when you tab through the form', '89984', release_category_id('bug');
    execute format(v_query) using 'Selfcare flight schedules - No handler found for POST', '89994', release_category_id('bug');
    execute format(v_query) using 'Invoices with interest charges on them are incorrect - many issues', '87167', release_category_id('bug');
    execute format(v_query) using 'Exempt aircraft and flights support', '88484', release_category_id('bug');
    execute format(v_query) using 'Translation issues (2 issues)', '86639', release_category_id('bug');
    execute format(v_query) using 'Aviation billing - disable IATA invoicing based on system configuration', '90143', release_category_id('bug');
    execute format(v_query) using 'Credit Transaction Approval workflow no more working', '90306', release_category_id('bug');
    execute format(v_query) using 'Self care portal - flight cost calculation - ADAP charges are always 0.00', '89983', release_category_id('bug');
    execute format(v_query) using '''Billing->Invoices'' Penalty is Not included into Invoice Amount, displayed on ''Invoices'' page', '89977', release_category_id('bug');
    execute format(v_query) using 'Continuous retries on failed flight plan upload from CRONOS', '89991', release_category_id('bug');

end $$;
