create table uploaded_files (
    id serial primary key,
    file_content bytea,
    file_name varchar(200) not null,
    file_record_type varchar(50) not null,
    file_type varchar(50) not null,
    manual_upload bool not null,
    records_created bigint,
    records_in_file bigint,
    records_rejected bigint,
    records_unused bigint,
    records_updated bigint,
    last_modified timestamp,
    created_at timestamp not null default now(),
    created_by varchar(50) not null,
    updated_at timestamp,
    updated_by varchar(50)
);
