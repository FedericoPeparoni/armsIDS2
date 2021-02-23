alter table currencies add allow_updated_from_web bool not null default false;
alter table currencies alter column allow_updated_from_web drop default;
