do $$
declare
    v_query varchar;
begin

    -- remove existing release notes with release_version 1.2.20 if any
    -- when adding more release notes for this version, rename file and update below accordingly
    DELETE FROM release_notes WHERE release_version = '1.2.20';

    -- update existing release notes with same number to reopened and insert new release notes
    v_query := 'UPDATE release_notes SET reopened = true, updated_at = now(), updated_by = ''system'' WHERE number = $2;
                INSERT INTO release_notes (title, number, release_category_id, release_version) VALUES ($1,$2,$3,''1.2.20'');';

    -- bugs
    execute format(v_query) using 'Spatia - Flight which is cancelled, then a new flight plan is submitted does not get a flight movement created', '104493', release_category_id('bug');
    execute format(v_query) using 'Unspecified destinations/departures - country code is not being set in system generated rows', '105476', release_category_id('bug');
    execute format(v_query) using 'Unspecified departure/destination - user cannot delete a record', '105477', release_category_id('bug');

end $$;
