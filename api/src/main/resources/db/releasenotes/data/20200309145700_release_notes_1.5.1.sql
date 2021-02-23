do $$
declare
    v_query varchar;
begin

    -- remove existing release notes with release_version 1.5.1 if any
    -- when adding more release notes for this version, rename file and update below accordingly
    DELETE FROM release_notes WHERE release_version = '1.5.1';

    -- update existing release notes with same number to reopened and insert new release notes
    v_query := 'UPDATE release_notes SET reopened = true, updated_at = now(), updated_by = ''system'' WHERE number = $2;
                INSERT INTO release_notes (title, number, release_category_id, release_version) VALUES ($1,$2,$3,''1.5.1'');';

    -- bugs
    execute format(v_query) using 'Invoices - during display, the entities associated with the invoice cannot be exported (flight movements, line items, transactions)', '106279', release_category_id('bug');
    execute format(v_query) using 'Left menu search box is too hard to see', '90156', release_category_id('bug');
    execute format(v_query) using 'User event log - incorrect data grid and edit form captions', '104281', release_category_id('bug');
    execute format(v_query) using 'All progress dialogues - time to completion does not show days', '114978', release_category_id('bug');
    execute format(v_query) using 'All datagrids - export from datagrid only downloads the first 10 pages (flight data and billing data)', '104665', release_category_id('bug');
    execute format(v_query) using 'Data grid export downloads all records in table', '72737', release_category_id('bug');
    execute format(v_query) using 'NON-IATA invoice preview / generation - monitor window shows incorrect information', '85717', release_category_id('bug');
    execute format(v_query) using 'Invoices - poor spacing on datagrid', '114984', release_category_id('bug');
    execute format(v_query) using 'Performance - flight movements, atc movements, tower movements and radar - entire aircraft types list is returned on each form display', '115303', release_category_id('bug');
    execute format(v_query) using 'Invoices - do not allow user to void an published invoice which as updated an external system', '115023', release_category_id('bug');
    execute format(v_query) using 'Flight movement builder - aircraft registration "is local" flag is not used in determining regional flights', '114986', release_category_id('bug');

    -- changes
    execute format(v_query) using 'All data grids - when the user pages to a new page the data grid should automatically scroll to the top', '115013', release_category_id('change');
    execute format(v_query) using 'Add support for Intelcan flight strips', '115322', release_category_id('change');

    -- feature
    execute format(v_query) using 'Usability - all forms – data grid – Fixed headers which are always displayed should be implemented', '90197', release_category_id('feature');

end $$;
