do $$
declare
    v_query varchar;
begin

    -- remove existing release notes with release_version 1.2.3 if any
    -- when adding more release notes for this version, rename file and update below accordingly
    DELETE FROM release_notes WHERE release_version = '1.2.3';

    -- update existing release notes with same number to reopened and insert new release notes
    v_query := 'UPDATE release_notes SET reopened = true, updated_at = now(), updated_by = ''system'' WHERE number = $2;
                INSERT INTO release_notes (title, number, release_category_id, release_version) VALUES ($1,$2,$3,''1.2.3'');';

    -- changes
    execute format(v_query) using 'Support for a connector to retrieve flight data from CRONOS-2', '77124', release_category_id('change');

    -- bugs
    execute format(v_query) using 'Enroute charges are not being calculated for some flights', '88969', release_category_id('bug');
    execute format(v_query) using 'Passenger Service Charge Return not taking into account "0" passengers', '88560', release_category_id('bug');
    execute format(v_query) using 'When creating a user, the option "Force Password Change" is not working', '87788', release_category_id('bug');
    execute format(v_query) using 'Reject handling - attempted correction of radar data results in a "Data not valid" error - no indication of what is wront', '84041', release_category_id('bug');
    execute format(v_query) using 'Rejected Items "Data not found" error', '88795', release_category_id('bug');
    execute format(v_query) using 'User cannot find transaction - Transactions interface does not provide filtering', '84536', release_category_id('bug');
    execute format(v_query) using 'User cannot find invoice - invoice interface does not provide filtering', '84535', release_category_id('bug');
    execute format(v_query) using 'Tower and ATC logs are not being uploaded automatically', '84282', release_category_id('bug');
    execute format(v_query) using 'Statistics sort by does not work - always ordered by time first', '86675', release_category_id('bug');
    execute format(v_query) using 'Penalties not printed on the invoice', '88890', release_category_id('bug');
    execute format(v_query) using 'Point-of-sale cash sale workflow must be the same as credit sale workflow', '87797', release_category_id('bug');
    execute format(v_query) using 'Creating/Updating/Recalculating an International Flight Movement (Dep or Arr) a null pointer error is displayed', '89239', release_category_id('bug');
    execute format(v_query) using 'Overdue invoice calculated on the wrong date', '88889', release_category_id('bug');

end $$;
