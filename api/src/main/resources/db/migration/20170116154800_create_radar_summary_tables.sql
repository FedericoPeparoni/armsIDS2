DROP TABLE IF EXISTS  radar_summaries;
create table radar_summaries
(
    id                      serial primary key,
	date                    timestamp with time zone,
	flight_identifier       varchar(20),
	day_of_flight           timestamp with time zone,
	dep_time 				varchar(4),
	dest_time 				varchar(4),
	registration_number     varchar(100),
	aircraft_type           varchar(4),
	dep_ad                  varchar(4),
    dest_ad                 varchar(4),
	route         		    varchar(2000),
    entry_point			    varchar(255),
	entry_time              varchar(4),
    exit_point 			    varchar(255),
	exit_time  				varchar(4),
    flight_type             varchar(10),
    flight_rule             varchar(10),
	flight_category         varchar(20),

    created_at 		    	timestamp with time zone not null default now(),
    updated_at 		    	timestamp with time zone,
    created_by              varchar(50),
    updated_by              varchar(50)
);

