ALTER table passenger_service_charge_returns
    ADD COLUMN account_id integer references accounts (id)