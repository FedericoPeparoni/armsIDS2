alter table invoice_templates add column invoice_filename varchar(128) not null default('file_name');
alter table invoice_templates alter column invoice_filename drop default;
