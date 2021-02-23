do $$
declare
    v_query varchar;
begin

    -- remove existing release notes with release_version 1.4.1 if any
    -- when adding more release notes for this version, rename file and update below accordingly
    DELETE FROM release_notes WHERE release_version = '1.4.1';

    -- update existing release notes with same number to reopened and insert new release notes
    v_query := 'UPDATE release_notes SET reopened = true, updated_at = now(), updated_by = ''system'' WHERE number = $2;
                INSERT INTO release_notes (title, number, release_category_id, release_version) VALUES ($1,$2,$3,''1.4.1'');';

    -- features
    execute format(v_query) using 'Add new organization name and invoice templates', '100900', release_category_id('feature');
    execute format(v_query) using 'Item 11.2 Landing (Manually Entered Outages)', '100700', release_category_id('feature');
    execute format(v_query) using 'Item 11.3 – Small Aircraft', '100701', release_category_id('feature');
    execute format(v_query) using 'Item 14 – Payment Management', '100702', release_category_id('feature');

    -- bugs
    execute format(v_query) using 'Aviation Invoice async report status error', '100853', release_category_id('bug');
    execute format(v_query) using 'Flight Movement - Charges not calculated', '100829', release_category_id('bug');
    execute format(v_query) using 'Flight Movements - Impossible to Update a flight movement', '100795', release_category_id('bug');
    execute format(v_query) using 'Flight Movements - Impossible to delete a flight movement', '100794', release_category_id('bug');
    execute format(v_query) using 'Incorrect translation of Account Type in English (EN)', '100856', release_category_id('bug');
    execute format(v_query) using 'Enroute Charges calculation', '100969', release_category_id('bug');
    execute format(v_query) using 'Enroute charge formula missing MTOW and AVGMASSFACTOR values', '100890', release_category_id('bug');
    execute format(v_query) using 'Country code data type', '100813', release_category_id('bug');
    execute format(v_query) using 'Can''t create Debit Note due to a nullPointerExcpetion', '100972', release_category_id('bug');
    execute format(v_query) using 'Can''t create a Credit Note when the Approval Workflow is active (and set)', '100973', release_category_id('bug');
    execute format(v_query) using 'Can''t upload modifed Invoice Template', '100974', release_category_id('bug');
    execute format(v_query) using 'POS Can''t generate Aviation Invioice', '100970', release_category_id('bug');
    execute format(v_query) using 'Remove transaction currency field title attribute', '101067', release_category_id('bug');
    execute format(v_query) using 'WebPortal Not allowing to register a new User', '100975', release_category_id('bug');

end $$;
