package ca.ids.abms.modules.aerodromeoperationalhours;

import ca.ids.abms.config.db.ABMSRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AerodromeOperationalHoursRepository extends ABMSRepository<AerodromeOperationalHours, Integer> {

    @Query("SELECT aoh FROM AerodromeOperationalHours aoh WHERE aerodrome.aerodromeName = :name")
    AerodromeOperationalHours getAerodromeOperationalHoursByAerodromeName(final @Param("name") String name);
}
