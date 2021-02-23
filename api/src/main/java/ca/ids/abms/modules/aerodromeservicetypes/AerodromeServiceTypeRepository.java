package ca.ids.abms.modules.aerodromeservicetypes;

import ca.ids.abms.config.db.ABMSRepository;

public interface AerodromeServiceTypeRepository extends ABMSRepository<AerodromeServiceType, Integer> {

    AerodromeServiceType findByServiceName(String name);
}
