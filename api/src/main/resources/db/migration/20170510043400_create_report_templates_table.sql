create table report_templates (
    id                   serial primary key,
    report_template_name varchar(50) unique not null,
    sql_query            varchar(200) not null,
    template_document    bytea,
    mime_type            varchar(100) not null,
    parameters           varchar(200) not null,
    report_filename      varchar(128) not null,
    created_at           timestamp not null default now(),
    created_by           varchar(50) not null,
    updated_at           timestamp,
    updated_by           varchar(50)
)
