package ca.ids.abms.modules.statistics;

import ca.ids.abms.config.db.ABMSRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AirTrafficStatisticRepository extends ABMSRepository<AirTrafficStatistic, Integer> {

    AirTrafficStatistic findByName(String name);

    /**
     * Find all AirTrafficStatistic names
     */
    @Query(nativeQuery = true, value =
        "SELECT name " +
        "FROM air_traffic_statistics " +
        "ORDER BY name ASC"
    )
    List<String> findAllNames ();
}
