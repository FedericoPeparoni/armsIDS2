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
	    (1, 'DOMESTIC/NATIONAL',      1, 'DN', currency_id ('VTU'), currency_id ('VEF'), 'admin'),
	    (2, 'DOMESTIC/FOREIGN',       2, 'DF', currency_id ('USD'), currency_id ('USD'), 'admin'),
	    (3, 'INTERNATIONAL/NATIONAL', 3, 'IN', currency_id ('USD'), currency_id ('VEF'), 'admin'),
	    (4, 'INTERNATIONAL/FOREIGN',  4, 'IF', currency_id ('USD'), currency_id ('USD'), 'admin'),
	    (5, 'OVERFLIGHT/NATIONAL',    5, 'ON', currency_id ('USD'), currency_id ('VEF'), 'admin'),
	    (6, 'OVERFLIGHT/FOREIGN',     6, 'OF', currency_id ('USD'), currency_id ('USD'), 'admin')
;

-- Re-create category attirbutes.
INSERT INTO flightmovement_category_attributes (flightmovement_category, flight_type, flight_scope, flight_nationality, created_by)
    VALUES
        (1, 'DO', 'DO', 'NA', 'admin'),
        (2, 'DO', 'DO', 'FO', 'admin'),
        (3, 'AR', 'IN', 'NA', 'admin'),
        (3, 'DE', 'IN', 'NA', 'admin'),
        (4, 'AR', 'IN', 'FO', 'admin'),
        (4, 'DE', 'IN', 'FO', 'admin'),
        (5, 'OV', 'IN', 'NA', 'admin'),
        (6, 'OV', 'IN', 'FO', 'admin')
;
