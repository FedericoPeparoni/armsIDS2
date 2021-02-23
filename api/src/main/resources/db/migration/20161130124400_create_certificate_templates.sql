create table certificate_templates (
    id                          serial primary key,
    certificate_template_name   varchar(50) not null,
    license_duration            int not null,
    expiry_warning_interval     int not null,
    template_document           bytea,
    template_document_type      varchar(100),
    created_at                  timestamp    not null default now(),
    created_by                  varchar(50)  not null,
    updated_at                  timestamp,
    updated_by                  varchar(50)
)
