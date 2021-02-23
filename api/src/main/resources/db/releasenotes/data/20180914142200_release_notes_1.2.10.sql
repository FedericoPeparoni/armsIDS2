do $$
declare
    v_query varchar;
begin

    -- remove existing release notes with release_version 1.2.10 if any
    -- when adding more release notes for this version, rename file and update below accordingly
    DELETE FROM release_notes WHERE release_version = '1.2.10';

    -- update existing release notes with same number to reopened and insert new release notes
    v_query := 'UPDATE release_notes SET reopened = true, updated_at = now(), updated_by = ''system'' WHERE number = $2;
                INSERT INTO release_notes (title, number, release_category_id, release_version) VALUES ($1,$2,$3,''1.2.10'');';

    -- bugs
    execute format(v_query) using 'Air France Flight Schedules not loading', '90280', release_category_id('bug');
    execute format(v_query) using 'Users - Null pointer exception when trying to update a user''s password', '90350', release_category_id('bug');
    execute format(v_query) using 'Sage integration - Error: Failed to export 471 out of 471 exportable documents', '91092', release_category_id('bug');
    execute format(v_query) using 'Waypoint rounding is not working properly', '91184', release_category_id('bug');

end $$;
