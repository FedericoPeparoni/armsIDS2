-- add flight movements to kcaa invoice permits
DO $$
    BEGIN
        BEGIN
            ALTER TABLE kcaa_aatis_transactions ADD COLUMN flight_movement_id integer;
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column flight_movement_id already exists in kcaa_aatis_transactions.';
        END;
    END;
$$
