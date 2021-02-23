package ca.ids.abms.modules.aircraft;

import ca.ids.abms.config.db.ABMSRepository;

import java.util.List;

public interface AircraftTypeRepository extends ABMSRepository<AircraftType, Integer> {

    AircraftType findByAircraftType(String aircraftType);

    AircraftType findById(Integer id);

    List<AircraftType> findAllByOrderByAircraftTypeAsc();
}
