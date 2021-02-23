/**
 * Usage:
 *   GET $API_HOST/dbqueries/stats/examples/flight-movements-by-account?[account_id=NNN]
 */

select
     a.id as account_id
    ,a.name as account_name
    ,count (fm.id) as flight_movement_count
from
    accounts a
left join
    flight_movements fm on a.id = fm.account
where
    case when :account_id is null then
        true
    else
        a.id = cast (:account_id as integer)
    end
 group by a.id
 order by 1
;
