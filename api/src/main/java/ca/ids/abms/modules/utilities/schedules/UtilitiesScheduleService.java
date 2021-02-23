package ca.ids.abms.modules.utilities.schedules;

import java.util.Collection;
import java.util.Set;

import ca.ids.abms.config.db.Filter;
import ca.ids.abms.config.db.FiltersSpecification;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.modules.util.models.ModelUtils;
import ca.ids.abms.modules.utilities.towns.UtilitiesTownsAndVillage;

@Service
@Transactional
public class UtilitiesScheduleService {

    private UtilitiesScheduleRepository utilitiesScheduleRepository;
    private UtilitiesRangeBracketRepository utilitiesRangeBracketRepository;

    public UtilitiesScheduleService (UtilitiesScheduleRepository utilitiesScheduleRepository,
                                     UtilitiesRangeBracketRepository utilitiesRangeBracketRepository) {
        this.utilitiesScheduleRepository = utilitiesScheduleRepository;
        this.utilitiesRangeBracketRepository = utilitiesRangeBracketRepository;
    }

    public UtilitiesSchedule createUtilitiesSchedule (final UtilitiesSchedule utilitiesSchedule) {
        return this.utilitiesScheduleRepository.save(utilitiesSchedule);
    }

    public UtilitiesSchedule updateUtilitiesSchedule (final Integer id, final UtilitiesSchedule utilitiesSchedule) {
        final UtilitiesSchedule existingSchedule = utilitiesScheduleRepository.getOne(id);
        ModelUtils.merge(utilitiesSchedule, existingSchedule, "id", "utilitiesRangeBracket",
            "utilitiesWaterTownsAndVillage", "utilitiesElectricityTownsAndVillage",
            "created_by", "created_at", "updated_by", "updated_by");
        return utilitiesScheduleRepository.save(existingSchedule);
    }

    @Transactional(readOnly = true)
    public UtilitiesSchedule getOneUtilitiesSchedule (final Integer id) {
        return this.utilitiesScheduleRepository.getOne(id);
    }

    @Transactional(readOnly = true)
    public Page<UtilitiesSchedule> getAllUtilitiesSchedule (final Pageable pageable) {
        return this.utilitiesScheduleRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<UtilitiesSchedule> getAllUtilitiesSchedule (final Pageable pageable, final ScheduleType scheduleType, final String textSearch) {
        final FiltersSpecification.Builder filterBuilder = new FiltersSpecification.Builder().lookFor(textSearch);
        if (scheduleType != null) {
            filterBuilder.restrictOn(Filter.equals("scheduleType", scheduleType));
        }
        return this.utilitiesScheduleRepository.findAll(filterBuilder.build(), pageable);
    }

    public void deleteUtilitiesSchedule (final Integer id) {
        final UtilitiesSchedule schedule = this.utilitiesScheduleRepository.getOne(id);
        final Set<UtilitiesRangeBracket> rangeBrackets = schedule.getUtilitiesRangeBracket();
        if (CollectionUtils.isNotEmpty(rangeBrackets)) {
            throw ExceptionFactory.getDepencencyViolationException(UtilitiesSchedule.class, UtilitiesRangeBracket.class);
        }
        if (CollectionUtils.isNotEmpty(schedule.getResidentialElectricityUtilitySchedule()) || (
            CollectionUtils.isNotEmpty(schedule.getResidentialElectricityUtilitySchedule()))) {
            throw ExceptionFactory.getDepencencyViolationException(UtilitiesSchedule.class, UtilitiesTownsAndVillage.class);
        }
        if (CollectionUtils.isNotEmpty(schedule.getCommercialElectricityUtilitySchedule()) || (
            CollectionUtils.isNotEmpty(schedule.getCommercialElectricityUtilitySchedule()))) {
            throw ExceptionFactory.getDepencencyViolationException(UtilitiesSchedule.class, UtilitiesTownsAndVillage.class);
        }

        this.utilitiesScheduleRepository.delete(id);
    }

    public UtilitiesRangeBracket createRangeBrackets (final Integer scheduleId, final UtilitiesRangeBracket utilitiesRangeBracket) {
        final UtilitiesSchedule schedule = this.utilitiesScheduleRepository.getOne(scheduleId);
        utilitiesRangeBracket.setSchedule(schedule);
        final UtilitiesRangeBracket savedBracket = this.utilitiesRangeBracketRepository.saveAndFlush(utilitiesRangeBracket);
        return savedBracket;
    }

    public UtilitiesRangeBracket updateRangeBrackets (final Integer id, final UtilitiesRangeBracket utilitiesRangeBracket) {
        final UtilitiesRangeBracket existingRange = utilitiesRangeBracketRepository.getOne(id);
        ModelUtils.merge(utilitiesRangeBracket, existingRange, "id", "schedule", "created_by", "created_at",
                "updated_by", "updated_by");
        return utilitiesRangeBracketRepository.saveAndFlush(existingRange);
    }

    @Transactional(readOnly = true)
    public Collection<UtilitiesRangeBracket> getAllUtilitiesRangeBracketByScheduleId (final Integer scheduleId) {
        final UtilitiesSchedule schedule = this.utilitiesScheduleRepository.getOne(scheduleId);
        return schedule.getUtilitiesRangeBracket();
    }

    @Transactional(readOnly = true)
    public UtilitiesRangeBracket getOneUtilitiesRangeBracket (final Integer id) {
        return this.utilitiesRangeBracketRepository.getOne(id);
    }

    public void deleteRangeBrackets (final Integer id) {
        this.utilitiesRangeBracketRepository.delete(id);
    }

    public long countAll() {
        return utilitiesScheduleRepository.count();
    }
}
