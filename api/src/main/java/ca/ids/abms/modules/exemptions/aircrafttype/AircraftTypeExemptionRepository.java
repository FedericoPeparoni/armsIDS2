package ca.ids.abms.modules.exemptions.aircrafttype;

import ca.ids.abms.config.db.ABMSRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AircraftTypeExemptionRepository extends ABMSRepository<AircraftTypeExemption, Integer> {

    Page<AircraftTypeExemption> findAllByOrderByAircraftTypeAsc(Pageable pageable);

    @Query("SELECT ate FROM AircraftTypeExemption ate WHERE ate.aircraftType.aircraftType = :aircraftType")
    AircraftTypeExemption findOneByAircraftType(@Param("aircraftType") String aircraftType);
}
