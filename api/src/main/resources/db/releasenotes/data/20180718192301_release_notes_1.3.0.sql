do $$
declare
    v_query varchar;
begin

    -- remove existing release notes with release_version 1.3.0 if any
    -- when adding more release notes for this version, rename file and update below accordingly
    DELETE FROM release_notes WHERE release_version = '1.3.0';

    -- update existing release notes with same number to reopened and insert new release notes
    v_query := 'UPDATE release_notes SET reopened = true, updated_at = now(), updated_by = ''system'' WHERE number = $2;
                INSERT INTO release_notes (title, number, release_category_id, release_version) VALUES ($1,$2,$3,$4);';

    -- change requests
    execute format(v_query) using 'Invoice Template additional info', '85575', release_category_id('change'), '1.3.0';
    execute format(v_query) using 'Calculate enroute charges for domestic flights for national registrations in Tax Units', '86546', release_category_id('change'), '1.3.0';
    execute format(v_query) using 'Invoice by flight movement type in currency dependent on domestic/international and national/foreign registration', '86547', release_category_id('change'), '1.3.0';
    execute format(v_query) using 'Categorise flight plans based on domestic / international route and national / foreign registration', '83684', release_category_id('change'), '1.3.0';

    -- bugs
    execute format(v_query) using 'New users can''t login the system', '87113', release_category_id('bug'), '1.3.0';
    execute format(v_query) using 'Can''t generate an invoice', '87180', release_category_id('bug'), '1.3.0';
    execute format(v_query) using 'Currencies, only the oldest 20 records are displayed', '87176', release_category_id('bug'), '1.3.0';
    execute format(v_query) using 'Flight Movement Category not working', '87175', release_category_id('bug'), '1.3.0';
    execute format(v_query) using 'Numeric field overflow flights with high cost', '84438', release_category_id('bug'), '1.3.0';
    execute format(v_query) using 'All flight movements have "OTHER" as movement type', '87103', release_category_id('bug'), '1.3.0';
    execute format(v_query) using 'Flight Movement Interface, Enroute label wrongly translated', '87181', release_category_id('bug'), '1.3.0';
    execute format(v_query) using 'Air Navigation Charges - Wrong Format message displayed not correctly', '87177', release_category_id('bug'), '1.3.0';
    execute format(v_query) using 'Can''t update a Flight Movement', '87220', release_category_id('bug'), '1.3.0';
    execute format(v_query) using 'Domestic/Foreign Flights wrong currency', '87178', release_category_id('bug'), '1.3.0';
    execute format(v_query) using 'Can''t generate Non-Aviation Invocies', '87312', release_category_id('bug'), '1.3.0';
    execute format(v_query) using 'Enroute costs not displayed', '87343', release_category_id('bug'), '1.3.0';
    execute format(v_query) using 'Currency assigned also for flight not categorized', '87179', release_category_id('bug'), '1.3.0';
    execute format(v_query) using 'En-route cost currency column', '87361', release_category_id('bug'), '1.3.0';
    execute format(v_query) using 'Aviation billing Flight Movement Category "foreign" different translations', '87174', release_category_id('bug'), '1.3.0';
    execute format(v_query) using 'Enroute Currency, wrongly displayed', '87409', release_category_id('bug'), '1.3.0';
    execute format(v_query) using 'Can''t generate invoice from Aviation Billing', '87438', release_category_id('bug'), '1.3.0';
    execute format(v_query) using 'Enroute Formulas interface', '87354', release_category_id('bug'), '1.3.0';
    execute format(v_query) using 'Can''t Generate Invoce from the Fligth Movement Inteface', '87412', release_category_id('bug'), '1.3.0';
    execute format(v_query) using 'Missing destination currency instead of always USD', '87410', release_category_id('bug'), '1.3.0';
    execute format(v_query) using 'Currency management - Exchange rate Labels inconsistency', '87411', release_category_id('bug'), '1.3.0';
    execute format(v_query) using 'Transaction interface NullPointerException', '87645', release_category_id('bug'), '1.3.0';
    execute format(v_query) using 'POS NullPointerException', '87644', release_category_id('bug'), '1.3.0';

end $$;
