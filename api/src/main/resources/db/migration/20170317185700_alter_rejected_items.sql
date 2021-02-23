alter table rejected_items drop column if exists record_text;
alter table rejected_items add column raw_text varchar(1024) not null;
alter table rejected_items add column json_text varchar(2048);
