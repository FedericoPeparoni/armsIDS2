package ca.ids.abms.modules.towermovements;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.time.LocalDateTime;
import java.util.List;

public interface TowerMovementLogRepository extends JpaRepository<TowerMovementLog, Integer>,
    JpaSpecificationExecutor {

    TowerMovementLog findByFlightId (String flightId);

    List<TowerMovementLog> findByFlightIdAndDateOfContactAndDestinationContactTime(String flightId,
                                                                                   LocalDateTime dateOfContact,
                                                                                   String destinationContactTime);

    List<TowerMovementLog> findByFlightIdAndDateOfContactAndDepartureContactTime(String flightId,
                                                                                  LocalDateTime dateOfContact,
                                                                                  String departureContactTime);

    TowerMovementLog findByFlightIdAndDateOfContactAndDepartureContactTimeAndDestinationContactTime(String flightId,
                                                                                 LocalDateTime dateOfContact,
                                                                                 String departureContactTime,
                                                                                 String destinationContactTime);
}
