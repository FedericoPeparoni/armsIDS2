-- Alter table users
alter table users add column billing_center_id int references billing_centers (id);
