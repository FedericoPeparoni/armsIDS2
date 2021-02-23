-- delete duplicate records from nominal_routes table,
-- adapted from https://stackoverflow.com/a/18949/2646348
delete from nominal_routes where id in (
	select nominal_routes.id from nominal_routes
	left outer join (
	    select min(id) as id, pointa, pointb
	      from nominal_routes
	     group by pointa, pointb
	) keep_rows on
	    nominal_routes.id = keep_rows.id
	where
	    keep_rows.id is null
)
;

-- drop existing constraint (created manually on ubidev04)
alter table nominal_routes
    drop constraint if exists nominal_routes_uniq1;

-- create constraint
alter table nominal_routes
    add constraint nominal_routes_uniq1 unique (pointa, pointb)
;

