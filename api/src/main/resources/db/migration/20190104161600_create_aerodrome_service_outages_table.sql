create table aerodrome_service_outages(
	id                  		serial primary key,
	aerodrome_id        		integer NOT NULL references aerodromes (id),
	aerodrome_service_type_id   integer NOT NULL references aerodrome_service_types (id),
	start_date_time			    timestamp with time zone NOT NULL,
	end_date_time			    timestamp with time zone NOT NULL,
	approach_discount_type		varchar(30) NOT NULL,
	approach_discount_amount	double precision NOT NULL,
	aerodrome_discount_type		varchar(30) NOT NULL,
	aerodrome_discount_amount	double precision NOT NULL,
	flight_notes			    varchar(255) NOT NULL,
	version                     bigint DEFAULT 0 NOT NULL,
    created_at                  timestamp with time zone NOT NULL DEFAULT now(),
    created_by                  varchar(50) NOT NULL,
    updated_at                  timestamp with time zone,
    updated_by                  varchar(50),
    FOREIGN KEY (aerodrome_id, aerodrome_service_type_id) REFERENCES aerodrome_services (aerodrome_id, service_type_id)
);
