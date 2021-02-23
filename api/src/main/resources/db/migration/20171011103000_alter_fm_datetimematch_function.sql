drop function if exists FM_DateTimeMatch(flight_movements, timestamp with time zone, character varying(4), numeric, numeric);

create function FM_DateTimeMatch(_fm flight_movements, _date timestamp with time zone, _time character varying(4), _perct numeric, _min numeric)
returns boolean as $$
declare
	_timeVal numeric;
	_timeRange numeric;
	_timeMin numeric;
	_timeMax numeric;
	_dateStart timestamp with time zone;
	_dateEnd timestamp with time zone;
begin

	-- calculate range value in minutes from percentage of flight movement estimated elapsed time
	_timeRange := ((substring(_fm.estimated_elapsed_time from 1 for 2)::numeric * 60) + substring(_fm.estimated_elapsed_time from 3 for 2)::numeric) * (_perct / 100);

	-- if calculated range is less then min, set range to min
	if _timeRange IS NULL or _timeRange < _min then
		_timeRange := _min;
	end if;

	-- convert lookup time value into numeric number of minutes
	_timeVal := (substring(_time from 1 for 2)::numeric * 60) + substring(_time from 3 for 2)::numeric;

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
	_timeVal := (substring(_fm.dep_time from 1 for 2)::numeric * 60) + substring(_fm.dep_time from 3 for 2)::numeric;

	-- if flight movement date of flight is equal to calcualted min/max and dep time outside calcualted time range,
	-- return false for no match
	if (_fm.date_of_flight::date = _dateStart::date and _timeVal < _timeMin) or
	   (_fm.date_of_flight::date = _dateEnd::date and _timeVal > _timeMax) then
		return false;
	end if;

	-- else return true for a match within the date and time range
	return true;
end
$$ language plpgsql;
