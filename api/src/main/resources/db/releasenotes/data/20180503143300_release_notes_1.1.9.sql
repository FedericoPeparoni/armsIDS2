do $$
declare
    v_query varchar;
begin

    -- remove existing release notes with release_version 1.1.9 if any
    -- previous script, data/20180406161800_release_notes_1.1.9.sql, being replaced with duplicates
    DELETE FROM release_notes WHERE release_version = '1.1.9';

    -- update existing release notes with same number to reopened and insert new release notes
    v_query := 'UPDATE release_notes SET reopened = true, updated_at = now(), updated_by = ''system'' WHERE number = $2;
                INSERT INTO release_notes (title, number, release_category_id, release_version) VALUES ($1,$2,$3,$4);';

    -- bugs
    execute format(v_query) using 'Charter flights discount for multiple landings on the same day', '83514', release_category_id('bug'), '1.1.9';
    execute format(v_query) using 'Non-aviation invoice is paid from previous credit which should have no balance left', '83444', release_category_id('bug'), '1.1.9';

end $$;
