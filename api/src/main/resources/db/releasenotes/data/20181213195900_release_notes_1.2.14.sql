do $$
declare
    v_query varchar;
begin

    -- remove existing release notes with release_version 1.2.14 if any
    -- when adding more release notes for this version, rename file and update below accordingly
    DELETE FROM release_notes WHERE release_version = '1.2.14';

    -- update existing release notes with same number to reopened and insert new release notes
    v_query := 'UPDATE release_notes SET reopened = true, updated_at = now(), updated_by = ''system'' WHERE number = $2;
                INSERT INTO release_notes (title, number, release_category_id, release_version) VALUES ($1,$2,$3,''1.2.14'');';

    -- bugs
    execute format(v_query) using 'Aerodromes filter works only on Refresh', '96524', release_category_id('bug');
    execute format(v_query) using 'Plugins form problems', '96530', release_category_id('bug');
    execute format(v_query) using 'Enroute Air Naviagation Charges create doesn''t work', '96518', release_category_id('bug');
    execute format(v_query) using 'Unspecified Aircraft Types & Unspecified Departure and Destination Locations - Filtered/Total', '96514', release_category_id('bug');
    execute format(v_query) using 'Flight Schedules - Updating schedule with no daily schedule (no days) doesnt create error message', '96469', release_category_id('bug');
    execute format(v_query) using 'Billing Centres form problems', '96525', release_category_id('bug');
    execute format(v_query) using 'Invoices: Approval limit message displays amount in scientific notation', '96504', release_category_id('bug');
    execute format(v_query) using 'Aerodrome Categories filter problem', '96522', release_category_id('bug');
    execute format(v_query) using 'Invoices: Flight movement selection breaks flight movement pagination', '96508', release_category_id('bug');
    execute format(v_query) using 'Countries form problems', '96528', release_category_id('bug');
    execute format(v_query) using 'Flight Movement not created. Validation error because of Aircraft type length', '96727', release_category_id('bug');
    execute format(v_query) using 'MTOW not resolved properly', '96771', release_category_id('bug');
    execute format(v_query) using 'Groups form problems', '96579', release_category_id('bug');
    execute format(v_query) using 'Transaction Workflow inconsistency', '96574', release_category_id('bug');
    execute format(v_query) using 'Local Aircraft Registry - expiry date from uploaded file + sort', '96512', release_category_id('bug');
    execute format(v_query) using 'Aircraft Registrations filtering', '96828', release_category_id('bug');
    execute format(v_query) using 'System Configuration validation', '96576', release_category_id('bug');
    execute format(v_query) using 'Overdue penalty applied twice under transaction balance', '93479', release_category_id('bug');
    execute format(v_query) using 'Cannot create Credit or Debit notes', '100676', release_category_id('bug');

end $$;
