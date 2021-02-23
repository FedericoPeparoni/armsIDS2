package ca.ids.abms.modules.utilities.towns;

import ca.ids.abms.config.db.FiltersSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.ids.abms.modules.utilities.schedules.ScheduleType;
import ca.ids.abms.modules.utilities.schedules.UtilitiesSchedule;
import ca.ids.abms.modules.utilities.schedules.UtilitiesScheduleRepository;

@Service
@Transactional
public class UtilitiesTownsAndVillageService {

    private UtilitiesTownsAndVillageRepository utilitiesTownsAndVillageRepository;
    private UtilitiesScheduleRepository utilitiesScheduleRepository;

    public UtilitiesTownsAndVillageService(UtilitiesTownsAndVillageRepository utilitiesTownsAndVillageRepository,
                                           UtilitiesScheduleRepository utilitiesScheduleRepository) {
        this.utilitiesTownsAndVillageRepository = utilitiesTownsAndVillageRepository;
        this.utilitiesScheduleRepository = utilitiesScheduleRepository;
    }

    public UtilitiesTownsAndVillage create(UtilitiesTownsAndVillage item) {
        final Integer waterUtilityScheduleId = item.getWaterUtilitySchedule().getScheduleId();
        final Integer electricityResidentialUtilityScheduleId = item.getResidentialElectricityUtilitySchedule().getScheduleId();
        final Integer electricityCommercialUtilityScheduleId = item.getCommercialElectricityUtilitySchedule().getScheduleId();

        final UtilitiesSchedule waterSchedule = utilitiesScheduleRepository.getUtilitiesScheduleByScheduleIdAndScheduleType
            (waterUtilityScheduleId, ScheduleType.WATER);
        item.setWaterUtilitySchedule(waterSchedule);

        final UtilitiesSchedule electricResidentialSchedule = utilitiesScheduleRepository.getUtilitiesScheduleByScheduleIdAndScheduleType
            (electricityResidentialUtilityScheduleId, item.getResidentialElectricityUtilitySchedule().getScheduleType());

        final UtilitiesSchedule electricCommercialSchedule = utilitiesScheduleRepository.getUtilitiesScheduleByScheduleIdAndScheduleType
            (electricityCommercialUtilityScheduleId, item.getCommercialElectricityUtilitySchedule().getScheduleType());

        item.setResidentialElectricityUtilitySchedule(electricResidentialSchedule);
        item.setCommercialElectricityUtilitySchedule(electricCommercialSchedule);
        return utilitiesTownsAndVillageRepository.saveAndFlush(item);
    }

    public UtilitiesTownsAndVillage update(Integer id, UtilitiesTownsAndVillage item) {
        final UtilitiesTownsAndVillage existingItem = utilitiesTownsAndVillageRepository.getOne(id);
        existingItem.setTownOrVillageName(item.getTownOrVillageName());

        // water
        if (item.getWaterUtilitySchedule() != null &&  item.getWaterUtilitySchedule().getScheduleId() != null) {
            final Integer waterUtilityScheduleId = item.getWaterUtilitySchedule().getScheduleId();
            final UtilitiesSchedule waterSchedule = utilitiesScheduleRepository.getUtilitiesScheduleByScheduleIdAndScheduleType
                (waterUtilityScheduleId, ScheduleType.WATER);
            existingItem.setWaterUtilitySchedule(waterSchedule);
        }

        // residential electric
        if (item.getResidentialElectricityUtilitySchedule() != null && item.getResidentialElectricityUtilitySchedule().getScheduleId() != null) {
            final Integer electricityUtilityScheduleId = item.getResidentialElectricityUtilitySchedule().getScheduleId();

            final UtilitiesSchedule electricSchedule = utilitiesScheduleRepository.getUtilitiesScheduleByScheduleIdAndScheduleType
                (electricityUtilityScheduleId, item.getResidentialElectricityUtilitySchedule().getScheduleType());
            existingItem.setResidentialElectricityUtilitySchedule(electricSchedule);
        }

        // commercial electric
        if (item.getCommercialElectricityUtilitySchedule() != null && item.getCommercialElectricityUtilitySchedule().getScheduleId() != null) {
            final Integer electricityUtilityScheduleId = item.getCommercialElectricityUtilitySchedule().getScheduleId();

            final UtilitiesSchedule electricSchedule = utilitiesScheduleRepository.getUtilitiesScheduleByScheduleIdAndScheduleType
                (electricityUtilityScheduleId, item.getCommercialElectricityUtilitySchedule().getScheduleType());
            existingItem.setCommercialElectricityUtilitySchedule(electricSchedule);
        }

        return utilitiesTownsAndVillageRepository.saveAndFlush(existingItem);
    }

    @Transactional(readOnly = true)
    public UtilitiesTownsAndVillage getOne(Integer id) {
        return utilitiesTownsAndVillageRepository.getOne(id);
    }

    @Transactional(readOnly = true)
    public Page<UtilitiesTownsAndVillage> findAll(Pageable pageable, final String textSearch) {
        final FiltersSpecification.Builder filterBuilder = new FiltersSpecification.Builder().lookFor(textSearch);
        return utilitiesTownsAndVillageRepository.findAll(filterBuilder.build(), pageable);
    }

    public void delete(Integer id) {
        utilitiesTownsAndVillageRepository.delete(id);
    }

    public long countAll() {
        return utilitiesTownsAndVillageRepository.count();
    }
}
