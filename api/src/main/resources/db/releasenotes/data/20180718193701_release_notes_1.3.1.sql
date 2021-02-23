do $$
declare
    v_query varchar;
begin

    -- remove existing release notes with release_version 1.3.1 if any
    -- when adding more release notes for this version, rename file and update below accordingly
    DELETE FROM release_notes WHERE release_version = '1.3.1';

    -- update existing release notes with same number to reopened and insert new release notes
    v_query := 'UPDATE release_notes SET reopened = true, updated_at = now(), updated_by = ''system'' WHERE number = $2;
                INSERT INTO release_notes (title, number, release_category_id, release_version) VALUES ($1,$2,$3,$4);';

    -- change requests
    execute format(v_query) using 'Translation for sprint 1', '86548', release_category_id('change'), '1.3.1';
    execute format(v_query) using 'External accounting system integration - SITGA', '85834', release_category_id('change'), '1.3.1';

    -- bugs
    execute format(v_query) using 'POS selecting the last page, makes all the flights/pages disappear', '87831', release_category_id('bug'), '1.3.1';
    execute format(v_query) using 'Aviation billing error', '87773', release_category_id('bug'), '1.3.1';
    execute format(v_query) using 'Invoice Totals not as expected', '87832', release_category_id('bug'), '1.3.1';
    execute format(v_query) using 'Organization Name missing', '87713', release_category_id('bug'), '1.3.1';
    execute format(v_query) using 'Wrong Invoice costs calcualtion Domestic/National Flights', '87413', release_category_id('bug'), '1.3.1';
    execute format(v_query) using 'Domesti/Foreign and International/ForeingInvoice different totals', '87621', release_category_id('bug'), '1.3.1';
    execute format(v_query) using 'Rejected items - "VFR" has to be considered a valid Flight Level', '88447', release_category_id('bug'), '1.3.1';
    execute format(v_query) using 'Frontend half in spanish, half in english', '87003', release_category_id('bug'), '1.3.1';
    execute format(v_query) using 'Non Aviation Billing totals', '87233', release_category_id('bug'), '1.3.1';
    execute format(v_query) using 'Generic error when Debit Transaction created', '87774', release_category_id('bug'), '1.3.1';
    execute format(v_query) using 'Dep/Arr in English (should be in Spanish), incorrect Flight ID', '87646', release_category_id('bug'), '1.3.1';
    execute format(v_query) using 'Receipt showing wrong values, and not displaying the Payment Currency', '87675', release_category_id('bug'), '1.3.1';
    execute format(v_query) using 'Dep Ad not shown on international/Foreign invoice', '87639', release_category_id('bug'), '1.3.1';
    execute format(v_query) using 'Generic Error on Aviation Billing', '87387', release_category_id('bug'), '1.3.1';
    execute format(v_query) using 'Rejected Items - Flight rejected due to "value too long"', '88449', release_category_id('bug'), '1.3.1';
    execute format(v_query) using 'Exchange Rate Decimals not following the Decimal places defined for the currency', '87714', release_category_id('bug'), '1.3.1';
    execute format(v_query) using 'Missing formulas and similar problems don''t create resolution errors', '87173', release_category_id('bug'), '1.3.1';

end $$;
