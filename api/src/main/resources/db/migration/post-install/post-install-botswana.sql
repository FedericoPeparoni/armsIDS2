-- Insert or update category 0 (OTHER). It may have been created by an earlier migration
-- script. We can't simply delete existing category = 0 because there may be flights
-- associated with it.
INSERT INTO flightmovement_categories (id, name, sort_order, short_name, enroute_result_currency_id, enroute_invoice_currency_id, created_by)
    VALUES
        (0, 'OTHER', 100, 'OT', currency_id ('USD'), currency_id ('USD'), 'admin')
    ON CONFLICT (ID) DO UPDATE SET
        name = EXCLUDED.name,
        sort_order = EXCLUDED.sort_order,
        short_name = EXCLUDED.short_name,
        enroute_result_currency_id = EXCLUDED.enroute_result_currency_id,
        enroute_invoice_currency_id = EXCLUDED.enroute_invoice_currency_id,
        updated_by = EXCLUDED.created_by,
        updated_at = now()
;

-- Delete all other categories and attributes. If there are flights with these categories, this delete statement
-- will fail; but in that cae we can't easily (re-)calculate the correct flight categories
-- here, anyway. So user will have to resolve such conflicts manually or something.
DELETE FROM flightmovement_category_attributes WHERE flightmovement_category <> 0;
DELETE FROM flightmovement_categories WHERE id <> 0;
-- Create categories
INSERT INTO flightmovement_categories (id, name, sort_order, short_name, enroute_result_currency_id, enroute_invoice_currency_id, created_by)
    VALUES
        (1, 'DOMESTIC',                1, 'DO', currency_id ('USD'), currency_id ('USD'), 'admin'),
        (2, 'INTERNATIONAL ARRIVAL',   2, 'IA', currency_id ('USD'), currency_id ('USD'), 'admin'),
        (3, 'INTERNATIONAL DEPARTURE', 3, 'ID', currency_id ('USD'), currency_id ('USD'), 'admin'),
        (4, 'OVERFLIGHT',              4, 'OV', currency_id ('USD'), currency_id ('USD'), 'admin')
;

-- Re-create category attirbutes.
INSERT INTO flightmovement_category_attributes (flightmovement_category, flight_type, flight_scope, flight_nationality, created_by)
    VALUES
        (1, 'DO', 'DO', null, 'admin'),
        (2, 'AR', 'IN', null, 'admin'),
        (3, 'DE', 'IN', null, 'admin'),
        (4, 'OV', 'IN', null, 'admin')
;
