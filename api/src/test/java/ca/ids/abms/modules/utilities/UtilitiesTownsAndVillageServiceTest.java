package ca.ids.abms.modules.utilities;

import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.modules.utilities.schedules.*;
import ca.ids.abms.modules.utilities.towns.UtilitiesTownsAndVillage;
import ca.ids.abms.modules.utilities.towns.UtilitiesTownsAndVillageRepository;
import ca.ids.abms.modules.utilities.towns.UtilitiesTownsAndVillageService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class UtilitiesTownsAndVillageServiceTest {

    private UtilitiesScheduleRepository utilitiesScheduleRepository;
    private UtilitiesTownsAndVillageService utilitiesTownsAndVillageService;
    private UtilitiesTownsAndVillageRepository utilitiesTownsAndVillageRepository;

    @Before
    public void setup() {
        this.utilitiesScheduleRepository = mock(UtilitiesScheduleRepository.class);
        this.utilitiesTownsAndVillageRepository = mock(UtilitiesTownsAndVillageRepository.class);
        this.utilitiesTownsAndVillageService = new UtilitiesTownsAndVillageService(utilitiesTownsAndVillageRepository,
            utilitiesScheduleRepository);

    }

    @Test
    public void create() throws Exception {
        UtilitiesSchedule eSchedule = new UtilitiesSchedule();
        eSchedule.setScheduleId(2);
        eSchedule.setMinimumCharge(10);
        eSchedule.setScheduleType(ScheduleType.ELECTRIC_COMM);
        UtilitiesSchedule wSchedule = new UtilitiesSchedule();
        wSchedule.setScheduleId(3);
        wSchedule.setMinimumCharge(11);
        wSchedule.setScheduleType(ScheduleType.WATER);

        UtilitiesTownsAndVillage item = new UtilitiesTownsAndVillage();
        item.setTownOrVillageName("Town");
        item.setResidentialElectricityUtilitySchedule(eSchedule);
        item.setCommercialElectricityUtilitySchedule(eSchedule);
        item.setWaterUtilitySchedule(wSchedule);
        item.setId(1);

        when(utilitiesScheduleRepository.getUtilitiesScheduleByScheduleIdAndScheduleType(any(Integer.class),
            eq(ScheduleType.ELECTRIC_COMM)))
            .thenReturn(eSchedule);
        when(utilitiesScheduleRepository.getUtilitiesScheduleByScheduleIdAndScheduleType(any(Integer.class),
            eq(ScheduleType.WATER)))
            .thenReturn(wSchedule);
        when(utilitiesTownsAndVillageRepository.saveAndFlush(any(UtilitiesTownsAndVillage.class))).thenReturn(item);

        UtilitiesTownsAndVillage result = utilitiesTownsAndVillageService.create(item);
        assertThat(result.getId() == item.getId());
        assertThat(result.getResidentialElectricityUtilitySchedule().getScheduleId() == item.getResidentialElectricityUtilitySchedule().getScheduleId());
        assertThat(result.getWaterUtilitySchedule().getScheduleId() == item.getWaterUtilitySchedule().getScheduleId());
    }

    @Test
    public void update() throws Exception {
        UtilitiesSchedule eSchedule = new UtilitiesSchedule();
        eSchedule.setScheduleId(2);
        eSchedule.setMinimumCharge(10);
        eSchedule.setScheduleType(ScheduleType.ELECTRIC_COMM);
        UtilitiesSchedule wSchedule = new UtilitiesSchedule();
        wSchedule.setScheduleId(3);
        wSchedule.setMinimumCharge(11);
        wSchedule.setScheduleType(ScheduleType.WATER);

        UtilitiesTownsAndVillage item = new UtilitiesTownsAndVillage();
        item.setTownOrVillageName("Town");
        item.setResidentialElectricityUtilitySchedule(eSchedule);
        item.setWaterUtilitySchedule(wSchedule);
        item.setId(1);

        when(utilitiesScheduleRepository.getUtilitiesScheduleByScheduleIdAndScheduleType(any(Integer.class),
            eq(ScheduleType.ELECTRIC_COMM)))
            .thenReturn(eSchedule);
        when(utilitiesScheduleRepository.getUtilitiesScheduleByScheduleIdAndScheduleType(any(Integer.class),
            eq(ScheduleType.WATER)))
            .thenReturn(wSchedule);

        when(utilitiesTownsAndVillageRepository.getOne(any(Integer.class))).thenReturn(item);

        when(utilitiesTownsAndVillageRepository.saveAndFlush(any(UtilitiesTownsAndVillage.class))).thenReturn(item);

        UtilitiesTownsAndVillage itemToUpdate = new UtilitiesTownsAndVillage();
        itemToUpdate.setTownOrVillageName("Changed");

        UtilitiesTownsAndVillage result = utilitiesTownsAndVillageService.update(1, itemToUpdate);
        assertThat(result.getId() == 1);

    }

    @Test
    public void findAll() throws Exception {
        UtilitiesSchedule eSchedule = new UtilitiesSchedule();
        eSchedule.setScheduleId(2);
        eSchedule.setMinimumCharge(10);
        eSchedule.setScheduleType(ScheduleType.ELECTRIC_RES);
        UtilitiesSchedule wSchedule = new UtilitiesSchedule();
        wSchedule.setScheduleId(3);
        wSchedule.setMinimumCharge(11);
        wSchedule.setScheduleType(ScheduleType.WATER);

        UtilitiesTownsAndVillage item = new UtilitiesTownsAndVillage();
        item.setTownOrVillageName("Town");
        item.setResidentialElectricityUtilitySchedule(eSchedule);
        item.setWaterUtilitySchedule(wSchedule);
        item.setId(1);

        List<UtilitiesTownsAndVillage> items = Collections.singletonList(item);

        when(utilitiesTownsAndVillageRepository.findAll(any(FiltersSpecification.class), any(Pageable.class)))
            .thenReturn(new PageImpl<>(items));

        Page<UtilitiesTownsAndVillage> results = utilitiesTownsAndVillageService.findAll(mock(Pageable.class), null);
        assertThat(items.size() == results.getTotalElements());
        assertThat(items.get(0).getId() == results.getContent().get(0).getId());
    }

    @Test
    public void getOne() throws Exception {
        UtilitiesSchedule eSchedule = new UtilitiesSchedule();
        eSchedule.setScheduleId(2);
        eSchedule.setMinimumCharge(10);
        eSchedule.setScheduleType(ScheduleType.ELECTRIC_RES);
        UtilitiesSchedule wSchedule = new UtilitiesSchedule();
        wSchedule.setScheduleId(3);
        wSchedule.setMinimumCharge(11);
        wSchedule.setScheduleType(ScheduleType.WATER);

        UtilitiesTownsAndVillage item = new UtilitiesTownsAndVillage();
        item.setTownOrVillageName("Town");
        item.setResidentialElectricityUtilitySchedule(eSchedule);
        item.setWaterUtilitySchedule(wSchedule);
        item.setId(1);

        when(utilitiesTownsAndVillageRepository.getOne(any()))
            .thenReturn(item);

        UtilitiesTownsAndVillage result = utilitiesTownsAndVillageService.getOne(1);
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getResidentialElectricityUtilitySchedule().getScheduleId()).isEqualTo(eSchedule.getScheduleId());
        assertThat(result.getWaterUtilitySchedule().getScheduleId()).isEqualTo(wSchedule.getScheduleId());
    }

    @Test
    public void delete() throws Exception {
        UtilitiesTownsAndVillage item = new UtilitiesTownsAndVillage();
        item.setTownOrVillageName("Town");
        item.setId(1);
        when(utilitiesTownsAndVillageRepository.getOne(any()))
            .thenReturn(item);
        utilitiesTownsAndVillageService.delete(1);
        verify(utilitiesTownsAndVillageRepository).delete(any(Integer.class));
    }
}
