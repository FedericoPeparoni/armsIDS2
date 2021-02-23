do $$
declare
    v_query varchar;
begin

    -- remove existing release notes with release_version 1.4.7 if any
    -- when adding more release notes for this version, rename file and update below accordingly
    DELETE FROM release_notes WHERE release_version = '1.4.7';

    -- update existing release notes with same number to reopened and insert new release notes
    v_query := 'UPDATE release_notes SET reopened = true, updated_at = now(), updated_by = ''system'' WHERE number = $2;
                INSERT INTO release_notes (title, number, release_category_id, release_version) VALUES ($1,$2,$3,''1.4.7'');';

    -- bugs
    execute format(v_query) using 'System configuration "aircraft" is mis-spelled', '101742', release_category_id('bug');
    execute format(v_query) using 'Clicking home on the map viewer zooms to Botswana', '90124', release_category_id('bug');
    execute format(v_query) using 'Zero length track is being reported erroneously', '101680', release_category_id('bug');
    execute format(v_query) using 'aviation billing - recalculate does not correctly resolve account for 5YSLD and 5YSLK (safarilink)', '102010', release_category_id('bug');
    execute format(v_query) using 'point of sale - non-aviation invoice - add aircraft registration should not be accessible from this form', '89973', release_category_id('bug');
    execute format(v_query) using 'recalculate exceptions - stale state exceptions', '101958', release_category_id('bug');
    execute format(v_query) using 'Incomplete flight reason is missing on UI', '101749', release_category_id('bug');
    execute format(v_query) using 'Falkland islands should not be included in Argentina FIRs', '101110', release_category_id('bug');
    execute format(v_query) using 'Flights from New Zealand have incorrect routes', '101111', release_category_id('bug');
    execute format(v_query) using 'PostGIS V_FlightMovementOutside_SP and V_FlightMovementInside_SP IOException', '100852', release_category_id('bug');
    execute format(v_query) using 'Can''t generate Invocies due to missing exchange rate not reported', '102012', release_category_id('bug');
    execute format(v_query) using 'Within receipt document, "dated" field should contain the payment date, not the current date', '102351', release_category_id('bug');
    execute format(v_query) using 'updating a flight movement clears the radar track geometry', '101390', release_category_id('bug');

    -- changes
    execute format(v_query) using 'Menu changes for recurring charges and flight reassignment', '102020', release_category_id('change');
    execute format(v_query) using 'International Flights Minimum 200 km Charge and Cumulative Distances', '101332', release_category_id('change');

    -- features
    execute format(v_query) using 'Item 6 Parameterization of Interest Amounts', '100982', release_category_id('feature');
    execute format(v_query) using 'Item 4 Issuance of a Financial Invoice', '100980', release_category_id('feature');
    execute format(v_query) using 'Item 10 - Support for Indra Radar', '100976', release_category_id('feature');

end $$;
