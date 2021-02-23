do $$
declare
    v_query varchar;
begin

    -- remove existing release notes with release_version 1.2.6 if any
    -- when adding more release notes for this version, rename file and update below accordingly
    DELETE FROM release_notes WHERE release_version = '1.2.6';

    -- update existing release notes with same number to reopened and insert new release notes
    v_query := 'UPDATE release_notes SET reopened = true, updated_at = now(), updated_by = ''system'' WHERE number = $2;
                INSERT INTO release_notes (title, number, release_category_id, release_version) VALUES ($1,$2,$3,''1.2.6'');';

    -- bugs
    execute format(v_query) using 'Sequrity -> Users -> Create a User: Force Password Change is saved as ''TRUE'' in DB whereas ''FALSE'' is selected in UI', '89724', release_category_id('bug');
    execute format(v_query) using 'SelfCare Portal - Password recovery no working on QA04', '89604', release_category_id('bug');
    execute format(v_query) using 'SelfCare Portal - New User Account is Forced to Change Password', '89723', release_category_id('bug');
    execute format(v_query) using 'Nominal Routes not taken into account', '87606', release_category_id('bug');
    execute format(v_query) using '''Download'' button fails to download and display a document in FireFox', '89861', release_category_id('bug');
    execute format(v_query) using 'Incorrect time is displayed', '89308', release_category_id('bug');
    execute format(v_query) using 'system configuration - default country is not validated - it must be a valid country code', '89690', release_category_id('bug');
    execute format(v_query) using 'Cannot start application after 1.2.6 update', '89909', release_category_id('bug');
    execute format(v_query) using 'Discrepancy between the Invoice Amount displayed and the one Stored', '88529', release_category_id('bug');
    execute format(v_query) using 'Flight Movements: ''Recalculate'' button can generate ''Error: Method not supported''', '89929', release_category_id('bug');
    execute format(v_query) using 'Downloaded flight schedule spreadsheet is formatted incorrectly', '89935', release_category_id('bug');
    execute format(v_query) using 'SelfCare Portal - ''Forgot Password?'' feature works for a New User Who Never Activated his/her Account', '89728', release_category_id('bug');
    execute format(v_query) using 'Rejected Items - Error found in the application: cannot perform the operation "" cannot be fixed', '89269', release_category_id('bug');

end $$;
