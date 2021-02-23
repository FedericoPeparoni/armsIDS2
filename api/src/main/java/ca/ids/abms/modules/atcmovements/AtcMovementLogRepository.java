package ca.ids.abms.modules.atcmovements;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import ca.ids.abms.config.db.ABMSRepository;

public interface AtcMovementLogRepository extends ABMSRepository<AtcMovementLog, Integer> {

    AtcMovementLog findByFlightIdAndDateOfContact (String flightId, LocalDate dateOfContact);

    AtcMovementLog findByFlightId (String flightId);

    List<AtcMovementLog> findByFlightIdAndDateOfContactAndFirEntryTimeAndFirMidTimeAndFirExitTimeOrderByDateOfContactDescFirEntryTimeDesc (String flightId, LocalDateTime dateOfContact,  String firEntryTime, String firMidTime, String firExitTime);
}
