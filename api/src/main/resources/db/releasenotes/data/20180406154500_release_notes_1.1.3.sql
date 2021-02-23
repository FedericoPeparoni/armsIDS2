do $$
declare
    v_query varchar;
begin

    -- update existing release notes with same number to reopened and insert new release notes
    v_query := 'UPDATE release_notes SET reopened = true, updated_at = now(), updated_by = ''system'' WHERE number = $2;
                INSERT INTO release_notes (title, number, release_category_id, release_version) VALUES ($1,$2,$3,$4);';

    -- change requests
    execute format(v_query) using 'Added support for regional countries and flights', '66922', release_category_id('change'), '1.1.3';
    execute format(v_query) using 'Added support for MTOW factor classes', '70115', release_category_id('change'), '1.1.3';
    execute format(v_query) using 'Added support for automated upload from EUROCAT', '74565', release_category_id('change'), '1.1.3';

    -- feature requests
    execute format(v_query) using 'Added local aircraft registry', '74550', release_category_id('feature'), '1.1.3';
    execute format(v_query) using 'Added support for Somalia flights', '74559', release_category_id('feature'), '1.1.3';
    execute format(v_query) using 'Added support for small registered aircraft', '74560', release_category_id('feature'), '1.1.3';
    execute format(v_query) using 'Added flight schedule management', '78104', release_category_id('feature'), '1.1.3';

    -- bugs
    execute format(v_query) using 'Automate upload of Radar, ATC, Tower, and Passenger', '58198', release_category_id('bug'), '1.1.3';
    execute format(v_query) using 'Fixed exporting of data-grids', '72340', release_category_id('bug'), '1.1.3';
    execute format(v_query) using 'Add missing control on country code', '77008', release_category_id('bug'), '1.1.3';
    execute format(v_query) using 'Prevent a used currency from being set inactive', '77269', release_category_id('bug'), '1.1.3';
    execute format(v_query) using 'Fixed MTOW conversion on aircraft registrations', '77565', release_category_id('bug'), '1.1.3';
    execute format(v_query) using 'Fixed international and domestic PAX exemptions', '77837', release_category_id('bug'), '1.1.3';
    execute format(v_query) using 'Fixed handling different currencies for air nav charges', '77840', release_category_id('bug'), '1.1.3';
    execute format(v_query) using 'Fixed adjustment debit note payment', '77879', release_category_id('bug'), '1.1.3';
    execute format(v_query) using 'Fixed recurring charges record creation', '78129', release_category_id('bug'), '1.1.3';
    execute format(v_query) using 'Allow account parking & penalty rate to accept decimals', '78159', release_category_id('bug'), '1.1.3';

    -- hotfix bug, do NOT update existing hotfix release note numbers
    v_query := 'INSERT INTO release_notes (title, number, release_category_id, release_version) VALUES ($1,$2,$3,$4);';
    execute format(v_query) using 'Use organization name configuration to determine site specific processing', 'Hotfix', release_category_id('bug'), '1.1.3';

end $$;
