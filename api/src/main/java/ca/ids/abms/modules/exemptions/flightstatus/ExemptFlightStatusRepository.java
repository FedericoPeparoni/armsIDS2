package ca.ids.abms.modules.exemptions.flightstatus;

import ca.ids.abms.config.db.ABMSRepository;
import ca.ids.abms.modules.common.enumerators.FlightItemType;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
interface ExemptFlightStatusRepository extends ABMSRepository<ExemptFlightStatus, Integer> {

    Collection<ExemptFlightStatus> findAllByFlightItemTypeAndFlightItemValue(
        FlightItemType flightItemType, String flightItemValue);
}
