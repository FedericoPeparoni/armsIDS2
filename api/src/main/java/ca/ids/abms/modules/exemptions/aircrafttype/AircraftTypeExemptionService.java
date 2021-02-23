package ca.ids.abms.modules.exemptions.aircrafttype;

import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.modules.exemptions.ExemptionType;
import ca.ids.abms.modules.exemptions.ExemptionTypeProvider;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.modules.aircraft.AircraftType;
import ca.ids.abms.modules.aircraft.AircraftTypeRepository;
import ca.ids.abms.modules.util.models.ModelUtils;

import java.util.ArrayList;
import java.util.Collection;

@Service
@Transactional
public class AircraftTypeExemptionService implements ExemptionTypeProvider {

    private AircraftTypeExemptionRepository aircraftTypeExemptionRepository;
    private AircraftTypeRepository aircraftTypeRepository;

    AircraftTypeExemptionService(AircraftTypeExemptionRepository anAircraftTypeExemptionRepository,
                                 AircraftTypeRepository anAircraftTypeRepository) {
        aircraftTypeExemptionRepository = anAircraftTypeExemptionRepository;
        aircraftTypeRepository = anAircraftTypeRepository;
    }

    public AircraftTypeExemption create(AircraftTypeExemption item, String type) {
        final AircraftType aircraftType = this.aircraftTypeRepository.findByAircraftType(type);
        item.setAircraftType(aircraftType);
        return aircraftTypeExemptionRepository.save(item);
    }

    public void delete(Integer id) {
        try {
            aircraftTypeExemptionRepository.delete(id);
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e,ErrorConstants.ERR_DELETE_NO_LONGER_EXISTS);
        }
    }

    @Transactional(readOnly = true)
    public Page<AircraftTypeExemption> findAll(Pageable pageable, final String textSearch) {
        final FiltersSpecification.Builder filterBuilder = new FiltersSpecification.Builder().lookFor(textSearch);
        return aircraftTypeExemptionRepository.findAll(filterBuilder.build(), pageable);
    }

    @Transactional(readOnly = true)
    public AircraftTypeExemption getOne(Integer id) {
        return aircraftTypeExemptionRepository.getOne(id);
    }

    public AircraftTypeExemption update(Integer id, AircraftTypeExemption item, String type) {
        try {
            final AircraftTypeExemption existingItem = aircraftTypeExemptionRepository.getOne(id);
            ModelUtils.merge(item, existingItem, "id", "aircraftType");
            final AircraftType aircraftType = this.aircraftTypeRepository.findByAircraftType(type);
            existingItem.setAircraftType(aircraftType);
            return aircraftTypeExemptionRepository.save(existingItem);
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e,ErrorConstants.ERR_UPDATE_NO_LONGER_EXISTS);
        }
    }

    /**
     * Return applicable AircraftTypeExemption by provided flight movement.
     */
    @Override
    @Transactional(readOnly = true)
    public Collection<ExemptionType> findApplicableExemptions(FlightMovement flightMovement) {
        Preconditions.checkArgument(flightMovement != null);

        Collection<ExemptionType> exemptions = new ArrayList<>();
        if (StringUtils.isNotBlank(flightMovement.getAircraftType())) {

            // find exemption by aircraft type value
            AircraftTypeExemption exemptionType = aircraftTypeExemptionRepository
                .findOneByAircraftType(flightMovement.getAircraftType());

            // only add to collection if not null
            if (exemptionType != null) {
                exemptions.add(exemptionType);
            }
        }

        return exemptions;
    }

    public long countAll() {
        return aircraftTypeExemptionRepository.count();
    }
}
