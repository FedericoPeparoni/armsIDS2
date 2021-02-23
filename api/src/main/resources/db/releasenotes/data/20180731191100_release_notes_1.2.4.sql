do $$
declare
    v_query varchar;
begin

    -- remove existing release notes with release_version 1.2.4 if any
    -- when adding more release notes for this version, rename file and update below accordingly
    DELETE FROM release_notes WHERE release_version = '1.2.4';

    -- update existing release notes with same number to reopened and insert new release notes
    v_query := 'UPDATE release_notes SET reopened = true, updated_at = now(), updated_by = ''system'' WHERE number = $2;
                INSERT INTO release_notes (title, number, release_category_id, release_version) VALUES ($1,$2,$3,''1.2.4'');';

    -- bugs
    execute format(v_query) using 'File upload forms should provide a description of the file format', '88921', release_category_id('bug');
    execute format(v_query) using 'Flight Movement track no more displayed', '88808', release_category_id('bug');
    execute format(v_query) using 'Statistics not working', '89200', release_category_id('bug');
    execute format(v_query) using 'Flight notes field is not being poplulated', '89077', release_category_id('bug');
    execute format(v_query) using 'Setting an Invoice to VOID (reject it) does not set the related Fligth Movements to PENDING', '89237', release_category_id('bug');
    execute format(v_query) using 'Display total records and filltered records on critical datagrids', '87791', release_category_id('bug');
    execute format(v_query) using 'Check Dataflow for Flight Movement Builder', '76136', release_category_id('bug');
    execute format(v_query) using 'Flight movement editing - editing a flight movement sets its created date/time to the current date/time', '70299', release_category_id('bug');
    execute format(v_query) using 'No busy cursor displayed when application is busy', '67812', release_category_id('bug');

end $$;
