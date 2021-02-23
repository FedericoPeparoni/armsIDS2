do $$
declare
    v_query varchar;
begin

    -- update existing release notes with same number to reopened and insert new release notes
    v_query := 'UPDATE release_notes SET reopened = true, updated_at = now(), updated_by = ''system'' WHERE number = $2;
                INSERT INTO release_notes (title, number, release_category_id, release_version) VALUES ($1,$2,$3,$4);';

    -- change requests
    execute format(v_query) using 'Support for adjustment credit and debit notes', '74094', release_category_id('change'), '1.1.2';
    execute format(v_query) using 'Support for weekly or monthly invoicing for Non-IATA invoices', '74502', release_category_id('change'), '1.1.2';
    execute format(v_query) using 'Support for weekly or monthly invoicing for IATA invoices', '76445', release_category_id('change'), '1.1.2';
    execute format(v_query) using 'Units of measure added system wide', '76446', release_category_id('change'), '1.1.2';
    execute format(v_query) using 'Support for point of sale cash accounts', '76450', release_category_id('change'), '1.1.2';
    execute format(v_query) using 'Support for payments and transactions in foreign currencies', '76968', release_category_id('change'), '1.1.2';
    execute format(v_query) using 'Top level help menu added', '76980', release_category_id('change'), '1.1.2';

    -- feature requests
    execute format(v_query) using 'Support for user event log management', '74083', release_category_id('feature'), '1.1.2';
    execute format(v_query) using 'Added Invoice Status filters to flight movements', '76447', release_category_id('feature'), '1.1.2';
    execute format(v_query) using 'Added IATA/Non-IATA filters to flight movements', '76448', release_category_id('feature'), '1.1.2';

    -- bugs
    execute format(v_query) using 'Fixed parking time calculations when uploading from Spatia', '76455', release_category_id('bug'), '1.1.2';
    execute format(v_query) using 'Aviation Point of Sale - Fixed data-grid sorting', '76726', release_category_id('bug'), '1.1.2';
    execute format(v_query) using 'Fixed parking charges when previous flight has ZZZZ destination', '76977', release_category_id('bug'), '1.1.2';

end $$;
