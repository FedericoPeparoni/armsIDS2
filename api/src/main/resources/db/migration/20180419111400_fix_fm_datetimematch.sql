DROP FUNCTION fm_datetimematch (flight_movements, timestamp with time zone, character varying, numeric, numeric);

CREATE OR REPLACE FUNCTION fm_datetimematch(
    _fm flight_movements,
    _date timestamp with time zone,
    _time character varying,
    _perct numeric,
    _min numeric)
  RETURNS boolean AS
$BODY$
declare
	_timeVal numeric;
	_timeRange numeric;
	_timeMin numeric;
	_timeMax numeric;
	_dateStart timestamp with time zone;
	_dateEnd timestamp with time zone;
begin
	if _fm.estimated_elapsed_time <> '' then
		-- calculate range value in minutes from percentage of flight movement estimated elapsed time
		_timeRange := to_number(substring(_fm.estimated_elapsed_time from 1 for 2), '99') * 60 +
            to_number(substring(_fm.estimated_elapsed_time from 3 for 2), '99') * (_perct / 100);
	end if;

	-- if calculated range is less then min, set range to min
	if _timeRange IS NULL or _timeRange < _min then
		_timeRange := _min;
	end if;

	-- convert lookup time value into numeric number of minutes
	_timeVal := to_number(substring(_time from 1 for 2), '99') * 60 + to_number(substring(_time from 3 for 2), '99');

	-- set min time value and max time value from _timeVal plus/minus _timeRange
	_timeMin := _timeVal - _timeRange;
	_timeMax := _timeVal + _timeRange;

	-- if min time is less then 0, calculate time and end date in past days from lookup date
	-- else, set start date to the lookup date
	if _timeMin < 0 then
		_dateStart := _date - trunc((@_timeMin - 1440) / 1440) * interval '1' day;
		_timeMin := 1440 - @_timeMin % 1440;
	else
		_dateStart := _date;
	end if;

	-- if max time is greater then 1440, calculate time and end date in future days from lookup date
	-- else, set end date to the lookup date
	if _timeMax > 1440 then
		_dateEnd := _date + trunc(_timeMax / 1440) * interval '1' day;
		_timeMax := _timeMax % 1440;
	else
		_dateEnd := _date;
	end if;

	-- if flight movement date of flight is outside calculated date range,
	-- return false for no match
	if _fm.date_of_flight::date < _dateStart::date or
	   _fm.date_of_flight::date > _dateEnd::date then
		return false;
	end if;

	-- convert flight movement dep time into numeric number of minutes
	_timeVal := to_number(substring(_fm.dep_time from 1 for 2), '99') * 60 + to_number(substring(_fm.dep_time from 3 for 2), '99');

	-- if flight movement date of flight is equal to calcualted min/max and dep time outside calcualted time range,
	-- return false for no match
	if (_fm.date_of_flight::date = _dateStart::date and _timeVal < _timeMin) or
	   (_fm.date_of_flight::date = _dateEnd::date and _timeVal > _timeMax) then
		return false;
	end if;

	-- else return true for a match within the date and time range
	return true;
end
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION fm_datetimematch(flight_movements, timestamp with time zone, character varying, numeric, numeric)
  OWNER TO abms;
