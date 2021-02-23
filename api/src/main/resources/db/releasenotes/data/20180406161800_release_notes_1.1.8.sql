do $$
declare
    v_query varchar;
begin

    -- update existing release notes with same number to reopened and insert new release notes
    v_query := 'UPDATE release_notes SET reopened = true, updated_at = now(), updated_by = ''system'' WHERE number = $2;
                INSERT INTO release_notes (title, number, release_category_id, release_version) VALUES ($1,$2,$3,$4);';

    -- feature requests
    execute format(v_query) using 'Support for NEW, APPROVED, invoice workflow', '74549', release_category_id('feature'), '1.1.8';
    execute format(v_query) using 'Self-care portal - home page', '70296', release_category_id('feature'), '1.1.8';
    execute format(v_query) using 'Self-care portal - query submission interface', '74567', release_category_id('feature'), '1.1.8';

    -- bugs
    execute format(v_query) using 'NullPointerException found when updating/deleting a flight movement and the related Radar Log', '81775', release_category_id('bug'), '1.1.8';
    execute format(v_query) using 'Service charge catalogue grid display only displays first page of grid', '83442', release_category_id('bug'), '1.1.8';
    execute format(v_query) using 'Can''t approve Invoices', '82217', release_category_id('bug'), '1.1.8';
    execute format(v_query) using 'Upload from spatia is not working - cannot upload feb jan-feb 2017 data', '82157', release_category_id('bug'), '1.1.8';
    execute format(v_query) using 'unknown locations shows 60 for seconds - this is invalid', '82397', release_category_id('bug'), '1.1.8';
    execute format(v_query) using 'Credit Note "COMPLETE" button disappearing', '83319', release_category_id('bug'), '1.1.8';

end $$;
