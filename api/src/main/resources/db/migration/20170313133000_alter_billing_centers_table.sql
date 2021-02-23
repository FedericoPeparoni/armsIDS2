alter table billing_centers add column is_hq boolean default false not null;
create unique index billing_centers_hq_unique on billing_centers (is_hq) where is_hq = true;
