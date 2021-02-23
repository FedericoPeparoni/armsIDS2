/**
 * Usage:
 *   GET $API_HOST/dbqueries/stats/examples/users?[login=LOGIN]
 */

select * from users
 where
    case
        when :login is null then
            true
        else
            login like :login
    end
;