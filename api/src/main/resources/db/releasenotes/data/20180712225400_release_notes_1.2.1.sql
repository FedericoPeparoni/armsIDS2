do $$
declare
    v_query varchar;
begin

    -- remove existing release notes with release_version 1.2.1 if any
    -- when adding more release notes for this version, rename file and update below accordingly
    DELETE FROM release_notes WHERE release_version = '1.2.1';

    -- update existing release notes with same number to reopened and insert new release notes
    v_query := 'UPDATE release_notes SET reopened = true, updated_at = now(), updated_by = ''system'' WHERE number = $2;
                INSERT INTO release_notes (title, number, release_category_id, release_version) VALUES ($1,$2,$3,$4);';

    -- change requests
    execute format(v_query) using 'Enhancements to system backup', '70123', release_category_id('change'), '1.2.1';
    execute format(v_query) using 'Add new format for data entry and display of aerodrome locations ddmmss.ssS dddmmss.ssW', '83756', release_category_id('change'), '1.2.1';
    execute format(v_query) using 'For all file uploads, indicate the required file type beside the upload button', '83700', release_category_id('change'), '1.2.1';

    -- bugs
    execute format(v_query) using 'IATA and NON-IATA invoices can''t be generated', '86693', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'Appoving an invoice, the system ignores the sorting order set before.', '86841', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'Approving invoices not on the first page, redirect the user to the first page.', '86840', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'Invoice pagination not properly working', '86931', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'Parking Charges not displayed in the Parking Charge Schedules', '86028', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'Zero Lenght Password not working', '87153', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'Delta Flights, wrong Arrival Ad on the invoice', '83749', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'Cannot create credit transaction', '87172', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'Training Flight Cost not correctly applied', '87261', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'Accounts - Exception when trying to create an account using only mandatory attributes', '87168', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'Users password buttons', '81975', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'Crosschecks fail', '87243', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'Problems with account statements', '78304', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'Passenger Charge Upload fail', '87242', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'Flight movement form - clear is disabled', '87161', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'Nominal Route gives a Generic Error', '87245', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'Fligth Cost Calculation - ADAP Applied on Arrival when set on Departure', '87282', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'Account''s Credit not printed on the invoice', '87240', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'PSQLException: FATAL: could not open file "base/226120/12676"', '87356', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'Air Traffic Stats', '87234', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'Point of sale invoices - clicking clear clears type and description controls', '87157', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'Accounts - non-mandatory fields become mandatory following an insert error', '87169', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'Unexpected flight showing 00:00:00Z as time', '87228', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'Invoices not rounded to even KES/USD', '87155', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'Invoice decimal places wrongly displayed', '87156', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'Password policy''s message errors not correct', '87226', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'Non-aviation billing – no line items shown, but the invoice total shows charges', '87452', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'Manually entering a passenger service charge return does not update the passenger counts in the flight movement', '87453', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'Cannot modify flight movement created from ATC log or tower logs or radar summaries', '87444', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'Cannot create a credit note for 400,000 KES', '87447', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'Flight schedule upload does not work correctly', '87443', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'Enroute charges are not converted from USD to KES when generating an invoice in KES', '87445', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'Invoice totals must be rounded up to the next KES/USD - both amounts must be shown on invoice', '83759', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'BAW non-IATA invoices show two enroute charges (276.00 and 3.00) for each flight', '87449', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'Amount should be written on receipt in full text along with currency code (not symbol)', '87796', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'All file upload interfaces should indicate the type of file being uploaded', '87786', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'Allow user to add passenger manifest image during passenger service charge creation', '87787', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'Nominal routes – sorting and filtering only work on the current page', '87455', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'The error generated on password length should contain the number of required characters', '87793', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'Filtering and sorting on critical forms', '87780', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'EUROCAT default file type is .PRINT', '87779', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'Regional overflight – entering a flight 5YQQQ HTDA-HSSA fails', '87785', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'Filtering by page only must be fixed to filter entire dataset for aircraft types, and registration numbers', '83754', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'User cannot apply a cash payment to an account which has no outstanding invoices', '87450', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'SelfCare Portal, nullPointerException trying to calculate Thru Fligth Plan costs', '87669', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'Display total records and filltered records on critical datagrids', '87791', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'Display inverse of exchange rate to USD based on system configuration', '87783', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'Template modifications', '87782', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'Date format used for data entry must be based on system configuration', '70119', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'Support system defined number of rows on critical data grids', '87789', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'Additional flight movement status filters', '87798', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'Soft delete of flight movements and reason for deletion', '87805', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'Non-admin user cannot change his own password', '87784', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'POS, General Sale, Invoice problems', '87648', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'User cannot apply a cash payment to an account which has no outstanding invoices', '87454', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'Drop unneeded flight_movement column flight_category', '86785', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'Account credit applied to invoice results in a negative amount payable shown on the invoice', '87451', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'Flight movement - text filter cannot filter for "scheduled"', '84259', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'Flight movements - selecting sort by date sometimes clears the data grid when filtering by text', '83752', release_category_id('bug'), '1.2.1';
    execute format(v_query) using 'Flight movement export problems (3 items)', '83978', release_category_id('bug'), '1.2.1';

end $$;
