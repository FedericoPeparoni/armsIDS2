-- See comment below
create or replace function try_parse_aviation_time_of_day (time_of_day varchar, dflt time with time zone default null)
returns time with time zone
language plpgsql
immutable
as $$
begin
	-- don't parse null values
	if time_of_day is not null then
	    -- make sure its 4 digits, HHMM
        if time_of_day ~ '^[0-9]{4}$' then
            -- cast the string to Postgres time type; the HHMM format
            -- is directly supported by Postgres type cast in this case
            begin
	            return cast (time_of_day as time with time zone);
	        -- ignore all errors
	        exception
	            when others then
	               null;
            end;
        end if;
       raise debug 'failed to parse "%" as aviation time value', time_of_day;
    end if;
    -- if we got here something went wrong => return provided default
    return dflt;
end
$$;

comment on function try_parse_aviation_time_of_day (time_of_day varchar, dflt time with time zone) is
$$Try to parse an aviation-style time-of-day string (HHMM) as a PostgreSQL "time with time zone" type.
If supplied string cannot be parsed, retruns the supplied default value, or null if none provided.

Example: try_parse_aviation_time_of_day ('0234') -- returns Postgres time value "02:34:00"

$$
;