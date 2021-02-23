alter table invoice_line_items
    add column user_unit_amount double precision,
    add column user_percentage double precision,
    add column user_price double precision,
    add column user_electricity_meter_reading double precision,
    add column user_water_meter_reading double precision,
    add column user_discount_percentage double precision,
    add column user_town_id integer,
    add constraint invoice_line_items_user_town_fk
        foreign key (user_town_id) references utilities_towns_and_villages (id)
;

