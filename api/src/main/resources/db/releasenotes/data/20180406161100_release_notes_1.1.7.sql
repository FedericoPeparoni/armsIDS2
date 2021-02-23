do $$
declare
    v_query varchar;
begin

    -- update existing release notes with same number to reopened and insert new release notes
    v_query := 'UPDATE release_notes SET reopened = true, updated_at = now(), updated_by = ''system'' WHERE number = $2;
                INSERT INTO release_notes (title, number, release_category_id, release_version) VALUES ($1,$2,$3,$4);';

    -- feature requests
    execute format(v_query) using 'Add support for internationalisation - Spanish in particular', '79153', release_category_id('feature'), '1.1.7';

    -- bugs
    execute format(v_query) using 'Can''t pay "PUBLISHED" Invoice', '83052', release_category_id('bug'), '1.1.7';
    execute format(v_query) using 'Generate and Pay Invoice button not working', '83118', release_category_id('bug'), '1.1.7';
    execute format(v_query) using 'Can''t create/update a FligthMovement', '83001', release_category_id('bug'), '1.1.7';
    execute format(v_query) using 'Enroute Basis wrongly spelled', '82059', release_category_id('bug'), '1.1.7';
    execute format(v_query) using 'Flight Movement''s Text Filter not working when Account is missing', '81902', release_category_id('bug'), '1.1.7';
    execute format(v_query) using 'Half system in Spanish Half in English', '81785', release_category_id('bug'), '1.1.7';
    execute format(v_query) using 'Item 18 A/C Type can''t be modified', '82126', release_category_id('bug'), '1.1.7';
    execute format(v_query) using 'Unknown A/C Type is not taken into account', '83103', release_category_id('bug'), '1.1.7';
    execute format(v_query) using 'Duplicate flights are sometimes marked as having a missing flight preceding them', '82018', release_category_id('bug'), '1.1.7';
    execute format(v_query) using 'Enroute basis "sheduled" is mis-spelled', '75325', release_category_id('bug'), '1.1.7';
    execute format(v_query) using 'Flight movements page refresh resets duplicate flights and show actual location checkboxes', '82899', release_category_id('bug'), '1.1.7';
    execute format(v_query) using 'Flight movements filter does not work on actual departure/destination fields when they are displayed', '82231', release_category_id('bug'), '1.1.7';
    execute format(v_query) using 'Issues with displaying the actual departure/destination', '82019', release_category_id('bug'), '1.1.7';
    execute format(v_query) using 'Many duplicate flights with identical day-of-flight/departure-time/registration', '82017', release_category_id('bug'), '1.1.7';
    execute format(v_query) using 'Spatia arrival ad contains first stop on multi-point flight', '82230', release_category_id('bug'), '1.1.7';
    execute format(v_query) using 'The word "hello" appears on the welcome page', '82617', release_category_id('bug'), '1.1.7';
    execute format(v_query) using 'Translation issues - many column heads showing up in Spanish', '82289', release_category_id('bug'), '1.1.7';
    execute format(v_query) using 'Translation issues - system works in spanish by default', '82616', release_category_id('bug'), '1.1.7';
    execute format(v_query) using 'Upload from spatia is not working - cannot upload March 2018 data', '82212', release_category_id('bug'), '1.1.7';
    execute format(v_query) using 'Upload radar report ambiguous error "Missing information. Flight ID: {{flightIdentifier}}, date: {{date}}, dep. ad.: {{depAd}}, dest. ad.: {{destAd}}"', '82262', release_category_id('bug'), '1.1.7';
    execute format(v_query) using 'Upload radar reports ambigous error "Error found in the application: cannot perform the operation"', '82260', release_category_id('bug'), '1.1.7';
    execute format(v_query) using 'Upload reports "unknown error" if file is too large to upload', '82259', release_category_id('bug'), '1.1.7';

end $$;
