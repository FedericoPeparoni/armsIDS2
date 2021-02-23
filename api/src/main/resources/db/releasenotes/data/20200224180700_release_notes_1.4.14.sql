do $$
declare
    v_query varchar;
begin

    -- remove existing release notes with release_version 1.4.14 if any
    -- when adding more release notes for this version, rename file and update below accordingly
    DELETE FROM release_notes WHERE release_version = '1.4.14';

    -- update existing release notes with same number to reopened and insert new release notes
    v_query := 'UPDATE release_notes SET reopened = true, updated_at = now(), updated_by = ''system'' WHERE number = $2;
                INSERT INTO release_notes (title, number, release_category_id, release_version) VALUES ($1,$2,$3,''1.4.14'');';

    -- bugs
    execute format(v_query) using 'Flight movement builder - flights through Wajir - flights from HCBO (and other undefined aerodromes) are not included', '115357', release_category_id('bug');
    execute format(v_query) using 'Aviation billing - Invoice generation blocks all other processes from generating invoices', '115381', release_category_id('bug');
    execute format(v_query) using 'Invoice generation - users cannot add flight movements while invoiced are being generated', '115409', release_category_id('bug');
    execute format(v_query) using 'Flight movements - flights created from the user interface are getting an arrival aerodrome set as a lat/lon string', '115423', release_category_id('bug');
    execute format(v_query) using 'Point of sale - flights through wajir are calculated incorrectly', '115050', release_category_id('bug');
    execute format(v_query) using 'Flight movements - data grid and download file should include entry/exit points and flight level', '115480', release_category_id('bug');
    execute format(v_query) using 'Flight movements - allow 10 character flight ids', '115362', release_category_id('bug');
    execute format(v_query) using 'Transactions - exception the given id must not be null', '115370', release_category_id('bug');
    execute format(v_query) using 'Radar import - Leonardo flight strip route generation is incorrect', '115537', release_category_id('bug');
    execute format(v_query) using 'Statistics - overflights are omitted from air traffic statistics', '115539', release_category_id('bug');
    execute format(v_query) using 'Radar import - Manual/Automated upload of radar flight strips fails', '115536', release_category_id('bug');
    execute format(v_query) using 'TTCAA invoices show IDS logo and address and use CAAB flight listing', '115336', release_category_id('bug');
    execute format(v_query) using 'Flight movements - provide support for AFIL flights', '115177', release_category_id('bug');
    execute format(v_query) using 'Aviation billing - MTOWS on invoices are shown to 2 place of decimal', '115374', release_category_id('bug');
    execute format(v_query) using 'Tower logs - A2LAG Sept 21 - passenger count from tower log is used to populate international passenger counts on domestic flights', '115200', release_category_id('bug');
    execute format(v_query) using 'Service outages - outages not shown for an aerodrome near the bottom of the list', '115355', release_category_id('bug');
    execute format(v_query) using 'Flight movements for ATC logs are not being added', '115380', release_category_id('bug');
    execute format(v_query) using 'Flight movement builder - flights with incorrect flight level are not recalculated', '115479', release_category_id('bug');
    execute format(v_query) using 'Statistics - bars on bar charts are out of order', '115513', release_category_id('bug');
    execute format(v_query) using 'Navdb - add aerodromes', '115375', release_category_id('bug');
    execute format(v_query) using 'Aviation billing - invoice preview status messages are mixed up - see attachment', '115408', release_category_id('bug');
    execute format(v_query) using 'Invoice generation - invoices always show departure/destination aerodromes not entry/exit points', '115514', release_category_id('bug');
    execute format(v_query) using 'Adding flights from point-of-sale is still very slow and gets progressively slower with each flight added', '115425', release_category_id('bug');
    execute format(v_query) using 'Route parser - BNJEE waypoint is found but not used as the exit point', '115377', release_category_id('bug');
    execute format(v_query) using 'Navdb - broken routes', '115376', release_category_id('bug');
    execute format(v_query) using 'Flights from spatia - many errors for non-compliant flight levels', '115345', release_category_id('bug');
    execute format(v_query) using 'Flight movement builder - flight with null crossing distance and null enroute charges is marked as pending', '115354', release_category_id('bug');
    execute format(v_query) using 'Sending of emails by the web server is broken - null pointer exception', '115549', release_category_id('bug');
    execute format(v_query) using 'Invoices - when an invoice is created for an account with a credit the credit is not used to reduce the invoice amount', '115360', release_category_id('bug');
    execute format(v_query) using 'Aviation billing - IATA invoice runs onto a second sheet at 10,000 flights', '115551', release_category_id('bug');
    execute format(v_query) using 'Aaviation billing - concurrent invoice generation for same account generates an error', '115412', release_category_id('bug');

    -- changes
    execute format(v_query) using 'Aircraft type - add new aircraft types', '115350', release_category_id('change');
    execute format(v_query) using 'Flight movement - add system configuration to allow extraction of ICAO code from non-standard flight id', '115366', release_category_id('change');
    execute format(v_query) using 'Add support for Leonardo format flight strips', '115413', release_category_id('change');
    execute format(v_query) using 'Point of sale - add support for lump sum advance payments (proforma invoice)', '115026', release_category_id('change');

    -- feature
    execute format(v_query) using 'Exemptions – all. Allow exemptions to be set as a percentage, not as a boolean', '90207', release_category_id('feature');

end $$;
