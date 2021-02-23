do $$
declare
    v_query varchar;
begin

    -- update existing release notes with same number to reopened and insert new release notes
    v_query := 'UPDATE release_notes SET reopened = true, updated_at = now(), updated_by = ''system'' WHERE number = $2;
                INSERT INTO release_notes (title, number, release_category_id, release_version) VALUES ($1,$2,$3,$4);';

    -- change requests
    execute format(v_query) using 'Automate duplicate / missing flight detection', '74556', release_category_id('change'), '1.1.6';
    execute format(v_query) using 'Add an aircraft registration tracking report', '70124', release_category_id('change'), '1.1.6';

    -- feature requests
    execute format(v_query) using 'User event log is required', '74083', release_category_id('feature'), '1.1.6';
    execute format(v_query) using 'Flight movement builder - flag flight for small aircraft without valid AoC as an error', '74551', release_category_id('feature'), '1.1.6';
    execute format(v_query) using 'Reconcilliation of passenger charges against payments', '74552', release_category_id('feature'), '1.1.6';

    -- bugs
    execute format(v_query) using 'Cannot generate invoices', '81639', release_category_id('bug'), '1.1.6';
    execute format(v_query) using 'Data not displayed', '81786', release_category_id('bug'), '1.1.6';
    execute format(v_query) using 'Flight Movements don''t change to INVOICED status', '81038', release_category_id('bug'), '1.1.6';
    execute format(v_query) using 'Half system in Spanish Half in English', '81785', release_category_id('bug'), '1.1.6';
    execute format(v_query) using 'User Crossing Distance not shown', '81873', release_category_id('bug'), '1.1.6';
    execute format(v_query) using 'Generic ''An error occurred...'' message is displayed', '68171', release_category_id('bug'), '1.1.6';
    execute format(v_query) using 'Duplicate flights are sometimes marked as having a missing flight preceding them', '82018', release_category_id('bug'), '1.1.6';
    execute format(v_query) using 'Maximum call stack size error', '79561', release_category_id('bug'), '1.1.6';
    execute format(v_query) using 'Flight movement editing - EET should be mandatory for domestic and arrivals', '72740', release_category_id('bug'), '1.1.6';
    execute format(v_query) using 'Flight movements - text filter using account name does not work', '72425', release_category_id('bug'), '1.1.6';
    execute format(v_query) using 'FPL upload generates ''internal server error''', '80416', release_category_id('bug'), '1.1.6';
    execute format(v_query) using 'Full item 18 DEST/ is shown for delta flight when ''show actual departure/destination'' is selected', '81860', release_category_id('bug'), '1.1.6';
    execute format(v_query) using 'Separate Passenger Invoice not generated', '79258', release_category_id('bug'), '1.1.6';
    execute format(v_query) using 'Thru Plans gives zero lenght billable track', '79562', release_category_id('bug'), '1.1.6';
    execute format(v_query) using 'Unexpected Flight not identified', '80564', release_category_id('bug'), '1.1.6';
    execute format(v_query) using 'Implement latest version of AIX/M', '70099', release_category_id('bug'), '1.1.6';
    execute format(v_query) using '''Created By'' not displayed in the Invoice interface', '80511', release_category_id('bug'), '1.1.6';
    execute format(v_query) using 'Invoice Interface - Export Function not working', '80543', release_category_id('bug'), '1.1.6';
    execute format(v_query) using 'System summary not displayed', '81265', release_category_id('bug'), '1.1.6';
    execute format(v_query) using 'Many duplicate flights with identical day-of-flight/departure-time/registration', '82017', release_category_id('bug'), '1.1.6';
    execute format(v_query) using 'Spatia update rejects flight plans if a current exchange rate for ANSP to USD is not defined', '76454', release_category_id('bug'), '1.1.6';
    execute format(v_query) using 'Upload from spatia - reject created for duplicate flight movement', '78707', release_category_id('bug'), '1.1.6';
    execute format(v_query) using 'User should not have to select username/logout twice to log out', '76973', release_category_id('bug'), '1.1.6';
    execute format(v_query) using 'Multi-user management', '63202', release_category_id('bug'), '1.1.6';

end $$;
