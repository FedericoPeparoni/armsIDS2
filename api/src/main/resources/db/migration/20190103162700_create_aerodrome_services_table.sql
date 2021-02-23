create table aerodrome_services(
    aerodrome_id        integer NOT NULL references aerodromes (id),
    service_type_id     integer NOT NULL references aerodrome_service_types (id)
);


alter table aerodrome_services
    add constraint aerodrome_service_type_pkey PRIMARY KEY (aerodrome_id, service_type_id)
