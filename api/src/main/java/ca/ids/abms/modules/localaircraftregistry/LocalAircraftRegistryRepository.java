package ca.ids.abms.modules.localaircraftregistry;

import ca.ids.abms.config.db.ABMSRepository;

public interface LocalAircraftRegistryRepository extends ABMSRepository<LocalAircraftRegistry, Integer> {

    LocalAircraftRegistry findByRegistrationNumber(String registrationNumber);

}
