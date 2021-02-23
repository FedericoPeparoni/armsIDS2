do $$
declare
    v_query varchar;
begin

    -- remove existing release notes with release_version 1.4.10 if any
    -- when adding more release notes for this version, rename file and update below accordingly
    DELETE FROM release_notes WHERE release_version = '1.4.10';

    -- update existing release notes with same number to reopened and insert new release notes
    v_query := 'UPDATE release_notes SET reopened = true, updated_at = now(), updated_by = ''system'' WHERE number = $2;
                INSERT INTO release_notes (title, number, release_category_id, release_version) VALUES ($1,$2,$3,''1.4.10'');';

    -- bugs
    execute format(v_query) using 'Interest calculations when multiple payments are made on an invoice are correct, but not what EANA wants', '105784', release_category_id('bug');
    execute format(v_query) using 'Indra radar upload does not include file type (.csv) in caption', '104947', release_category_id('bug');
    execute format(v_query) using 'Flight Reassignment end date not taken into account', '98599', release_category_id('bug');
    execute format(v_query) using 'Aviation billing - validate by a selected flight category displays flights from other categories', '104619', release_category_id('bug');
    execute format(v_query) using 'Aviation invoice departure time is showing actual departure time', '106093', release_category_id('bug');
    execute format(v_query) using 'Use latest revision of ARMS user manual with the web app', '105880', release_category_id('bug');
    execute format(v_query) using 'Selfcare portal translation and "self care portal" titles', '105292', release_category_id('bug');
    execute format(v_query) using 'Scheduled route is being charged instead of nominal route for OMBD-FAOR via ITMAR EVARU IMKAK NIDED ELAVA', '107172', release_category_id('bug');
    execute format(v_query) using 'Delta flights not taking into account minimum billable distance per flight leg', '106135', release_category_id('bug');
    execute format(v_query) using 'Passenger charge is not recalculated when count cleared and count is truncated on invoices', '106132', release_category_id('bug');
    execute format(v_query) using 'Installation bugs to be fixed prior to CAAB update', '101741', release_category_id('bug');
    execute format(v_query) using 'AIX/M - missing international aerodromes', '103148', release_category_id('bug');
    execute format(v_query) using 'AIX/M - sobto vi ut252 does not exit at esres', '103149', release_category_id('bug');
    execute format(v_query) using 'Many tower and ATC log records which appear to be valid do not have corresponding flight movements', '107166', release_category_id('bug');
    execute format(v_query) using 'THRU plan graphics show billed but no plan/radar data', '107329', release_category_id('bug');
    execute format(v_query) using 'Invoice generation - invoices show 3 places of decimal for USD', '103243', release_category_id('bug');
    execute format(v_query) using 'Waypoint Z included in flight graphics', '107542', release_category_id('bug');
    execute format(v_query) using 'Invoice display form initial query is very slow', '107330', release_category_id('bug');
    execute format(v_query) using 'Point of Sale - Accounts list takes a long time to load', '100968', release_category_id('bug');
    execute format(v_query) using 'Aerodrome - lat/long - the error indicates the text entered does not match the format but does not indicate what the format is', '101679', release_category_id('bug');
    execute format(v_query) using 'Aircraft types - add to aircraft types database', '101779', release_category_id('bug');
    execute format(v_query) using 'THRU flights - multiple landing charges (at intermediate aerodrome) are not charged', '101955', release_category_id('bug');
    execute format(v_query) using 'Flight movements mapped to incorrect accounts by ICAO code', '102630', release_category_id('bug');
    execute format(v_query) using 'Spatia - Flight which is cancelled, then a new flight plan is submitted does not get a flight movement created', '104493', release_category_id('bug');
    execute format(v_query) using 'Transactions - invoices show in transaction interface for selection by the user should be in reverse cronological order (newest at the top)', '105997', release_category_id('bug');
    execute format(v_query) using 'FBCO flights handled incorrectly - appears to be located in Chad', '106001', release_category_id('bug');
    execute format(v_query) using 'Enroute basis is not set for flights shorter than the exempt minimum', '106005', release_category_id('bug');
    execute format(v_query) using 'Transactions - credit / debit note creation – the description entered by the user is overwritten with the invoice number', '106263', release_category_id('bug');
    execute format(v_query) using 'Aerodromes - data entry interface needs to prevent international airports from being entered', '106281', release_category_id('bug');
    execute format(v_query) using 'Aviation invoice for cash account created from flight movements cannot be paid', '106283', release_category_id('bug');
    execute format(v_query) using 'When existing flight movement has flight level "VFR" update button is not enabled but no error message is shown', '106285', release_category_id('bug');
    execute format(v_query) using 'Service charge catalogue - data grid filter only works on current page', '106286', release_category_id('bug');
    execute format(v_query) using 'Delta flights are not calculated correctly if MTOW is above 5700kg', '107163', release_category_id('bug');
    execute format(v_query) using 'Flights are not being billed HKLK-HJJJJ', '107171', release_category_id('bug');
    execute format(v_query) using 'Null pointer exception uploading from spatia results in reject', '107175', release_category_id('bug');
    execute format(v_query) using 'Bulk recalculation is not working as expected', '107317', release_category_id('bug');
    execute format(v_query) using 'Point of sale - data entry form does not require EET to be entered but calculate invoice then generates an error', '107332', release_category_id('bug');
    execute format(v_query) using 'THRU flight plans do not have FPL graphics created', '107333', release_category_id('bug');
    execute format(v_query) using 'Local aircraft registry - updated records generate duplicate primary key error instead of updating existing record', '107335', release_category_id('bug');
    execute format(v_query) using 'Transactions - replace "local" with "invoice"', '107371', release_category_id('bug');
    execute format(v_query) using 'Matching tower log to delta/thru plans between dep/arr time is incorrectly applied to all flights', '107385', release_category_id('bug');
    execute format(v_query) using 'SQL error when registration number is not set', '107534', release_category_id('bug');
    execute format(v_query) using 'Incorrect crossing distance used', '107545', release_category_id('bug');
    execute format(v_query) using 'Cesium no longer renders in Google Chrome v75.0 when selecting a flight movement or airspace', '107640', release_category_id('bug');

    -- changes
    execute format(v_query) using 'Invoices - exclude unapproved and unpublished filter options based on workflow enabled', '105245', release_category_id('change');
    execute format(v_query) using 'International Flights Minimum 200 km Charge and Cumulative Distances', '101332', release_category_id('change');
    execute format(v_query) using 'Calculate billing date for overnight flights', '106097', release_category_id('change');
    execute format(v_query) using 'Remove FURH logic added in 94622 - CR - INAC OCT2018 - Support for FURH charges', '103372', release_category_id('change');
    execute format(v_query) using 'Add an account filter to aviation billing based on account type', '103599', release_category_id('change');
    execute format(v_query) using 'Transactions - interest only invoice should contain the number of days over due and original invoice amount', '104621', release_category_id('change');
    execute format(v_query) using 'Allow user to create a payment transaction from the invoices form by selecting "Pay Invoice"', '102189', release_category_id('change');
    execute format(v_query) using 'International arrivals with an enroute distance of zero', '101671', release_category_id('change');
    execute format(v_query) using 'Invoices - Add functionality to allow user to void a CASH invoice', '102415', release_category_id('change');
    execute format(v_query) using 'Transactions - add invoice number to columns displayed on page two of transaction creation (select invoice)', '105420', release_category_id('change');
    execute format(v_query) using 'Flight movement builder - if a leg of a THRU plan is a repositioning flight it should not be billed', '107174', release_category_id('change');
    execute format(v_query) using 'Passenger service charge return - add format button showing file format', '107372', release_category_id('change');

    -- features
    execute format(v_query) using 'Item 14 – Payment Management', '100702', release_category_id('feature');
    execute format(v_query) using 'Item 10 - Support for Indra Radar', '100976', release_category_id('feature');
    execute format(v_query) using 'Update user guide', '101535', release_category_id('feature');

end $$;
