package ca.ids.abms.modules.unspecifiedaircraft;

import java.util.List;

import ca.ids.abms.config.db.FiltersSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.error.ErrorDTO;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.modules.util.models.ModelUtils;

@Service
@Transactional
public class UnspecifiedAircraftTypeService {

    private static final Logger LOG = LoggerFactory.getLogger(UnspecifiedAircraftTypeService.class);

    private UnspecifiedAircraftTypeRepository unspecifiedAircraftTypeRepository;

    public UnspecifiedAircraftTypeService(UnspecifiedAircraftTypeRepository unspecifiedAircraftTypeRepository){

        this.unspecifiedAircraftTypeRepository=unspecifiedAircraftTypeRepository;
    }

    @Transactional(readOnly = true)
    public Page<UnspecifiedAircraftType> findAll(final String search, final Pageable pageable) {
        LOG.debug("Request to find all UnspecifiedAircraftType with text search '{}'", search);

        // build filter by searchable text
        FiltersSpecification.Builder builder = new FiltersSpecification.Builder(search);

        // find all based on filter specifications and pageable
        return unspecifiedAircraftTypeRepository.findAll(builder.build(), pageable);
    }

    @Transactional(readOnly = true)
    public UnspecifiedAircraftType findOne(Integer id) {
        LOG.debug("Request to find UnspecifiedAircraftType by ID: {}",id);
        return unspecifiedAircraftTypeRepository.findOne(id);
    }

    @Transactional(readOnly = true)
    public UnspecifiedAircraftType findTextIdentifier(String textIdentifier) {
        LOG.debug("Request to find UnspecifiedAircraftType by TextIdentifier: {}",textIdentifier);
        return unspecifiedAircraftTypeRepository.findByTextIdentifier(textIdentifier);
    }

    @Transactional(readOnly = true)
    public List<UnspecifiedAircraftType> findAircraftType(String aircraftType) {
        LOG.debug("Request to find UnspecifiedAircraftType by aircraftType: {}",aircraftType);
        return unspecifiedAircraftTypeRepository.findByAircraftType(aircraftType);
    }

    public UnspecifiedAircraftType create(UnspecifiedAircraftType unspecifiedAircraftType) {
        LOG.debug("Request to create UnspecifiedAircraftType ");
        validate(unspecifiedAircraftType);
        return unspecifiedAircraftTypeRepository.save(unspecifiedAircraftType);

    }

    private void validate (final UnspecifiedAircraftType aircraftType) {
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
        }
    }

    public void delete(Integer id) {
        LOG.debug("Request to delete UnspecifiedAircraftType : {}", id);
        try {
            unspecifiedAircraftTypeRepository.delete(id);
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e,ErrorConstants.ERR_DELETE_NO_LONGER_EXISTS);
        }
    }

    public UnspecifiedAircraftType update(Integer id, UnspecifiedAircraftType unspecifiedAircraftType) {
        LOG.debug("Request to unspecifiedAircraftType : {}", unspecifiedAircraftType);
        validate(unspecifiedAircraftType);
        UnspecifiedAircraftType uat = null;
        try {
            UnspecifiedAircraftType existingUnspecifiedAircraftType = unspecifiedAircraftTypeRepository.getOne(id);
            ModelUtils.merge(unspecifiedAircraftType, existingUnspecifiedAircraftType, "id");
            uat = unspecifiedAircraftTypeRepository.save(existingUnspecifiedAircraftType);
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e,ErrorConstants.ERR_UPDATE_NO_LONGER_EXISTS);
        }
        return uat;
    }

    public long countAllUnspecifiedAircraftTypes() {
        return unspecifiedAircraftTypeRepository.count();
    }

}
