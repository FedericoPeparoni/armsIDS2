do $$
declare
    v_query varchar;
begin

    -- remove existing release notes with release_version 1.2.5 if any
    -- when adding more release notes for this version, rename file and update below accordingly
    DELETE FROM release_notes WHERE release_version = '1.2.5';

    -- update existing release notes with same number to reopened and insert new release notes
    v_query := 'UPDATE release_notes SET reopened = true, updated_at = now(), updated_by = ''system'' WHERE number = $2;
                INSERT INTO release_notes (title, number, release_category_id, release_version) VALUES ($1,$2,$3,''1.2.5'');';

    -- bugs
    execute format(v_query) using 'Selfcare users form - login id should be read-only for updates', '88951', release_category_id('bug');
    execute format(v_query) using 'Selfcare login errors after lost password recovery', '88950', release_category_id('bug');
    execute format(v_query) using 'installer installs a complete botswana database including flight, radar, atc logs etc', '77122', release_category_id('bug');
    execute format(v_query) using 'New INFO - System Summary''s data and percentage', '64966', release_category_id('bug');
    execute format(v_query) using 'Add [zmodem] protocol support to automated uploads along with TCP/IP system configuration values', '77704', release_category_id('bug');
    execute format(v_query) using 'Update from Web button not working', '79730', release_category_id('bug');
    execute format(v_query) using 'Nominal routes should not be added automatically when distances are calculated', '89078', release_category_id('bug');
    execute format(v_query) using 'Delta Flight no Aerodrome charges, and wrong arrival time set', '85407', release_category_id('bug');
    execute format(v_query) using 'Invoice for Safari air 2018-04-04. Invalid landing charge of 12.50', '84541', release_category_id('bug');
    execute format(v_query) using 'Report generation issues', '72732', release_category_id('bug');
    execute format(v_query) using 'International flight can not be delta', '69331', release_category_id('bug');
    execute format(v_query) using 'filtering, sorting and record counts for unknown locations and unknown aircraft types', '89072', release_category_id('bug');
    execute format(v_query) using 'Recalculate function does not work', '89248', release_category_id('bug');
    execute format(v_query) using 'Calculation of aerodrome fees exemptions for charter flights', '84293', release_category_id('bug');
    execute format(v_query) using 'Age Analysis Report should flow from 30, to 60, to 90, to 120 to 120+', '84295', release_category_id('bug');
    execute format(v_query) using 'Charge schedule uploads report generic error "Error: File not valid"', '89329', release_category_id('bug');
    execute format(v_query) using 'non-iata invoice for 2018-April Moremi Air has incorrect route text on delta ZZZZ departures', '89326', release_category_id('bug');
    execute format(v_query) using 'Non-iata invoices with ZZZZ dep/dest should show actual location for dep/dest not ZZZZ', '89508', release_category_id('bug');
    execute format(v_query) using 'The system charges parking per day while CAAB fees regulations says 24hrs', '84285', release_category_id('bug');
    execute format(v_query) using 'Parking Charges not applied on 2 flights', '87082', release_category_id('bug');
    execute format(v_query) using 'Aircraft registration tracking report - UI is not usable', '87446', release_category_id('bug');
    execute format(v_query) using 'Aviation invoices are including passenger charges in the total', '89578', release_category_id('bug');
    execute format(v_query) using 'Clerk name and receipt number are mandatory for cash receipts', '87794', release_category_id('bug');
    execute format(v_query) using 'Suspicious numbers in Revenue stats', '86846', release_category_id('bug');
    execute format(v_query) using 'Flight movement rejected due to exception', '89065', release_category_id('bug');
    execute format(v_query) using 'Thru Flight''s Nominal Route not handled as expected', '88874', release_category_id('bug');
    execute format(v_query) using 'Password recovery interface displays the message "null"', '89583', release_category_id('bug');
    execute format(v_query) using 'Non-IATA invoice contains incorrect entry/exit points', '88980', release_category_id('bug');
    execute format(v_query) using 'Many exceptions related to non-aviation invoice generation', '89328', release_category_id('bug');
    execute format(v_query) using 'Non-aviation billing engine should not show categories for IATA and ENROUTE', '89629', release_category_id('bug');
    execute format(v_query) using 'Non-aviation billing engine should not generate/preview empty invoices', '89631', release_category_id('bug');
    execute format(v_query) using 'Aviation invoicing - "Error: Generic error Description: Null"', '89664', release_category_id('bug');
    execute format(v_query) using 'Non-iata invoices for delta flights "route" column should include all delta stop points', '89507', release_category_id('bug');

    -- change
    execute format(v_query) using 'Non-Aviation invoice generation should be done - i.e. billing period in the future', '84294', release_category_id('change');


    -- feature
    execute format(v_query) using 'Database creation and upgrade management', '65111', release_category_id('feature');
    execute format(v_query) using 'Failover', '58334', release_category_id('feature');
    execute format(v_query) using 'Report Generation', '58358', release_category_id('feature');
    execute format(v_query) using 'User interface - user should be notified if obsolete version of browser is being used', '63508', release_category_id('feature');


end $$;
