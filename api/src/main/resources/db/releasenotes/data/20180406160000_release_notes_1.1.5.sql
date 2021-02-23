do $$
declare
    v_query varchar;
begin

    -- update existing release notes with same number to reopened and insert new release notes
    v_query := 'UPDATE release_notes SET reopened = true, updated_at = now(), updated_by = ''system'' WHERE number = $2;
                INSERT INTO release_notes (title, number, release_category_id, release_version) VALUES ($1,$2,$3,$4);';

    -- change requests
    execute format(v_query) using 'Modified account management', '70129', release_category_id('change'), '1.1.5';
    execute format(v_query) using 'Modified account statements', '78304', release_category_id('change'), '1.1.5';

    -- feature requests
    execute format(v_query) using 'Added reconciliation of passenger charges', '74552', release_category_id('feature'), '1.1.5';
    execute format(v_query) using 'Added preconfigured reports', '74557', release_category_id('feature'), '1.1.5';

    -- bugs
    execute format(v_query) using 'Added unit of measure support for distances', '70120', release_category_id('bug'), '1.1.5';
    execute format(v_query) using 'Synchronized data grid and geographical display', '77706', release_category_id('bug'), '1.1.5';
    execute format(v_query) using 'Fixed internal server error when creating domestic flight movements', '77991', release_category_id('bug'), '1.1.5';
    execute format(v_query) using 'Fixed flight schedule aerodrome name validation', '78243', release_category_id('bug'), '1.1.5';
    execute format(v_query) using 'Added charter flight discount for multiple same-day landings', '78303', release_category_id('bug'), '1.1.5';
    execute format(v_query) using 'Fixed display of overdue invoices', '78305', release_category_id('bug'), '1.1.5';
    execute format(v_query) using 'Removed unnecessary flight plan fields', '78389', release_category_id('bug'), '1.1.5';
    execute format(v_query) using 'Fixed incorrectly printed receipt title', '78405', release_category_id('bug'), '1.1.5';
    execute format(v_query) using 'Fixed invoice total inconsistency', '78432', release_category_id('bug'), '1.1.5';
    execute format(v_query) using 'Fixed ability to modify invoice after generation', '78541', release_category_id('bug'), '1.1.5';
    execute format(v_query) using 'Fixed login error message / failed login event capture', '78690', release_category_id('bug'), '1.1.5';
    execute format(v_query) using 'Fixed spatia upload internal server error', '78703', release_category_id('bug'), '1.1.5';
    execute format(v_query) using 'Fixed rejection of duplicate flight movement', '78707', release_category_id('bug'), '1.1.5';
    execute format(v_query) using 'Fixed stale verstion handling during spatia upload', '78709', release_category_id('bug'), '1.1.5';
    execute format(v_query) using 'Fixed incorrect default month selected', '78713', release_category_id('bug'), '1.1.5';
    execute format(v_query) using 'Fixed flight movement text filtering by account name', '78715', release_category_id('bug'), '1.1.5';
    execute format(v_query) using 'Changed display of invoice penalties', '78717', release_category_id('bug'), '1.1.5';
    execute format(v_query) using 'Fixed distance and rounding on invoices', '79074', release_category_id('bug'), '1.1.5';
    execute format(v_query) using 'Added notification of missing and unexpected flights', '79261', release_category_id('bug'), '1.1.5';
    execute format(v_query) using 'Added exchange rate sheet to IATA invoice', '79600', release_category_id('bug'), '1.1.5';
    execute format(v_query) using 'Added option to remove passenger costs from invoices', '79907', release_category_id('bug'), '1.1.5';
    execute format(v_query) using 'Fixed broken pipe generating IATA invoice', '79916', release_category_id('bug'), '1.1.5';
    execute format(v_query) using 'Fixed invoice rounding of MTOW in kg / tons', '80327', release_category_id('bug'), '1.1.5';
    execute format(v_query) using 'Considers movement type when looking for AMF', '80355', release_category_id('bug'), '1.1.5';
    execute format(v_query) using 'Fixed invoice template interest rate', '80410', release_category_id('bug'), '1.1.5';

end $$;
