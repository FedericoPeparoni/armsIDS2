-- drop old datetimematch function without EET param if still exists
DROP FUNCTION IF EXISTS fm_datetimematch (flight_movements, timestamp with time zone, character varying, numeric, numeric);

-- create or replace new datetimematch function with EET param
CREATE OR REPLACE FUNCTION fm_datetimematch(
    _fm flight_movements,
    _date timestamp with time zone,
    _time character varying,
    _estimated_elapsed_time character varying,
    _perct numeric,
    _min numeric)
  RETURNS boolean AS
$BODY$
declare
    _timeDep numeric;
	_timeVal numeric;
	_dateFrom timestamp with time zone;
	_dateTo timestamp with time zone;
	_timeFrom numeric;
	_timeTo numeric;
	_timeRange numeric;
	_timeMin numeric;
	_timeMax numeric;
	_dateStart timestamp with time zone;
	_dateEnd timestamp with time zone;
begin

    -- convert fm dep time and lookup time into numeric number of minutes for comparison
    _timeDep := to_number(substring(_fm.dep_time from 1 for 2), '99') * 60 +
                to_number(substring(_fm.dep_time from 3 for 2), '99');
    _timeVal := to_number(substring(_time from 1 for 2), '99') * 60 +
                to_number(substring(_time from 3 for 2), '99');

    -- if fm earlier then lookup, match using fm values
    -- else if fm later or equal to lookup, match using lookup values
    if (_fm.date_of_flight::date = _date::date and _timeDep < _timeVal) or
       (_fm.date_of_flight::date < _date::date) then
        _estimated_elapsed_time := _fm.estimated_elapsed_time;
        _dateFrom := _fm.date_of_flight;
        _timeFrom := _timeDep;
        _dateTo := _date;
        _timeTo := _timeVal;
    else
        _dateFrom := _date;
        _timeFrom := _timeVal;
        _dateTo := _fm.date_of_flight;
        _timeTo := _timeDep;
    end if;

    -- calculate range value in minutes from percentage of estimated elapsed time
    if _estimated_elapsed_time <> '' then
        _timeRange := (
            to_number(substring(_estimated_elapsed_time from 1 for 2), '99') * 60 +
            to_number(substring(_estimated_elapsed_time from 3 for 2), '99')
        ) * (_perct / 100);
    end if;

    -- if calculated range is less then min, set range to min
    if _timeRange IS NULL or _timeRange < _min then
        _timeRange := _min;
    end if;

    -- set min time value and max time value from _timeFrom and _timeRange
    _timeMin := _timeFrom;
    _timeMax := _timeFrom + _timeRange;

    -- if min time is less then 0, calculate time and end date in past days from fm date of flight
    -- else, set start date to the fm date of flight
    if _timeMin < 0 then
        _dateStart := _dateFrom::date - trunc((@_timeMin - 1440) / 1440) * interval '1' day;
        _timeMin := 1440 - @_timeMin % 1440;
    else
        _dateStart := _dateFrom::date;
    end if;

    -- if max time is greater then 1440, calculate time and end date in future days from fm date of flight
    -- else, set end date to the fm date of flight
    if _timeMax > 1440 then
        _dateEnd := _dateFrom::date + trunc(_timeMax / 1440) * interval '1' day;
        _timeMax := _timeMax % 1440;
    else
        _dateEnd := _dateFrom::date;
    end if;

    -- if lookup date is outside calculated date range,
    -- return false for no match
    if _dateTo::date < _dateStart::date or
       _dateTo::date > _dateEnd::date then
        return false;
    end if;

    -- if lookup date is equal to calculated min/max and time outside calculated time range,
    -- return false for no match
    if (_dateTo::date = _dateStart::date and _timeTo < _timeMin) or
       (_dateTo::date = _dateEnd::date and _timeTo > _timeMax) then
        return false;
    end if;

    -- else return true for a match within the date and time range
    return true;

end
$BODY$
  LANGUAGE plpgsql;
