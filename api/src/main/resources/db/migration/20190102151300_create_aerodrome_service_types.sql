create table aerodrome_service_types
(
    id                                      serial primary key,
    service_name                            varchar(50) NOT NULL unique,
    service_outage_approach_discount_type   varchar(30) NOT NULL,
    service_outage_approach_amount          double precision NOT NULL,
    service_outage_aerodrome_discount_type  varchar(30) NOT NULL,
    service_outage_aerodrome_amount         double precision NOT NULL,
    default_flight_notes                    varchar(255) NOT NULL,
    version                                 bigint DEFAULT 0 NOT NULL,
    created_at                              timestamp with time zone NOT NULL DEFAULT now(),
    created_by                              varchar(50) NOT NULL,
    updated_at                              timestamp with time zone,
    updated_by                              varchar(50)
);

insert into aerodrome_service_types
    (service_name, service_outage_approach_discount_type, service_outage_approach_amount, service_outage_aerodrome_discount_type,
    service_outage_aerodrome_amount, default_flight_notes, created_at, created_by)
    values ('Instrument Landing System (ILS)', 'percentage', 50, 'percentage', 50, 'ILS outage', now(), 'system');

insert into aerodrome_service_types
    (service_name, service_outage_approach_discount_type, service_outage_approach_amount, service_outage_aerodrome_discount_type,
    service_outage_aerodrome_amount, default_flight_notes, created_at, created_by)
    values ('Radar', 'percentage', 50, 'percentage', 50, 'Radar outage', now(), 'system');
