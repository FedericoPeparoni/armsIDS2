do $$
declare
    v_query varchar;
begin

    -- update existing release notes with same number to reopened and insert new release notes
    v_query := 'UPDATE release_notes SET reopened = true, updated_at = now(), updated_by = ''system'' WHERE number = $2;
                INSERT INTO release_notes (title, number, release_category_id, release_version) VALUES ($1,$2,$3,$4);';

    -- change requests
    execute format(v_query) using 'Make coordinates configurable for aerodromes', '70100', release_category_id('change'), '1.1.4';
    execute format(v_query) using 'Make coordinates configurable for unspecified aerodromes', '70101', release_category_id('change'), '1.1.4';
    execute format(v_query) using 'Allow for non mandatory parking time', '70102', release_category_id('change'), '1.1.4';
    execute format(v_query) using 'Hide references to parking time on UI', '70103', release_category_id('change'), '1.1.4';
    execute format(v_query) using 'Added CSV upload support for aircraft registrations', '70117', release_category_id('change'), '1.1.4';
    execute format(v_query) using 'Combine aerodrome and approach charges', '70128', release_category_id('change'), '1.1.4';

    -- feature requests
    execute format(v_query) using 'Added support THRU Flights', '74558', release_category_id('feature'), '1.1.4';
    execute format(v_query) using 'Added support Training Flights', '74561', release_category_id('feature'), '1.1.4';
    execute format(v_query) using 'Added support for manual EUROCAT upload', '74718', release_category_id('feature'), '1.1.4';

end $$;
