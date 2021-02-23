package ca.ids.abms.modules.aerodromeserviceoutages;

import ca.ids.abms.config.db.ABMSRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AerodromeServiceOutageRepository extends ABMSRepository<AerodromeServiceOutage, Integer> {

    @Query("SELECT aso FROM AerodromeServiceOutage aso JOIN aso.aerodrome a WHERE a.id = :id")
    List<AerodromeServiceOutage> findAllByAerodromeId(final @Param("id") Integer id);


    @Query("SELECT aso FROM AerodromeServiceOutage aso WHERE aso.aerodrome.id = :aerodromeId AND aso.aerodromeServiceType.id = :serviceTypeId")
    List<AerodromeServiceOutage> findAllByAerodromeAndServiceType(final @Param("aerodromeId") Integer aerodromeId,
                                                                  final @Param("serviceTypeId") Integer serviceTypeId);


    @Query(
        "SELECT aso FROM AerodromeServiceOutage aso " +
            "WHERE ( ?1 <= aso.endDateTime " +
                "and ?2 >= aso.startDateTime " +
                "and aso.aerodromeServiceTypeMap.id.aerodrome.id = ?3 " +
                "and aso.aerodromeServiceTypeMap.id.aerodromeServiceType.id = ?4 )"
    )
    List<AerodromeServiceOutage> getOverlapsDates(LocalDateTime startDate,
                                                  LocalDateTime endDate,
                                                  Integer aerodromeId,
                                                  Integer aerodromeServiceTypeId);

    @Query(
        "SELECT aso FROM AerodromeServiceOutage aso " +
            "WHERE ( ?1 <= aso.endDateTime " +
            "and ?2 >= aso.startDateTime " +
            "and aso.aerodromeServiceTypeMap.id.aerodrome.id = ?3 " +
            "and aso.aerodromeServiceTypeMap.id.aerodromeServiceType.id = ?4 " +
            "and aso.id != ?5)"
    )
    List<AerodromeServiceOutage> getOverlapsDates(LocalDateTime startDate,
                                                  LocalDateTime endDate,
                                                  Integer aerodromeId,
                                                  Integer aerodromeServiceTypeId,
                                                  Integer id);

    @Query(
        "SELECT aso FROM AerodromeServiceOutage aso " +
            "WHERE aso.aerodrome.aerodromeName = ?1 " +
            "and ?2 BETWEEN aso.startDateTime and aso.endDateTime"
    )
    List<AerodromeServiceOutage> getAerodromeServiceOutagesByAerodromeAndDateOfFlight(String aerodromeName, LocalDateTime dateTime);
}
