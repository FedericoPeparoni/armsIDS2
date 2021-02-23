do $$
declare
    v_query varchar;
begin

    -- remove existing release notes with release_version 1.2.18 if any
    -- when adding more release notes for this version, rename file and update below accordingly
    DELETE FROM release_notes WHERE release_version = '1.2.18';

    -- update existing release notes with same number to reopened and insert new release notes
    v_query := 'UPDATE release_notes SET reopened = true, updated_at = now(), updated_by = ''system'' WHERE number = $2;
                INSERT INTO release_notes (title, number, release_category_id, release_version) VALUES ($1,$2,$3,''1.2.18'');';

    -- bugs
    execute format(v_query) using 'Delta flight calculation always calculates adap on arrival, ignoring the system setting for this', '103312', release_category_id('bug');
    execute format(v_query) using 'Flight movements - locate missing/duplicate flights displays deleted/cancelled flights', '101615', release_category_id('bug');
    execute format(v_query) using 'Flight movements editing - errors in key fields which are read-only are not indicated', '103309', release_category_id('bug');
    execute format(v_query) using 'Flight movement data entry from - parking hours set to 0 is mishandled', '101504', release_category_id('bug');
    execute format(v_query) using 'Tower log - when dep/dest is not an aerodrome it should be added to item 18 and the dep/dest should be set to ZZZZ', '103308', release_category_id('bug');
    execute format(v_query) using 'Implementation of tower log does not match technical specification and does not work', '102478', release_category_id('bug');
    execute format(v_query) using 'API runs out of memory', '103518', release_category_id('bug');
    execute format(v_query) using 'Air navigation currencies incorrect after update', '102219', release_category_id('bug');

    -- change requests
    execute format(v_query) using 'Calculate day-of-flight and departure-time for ATC and tower logs', '101616', release_category_id('change');

end $$;
