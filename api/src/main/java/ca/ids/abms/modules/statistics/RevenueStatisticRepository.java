package ca.ids.abms.modules.statistics;

import ca.ids.abms.config.db.ABMSRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RevenueStatisticRepository extends ABMSRepository<RevenueStatistic, Integer> {

    RevenueStatistic findByName(String name);

    /**
     * Find all RevenueStatistic names
     */
    @Query(value = "SELECT revenue.name FROM RevenueStatistic revenue ORDER BY name ASC")
    List<String> findAllNames ();
}
