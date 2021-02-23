package ca.ids.abms.modules.interestrates;

import ca.ids.abms.config.db.ABMSRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface InterestRateRepository extends ABMSRepository<InterestRate, Integer> {

    @Query("SELECT ir FROM InterestRate ir WHERE ?1 = ir.startDate")
    InterestRate getExistingInterestRateByStartDate(LocalDate startDate);

    @Query("SELECT ir FROM InterestRate ir WHERE ?1 = ir.startDate and ir.id != ?2")
    InterestRate getExistingInterestRateByStartDate(LocalDate startDate, Integer id);

    @Modifying
    @Query("UPDATE InterestRate ir SET ir.endDate = :endDate where ir.id = :id")
    void updateEndDateById(@Param("id") Integer id,
                           @Param("endDate") LocalDate endDate);

    @Query(value = "SELECT ir.id FROM interest_rates ir WHERE ir.start_date < ?1 " +
                    "ORDER BY ir.start_date DESC LIMIT 1", nativeQuery = true)
    Integer getRecordWithPreviousStartDate(LocalDate startDate);

    List<InterestRate> findAllByOrderByStartDateDesc();

    @Query("SELECT ir FROM InterestRate ir WHERE ir.startDate BETWEEN ?1 AND ?2 ORDER BY ir.startDate")
    List<InterestRate> getInterestRatesByStartDateAndEndDate(LocalDate start, LocalDate end);

    @Query("SELECT ir FROM InterestRate ir WHERE ?1 >= ir.startDate ORDER BY ir.startDate")
    List<InterestRate> getInterestRatesFromStartDate(LocalDate start);

    @Query("SELECT ir FROM InterestRate ir WHERE ?1 BETWEEN ir.startDate AND ir.endDate OR ?1 >= ir.startDate AND ir.endDate IS NULL")
    InterestRate getInterestRateByStartDate(LocalDate startDate);
}
