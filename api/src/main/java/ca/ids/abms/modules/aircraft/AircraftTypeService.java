package ca.ids.abms.modules.aircraft;

import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.error.ErrorDTO;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.modules.exemptions.aircrafttype.AircraftTypeExemption;
import ca.ids.abms.modules.exemptions.aircrafttype.AircraftTypeExemptionRepository;
import ca.ids.abms.modules.util.models.ModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AircraftTypeService {

    private static final Logger LOG = LoggerFactory.getLogger(AircraftTypeService.class);

    private AircraftTypeRepository aircraftTypeRepository;
    private AircraftTypeExemptionRepository aircraftTypeExemptionRepository;
    private AircraftRegistrationRepository aircraftRegistrationRepository;

    public AircraftTypeService(AircraftTypeRepository aAircraftTypeRepository, AircraftRegistrationRepository aAircraftRegistrationRepository, AircraftTypeExemptionRepository aAircraftTypeExemptionRepository) {
        aircraftTypeRepository = aAircraftTypeRepository;
        aircraftRegistrationRepository = aAircraftRegistrationRepository;
        aircraftTypeExemptionRepository = aAircraftTypeExemptionRepository;
    }

    public AircraftType createAircraftType(AircraftType aircraftType) {
        return aircraftTypeRepository.save(aircraftType);

    }

    public void delete(Integer id) {
        LOG.debug("Request to delete AircraftType : {}", id);
        try {
            if (validateDelete(id)) {
                aircraftTypeRepository.delete(id);
            }
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e, ErrorConstants.ERR_DELETE_NO_LONGER_EXISTS);

        }
    }

    private boolean validateDelete(Integer id) {
        LOG.debug("Validate delete : {}", id);

        List<AircraftRegistration> aircraftRegistrations = aircraftRegistrationRepository
            .findAircraftRegistrationByAircraftTypeId(id);
        AircraftType at = aircraftTypeRepository.findById(id);
        AircraftTypeExemption aircraftTypeExemption = null;
        if (at != null) {
            aircraftTypeExemption = aircraftTypeExemptionRepository
                .findOneByAircraftType(at.getAircraftType());
        }

        if (aircraftTypeExemption == null && aircraftRegistrations.isEmpty()) {
            return true;
        } else if (!aircraftRegistrations.isEmpty()) {
            LOG.debug("Bad request: The Aircraft Type is used in Aircraft Registration: {}", at);
            throw new CustomParametrizedException(ErrorConstants.ERR_DEPENDENCY_VIOLATION,
                    new Exception("The Aircraft Type cannot be deleted, it is used in Aircraft Registration"));
        } else {
            LOG.debug("Bad request: The Aircraft Type is used in Exempt Aircraft Types: {}", at);
            throw new CustomParametrizedException(ErrorConstants.ERR_DEPENDENCY_VIOLATION,
                    new Exception("The Aircraft Type cannot be deleted, it is used in Exempt Aircraft Types"));
        }
    }

    @Transactional(readOnly = true)
    public List<AircraftType> findAll() {
        LOG.debug("Request to get all Aircraft Types");
        return aircraftTypeRepository.findAllByOrderByAircraftTypeAsc();
    }

    @Transactional(readOnly = true)
    public Page<AircraftType> findAll(Pageable pageable, String searchText) {
        LOG.debug("Request to get aircraftTypes");
        final FiltersSpecification.Builder filterBuilder = new FiltersSpecification.Builder()
            .lookFor(searchText);
        return aircraftTypeRepository.findAll(filterBuilder.build(), pageable);
    }

    @Transactional(readOnly = true)
    public AircraftType getOne(Integer id) {
        LOG.debug("Request to get AircraftType : {}", id);
        return aircraftTypeRepository.getOne(id);
    }

    public AircraftType save(AircraftType aircraftType) {
        LOG.debug("Request to save AircraftType : {}", aircraftType);
        validateForCreate(aircraftType);
        return aircraftTypeRepository.save(aircraftType);
    }

    public AircraftType update(Integer id, AircraftType aircraftType) {
        LOG.debug("Request to update AircraftType : {}", aircraftType);
        AircraftType at = null;
        try {
            validateForUpdate(id, aircraftType);
            AircraftType existingAircraftType = aircraftTypeRepository.getOne(id);
            ModelUtils.merge(aircraftType, existingAircraftType);
            at = aircraftTypeRepository.save(existingAircraftType);
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e,ErrorConstants.ERR_UPDATE_NO_LONGER_EXISTS);
        }
        return at;
    }

    private void validateForUpdate(Integer id, AircraftType aircraftType) {
        LOG.debug("Validate update : {}", aircraftType);
        validateForCreate(aircraftType);
        AircraftType existingAircraftType = aircraftTypeRepository.getOne(id);
        if (!aircraftType.getAircraftType().equals(existingAircraftType.getAircraftType())) {
            AircraftTypeExemption aircraftTypeExemption =
                aircraftTypeExemptionRepository.findOneByAircraftType(existingAircraftType.getAircraftType());
            if (aircraftTypeExemption != null) {
                LOG.debug("Bad request: The Aircraft Type is used in Exempt Aircraft Types {}", aircraftTypeExemption);
                throw new CustomParametrizedException(ErrorConstants.ERR_DEPENDENCY_VIOLATION,
                    new Exception("The Aircraft Type cannot be updated, it is used in Exempt Aircraft Types"));
            }
        }
    }

    private void validateForCreate (final AircraftType aircraftType) {
        if (aircraftType.getAircraftType() != null) {
            final String aircraftTypeNormalized = aircraftType.getAircraftType().toUpperCase().trim();
            if (aircraftTypeNormalized.length() > 4) {
                throw new ErrorDTO.Builder(ErrorConstants.ERR_VALIDATION)
                .appendDetails("The aircraft type is not valid")
                .addInvalidField(aircraftType.getClass(), "aircraftType", "not valid or too long", aircraftTypeNormalized)
                .buildInvalidDataException();
            } else {
                aircraftType.setAircraftType(aircraftTypeNormalized);
            }
        } else {
            throw new ErrorDTO.Builder(ErrorConstants.ERR_VALIDATION)
                .appendDetails("The aircraft type is missing")
                .addInvalidField(aircraftType.getClass(), "aircraftType", "is required")
                .buildInvalidDataException();
        }
    }

    @Transactional(readOnly = true)
    public AircraftType findByAircraftType(String aircraftTypeStr){
        AircraftType aircraftType=null;
        if(aircraftTypeStr!=null && !aircraftTypeStr.isEmpty()){
            aircraftType=aircraftTypeRepository.findByAircraftType(aircraftTypeStr);
        }
        return aircraftType;
    }

    long countAllAircraftTypes() {
        return aircraftTypeRepository.count();
    }
}
