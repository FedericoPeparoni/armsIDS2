do $$
declare
    v_query varchar;
begin

    -- remove existing release notes with release_version 1.3.4 if any
    -- when adding more release notes for this version, rename file and update below accordingly
    DELETE FROM release_notes WHERE release_version = '1.3.4';

    -- update existing release notes with same number to reopened and insert new release notes
    v_query := 'UPDATE release_notes SET reopened = true, updated_at = now(), updated_by = ''system'' WHERE number = $2;
                INSERT INTO release_notes (title, number, release_category_id, release_version) VALUES ($1,$2,$3,''1.3.4'');';

    -- change requests
    execute format(v_query) using 'Flight Reassignment - Aerodrome selection have to be a "multi selection" combo box', '89458', release_category_id('change');

    -- bugs
    execute format(v_query) using 'Can''t Preview/Generates Aviation Invoices', '89493', release_category_id('bug');
    execute format(v_query) using 'Combo box for Accounts takes a lot of time to populate', '89109', release_category_id('bug');
    execute format(v_query) using 'System configuration not taking into account negative coordinates', '89529', release_category_id('bug');

end $$;
