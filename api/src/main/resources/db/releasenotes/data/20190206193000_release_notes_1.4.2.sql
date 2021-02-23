do $$
declare
    v_query varchar;
begin

    -- remove existing release notes with release_version 1.4.2 if any
    -- when adding more release notes for this version, rename file and update below accordingly
    DELETE FROM release_notes WHERE release_version = '1.4.2';

    -- update existing release notes with same number to reopened and insert new release notes
    v_query := 'UPDATE release_notes SET reopened = true, updated_at = now(), updated_by = ''system'' WHERE number = $2;
                INSERT INTO release_notes (title, number, release_category_id, release_version) VALUES ($1,$2,$3,''1.4.2'');';

    -- features
    execute format(v_query) using 'Item 11 Additional Passenger Information', '100965', release_category_id('feature');

    -- changes
    execute format(v_query) using 'Add a column for approach charges to non-iata aviation invoices', '101289', release_category_id('change');

    -- bugs
    execute format(v_query) using 'Approval Workflow not working as expected', '100990', release_category_id('bug');
    execute format(v_query) using 'Discount for subsequent landings same AC/same AD/same day is not applied', '101299', release_category_id('bug');
    execute format(v_query) using 'Map display is not working correctly (SAVF airspace issue)', '101329', release_category_id('bug');
    execute format(v_query) using 'Aviation billing web page does not disable flight movement type selection when billing by flight movement type is disabled', '101109', release_category_id('bug');
    execute format(v_query) using 'Additional self-care aircraft registration form fields', '101241', release_category_id('bug');
    execute format(v_query) using 'Regional flights South Sudan', '101843', release_category_id('bug');
    execute format(v_query) using 'Configuration - aircraft types with wrong MTOW', '101840', release_category_id('bug');

end $$;
