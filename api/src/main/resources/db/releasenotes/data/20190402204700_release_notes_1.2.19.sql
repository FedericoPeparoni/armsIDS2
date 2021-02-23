do $$
declare
    v_query varchar;
begin

    -- remove existing release notes with release_version 1.2.19 if any
    -- when adding more release notes for this version, rename file and update below accordingly
    DELETE FROM release_notes WHERE release_version = '1.2.19';

    -- update existing release notes with same number to reopened and insert new release notes
    v_query := 'UPDATE release_notes SET reopened = true, updated_at = now(), updated_by = ''system'' WHERE number = $2;
                INSERT INTO release_notes (title, number, release_category_id, release_version) VALUES ($1,$2,$3,''1.2.19'');';

    -- bugs
    execute format(v_query) using 'Tower log - if flight is not indicated as SCH, then set default to NONSCH, set passengers to 0 if null', '104483', release_category_id('bug');
    execute format(v_query) using 'Passenger service charge returns - number of domestic and international passengers is not used to update flight movements', '104617', release_category_id('bug');
    execute format(v_query) using 'Where ZZZZ is used in the departure/destination endpoint used to determine the billing centre, use the other endpoint', '104798', release_category_id('bug');
    execute format(v_query) using 'Aviation billing - flight movements with non-null enroute, passenger and other invoice ids are still marked as PENDING', '104792', release_category_id('bug');
    execute format(v_query) using 'Do not use destination aerodrome in determining flight uniqueness', '104618', release_category_id('bug');
    execute format(v_query) using 'Radar import - SQL error ERROR: operator does not exist: character varying = bytea', '104766', release_category_id('bug');
    execute format(v_query) using 'Better detection of duplicate tower movement when THRU PLAN is A-B-C-A and tower log is segment C-A or D-A', '104328', release_category_id('bug');

    -- change requests
    execute format(v_query) using 'Passenger counts from tower log departures are not updated in flight movement', '104441', release_category_id('change');
    execute format(v_query) using 'Calculate day-of-flight and departure-time for ATC and tower logs', '101616', release_category_id('change');

end $$;
