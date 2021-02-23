package ca.ids.abms.modules.interestrates;

import ca.ids.abms.modules.interestrates.enumerate.AppliedRate;
import ca.ids.abms.modules.interestrates.enumerate.SpecifiedRate;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class InterestRateServiceTest {

    private InterestRateRepository interestRateRepository;
    private InterestRateService interestRateService;

    @Before
    public void setup() {
        interestRateRepository = mock(InterestRateRepository.class);
        interestRateService = new InterestRateService(interestRateRepository);
    }

    @Test
    public void createInterestRate() {
        InterestRate interestRate = new InterestRate();

        interestRate.setDefaultInterestSpecification(SpecifiedRate.MONTHLY);
        interestRate.setDefaultInterestApplication(AppliedRate.DAILY);
        interestRate.setDefaultInterestGracePeriod(0);
        interestRate.setDefaultForeignInterestSpecifiedPercentage(15.0);
        interestRate.setDefaultNationalInterestSpecifiedPercentage(25.0);
        interestRate.setDefaultForeignInterestAppliedPercentage(1.25);
        interestRate.setDefaultNationalInterestAppliedPercentage(1.5);
        interestRate.setPunitiveInterestSpecification(SpecifiedRate.MONTHLY);
        interestRate.setPunitiveInterestApplication(AppliedRate.DAILY);
        interestRate.setPunitiveInterestGracePeriod(10);
        interestRate.setPunitiveInterestSpecifiedPercentage(15.0);
        interestRate.setPunitiveInterestAppliedPercentage(1.22);
        interestRate.setStartDate(LocalDate.now());

        List<InterestRate> rates = Collections.singletonList(interestRate);

        when(interestRateRepository.save(any(InterestRate.class))).thenReturn(interestRate);
        when(interestRateRepository.findAllByOrderByStartDateDesc()).thenReturn(rates);

        InterestRate result = interestRateService.save(interestRate);
        assertThat(result.getDefaultInterestSpecification()).isEqualTo(interestRate.getDefaultInterestSpecification());
        assertThat(result.getDefaultInterestApplication()).isEqualTo(interestRate.getDefaultInterestApplication());
        assertThat(result.getDefaultInterestGracePeriod()).isEqualTo(interestRate.getDefaultInterestGracePeriod());
        assertThat(result.getDefaultNationalInterestSpecifiedPercentage()).isEqualTo(interestRate.getDefaultNationalInterestSpecifiedPercentage());
        assertThat(result.getPunitiveInterestSpecification()).isEqualTo(interestRate.getPunitiveInterestSpecification());
        assertThat(result.getStartDate()).isEqualTo(interestRate.getStartDate());
        assertThat(result.getEndDate()).isEqualTo(interestRate.getEndDate());
    }

    @Test
    public void updateInterestRate() {
        InterestRate existingInterestRate = new InterestRate();

        existingInterestRate.setId(1);
        existingInterestRate.setDefaultInterestSpecification(SpecifiedRate.MONTHLY);
        existingInterestRate.setDefaultInterestApplication(AppliedRate.DAILY);
        existingInterestRate.setDefaultInterestGracePeriod(0);
        existingInterestRate.setDefaultForeignInterestSpecifiedPercentage(15.0);
        existingInterestRate.setDefaultNationalInterestSpecifiedPercentage(25.0);
        existingInterestRate.setDefaultForeignInterestAppliedPercentage(1.25);
        existingInterestRate.setDefaultNationalInterestAppliedPercentage(1.5);
        existingInterestRate.setPunitiveInterestSpecification(SpecifiedRate.MONTHLY);
        existingInterestRate.setPunitiveInterestApplication(AppliedRate.DAILY);
        existingInterestRate.setPunitiveInterestGracePeriod(10);
        existingInterestRate.setPunitiveInterestSpecifiedPercentage(15.0);
        existingInterestRate.setPunitiveInterestAppliedPercentage(1.22);
        existingInterestRate.setStartDate(LocalDate.now());

        InterestRate interestRate = new InterestRate();
        interestRate.setId(1);
        interestRate.setDefaultInterestSpecification(SpecifiedRate.YEARLY);
        interestRate.setDefaultInterestApplication(AppliedRate.MONTHLY);
        interestRate.setDefaultInterestGracePeriod(5);
        interestRate.setDefaultForeignInterestSpecifiedPercentage(15.0);
        interestRate.setDefaultNationalInterestSpecifiedPercentage(25.0);
        interestRate.setDefaultForeignInterestAppliedPercentage(1.25);
        interestRate.setDefaultNationalInterestAppliedPercentage(1.5);
        interestRate.setPunitiveInterestSpecification(SpecifiedRate.MONTHLY);
        interestRate.setPunitiveInterestApplication(AppliedRate.DAILY);
        interestRate.setPunitiveInterestGracePeriod(10);
        interestRate.setPunitiveInterestSpecifiedPercentage(15.0);
        interestRate.setPunitiveInterestAppliedPercentage(1.22);
        interestRate.setStartDate(LocalDate.now());

        List<InterestRate> rates = Collections.singletonList(existingInterestRate);

        when(interestRateRepository.getOne(any())).thenReturn(existingInterestRate);
        when(interestRateRepository.save(any(InterestRate.class))).thenReturn(existingInterestRate);
        when(interestRateRepository.findAllByOrderByStartDateDesc()).thenReturn(rates);

        InterestRate result = interestRateService.update(1, interestRate);
        assertThat(result.getDefaultInterestSpecification()).isEqualTo(SpecifiedRate.YEARLY);
        assertThat(result.getDefaultInterestApplication()).isEqualTo(AppliedRate.MONTHLY);
        assertThat(result.getDefaultInterestGracePeriod()).isEqualTo(5);
    }

    @Test
    public void deleteInterestRate(){
        getInterestRateById();
        interestRateService.delete(1);
        verify(interestRateRepository).delete(any(Integer.class));
    }

    @Test
    public void getInterestRateById() {
        InterestRate interestRate = new InterestRate();
        interestRate.setId(1);

        when(interestRateRepository.getOne(any())).thenReturn(interestRate);

        InterestRate result = interestRateService.getOne(1);
        assertThat(result).isEqualTo(interestRate);
    }

    @Test
    public void getAllInterestRates() {
        List<InterestRate> rates = Collections.singletonList(new InterestRate());

        when(interestRateRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(rates));

        Page<InterestRate> results = interestRateService.findAll(mock(Pageable.class));
        assertThat(results.getTotalElements()).isEqualTo(rates.size());
    }

    @Test
    public void validateInterestRate() {

        // validate start date
        assertThatThrownBy(() -> {
            InterestRate interestRate = new InterestRate();
            interestRate.setStartDate(LocalDate.of(2019, 01, 29));

            InterestRate existingInterestRate = new InterestRate();
            existingInterestRate.setStartDate(LocalDate.of(2019, 01, 29));

            when(interestRateRepository.getExistingInterestRateByStartDate(interestRate.getStartDate()))
                .thenReturn(existingInterestRate);

            interestRateService.validate(interestRate, null);
        }).hasMessageMatching("ERR_UNIQUENESS_VIOLATION").hasCause(new Exception("An interest rate with this start date already exists"));


        // validate that grace period is more then 0
        assertThatThrownBy(() -> {
            InterestRate interestRate = new InterestRate();
            interestRate.setDefaultInterestGracePeriod(-1);
            interestRate.setPunitiveInterestGracePeriod(2);

            interestRateService.validate(interestRate, null);
        }).hasMessageMatching("DEF_ERR_VALIDATION").hasCause(new Exception("Grace period should be equal or more than 0"));

        // validate that Punitive Grace Period is more than Default Grace Period
        assertThatThrownBy(() -> {
            InterestRate interestRate = new InterestRate();
            interestRate.setDefaultInterestGracePeriod(5);
            interestRate.setPunitiveInterestGracePeriod(5);

            interestRateService.validate(interestRate, null);
        }).hasMessageMatching("DEF_ERR_VALIDATION").hasCause(new Exception("Punitive Interest Grace Period should be more than Default Interest Grace Period"));

        // validate percentage
        assertThatThrownBy(() -> {
            InterestRate interestRate = new InterestRate();
            interestRate.setDefaultInterestGracePeriod(0);
            interestRate.setPunitiveInterestGracePeriod(5);
            interestRate.setPunitiveInterestSpecifiedPercentage(-8.0);
            interestRate.setDefaultForeignInterestSpecifiedPercentage(5.0);
            interestRate.setDefaultNationalInterestSpecifiedPercentage(6.5);

            interestRateService.validate(interestRate, null);
        }).hasMessageMatching("DEF_ERR_VALIDATION").hasCause(new Exception("Percentage should be equal or more than 0"));
    }
}
