create table certificates (
    id                                  serial          primary key,
    certificate_type_id                 int             not null references certificate_templates(id),
    certified_organization_or_person    varchar(100)    not null,
    certificate_image                   bytea,
    certificate_image_type              varchar(100),
    expiry_warning_issued               bool            not null default(false),
    date_of_issue                       timestamp       not null,
    date_of_expiry_warning              timestamp       not null,
    date_of_expiry                      timestamp       not null,
    created_at                          timestamp       not null default now(),
    created_by                          varchar(50)     not null,
    updated_at                          timestamp,
    updated_by                          varchar(50)
)
