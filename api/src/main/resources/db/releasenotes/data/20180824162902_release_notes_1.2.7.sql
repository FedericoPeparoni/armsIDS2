do $$
declare
    v_query varchar;
begin

    -- remove existing release notes with release_version 1.2.7 if any
    -- when adding more release notes for this version, rename file and update below accordingly
    DELETE FROM release_notes WHERE release_version = '1.2.7';

    -- update existing release notes with same number to reopened and insert new release notes
    v_query := 'UPDATE release_notes SET reopened = true, updated_at = now(), updated_by = ''system'' WHERE number = $2;
                INSERT INTO release_notes (title, number, release_category_id, release_version) VALUES ($1,$2,$3,''1.2.7'');';

    -- bugs
    execute format(v_query) using 'selfcare user registration - captcha is hidden until user enters contact information', '89985', release_category_id('bug');
    execute format(v_query) using 'flight schedules - account filter should only contain accounts with flight schedules defined', '89938', release_category_id('bug');
    execute format(v_query) using 'selfcare flight schedules - end time date selector does not work', '89995', release_category_id('bug');
    execute format(v_query) using 'create transaction - exchange rate is not shown as inverse on data entry form when system configuration indicates it should be', '89968', release_category_id('bug');
    execute format(v_query) using 'Edit a User/Self-Care User: ''Error: Already exists'' remains active and blocks Update after clearing duplicated value in ''SMS Number'' field', '89854', release_category_id('bug');
    execute format(v_query) using 'Aerodrome/Approach charges are calculated incorrectly', '89325', release_category_id('bug');
    execute format(v_query) using 'No handler found for PUT - many locations', '90040', release_category_id('bug');
    execute format(v_query) using 'selfcare user registration - user record is created even when no email is sent', '90003', release_category_id('bug');
    execute format(v_query) using '''Security->Users->Edit a User'' - ''Groups'' and ''Billing Centre'' fields should automatically be disabled When a SelfCare user is selected/edited', '90006', release_category_id('bug');
    execute format(v_query) using 'self care account management - user can modify account fields he should not be allowed to', '89986', release_category_id('bug');
    execute format(v_query) using 'transactions - debit/credit note documents are incorrect', '89967', release_category_id('bug');
    execute format(v_query) using 'Sage - string or binary data would be truncated (ARBTA,ARTCR) on insert', '89904', release_category_id('bug');
    execute format(v_query) using 'Sage retry after sage code is added to service charge catalogue still fails', '89945', release_category_id('bug');
    execute format(v_query) using 'Sage update fails on rentals because billing centre is not defined', '89946', release_category_id('bug');
    execute format(v_query) using 'Invoices - VOID, APPROVE and PUBLISH buttons are not displayed until an invoice is selected', '89947', release_category_id('bug');
    execute format(v_query) using 'Incorrect account list in aviation billing engine', '89920', release_category_id('bug');

end $$;
