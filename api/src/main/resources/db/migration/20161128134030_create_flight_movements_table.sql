DROP TABLE IF EXISTS  flight_movement;
DROP SEQUENCE IF EXISTS  flight_movement_seq;

CREATE SEQUENCE flight_movements_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

create table flight_movements
(
    id bigint DEFAULT nextval('flight_movements_id_seq'::regclass) NOT NULL,
    created_at 		    		timestamp with time zone not null default now(),
    updated_at 		    		timestamp with time zone,
    created_by                  varchar(50),
    updated_by                  varchar(50),
    status				    	varchar(20) not null,
    movement_type 			    varchar(20) not null,
    spatia_fpl_object_id	    bigint,
    date_of_flight			    timestamp with time zone not null,

    flight_id 				    varchar(7) not null,
    flight_type 			    varchar(2),
    dep_time  				    varchar(4) not null,
    dep_ad    				    varchar(4) not null,
    dest_ad   				    varchar(4) not null,
    item18_reg_num 			    varchar (20),
    fpl_route     			    varchar(2000),
    aircraft_type 			    varchar(4),
    wake_turb 				    varchar(1),
    item18_status  			    varchar(20),
    item18_dep     			    varchar(50),
    item18_dest    			    varchar(50),
    initial_fpl_data 		    varchar(4000),
    actual_departure_time 	    varchar(4),
    arrival_time 			    varchar(4),
    arrival_ad 				    varchar(4),
    other_info                  varchar(1800),

    fpl_route_geom  		    public.geometry(Geometry,4326),
    entry_point 			    public.geometry(Geometry,4326),
    exit_point  			    public.geometry(Geometry,4326),
    entry_time 				    timestamp with time zone,
    exit_time  				    timestamp with time zone,
    billable_route 			    public.geometry(Geometry,4326),
    radar_route 			    public.geometry(Geometry,4326),

    fpl_crossing_distance 	    numeric(10,2),
    radar_crossing_distance     numeric(10,2),
    nominal_crossing_distance   numeric(10,2),
    fpl_crossing_cost 		    numeric(10,2),
    radar_crossing_cost 	    numeric(10,2),
    nominal_crossing_cost 	    numeric(10,2),

    actual_mtow  			    numeric(10,2),
    average_mass_factor 	    numeric(10,2),

    associated_aircraft 	    varchar(20),

    enroute_charges 		    numeric(10,2),
    arrival_charges 		    numeric(10,2),
    departure_charges 		    numeric(10,2),
    parking_charges 		    numeric(10,2),
    total_charges   		    numeric(10,2),
    flight_notes			    varchar(100),

    passengers_transit_adult        int,
    passengers_transit_child        int,
    passengers_joining_adult        int,
    passengers_joining_child        int,
    passengers_chargeable_domestic	int,
    passengers_chargeable_intern 	int,
    prepaid_amount                  numeric(10,2),
    crew_members          			int
);

-- primary key
alter table flight_movements
    add constraint flight_movements_pk
        primary key (id)
;
