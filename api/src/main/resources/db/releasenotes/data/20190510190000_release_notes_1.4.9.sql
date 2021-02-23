do $$
declare
    v_query varchar;
begin

    -- remove existing release notes with release_version 1.4.9 if any
    -- when adding more release notes for this version, rename file and update below accordingly
    DELETE FROM release_notes WHERE release_version = '1.4.9';

    -- update existing release notes with same number to reopened and insert new release notes
    v_query := 'UPDATE release_notes SET reopened = true, updated_at = now(), updated_by = ''system'' WHERE number = $2;
                INSERT INTO release_notes (title, number, release_category_id, release_version) VALUES ($1,$2,$3,''1.4.9'');';

    -- bugs
    execute format(v_query) using 'Delta flight calculation always calculates adap on arrival, ignoring the system setting for this', '103312', release_category_id('bug');
    execute format(v_query) using 'Flight movements - locate missing/duplicate flights displays deleted/cancelled flights', '101615', release_category_id('bug');
    execute format(v_query) using 'Flight movements editing - errors in key fields which are read-only are not indicated', '103309', release_category_id('bug');
    execute format(v_query) using 'Flight movement data entry from - parking hours set to 0 is mishandled', '101504', release_category_id('bug');
    execute format(v_query) using 'API runs out of memory', '103518', release_category_id('bug');
    execute format(v_query) using 'Air navigation currencies incorrect after update', '102219', release_category_id('bug');
    execute format(v_query) using 'Tower log - when dep/dest is not an aerodrome it should be added to item 18 and the dep/dest should be set to ZZZZ', '103308', release_category_id('bug');
    execute format(v_query) using 'Implementation of tower log does not match technical specification and does not work', '102478', release_category_id('bug');
    execute format(v_query) using 'Flight Movement not created from Radar Summary', '103945', release_category_id('bug');
    execute format(v_query) using 'Currencies management - list currencies in reverse chronological order (latest at top)', '101331', release_category_id('bug');
    execute format(v_query) using 'Mishandled route SAAR-MPTO - zigzag at IQT - route UM776 duplicated between changing speed/flight level', '103944', release_category_id('bug');
    execute format(v_query) using 'QA SERVER - Impossible to create a new Aircraft Registration record from the UI', '101750', release_category_id('bug');
    execute format(v_query) using 'Update NAVDB', '101330', release_category_id('bug');
    execute format(v_query) using 'Aviation billing - weekly invoicing appears to use the wrong dates for selecting flights', '104620', release_category_id('bug');
    execute format(v_query) using 'THRU flights with ZZZZ result in incorrect route calculation (caused by extraneous DCT, not ZZZZ)', '101905', release_category_id('bug');
    execute format(v_query) using 'Point of sale - aviation - back end hangs on flight movement creation (not reproducible)', '102408', release_category_id('bug');
    execute format(v_query) using 'Tower log - if flight is not indicated as SCH, then set default to NONSCH, set passengers to 0 if null', '104483', release_category_id('bug');
    execute format(v_query) using 'Passenger service charge returns - number of domestic and international passengers is not used to update flight movements', '104617', release_category_id('bug');
    execute format(v_query) using 'Where ZZZZ is used in the departure/destination endpoint used to determine the billing centre, use the other endpoint', '104798', release_category_id('bug');
    execute format(v_query) using 'Aviation billing - flight movements with non-null enroute, passenger and other invoice ids are still marked as PENDING', '104792', release_category_id('bug');
    execute format(v_query) using 'Missing navdb aerodromes', '101774', release_category_id('bug');
    execute format(v_query) using 'Do not use destination aerodrome in determining flight uniqueness', '104618', release_category_id('bug');
    execute format(v_query) using 'Radar import - SQL error ERROR: operator does not exist: character varying = bytea', '104766', release_category_id('bug');
    execute format(v_query) using 'Better detection of duplicate radar when THRU PLAN is A-B-C-A and radar segment C-A or D-A', '102156', release_category_id('bug');
    execute format(v_query) using 'Privileges - aviation billing - having any aviation billing privilege grants access to all functions', '102018', release_category_id('bug');
    execute format(v_query) using 'Privileges - there is no privilege for pending transactions', '101961', release_category_id('bug');
    execute format(v_query) using 'Privileges - approval workflow privileges do not work properly', '101960', release_category_id('bug');
    execute format(v_query) using 'Privileges - flight movements privileges are misnamed', '101959', release_category_id('bug');
    execute format(v_query) using 'Privileges - possibly unused privileges', '101963', release_category_id('bug');
    execute format(v_query) using 'Privileges - read-only forms with upload capabilities', '101965', release_category_id('bug');
    execute format(v_query) using 'Privileges - data grids with hyperlink columns to other interfaces', '101966', release_category_id('bug');
    execute format(v_query) using 'Privileges - two sets of privileges for aircraft registrations', '102358', release_category_id('bug');
    execute format(v_query) using 'Privileges - point of sale buttons (accounts, aircraft registration)', '101964', release_category_id('bug');
    execute format(v_query) using 'Privileges - rename user_profile_view and user_profile_modify to group_view and group_modify', '104452', release_category_id('bug');
    execute format(v_query) using 'Unspecified destinations/departures - country code is not being set in system generated rows', '105476', release_category_id('bug');
    execute format(v_query) using 'Unspecified departure/destination - user cannot delete a record', '105477', release_category_id('bug');
    execute format(v_query) using 'Issues with Flight Movement Select checkbox', '95253', release_category_id('bug');
    execute format(v_query) using 'Recalculate fails - mtow null pointer on aircraft type zzzz', '101780', release_category_id('bug');
    execute format(v_query) using 'Flight movements - Invalid warning issued when "recalculate"" is invoked', '101735', release_category_id('bug');
    execute format(v_query) using 'Cannot update or create flight movement Error: generic error Description: null', '101748', release_category_id('bug');
    execute format(v_query) using 'Flight movements - FURH columns should be INAC specific - resolution errors are displayed in the wrong column', '101734', release_category_id('bug');
    execute format(v_query) using 'Aerodromes in domestic aerodrome tables are considered to be invalid dep/dest if they are not also defined in AIX/M', '101773', release_category_id('bug');
    execute format(v_query) using 'Flight movements is very slow - unfiltered data is being returned', '101745', release_category_id('bug');
    execute format(v_query) using 'Small aircraft missing AOC resolution error reported incorrectly', '101743', release_category_id('bug');
    execute format(v_query) using 'Credit Transactions - Exchange Rate To USD', '101899', release_category_id('bug');
    execute format(v_query) using 'Flights created in point-of-sale and then paid for do not show up in CRONOS', '101936', release_category_id('bug');
    execute format(v_query) using 'Bug in radar summary loader - SQLGrammarException', '101736', release_category_id('bug');
    execute format(v_query) using 'Point Of Sale Invoice Generation - create aviation invoice', '101871', release_category_id('bug');
    execute format(v_query) using 'Rejects - many valid flight movements and radar summaries with the error "General Error" "Error found in the application"', '101781', release_category_id('bug');
    execute format(v_query) using 'Recalculate fails - reconcileFlightMovementById null pointer exception', '101746', release_category_id('bug');
    execute format(v_query) using 'Users - error updating users - record cannot be inserted because it already exists', '102352', release_category_id('bug');
    execute format(v_query) using 'Transactions - Create button is disabled for cash payment when payment ref number is blank but read-only', '102188', release_category_id('bug');
    execute format(v_query) using 'Transactions - during transaction creation, browser hangs when firefox is used', '102354', release_category_id('bug');
    execute format(v_query) using 'Flight movements - filtering and multiselect has problems', '102152', release_category_id('bug');
    execute format(v_query) using 'Accounts - cash account selection locks credit limit but does not set it to zero first', '102357', release_category_id('bug');
    execute format(v_query) using 'Radar loading - valid records are being discarded - no info is stored in rejects', '102167', release_category_id('bug');
    execute format(v_query) using 'Flight category ''OTHER'' flights marked as incomplete - zero length billable track', '101874', release_category_id('bug');
    execute format(v_query) using 'Allow override for zero length billable track if it is not required for invoicing', '90104', release_category_id('bug');
    execute format(v_query) using 'Errors in adding registration when the aircraft type is detected from registration number and existing flight movements', '102009', release_category_id('bug');
    execute format(v_query) using 'Transactions - create payment - exception "failed to convert string "null"" (not reproducible)', '102171', release_category_id('bug');
    execute format(v_query) using 'Point of sale - aviation - after adding an aircraft registration, control does not return to point of sale / aviation', '102359', release_category_id('bug');
    execute format(v_query) using 'Point of sale - calculate invoice - "Landing Charges" is used instead of "ADAP"', '102353', release_category_id('bug');
    execute format(v_query) using 'Point of sale - aviation - flight data entry form should not contain radar route or flight notes fields', '102019', release_category_id('bug');
    execute format(v_query) using 'THRU flights - multiple landing charges (at intermediate aerodrome) are not charged', '101955', release_category_id('bug');
    execute format(v_query) using 'Transactions - when adding an adjustment, the correct label (ADAP) should be used instead of "landing charges"', '102419', release_category_id('bug');
    execute format(v_query) using 'Point of sale - entry and exit points for first flight are reversed on calculated invoice', '101954', release_category_id('bug');
    execute format(v_query) using 'Point of sale - aviation - item18 DEST/ is not handled correctly', '102362', release_category_id('bug');
    execute format(v_query) using 'Pending transactions – transaction approval level filter does not work', '102411', release_category_id('bug');
    execute format(v_query) using 'Cannot generate an invoice from flight movements: Error: Generic error Description: null', '105782', release_category_id('bug');
    execute format(v_query) using 'Credit/debit transactions description is overwritten with invoice number', '105781', release_category_id('bug');
    execute format(v_query) using 'Point of sale - invoice numbers increment by 2', '101941', release_category_id('bug');
    execute format(v_query) using 'Point of sale - Invoice total is calculated correctly with account credit but account credit is not shown', '102413', release_category_id('bug');
    execute format(v_query) using 'Point of sale aviation billing does not show any flights to be billed', '105783', release_category_id('bug');
    execute format(v_query) using 'Pending transactions - exported column', '102021', release_category_id('bug');
    execute format(v_query) using 'Flightmovement route field - should not be mandatory for radar - only flight movement', '101841', release_category_id('bug');
    execute format(v_query) using 'Flight movements - Provide support for user entry of item 18 OPR', '105279', release_category_id('bug');
    execute format(v_query) using 'PDF previews should use the entire screen width available', '101606', release_category_id('bug');
    execute format(v_query) using 'Flight movements - when filters are applied, sorting of flights by day-of-flight, departure-time is not always retained', '102154', release_category_id('bug');
    execute format(v_query) using 'Radar loading -Aircraft registrations from radar sometimes include invalid characters including periods, commas and dashes', '102148', release_category_id('bug');
    execute format(v_query) using 'Radar loading - radar is rejected because of missing FIR entry/exit information but it is specified', '102186', release_category_id('bug');
    execute format(v_query) using 'Radar loading - HCCM - MAV - HAAB generates a zero length flight track', '102159', release_category_id('bug');
    execute format(v_query) using 'User "Administrator" doesn''t have a name', '105835', release_category_id('bug');
    execute format(v_query) using 'Check for small Kenyan aircraft is not working', '101943', release_category_id('bug');
    execute format(v_query) using 'Small aircraft use local table for KCAA', '101767', release_category_id('bug');
    execute format(v_query) using 'Flight movements - cannot update/save flight movement after "duplicate flight movement" error', '105788', release_category_id('bug');
    execute format(v_query) using 'Flight movements - Need to display/edit item18_opr from “other_info” on the flight plan editing form', '102149', release_category_id('bug');
    execute format(v_query) using 'Incorrect privelege name for countries', '105883', release_category_id('bug');
    execute format(v_query) using 'Unused privileges - remote_updates_xxxx', '105884', release_category_id('bug');
    execute format(v_query) using 'Flight from spatia - THRU flight plan generates null pointer exception - Point.setCoordinates method cannot accept null', '102181', release_category_id('bug');
    execute format(v_query) using 'Flight movement builder - Circular flight FBSK-FBSK with valid route specified is billed based on eet and speed', '105871', release_category_id('bug');

    -- changes
    execute format(v_query) using 'Passenger counts from tower log departures are not updated in flight movement', '104441', release_category_id('change');
    execute format(v_query) using 'Privileges - add new privileges for invoices_approve, invoices_publish, invoices_void, and transaction_adjustment_create', '101962', release_category_id('change');
    execute format(v_query) using 'Accounts - reasonable defaults should be set where they can be set', '102005', release_category_id('change');
    execute format(v_query) using 'Radar loading - Eurocat radar file format has changed', '103836', release_category_id('change');
    execute format(v_query) using 'International overflights are billed based on day-of-flight not contact-time', '102158', release_category_id('change');
    execute format(v_query) using 'Modify zzzz location parsing to accept N/S before location as well as after', '102182', release_category_id('change');
    execute format(v_query) using 'Invoices - need a filter for voided invoices', '102414', release_category_id('change');
    execute format(v_query) using 'Add filters to invoices (by user) (by flight id/registration number)', '102187', release_category_id('change');
    execute format(v_query) using 'Allow user to create a payment transaction from the invoices form by selecting "Pay Invoice"', '102189', release_category_id('change');
    execute format(v_query) using 'All datagrids with documents - move the document download icon to the leftmost column in the data grid', '102356', release_category_id('change');
    execute format(v_query) using 'User cannot undelete a deleted flight movement', '105170', release_category_id('change');
    execute format(v_query) using 'Pending transactions - need to record the name of approver and approval date/time', '102412', release_category_id('change');

end $$;
