do $$
declare
    v_query varchar;
begin

    -- remove existing release notes with release_version 1.4.8 if any
    -- when adding more release notes for this version, rename file and update below accordingly
    DELETE FROM release_notes WHERE release_version = '1.4.8';

    -- update existing release notes with same number to reopened and insert new release notes
    v_query := 'UPDATE release_notes SET reopened = true, updated_at = now(), updated_by = ''system'' WHERE number = $2;
                INSERT INTO release_notes (title, number, release_category_id, release_version) VALUES ($1,$2,$3,''1.4.8'');';

    -- bugs
    execute format(v_query) using 'Enroute charges interface - MTOW is always interpreted as TONS on input', '103235', release_category_id('bug');
    execute format(v_query) using 'Currencies - For defining interest rates target currency should not be user- settable - it is that for the selected currency', '102146', release_category_id('bug');
    execute format(v_query) using 'RouteParser - projection errors', '102656', release_category_id('bug');
    execute format(v_query) using 'Provide support for rounding flight leg and flight total to a specified number of decimal places', '101433', release_category_id('bug');
    execute format(v_query) using 'Zero total amount flight movements'' status is always PENDING', '101240', release_category_id('bug');

    -- changes
    execute format(v_query) using 'Extended Hours of Service Surcharge', '101127', release_category_id('change');
    execute format(v_query) using 'International Flights Minimum 200 km Charge and Cumulative Distances', '101332', release_category_id('change');

end $$;
