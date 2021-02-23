
alter table flight_movements
    drop constraint flight_movement_enroute_charges_status_ck1;
update flight_movements
   set enroute_charges_status = null
 where enroute_charges_status = 'CANCELED'
;
alter table flight_movements
    add constraint flight_movement_enroute_charges_status_ck1
        check (enroute_charges_status in ('PENDING', 'INVOICED', 'PAID'));

alter table flight_movements
    drop constraint flight_movement_passenger_charges_status_ck1;
update flight_movements
   set passenger_charges_status = null
 where passenger_charges_status = 'CANCELED'
;
alter table flight_movements
    add constraint flight_movement_passenger_charges_status_ck1
        check (passenger_charges_status in ('PENDING', 'INVOICED', 'PAID'));

alter table flight_movements
    drop constraint flight_movement_other_charges_status_ck1;
update flight_movements
   set other_charges_status = null
 where other_charges_status = 'CANCELED'
;
alter table flight_movements
    add constraint flight_movement_other_charges_status_ck1
        check (other_charges_status in ('PENDING', 'INVOICED', 'PAID'));
