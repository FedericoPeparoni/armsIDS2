select fra.aerodrome_identifier, fr.* 
from abms.flight_reassignments fr left join abms.flight_reassignment_aerodromes fra on fr.id = fra.flight_reassignment_id 
where fr.identifier_text = :identifier_text
	and
	     cast(:flight_date AS date) between cast(fr.reassignment_start_date as date) and cast(fr.reassignment_end_date as date)
	and
	    case
	        when :applies_to_type_arrival is not null then
	            fr.applies_to_type_arrival = cast(:applies_to_type_arrival AS boolean)
	        else
	            true
	    end
	and
	    case
	        when :applies_to_type_departure is not null then
	            fr.applies_to_type_departure = cast(:applies_to_type_departure AS boolean)
	        else
	            true
	    end
	and
	    case
	        when :applies_to_type_domestic is not null then
	            fr.applies_to_type_domestic = cast(:applies_to_type_domestic AS boolean)
	        else
	            true
	    end
	and
	    case
	        when :applies_to_type_overflight is not null then
	            fr.applies_to_type_overflight = cast(:applies_to_type_overflight AS boolean)
	        else
	            true
	    end
	and
	    case
	        when :applies_to_scope_domestic is not null then
	            fr.applies_to_scope_domestic = cast(:applies_to_scope_domestic AS boolean)
	        else
	            true
	    end
	and
	    case
	        when :applies_to_scope_regional is not null then
	            fr.applies_to_scope_regional = cast(:applies_to_scope_regional AS boolean)
	        else
	            true
	    end
	and
	    case
	        when :applies_to_scope_international is not null then
	            fr.applies_to_scope_international = cast(:applies_to_scope_international AS boolean)
	        else
	            true
	    end
	and
	    case
	        when :applies_to_nationality_national is not null then
	            fr.applies_to_nationality_national = cast(:applies_to_nationality_national AS boolean)
	        else
	            true
	    end
	and
	    case
	        when :applies_to_nationality_foreign is not null then
	            fr.applies_to_nationality_foreign = cast(:applies_to_nationality_foreign AS boolean)
	        else
	            true
	    end
	and
	    case
	        when (fra.aerodrome_identifier is not null and :aerodrome_dep is not null) then
	            fra.aerodrome_identifier = :aerodrome_dep
	        else
	            1=1
	    end
	and
	    case
	        when (fra.aerodrome_identifier is not null and :aerodrome_dest is not null) then
	            fra.aerodrome_identifier = :aerodrome_dest
	        else
	            1=1
	    end
	and fr.identification_type = :identification_type
;
