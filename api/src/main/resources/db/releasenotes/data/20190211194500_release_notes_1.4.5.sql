do $$
declare
    v_query varchar;
begin

    -- remove existing release notes with release_version 1.4.5 if any
    -- when adding more release notes for this version, rename file and update below accordingly
    DELETE FROM release_notes WHERE release_version = '1.4.5';

    -- update existing release notes with same number to reopened and insert new release notes
    v_query := 'UPDATE release_notes SET reopened = true, updated_at = now(), updated_by = ''system'' WHERE number = $2;
                INSERT INTO release_notes (title, number, release_category_id, release_version) VALUES ($1,$2,$3,''1.4.5'');';

    -- changes
    execute format(v_query) using 'Use exchange rate for previous day for payments and interest-only invoices', '101435', release_category_id('change');
    execute format(v_query) using 'Different currencies for international and domestic approach fees', '101436', release_category_id('change');

    -- bugs
    execute format(v_query) using 'Aircraft registration filter problem', '101778', release_category_id('bug');
    execute format(v_query) using 'UI - System configuration - precedence', '101872', release_category_id('bug');
    execute format(v_query) using 'Cannot fix a Rejected Item', '100997', release_category_id('bug');

end $$;
