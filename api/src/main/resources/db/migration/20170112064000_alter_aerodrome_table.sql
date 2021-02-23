-- Alter table aerodromes
alter table aerodromes add column is_default_billing_center bool not null default false;
alter table aerodromes add column billing_center_id int not null references billing_centers (id);
alter table aerodromes add column aerodromecategory_id int not null references aerodromecategories (id);

drop table aerodrome_aerodromecategories;