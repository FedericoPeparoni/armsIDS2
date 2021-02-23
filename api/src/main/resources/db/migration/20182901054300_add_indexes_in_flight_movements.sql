drop index if exists flight_movements_account_i1;
create index flight_movements_account_i1
    on flight_movements (account);

drop index if exists flight_movements_billing_date_i1;
create index flight_movements_billing_date_i1
    on flight_movements (billing_date);

drop index if exists flight_movements_flight_id_i1;
create index flight_movements_flight_id_i1
    on flight_movements (flight_id);
