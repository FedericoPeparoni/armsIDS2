package ca.ids.abms.modules.flight;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ca.ids.abms.config.db.ABMSRepository;
import ca.ids.abms.modules.accounts.Account;

public interface FlightScheduleRepository extends ABMSRepository<FlightSchedule, Integer> {

    List<FlightSchedule> findByAccount(Account aAccount);

    @Modifying
    @Query("UPDATE FlightSchedule fs SET fs.activeIndicator = :active WHERE fs.startDate < :date and fs.activeIndicator is null")
    int updateActiveIndicatorBeforeDate(@Param("active") String active, @Param("date") LocalDateTime date);

    List<FlightSchedule> findByFlightServiceNumber(String flightServiceNumber);

    @Query (value = "SELECT COUNT(fs) FROM FlightSchedule fs JOIN fs.account ac JOIN ac.accountUsers au")
    long countAllSelfCareFlightSchedules();

    @Query (value = "SELECT COUNT(fs) FROM FlightSchedule fs JOIN fs.account ac JOIN ac.accountUsers au WHERE au.id = :userId")
    long countAllSelfCareFlightSchedules(@Param("userId") Integer userId);
}
