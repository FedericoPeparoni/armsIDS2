do $$
declare
    v_query varchar;
begin

    -- remove existing release notes with release_version 1.2.0 if any
    -- when adding more release notes for this version, rename file and update below accordingly
    DELETE FROM release_notes WHERE release_version = '1.2.0';

    -- update existing release notes with same number to reopened and insert new release notes
    v_query := 'UPDATE release_notes SET reopened = true, updated_at = now(), updated_by = ''system'' WHERE number = $2;
                INSERT INTO release_notes (title, number, release_category_id, release_version) VALUES ($1,$2,$3,$4);';

    -- change requests
    execute format(v_query) using 'Flights through Wajir - there should be no ADAP charge on the arrival at Wajir', '83758', release_category_id('change'), '1.2.0';
    execute format(v_query) using 'Invoice totals must be rounded up to the next KES/USD - both amounts must be shown on invoice', '83759', release_category_id('change'), '1.2.0';
    execute format(v_query) using 'Approval at different levels for credit and debit notes is required plus review / approval process', '70118', release_category_id('change'), '1.2.0';
    execute format(v_query) using 'Tower and atc upload - automated upload of tower and atc logs', '75321', release_category_id('change'), '1.2.0';
    execute format(v_query) using 'Enhancements to system backup', '70123', release_category_id('change'), '1.2.0';
    execute format(v_query) using 'Regional flight identification (issue 1 ZZZZ locations)', '84213', release_category_id('change'), '1.2.0';

    -- feature requests
    execute format(v_query) using 'Data Analysis and Statistics – Air Traffic', '58356', release_category_id('feature'), '1.2.0';
    execute format(v_query) using 'Data Analysis and Statistics – Revenue', '58357', release_category_id('feature'), '1.2.0';
    execute format(v_query) using 'Database replication', '58333', release_category_id('feature'), '1.2.0';
    execute format(v_query) using 'Development environment: create Linux virtual machine template', '67843', release_category_id('feature'), '1.2.0';
    execute format(v_query) using 'Flights flagged as been billed via nominal distance when it is not selected', '83698', release_category_id('feature'), '1.2.0';
    execute format(v_query) using 'EUROCAT Radar upload issues (6 items)', '74718', release_category_id('feature'), '1.2.0';
    execute format(v_query) using 'Flight movement specific operations - Somalia flights', '74559', release_category_id('feature'), '1.2.0';
    execute format(v_query) using 'Self-care portal - account management interface', '74570', release_category_id('feature'), '1.2.0';
    execute format(v_query) using 'Self-care portal - aircraft registration management interface', '74571', release_category_id('feature'), '1.2.0';
    execute format(v_query) using 'Self-care portal - invoice display interface', '74574', release_category_id('feature'), '1.2.0';
    execute format(v_query) using 'Self-care portal - transaction display interface', '74573', release_category_id('feature'), '1.2.0';
    execute format(v_query) using 'Self-care portal - user registration interface', '74568', release_category_id('feature'), '1.2.0';
    execute format(v_query) using 'Passenger service charge return image upload', '84642', release_category_id('feature'), '1.2.0';
    execute format(v_query) using 'Report Templates', '58343', release_category_id('feature'), '1.2.0';
    execute format(v_query) using 'Support for categorisation of revenue statistics by mode of payment (cash, credit)', '74554', release_category_id('feature'), '1.2.0';
    execute format(v_query) using 'Self-care portal - login interface', '74566', release_category_id('feature'), '1.2.0';
    execute format(v_query) using 'Preconfigured revenue and air traffic statistics', '74555', release_category_id('feature'), '1.2.0';
    execute format(v_query) using 'Passenger service charge return - allow upload of passenger manifest image', '70113', release_category_id('feature'), '1.2.0';
    execute format(v_query) using 'Preconfigured reports', '74557', release_category_id('feature'), '1.2.0';
    execute format(v_query) using 'Report Generation', '65037', release_category_id('feature'), '1.2.0';

    -- bugs
    execute format(v_query) using 'Transaction Approval not working', '84742', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Regional flight identification (issue 2 HS aerodrome prefix)', '85560', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Credit Note not created and not displayed in the pending transactions', '84734', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Percentage for match flights not working', '82974', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'System can''t generate NON-IATA Invoice', '85552', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Transaction interface giving unusual error', '85594', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Self-care portal - flight cost calculation interface', '74569', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Demo - invoice numbering', '84042', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Can''t upload a new, or update an existing, Air Navigation Charge Schedule record', '84008', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Abms-update fails from remote sites - operation too slow', '72743', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Account management - delivery format should be a list box, not a text box', '58345', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Fallout from 83444 - invoice is paid from previous credit which has no balance left', '83608', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Invoices are not linked to billing centre', '83573', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Non-aviation invoice is paid from previous credit which should have no balance left', '83444', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Non-aviation invoice should have header "this is due to movement log" removed', '84296', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Null Pointer Exception on almost every interface with aircraft involved', '84588', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Radar is not being uploaded automatically', '84281', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'System is missing delta flights which are in the tower and atc logs', '84283', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Account not set via Registration Number', '83499', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Account sorting not working when Text Filter is in use', '84044', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Users password buttons', '81975', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Aircraft association with Reg Num not working', '84701', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Invoice Template Upload fail', '84575', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'ADAP should only be charged on departures', '84216', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Charges Calculation', '66229', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Flight movement display - garbled flight graphics are generated - all flights', '84262', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Flight movement editing - support for unspecified aircraft types', '72736', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Flight movements search form showing validation errors that are not applicable', '84780', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Investigate other missing AIX/M data', '75863', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'ADAP charges are not being calculated correctly', '83694', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Automated file upload issues (7 items)', '83967', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Dead link, typo and misnamed labels (4 items)', '83976', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Flight Level field gives error with no apparent reason', '84385', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Flight MTOW resolution is incorrect when a registration has an MTOW different from the default for the aircraft type', '83692', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Regional arrival / departure aerodromes are not labelled on map', '83760', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Selfcare portal - provide support for lost password recovery via email', '84229', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Selfcare portal - user management', '84230', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'THRU flight plan demonstrated did not include an ADAP charge', '83757', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'THRU flight plan logic should always be executed', '84214', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'When THRU flight plans are displayed, the aerodrome locations are labelled as lat/long points, not with aerodrome names', '83693', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Wrong Radar Route and Distance', '84313', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Entire field in error must be highlighted; error message display must be consistent throughout the application', '77707', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Handle overdue invoices and penalties', '77710', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Specific flight movement display characteristics', '77709', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Invoices - exchange rate dates - cash invoices use current date; credit invoices use end date of billing period', '84215', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'EUROCAT radar importer - "may not be null" exception', '83973', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'EUROCAT radar importer exceptions (many items)', '84033', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'EUROCAT radar importer fails all records with ZZZZ aerodromes', '83975', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'EUROCAT radar importer issues (3 items)', '84032', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Flight movements are not automatically updated with passenger information when they are created', '83699', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'A/C type displayerd after a new record is inserted', '84705', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Aerodrome Category not displayed all over the system', '84707', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Blacklisted Account job not working', '84000', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Can''t create Radar Summary Records', '83996', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Data Analysis and Statistic, Air Traffic and Revenue - Download pdf button not working', '83998', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Export function not working on the FM interface', '83965', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Flight Movement Status not update after the invoice generation', '84729', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Invoice approval workflow not working', '84736', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Local Aircraft Registry not displaying the Upload report', '83981', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Thru Plan, missing ADAP charge', '84777', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Unspecified A/C not taken into account', '84017', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Upload of ATC Log gives a Generic error', '84010', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Users table not dowloaded', '84703', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Null handling for billing centres invoice and receipt number', '83609', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Order of Migration scripts', '83632', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Null pointer blocks all the invoice creation', '84560', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Preconfigured Report - Debtor Report not working', '84743', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Radar file upload failed with sample provided', '83751', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Server hardcoded to localhost', '65017', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Switching on and off the "Approach fee as ADAP" the system charges the Aerodrome fees on the Approach field', '84680', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Unfinished part of transaction management - update external accounting system', '68205', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'View/Edit form wont enable the Create button', '84776', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Flight present on the DB, but not displayed on the FE', '83378', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Receipt''s download button not shown', '83323', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'RouteParser Detailed Documentation in GitLab', '64747', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Charter flights discount for multiple landings on the same day', '83514', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'System Configuration not taken into account', '84439', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'SelfCare Portal dispalys CAAB log, instead of KCAA', '85671', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Approach Charges wrong computed when MTOW unit system parameter is kg', '77643', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Unexpected Flight not detected for Flight Schedule', '83983', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Account "Delete not allowed" error not displaying correct values', '85964', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'PAID Flight Movement can be modified', '85965', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Password policy error not displayed', '85960', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Enroute cost wrongly calculated', '85984', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Enroute charges not converted if invoice is in KES', '85996', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'ADAP fee for Somalian Flight not charged if created via Radar Summary', '84778', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'MTOW wrongly uploaded from the Aircraft Registration upload', '85961', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Cannot create unspecified locations', '85932', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'International Overflights not displayed from the FE', '85979', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Exemptions - ADAP not exempted', '86074', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Training Flight''s charges not correctly applied', '86072', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Small Kenyan Aircraft charges wrongly applied', '86073', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Overflight with Invoice generated but status PENDING', '85697', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'SelfCare Portal - Query Submission doesn''t clear the from after the email is sent, when the user is logged in', '86076', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Aviation billing - reconcile and recalculate functions interfere with each other', '71956', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'SQL Exception inserting a new flight without Aircraft Registration Number', '86071', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Invoice''s different currencies not taken into account', '85985', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Aviation Billing - Preview button can''t be pressed if more than 1 account is selected', '86139', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Recalculation not Assigning the Account', '85796', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Penalties not handled', '84054', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Delta flight, MTOW wrongly taken into account for Landing/Parking charges', '84238', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'THRU flight plans with radar', '83696', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Report Generation Error due to "page_brake"', '86478', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Receipts displays the wrong value on the foreign currency column', '85679', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Transaction receipt wrong labels and values', '86088', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Air traffic statistics', '76087', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Revenue statistics', '76179', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Self care portal flight cost calculation null pointer error', '86502', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'If Rounding is selected (up or down) POS works incorrectly', '86098', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Self care portal flight cost calculation - minor issues', '86501', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Self-care portal - All form labels should be mixed case to match the rest of the application', '86500', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'Self care portal minor issues', '85430', release_category_id('bug'), '1.2.0';
    execute format(v_query) using 'POS Wrongly converting KES to KES with USD exchange rate', '86678', release_category_id('bug'), '1.2.0';

end $$;
