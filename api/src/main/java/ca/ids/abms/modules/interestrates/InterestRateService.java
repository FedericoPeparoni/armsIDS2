package ca.ids.abms.modules.interestrates;

import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.modules.util.models.ModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class InterestRateService {

    private InterestRateRepository interestRateRepository;
    private static final Logger LOG = LoggerFactory.getLogger(InterestRateService.class);

    public InterestRateService(InterestRateRepository interestRateRepository) {
        this.interestRateRepository = interestRateRepository;
    }

    @Transactional(readOnly = true)
    public List<InterestRate> findAll() {
        LOG.debug("Request to get all Interest Rates");
        return interestRateRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<InterestRate> findAll(Pageable pageable) {
        LOG.debug("Request to get all Interest Rates");
        return interestRateRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<InterestRate> findAll(Pageable pageable, String textSearch) {
        LOG.debug("Request to get Interest Rates by text search. Search: {}", textSearch);
        final FiltersSpecification.Builder filterBuilder = new FiltersSpecification.Builder().lookFor(textSearch);
        return interestRateRepository.findAll(filterBuilder.build(), pageable);
    }

    @Transactional(readOnly = true)
    public InterestRate getOne(Integer id) {
        LOG.debug("Request to get Interest Rate by id : {}", id);
        return interestRateRepository.getOne(id);
    }

    public InterestRate save(InterestRate interestRate) {
        LOG.debug("Request to save Interest Rate : {}", interestRate);
        validate(interestRate, null);
        InterestRate result = interestRateRepository.save(interestRate);
        updateAllEndDates();
        return result;
    }

    public void delete(Integer id) {
        LOG.debug("Request to delete Interest Rate : {}", id);

        InterestRate deletedInterestRate = interestRateRepository.getOne(id);
        LocalDate endDate = deletedInterestRate.getEndDate();

        // return id of the record that has the closest
        // start date before the start date of the deleted InterestRate
        Integer previousStartDateId = interestRateRepository.getRecordWithPreviousStartDate(deletedInterestRate.getStartDate());

        interestRateRepository.delete(id);

        if (previousStartDateId != null) {
            interestRateRepository.updateEndDateById(previousStartDateId, endDate);
        }
    }

    public InterestRate update(Integer id, InterestRate interestRate) {
        LOG.debug("Request to update Interest Rate : {}", interestRate);
        validate(interestRate, id);
        try {
            InterestRate existingInterestRate = interestRateRepository.getOne(id);
            ModelUtils.merge(interestRate, existingInterestRate);
            InterestRate result = interestRateRepository.save(existingInterestRate);
            updateAllEndDates();
            return result;
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e, ErrorConstants.ERR_UPDATE_NO_LONGER_EXISTS);
        }
    }

    public void validate(InterestRate interestRate, Integer id) {

        // validate start date
        LocalDate sd = interestRate.getStartDate();
        InterestRate overlaps = id == null
            ? interestRateRepository.getExistingInterestRateByStartDate(sd)
            : interestRateRepository.getExistingInterestRateByStartDate(sd, id);
        if (overlaps != null) {
            LOG.debug("Bad request: An interest rate with the start date: {} is already exist", sd);
            final String details = "An interest rate with this start date already exists";
            throw new CustomParametrizedException(ErrorConstants.ERR_UNIQUENESS_VIOLATION, new Exception(details));
        }

        // validate grace period
        if (interestRate.getDefaultInterestGracePeriod() < 0 || interestRate.getPunitiveInterestGracePeriod() < 0) {
            LOG.debug("Bad request: grace period is less than 0");
            final String details = "Grace period should be equal or more than 0";
            throw new CustomParametrizedException(ErrorConstants.DEF_ERR_VALIDATION, new Exception(details));
        }

        if (interestRate.getDefaultInterestGracePeriod() >= interestRate.getPunitiveInterestGracePeriod()) {
            LOG.debug("Bad request: Punitive Interest Grace Period is less than Default Interest Grace Period");
            final String details = "Punitive Interest Grace Period should be more than Default Interest Grace Period";
            throw new CustomParametrizedException(ErrorConstants.DEF_ERR_VALIDATION, new Exception(details));
        }

        // validate percentage
        if (interestRate.getDefaultForeignInterestSpecifiedPercentage() < 0 || interestRate.getDefaultNationalInterestSpecifiedPercentage() < 0
            || interestRate.getPunitiveInterestSpecifiedPercentage() < 0) {
            LOG.debug("Bad request: Specified Percentage is less than 0");
            final String details = "Percentage should be equal or more than 0";
            throw new CustomParametrizedException(ErrorConstants.DEF_ERR_VALIDATION, new Exception(details));
        }
    }

    private void updateAllEndDates() {
        List<InterestRate> list = interestRateRepository.findAllByOrderByStartDateDesc();
        list.get(0).setEndDate(null);
        LocalDate startDate = null;
        for (InterestRate rate : list) {
            if (startDate != null
                    && (rate.getEndDate() == null || !rate.getEndDate().equals(startDate.minusDays(1)))) {
                Integer id = rate.getId();
                LOG.debug("Update endDate for Interest Rate with id: {}", id);
                interestRateRepository.updateEndDateById(id, startDate.minusDays(1));
            }
            startDate = rate.getStartDate();
        }
    }

    public List<InterestRate> getInterestRatesByStartDateAndEndDate(LocalDate start, LocalDate end){
        return interestRateRepository.getInterestRatesByStartDateAndEndDate(start, end);
    }

    public boolean existInterestRatesFromStartDate(LocalDate start){
        List<InterestRate> rates = interestRateRepository.getInterestRatesFromStartDate(start);
        return !(rates == null || rates.isEmpty());
    }

    public InterestRate getInterestRateByStartDate(LocalDate start){
        return interestRateRepository.getInterestRateByStartDate(start);
    }

    public long countAll() {
        return interestRateRepository.count();
    }
}
