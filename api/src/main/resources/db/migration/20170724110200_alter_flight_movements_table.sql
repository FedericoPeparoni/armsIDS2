alter table flight_movements add column billing_center_id int references billing_centers (id);

alter table flight_movements
   add constraint flight_movements_billing_center_fkey
       foreign key (billing_center_id) references billing_centers (id);
