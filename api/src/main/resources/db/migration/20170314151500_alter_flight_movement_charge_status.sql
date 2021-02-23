update flight_movements
  set enroute_charges_status = null,
      passenger_charges_status = null,
      other_charges_status = null;

alter table flight_movements
    add constraint flight_movement_enroute_charges_status_ck1
        check (enroute_charges_status in ('PENDING', 'INVOICED', 'CANCELED'));

alter table flight_movements
    add constraint flight_movement_passenger_charges_status_ck1
        check (passenger_charges_status in ('PENDING', 'INVOICED', 'CANCELED'));

alter table flight_movements
    add constraint flight_movement_other_charges_status_ck1
        check (other_charges_status in ('PENDING', 'INVOICED', 'CANCELED'));
