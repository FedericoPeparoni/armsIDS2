do $$
declare
    v_query varchar;
begin

    -- remove existing release notes with release_version 1.2.9 if any
    -- when adding more release notes for this version, rename file and update below accordingly
    DELETE FROM release_notes WHERE release_version = '1.2.9';

    -- update existing release notes with same number to reopened and insert new release notes
    v_query := 'UPDATE release_notes SET reopened = true, updated_at = now(), updated_by = ''system'' WHERE number = $2;
                INSERT INTO release_notes (title, number, release_category_id, release_version) VALUES ($1,$2,$3,''1.2.9'');';

    -- bugs
    execute format(v_query) using 'Approval required for self-care entered account, aircraft registration and flight schedules', '88944', release_category_id('bug');
    execute format(v_query) using 'Radar importer – validation is incorrectly flagging missing FIR entry/exit times when the information is there', '90140', release_category_id('bug');
    execute format(v_query) using 'Support multiple web portal users per operator account', '88955', release_category_id('bug');

end $$;
