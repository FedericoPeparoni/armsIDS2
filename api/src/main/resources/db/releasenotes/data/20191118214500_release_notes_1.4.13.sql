do $$
declare
    v_query varchar;
begin

    -- remove existing release notes with release_version 1.4.13 if any
    -- when adding more release notes for this version, rename file and update below accordingly
    DELETE FROM release_notes WHERE release_version = '1.4.13';

    -- update existing release notes with same number to reopened and insert new release notes
    v_query := 'UPDATE release_notes SET reopened = true, updated_at = now(), updated_by = ''system'' WHERE number = $2;
                INSERT INTO release_notes (title, number, release_category_id, release_version) VALUES ($1,$2,$3,''1.4.13'');';

    -- bugs
    execute format(v_query) using 'Invoices show IDS logo and address and use CAAB flight listing', '115336', release_category_id('bug');
    execute format(v_query) using 'Cannot connect to spatia - no meaningful error provided', '115324', release_category_id('bug');
    execute format(v_query) using 'Enroute air navigation charges - download fails', '115340', release_category_id('bug');
    execute format(v_query) using 'Aerodromes - latitude format shows three places left of decimal', '115339', release_category_id('bug');
    execute format(v_query) using 'Airspaces - remove airspaces function does not work', '115338', release_category_id('bug');
    execute format(v_query) using 'Airspaces - text filter does not work', '115337', release_category_id('bug');
    execute format(v_query) using 'System configuration - saving changed configuration sets language to spanish if no default language is selected', '115327', release_category_id('bug');
    execute format(v_query) using 'Recalculate - recalculate failure - AerodromeOperationalHoursService.getExtendedAerodromeOperationalHours exception', '115302', release_category_id('bug');
    execute format(v_query) using 'Installation - default installation activates VTU currency and includes exchange rates (v.1.4.7)', '115326', release_category_id('bug');
    execute format(v_query) using 'Left menu with multiple scroll bars does not work properly after CHROME update', '115329', release_category_id('bug');
    execute format(v_query) using 'Tower logs - passenger count from tower log is used to populate international passenger counts on domestic flights', '115200', release_category_id('bug');
    execute format(v_query) using 'Reports - summarised invoice total report is not implemented correctly', '115020', release_category_id('bug');
    execute format(v_query) using 'Transactions - need to expose transaction adjustments charges interfaces to support SAP requirements', '114770', release_category_id('bug');
    execute format(v_query) using 'Multiple landing incorrectly discounted for A2LZD on 01/07/2019 @1210', '115066', release_category_id('bug');
    execute format(v_query) using 'Flight movement display - cesium map does not display', '115330', release_category_id('bug');
    execute format(v_query) using 'Point of sale - duplicate flights entered by user are not trapped/reported until invoice is generated', '115052', release_category_id('bug');
    execute format(v_query) using 'Point of sale - adhoc fees are not calculated', '115260', release_category_id('bug');
    execute format(v_query) using 'Point of sale - aviation invoices - calculate invoice - some fields are blank or incorrect', '114996', release_category_id('bug');
    execute format(v_query) using 'Point-of-sale - Flight movement cannot be created for another billing centre', '115216', release_category_id('bug');
    execute format(v_query) using 'Accounts - Inactive accounts may still be used in billing operations', '115213', release_category_id('bug');
    execute format(v_query) using 'Point of sale - flights from jkia/wilson to wajir are calculated incorrectly', '115050', release_category_id('bug');
    execute format(v_query) using 'Checks for unique account names should be case and punctuation blind', '115212', release_category_id('bug');
    execute format(v_query) using 'Unspecified locations - latitude and longitude labels are switched', '115214', release_category_id('bug');
    execute format(v_query) using 'Flight movements - arrival time is not updated when departure time or EET is changed', '115239', release_category_id('bug');
    execute format(v_query) using 'Invoices - display of transactions and transaction payments when an invoice is selected does not display inverse exchange rate when inverse is selected', '115000', release_category_id('bug');
    execute format(v_query) using 'Credit/debit notes - web service does not return invoice reference number', '115089', release_category_id('bug');
    execute format(v_query) using 'Flight movements - attempt to save flight results in "already exists" error', '115225', release_category_id('bug');

    -- changes
    execute format(v_query) using 'Add ANSP names TTCAA and CASAS under system configuration', '115328', release_category_id('change');
    execute format(v_query) using 'Reports - Revenue reports to be added', '115220', release_category_id('change');
    execute format(v_query) using 'Invoices - allow non-aviation point of sale published invoices to be voided', '115221', release_category_id('change');
    execute format(v_query) using 'Invoices - add billing centre id to invoices display', '115217', release_category_id('change');
    execute format(v_query) using 'Point of sale - allow "all accounts" to be the default setting for account selection', '115218', release_category_id('change');

end $$;
