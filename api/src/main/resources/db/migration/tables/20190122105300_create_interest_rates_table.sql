create table interest_rates(
	id                  	                        serial primary key,
	default_interest_specification                  varchar(30) NOT NULL,
	default_interest_application                    varchar(30) NOT NULL,
	default_interest_grace_period                   integer NOT NULL,
	default_foreign_interest_specified_percentage   double precision NOT NULL,
	default_national_interest_specified_percentage  double precision NOT NULL,
	default_foreign_interest_applied_percentage     double precision NOT NULL,
	default_national_interest_applied_percentage    double precision NOT NULL,
	punitive_interest_specification                 varchar(30) NOT NULL,
	punitive_interest_application                   varchar(30) NOT NULL,
	punitive_interest_grace_period                  integer NOT NULL,
	punitive_interest_specified_percentage          double precision NOT NULL,
	punitive_interest_applied_percentage            double precision NOT NULL,
	start_date                                      date NOT NULL UNIQUE,
	end_date                                        date,
	version                                         bigint DEFAULT 0 NOT NULL,
    created_at                                      timestamp with time zone NOT NULL DEFAULT now(),
    created_by                                      varchar(50) NOT NULL,
    updated_at                                      timestamp with time zone,
    updated_by                                      varchar(50)
);
