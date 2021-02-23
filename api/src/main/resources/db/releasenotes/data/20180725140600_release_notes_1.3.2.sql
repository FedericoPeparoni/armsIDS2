do $$
declare
    v_query varchar;
begin

    -- remove existing release notes with release_version 1.3.2 if any
    -- when adding more release notes for this version, rename file and update below accordingly
    DELETE FROM release_notes WHERE release_version = '1.3.2';

    -- update existing release notes with same number to reopened and insert new release notes
    v_query := 'UPDATE release_notes SET reopened = true, updated_at = now(), updated_by = ''system'' WHERE number = $2;
                INSERT INTO release_notes (title, number, release_category_id, release_version) VALUES ($1,$2,$3,$4);';

    -- change requests
   	execute format(v_query) using 'Handling of leased aircraft (flight reassignment)', '88370', release_category_id('change'), '1.3.2';

    -- bugs
    execute format(v_query) using 'Reports are not working at all - no reports are defined', '88367', release_category_id('bug'), '1.3.2';
	execute format(v_query) using 'Recalculation does not remove the "MISSING_EXCHANGE_RATE" error', '88984', release_category_id('bug'), '1.3.2';
	execute format(v_query) using 'Flight Reassignment translation', '89193', release_category_id('bug'), '1.3.2';
	execute format(v_query) using 'Aviation Billing Recalculation', '88985', release_category_id('bug'), '1.3.2';
	execute format(v_query) using 'All USD invoices should also show EUR', '88369', release_category_id('bug'), '1.3.2';
	execute format(v_query) using 'Flight Reassignment Interface not really User Friendly', '89208', release_category_id('bug'), '1.3.2';

end $$;
