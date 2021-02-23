package ca.ids.abms.modules.exemptions.aircraftflights;

import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.modules.exemptions.ExemptionType;
import ca.ids.abms.modules.exemptions.ExemptionTypeProvider;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.util.models.ModelUtils;
import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Transactional
public class AircraftFlightsExemptionService implements ExemptionTypeProvider {

    private static final Logger LOG = LoggerFactory.getLogger(AircraftFlightsExemptionService.class);

    private AircraftFlightsExemptionRepository aircraftFlightsExemptionRepository;

    AircraftFlightsExemptionService(AircraftFlightsExemptionRepository aircraftFlightsExemptionRepository) {
        this.aircraftFlightsExemptionRepository = aircraftFlightsExemptionRepository;
    }

    public AircraftFlightsExemption create(AircraftFlightsExemption item) {

    	doValidate(item);

        return aircraftFlightsExemptionRepository.saveAndFlush(item);
    }

    public AircraftFlightsExemption update(Integer id, AircraftFlightsExemption item) {

        doValidate(item);

        try {

        	final AircraftFlightsExemption existingItem = aircraftFlightsExemptionRepository.getOne(id);

            ModelUtils.merge(item, existingItem, "id", "createdAt", "createdBy", "updateAt", "updatedBy");
            return aircraftFlightsExemptionRepository.saveAndFlush(existingItem);
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e,ErrorConstants.ERR_UPDATE_NO_LONGER_EXISTS);
        }
    }

    @Transactional(readOnly = true)
    public AircraftFlightsExemption getOne(Integer id) {
        return aircraftFlightsExemptionRepository.getOne(id);
    }

    @Transactional(readOnly = true)
    public Page<AircraftFlightsExemption> findAll(Pageable pageable, String searchFilter) {

    	// define filter spec with text search
        final FiltersSpecification.Builder filterBuilder = new FiltersSpecification.Builder()
            .lookFor(searchFilter);
        return aircraftFlightsExemptionRepository.findAll(filterBuilder.build(), pageable);
    }

    /**
     * Return applicable AircraftFlightsExemption by provided flight movement.
     */
    @Override
    @Transactional(readOnly = true)
    public Collection<ExemptionType> findApplicableExemptions(FlightMovement flightMovement) {
        Preconditions.checkArgument(flightMovement != null);

        String aircraftRegistration = flightMovement.getItem18RegNum();
        String flightId = flightMovement.getFlightId();
        LocalDateTime dateOfFlight = flightMovement.getDateOfFlight();

        Collection<ExemptionType> exemptions = new ArrayList<>();
        if (aircraftRegistration != null && flightId != null && dateOfFlight != null) {

            exemptions.addAll(findApplicableExemptionsByRegNumAndFlightId(
                aircraftRegistration, flightId, dateOfFlight));

        } else if (aircraftRegistration != null && dateOfFlight != null) {

            AircraftFlightsExemption exemption = aircraftFlightsExemptionRepository.findExemptionByAircraftRegistration(
                aircraftRegistration, dateOfFlight);
            if (exemption != null) exemptions.add(exemption);

        } else if (flightId !=null && dateOfFlight != null) {

            AircraftFlightsExemption exemption = aircraftFlightsExemptionRepository.findExemptionByFlightId(
                flightId, dateOfFlight);
            if (exemption != null) exemptions.add(exemption);
        }

        return exemptions;
    }

    public void delete(Integer id) {
        try {
            aircraftFlightsExemptionRepository.delete(id);
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e,ErrorConstants.ERR_DELETE_NO_LONGER_EXISTS);
        }
    }

    private void doValidate(AircraftFlightsExemption item) {

    	//both aircraftRegistration and flightId can't be null at the same time
        if(StringUtils.isBlank(item.getAircraftRegistration()) && StringUtils.isBlank(item.getFlightId())){
            throw ExceptionFactory.getInvalidDataException(ErrorConstants.ERR_VALIDATION, AircraftFlightsExemption.class, "aircraftRegistration", "flightId" );
        }

        //exemptionStartDate and exemptionEndDate are required. exemptionStartDate should be before exemptionEndDate
        if (item.getExemptionStartDate() == null) {
        	throw ExceptionFactory.getInvalidDataException(ErrorConstants.ERR_VALIDATION, AircraftFlightsExemption.class, "exemptionStartDate");
        }

        if (item.getExemptionEndDate() == null) {
        	throw ExceptionFactory.getInvalidDataException(ErrorConstants.ERR_VALIDATION, AircraftFlightsExemption.class, "exemptionEndDate");
        }

        if (item.getExemptionStartDate().isAfter(item.getExemptionEndDate())) {
        	throw ExceptionFactory.getInvalidDataException(ErrorConstants.ERR_VALIDATION, AircraftFlightsExemption.class, "exemptionStartDate");
        }

        // validate unique constraint, because Null values are not considered equal
        validateUniqueFields(item);
    }

    private void validateUniqueFields(AircraftFlightsExemption item) {
        if ((StringUtils.isBlank(item.getAircraftRegistration()) || StringUtils.isBlank(item.getFlightId())) && item.getExemptionStartDate() != null) {

            if (StringUtils.isNotBlank(item.getAircraftRegistration())) {
                checkUniqueFieldsByAircraftRegistration(item);
            }

            if (StringUtils.isNotBlank(item.getFlightId())) {
                checkUniqueFieldsByFlightId(item);
            }
        }
    }

    private void checkUniqueFieldsByAircraftRegistration(AircraftFlightsExemption item) {
        List<AircraftFlightsExemption> exemptions;
        LocalDateTime startDate = item.getExemptionStartDate();
        String aircraftRegistration = item.getAircraftRegistration();
        if (item.getId() == null) {
            exemptions = aircraftFlightsExemptionRepository.findExemptionsByAircraftRegistrationAndStartDate(aircraftRegistration, startDate);
        } else {
            exemptions = aircraftFlightsExemptionRepository.findExemptionsByAircraftRegistrationAndStartDate(aircraftRegistration, startDate, item.getId());
        }

        if (exemptions != null && !exemptions.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
            final String details = String.format("An Exemption with the Aircraft Registration: %s and Start Date: %s is already exist",
                aircraftRegistration, startDate.format(formatter));
            LOG.debug("Bad request: {}", details);
            throw new CustomParametrizedException(ErrorConstants.ERR_UNIQUENESS_VIOLATION, new Exception(details));
        }
    }

    private void checkUniqueFieldsByFlightId(AircraftFlightsExemption item) {
        List<AircraftFlightsExemption> exemptions;
        LocalDateTime startDate = item.getExemptionStartDate();
        String flightId = item.getFlightId();
        if (item.getId() == null) {
            exemptions = aircraftFlightsExemptionRepository.findExemptionsByFlightIdAndStartDate(flightId, startDate);
        } else {
            exemptions = aircraftFlightsExemptionRepository.findExemptionsByFlightIdAndStartDate(flightId, startDate, item.getId());
        }

        if (exemptions != null && !exemptions.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
            final String details = String.format("An Exemption with the Flight id: %s and Start Date: %s is already exist",
                flightId, startDate.format(formatter));
            LOG.debug("Bad request: {}", details);
            throw new CustomParametrizedException(ErrorConstants.ERR_UNIQUENESS_VIOLATION, new Exception(details));
        }
    }

    private Collection<AircraftFlightsExemption> findApplicableExemptionsByRegNumAndFlightId(
        final String regNum, final String flightId, final LocalDateTime dateOfFlight
    ) {
        Preconditions.checkArgument(regNum != null && flightId != null && dateOfFlight != null);

        AircraftFlightsExemption exemption = aircraftFlightsExemptionRepository.findExemptionByAircraftRegistrationAndFlightId(
            flightId, regNum, dateOfFlight);

        Collection<AircraftFlightsExemption> exemptions = new ArrayList<>();
        if (exemption != null) {
            exemptions.add(exemption);
        } else {

            // try to find by aircraftRegistration
            exemption = aircraftFlightsExemptionRepository.findExemptionByAircraftRegistration(regNum, dateOfFlight);
            if (exemption != null) exemptions.add(exemption);

            // try to find by flightId
            exemption = aircraftFlightsExemptionRepository.findExemptionByFlightId(flightId, dateOfFlight);
            if (exemption != null) exemptions.add(exemption);
        }

        return exemptions;
    }

    public long countAll() {
        return aircraftFlightsExemptionRepository.count();
    }
}
