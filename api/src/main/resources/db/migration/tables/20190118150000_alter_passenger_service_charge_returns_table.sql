ALTER TABLE passenger_service_charge_returns
    ADD COLUMN arriving_pax_domestic_airport integer,
    ADD COLUMN landing_pax_domestic_airport integer,
    ADD COLUMN transfer_pax_domestic_airport integer,
    ADD COLUMN departing_pax_domestic_airport integer,
    ADD COLUMN arriving_child_domestic_airport integer,
    ADD COLUMN landing_child_domestic_airport integer,
    ADD COLUMN transfer_child_domestic_airport integer,
    ADD COLUMN departing_child_domestic_airport integer,
    ADD COLUMN exempt_arriving_pax_domestic_airport integer,
    ADD COLUMN exempt_landing_pax_domestic_airport integer,
    ADD COLUMN exempt_transfer_pax_domestic_airport integer,
    ADD COLUMN exempt_departing_pax_domestic_airport integer,
    ADD COLUMN loaded_goods double precision,
    ADD COLUMN discharged_goods double precision,
    ADD COLUMN loaded_mail double precision,
    ADD COLUMN discharged_mail double precision;

