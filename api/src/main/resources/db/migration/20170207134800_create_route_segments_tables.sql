create table route_segments
(
    id                      serial primary key,
    flight_record_id        bigint references flight_movements (id),
    segment_type            varchar(20),
    segment_number          int,
    location                public.geometry(Geometry,4326),
    segment_start_label     varchar(255),
    segment_lenght          double precision,
    segment_cost            double precision,


    created_at 		    	timestamp with time zone not null default now(),
    updated_at 		    	timestamp with time zone,
    created_by              varchar(50),
    updated_by              varchar(50)
);

