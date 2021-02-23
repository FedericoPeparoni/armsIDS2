CREATE TABLE
    exempt_aircraft_flights
    (
        id SERIAL NOT NULL,		
		aircraft_registration  CHARACTER VARYING(10),
		flight_id  CHARACTER VARYING(10),		
        enroute_fees_exempt BOOLEAN DEFAULT false NOT NULL,
        parking_fees_exempt BOOLEAN DEFAULT false NOT NULL,
        flight_notes CHARACTER VARYING(255) NOT NULL,
        approach_fees_exempt BOOLEAN DEFAULT false NOT NULL,
        aerodrome_fees_exempt BOOLEAN DEFAULT false NOT NULL,
        late_arrival_fees_exempt BOOLEAN DEFAULT false NOT NULL,
        late_departure_fees_exempt BOOLEAN DEFAULT false NOT NULL,
        version BIGINT DEFAULT 0 NOT NULL,
        domestic_pax BOOLEAN DEFAULT false NOT NULL,
        international_pax BOOLEAN DEFAULT false NOT NULL,
		exemption_start_date  TIMESTAMP(6) WITH TIME ZONE NOT NULL,
		exemption_end_date  TIMESTAMP(6) WITH TIME ZONE NOT NULL,
        created_at TIMESTAMP(6) WITH TIME ZONE DEFAULT now() NOT NULL,
        created_by CHARACTER VARYING(50) NOT NULL,
        updated_at TIMESTAMP(6) WITH TIME ZONE,
        updated_by CHARACTER VARYING(50),		
        PRIMARY KEY (id),        
		CONSTRAINT unique_fields_eaf UNIQUE (aircraft_registration, flight_id)
    );