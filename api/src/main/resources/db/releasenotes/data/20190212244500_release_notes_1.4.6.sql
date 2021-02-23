do $$
declare
    v_query varchar;
begin

    -- remove existing release notes with release_version 1.4.6 if any
    -- when adding more release notes for this version, rename file and update below accordingly
    DELETE FROM release_notes WHERE release_version = '1.4.6';

    -- update existing release notes with same number to reopened and insert new release notes
    v_query := 'UPDATE release_notes SET reopened = true, updated_at = now(), updated_by = ''system'' WHERE number = $2;
                INSERT INTO release_notes (title, number, release_category_id, release_version) VALUES ($1,$2,$3,''1.4.6'');';

    -- bugs
    execute format(v_query) using 'Add transaction payment date to receipt templates', '102082', release_category_id('bug');
    execute format(v_query) using 'Set invoice_period_or_date and payment_due_date without time', '102086', release_category_id('bug');
    execute format(v_query) using 'Wrong Air Navigation Charge Schedules currency association', '102093', release_category_id('bug');
    execute format(v_query) using 'NullPointerException on ERP payment processing', '102091', release_category_id('bug');

end $$;
