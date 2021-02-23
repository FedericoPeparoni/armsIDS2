package ca.ids.abms.modules.exemptions.flightstatus;

import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.modules.common.enumerators.FlightItemType;
import ca.ids.abms.modules.exemptions.ExemptionType;
import ca.ids.abms.modules.exemptions.ExemptionTypeProvider;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.modules.util.models.ModelUtils;

import java.util.ArrayList;
import java.util.Collection;

@Service
@Transactional
public class ExemptFlightStatusService implements ExemptionTypeProvider {

    private final ExemptFlightStatusRepository exemptFlightStatusRepository;

    ExemptFlightStatusService(
        final ExemptFlightStatusRepository exemptFlightStatusRepository
    ) {
        this.exemptFlightStatusRepository = exemptFlightStatusRepository;
    }

    public ExemptFlightStatus create(ExemptFlightStatus item) {
        return exemptFlightStatusRepository.saveAndFlush(item);
    }

    public ExemptFlightStatus update(Integer id, ExemptFlightStatus item) {
        try {
            final ExemptFlightStatus existingItem = exemptFlightStatusRepository.getOne(id);
            if (existingItem != null && (
                    !item.getFlightItemType().equals(existingItem.getFlightItemType()) ||
                    !item.getFlightItemValue().equals(existingItem.getFlightItemValue())
                ) && exists(item)
            ) {
                throw new CustomParametrizedException(ErrorConstants.ERR_UNIQUENESS_VIOLATION_FLIGHT_TYPE_VALUE);
            }

            ModelUtils.merge(item, existingItem, "id", "createdAt", "createdBy", "updateAt", "updatedBy");
            return exemptFlightStatusRepository.saveAndFlush(existingItem);
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e,ErrorConstants.ERR_UPDATE_NO_LONGER_EXISTS);
        }
    }

    @Transactional(readOnly = true)
    public ExemptFlightStatus getOne(Integer id) {
        return exemptFlightStatusRepository.getOne(id);
    }

    @Transactional(readOnly = true)
    public Page<ExemptFlightStatus> findAll(final Pageable pageable, final String textSearch) {
        final FiltersSpecification.Builder filterBuilder = new FiltersSpecification.Builder().lookFor(textSearch);
        return exemptFlightStatusRepository.findAll(filterBuilder.build(), pageable);
    }

    public void delete(Integer id) {
        try {
            exemptFlightStatusRepository.delete(id);
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e,ErrorConstants.ERR_DELETE_NO_LONGER_EXISTS);
        }
    }

    @Transactional(readOnly = true)
    public boolean exists(ExemptFlightStatus item) {
        ExemptFlightStatus exemptFlightStatus = new ExemptFlightStatus();
        exemptFlightStatus.setFlightItemType(item.getFlightItemType());
        exemptFlightStatus.setFlightItemValue(item.getFlightItemValue());

        Example<ExemptFlightStatus> example = Example.of(exemptFlightStatus);
        return exemptFlightStatusRepository.exists(example);
    }

    /**
     * Return applicable ExemptFlightStatus by provided flight movement.
     */
    @Override
    @Transactional(readOnly = true)
    public Collection<ExemptionType> findApplicableExemptions(FlightMovement flightMovement) {
        Preconditions.checkArgument(flightMovement != null);

        Collection<ExemptionType> exemptions = new ArrayList<>();

        if (StringUtils.isNotBlank(flightMovement.getItem18Status())) {
            exemptions.addAll(exemptFlightStatusRepository.findAllByFlightItemTypeAndFlightItemValue(
                FlightItemType.ITEM18_STS, flightMovement.getItem18Status()));
        }

        if (StringUtils.isNotBlank(flightMovement.getItem18Rmk())) {
            exemptions.addAll(exemptFlightStatusRepository.findAllByFlightItemTypeAndFlightItemValue(
                FlightItemType.ITEM18_RMK, flightMovement.getItem18Rmk()));
        }

        if (StringUtils.isNotBlank(flightMovement.getItem18RegNum())) {
            exemptions.addAll(exemptFlightStatusRepository.findAllByFlightItemTypeAndFlightItemValue(
                FlightItemType.ITEM8_TYPE, flightMovement.getItem18RegNum()));
        }

        return exemptions;
    }

    public long countAll() {
        return exemptFlightStatusRepository.count();
    }
}
