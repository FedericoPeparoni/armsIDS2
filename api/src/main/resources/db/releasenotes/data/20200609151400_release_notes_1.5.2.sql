do $$
declare
    v_query varchar;
begin

    -- remove existing release notes with release_version 1.5.2 if any
    -- when adding more release notes for this version, rename file and update below accordingly
    DELETE FROM release_notes WHERE release_version = '1.5.2';

    -- update existing release notes with same number to reopened and insert new release notes
    v_query := 'UPDATE release_notes SET reopened = true, updated_at = now(), updated_by = ''system'' WHERE number = $2;
                INSERT INTO release_notes (title, number, release_category_id, release_version) VALUES ($1,$2,$3,''1.5.2'');';

    -- bugs
    execute format(v_query) using 'Cancelled invoice - Flight Movement can not be invoiced', '115697', release_category_id('bug');
    execute format(v_query) using 'Revenue report problems', '115718', release_category_id('bug');

    -- feature
    execute format(v_query) using 'Zero billable length', '115719', release_category_id('feature');
end $$;
