create table invoice_templates (
    id                    serial primary key,
    invoice_template_name varchar(100) unique not null,
    invoice_category      varchar(50) not null,
    template_document     bytea not null,
    created_at            timestamp    not null default now(),
    created_by            varchar(50)  not null,
    updated_at            timestamp,
    updated_by            varchar(50)
);
