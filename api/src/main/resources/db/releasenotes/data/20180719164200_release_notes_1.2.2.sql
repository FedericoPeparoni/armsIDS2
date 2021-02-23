do $$
declare
    v_query varchar;
begin

    -- remove existing release notes with release_version 1.2.2 if any
    -- when adding more release notes for this version, rename file and update below accordingly
    DELETE FROM release_notes WHERE release_version = '1.2.2';

    -- update existing release notes with same number to reopened and insert new release notes
    v_query := 'UPDATE release_notes SET reopened = true, updated_at = now(), updated_by = ''system'' WHERE number = $2;
                INSERT INTO release_notes (title, number, release_category_id, release_version) VALUES ($1,$2,$3,''1.2.2'');';

    -- bugs
    execute format(v_query) using 'System Configuration interface showing 20 Elements only', '88555', release_category_id('bug');
    execute format(v_query) using 'Flight Recalculation Status not shown on the FM interface', '88556', release_category_id('bug');
    execute format(v_query) using 'Pax count can''t be update from FM interface', '88559', release_category_id('bug');
    execute format(v_query) using 'Passenger Report', '88436', release_category_id('bug');
    execute format(v_query) using 'Nonfunctional filter "clear" buttons', '88860', release_category_id('bug');
    execute format(v_query) using 'Display total records and filltered records on critical datagrids', '87791', release_category_id('bug');
    execute format(v_query) using 'Wrong invoice templates are being used', '88970', release_category_id('bug');

end $$;
