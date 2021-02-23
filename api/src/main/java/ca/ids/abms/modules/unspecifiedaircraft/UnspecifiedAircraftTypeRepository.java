package ca.ids.abms.modules.unspecifiedaircraft;

import ca.ids.abms.config.db.ABMSRepository;

import java.util.List;


public interface UnspecifiedAircraftTypeRepository extends ABMSRepository<UnspecifiedAircraftType, Integer> {

    public UnspecifiedAircraftType findByTextIdentifier(String textIdentifier);

    public List<UnspecifiedAircraftType> findByAircraftType(String aircraftType);

}
