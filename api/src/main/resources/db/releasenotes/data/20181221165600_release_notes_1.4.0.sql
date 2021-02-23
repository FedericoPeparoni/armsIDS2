do $$
declare
    v_query varchar;
begin

    -- remove existing release notes with release_version 1.4.0 if any
    -- when adding more release notes for this version, rename file and update below accordingly
    DELETE FROM release_notes WHERE release_version = '1.4.0';

    -- update existing release notes with same number to reopened and insert new release notes
    v_query := 'UPDATE release_notes SET reopened = true, updated_at = now(), updated_by = ''system'' WHERE number = $2;
                INSERT INTO release_notes (title, number, release_category_id, release_version) VALUES ($1,$2,$3,''1.4.0'');';

    -- features
    execute format(v_query) using 'Item 6 – Flight Movement Note on DELETE', '100703', release_category_id('feature');

    -- bugs
    execute format(v_query) using 'Air Navigation Charges - Weight entered in Kg managed as Short Tons', '100792', release_category_id('bug');
    execute format(v_query) using 'Update button not enabled', '100893', release_category_id('bug');
    execute format(v_query) using 'Flight Movements - Late Departure Charges not calculated', '100839', release_category_id('bug');

end $$;
