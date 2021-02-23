package ca.ids.abms.modules.charges;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExternalChargeCategoryRepository extends JpaRepository<ExternalChargeCategory, Integer> {

    ExternalChargeCategory findByName(final String name);

    @Query(value="SELECT eCC  FROM ExternalChargeCategory eCC WHERE eCC.name NOT IN ('ENROUTE', 'IATA')")
    List<ExternalChargeCategory> findAllNonAviation();
}
