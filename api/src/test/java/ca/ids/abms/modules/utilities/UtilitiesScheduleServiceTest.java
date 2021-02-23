package ca.ids.abms.modules.utilities;

import ca.ids.abms.modules.utilities.schedules.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UtilitiesScheduleServiceTest {

    private UtilitiesScheduleRepository utilitiesScheduleRepository;
    private UtilitiesRangeBracketRepository utilitiesRangeBracketRepository;
    private UtilitiesScheduleService utilitiesScheduleService;

    @Before
    public void setup() {
        this.utilitiesScheduleRepository = mock(UtilitiesScheduleRepository.class);
        this.utilitiesRangeBracketRepository = mock(UtilitiesRangeBracketRepository.class);
        this.utilitiesScheduleService = new UtilitiesScheduleService(utilitiesScheduleRepository,
            utilitiesRangeBracketRepository);

    }

    @Test
    public void createUtilitiesSchedule()  throws Exception {
        UtilitiesSchedule schedule = new UtilitiesSchedule();
        schedule.setMinimumCharge(10);
        UtilitiesRangeBracket bracket = new UtilitiesRangeBracket();
        bracket.setRangeTopEnd(2.0);
        schedule.getUtilitiesRangeBracket().add(bracket);
        bracket.setSchedule(schedule);
        when(utilitiesScheduleRepository.save(any(UtilitiesSchedule.class))).thenReturn(schedule);

        UtilitiesSchedule result = utilitiesScheduleService.createUtilitiesSchedule(schedule);
        assertThat(result.getMinimumCharge() == schedule.getMinimumCharge());
        assertThat(result.getUtilitiesRangeBracket().size() == schedule.getUtilitiesRangeBracket().size());
    }

    @Test
    public void updateUtilitiesSchedule() throws Exception {
        UtilitiesSchedule schedule = new UtilitiesSchedule();
        schedule.setScheduleId(2);
        schedule.setMinimumCharge(10);
        UtilitiesRangeBracket bracket = new UtilitiesRangeBracket();
        bracket.setRangeTopEnd(2.0);
        schedule.getUtilitiesRangeBracket().add(bracket);

        when(utilitiesScheduleRepository.getOne(any()))
            .thenReturn(schedule);

        when(utilitiesScheduleRepository.save(any(UtilitiesSchedule.class)))
            .thenReturn(schedule);

        UtilitiesSchedule scheduleToUpdate = new UtilitiesSchedule();
        schedule.setMinimumCharge(11);

        UtilitiesSchedule result = utilitiesScheduleService.updateUtilitiesSchedule(1,scheduleToUpdate);
        assertThat(result.getMinimumCharge() == 11);
        assertThat(result.getScheduleId() == 2);
    }

    @Test
    public void getOneUtilitiesSchedule() throws Exception {
        UtilitiesSchedule schedule = new UtilitiesSchedule();
        schedule.setScheduleId(2);
        schedule.setMinimumCharge(10);

        UtilitiesRangeBracket bracket = new UtilitiesRangeBracket();
        bracket.setRangeTopEnd(2.0);
        schedule.getUtilitiesRangeBracket().add(bracket);
        when(utilitiesScheduleRepository.getOne(any()))
            .thenReturn(schedule);

        UtilitiesSchedule result = utilitiesScheduleService.getOneUtilitiesSchedule(2);
        assertThat(result.getScheduleId() == 2);
        assertThat(result.getMinimumCharge() == schedule.getMinimumCharge());
        assertThat(result.getUtilitiesRangeBracket().size() == schedule.getUtilitiesRangeBracket().size());
    }

    @Test
    public void getAllUtilitiesSchedule() throws Exception {
        UtilitiesSchedule schedule = new UtilitiesSchedule();
        schedule.setScheduleId(2);
        schedule.setMinimumCharge(10);

        UtilitiesRangeBracket bracket = new UtilitiesRangeBracket();
        bracket.setRangeTopEnd(2.0);
        schedule.getUtilitiesRangeBracket().add(bracket);

        List<UtilitiesSchedule> schedules = Collections.singletonList(schedule);
        when(utilitiesScheduleRepository.findAll(any(Pageable.class)))
            .thenReturn(new PageImpl<>(schedules));

        Page<UtilitiesSchedule> results = utilitiesScheduleService.getAllUtilitiesSchedule(mock(Pageable.class));

        assertThat(schedules.size() == results.getTotalElements());
        assertThat(schedules.get(0).getScheduleId() == results.getContent().get(0).getScheduleId());
    }

    @Test
    public void deleteUtilitiesSchedule() throws Exception {
        UtilitiesSchedule schedule = new UtilitiesSchedule();
        schedule.setScheduleId(1);
        when(utilitiesScheduleRepository.getOne(any()))
            .thenReturn(schedule);
        utilitiesScheduleService.deleteUtilitiesSchedule(1);
        verify(utilitiesScheduleRepository).delete(any(Integer.class));
    }

    @Test
    public void createRangeBrackets() throws Exception {
        UtilitiesSchedule schedule = new UtilitiesSchedule();
        schedule.setScheduleId(2);
        schedule.setMinimumCharge(10);
        UtilitiesRangeBracket bracket = new UtilitiesRangeBracket();
        bracket.setRangeTopEnd(2.0);
        bracket.setSchedule(schedule);
        schedule.getUtilitiesRangeBracket().add(bracket);
        when(utilitiesRangeBracketRepository.saveAndFlush(any(UtilitiesRangeBracket.class))).thenReturn(bracket);

        UtilitiesRangeBracket result = utilitiesScheduleService.createRangeBrackets(2, bracket);
        assertThat(result.getRangeTopEnd() == bracket.getRangeTopEnd());
    }

    @Test
    public void updateRangeBrackets() throws Exception {
        UtilitiesSchedule schedule = new UtilitiesSchedule();
        schedule.setScheduleId(2);
        schedule.setMinimumCharge(10);
        UtilitiesRangeBracket bracket = new UtilitiesRangeBracket();
        bracket.setId(1);
        bracket.setRangeTopEnd(2.0);
        bracket.setSchedule(schedule);
        schedule.getUtilitiesRangeBracket().add(bracket);

        when(utilitiesScheduleRepository.getOne(any()))
            .thenReturn(schedule);

        when(utilitiesRangeBracketRepository.getOne(any()))
            .thenReturn(bracket);

        when(utilitiesRangeBracketRepository.saveAndFlush(any(UtilitiesRangeBracket.class)))
            .thenReturn(bracket);

        when(utilitiesScheduleRepository.save(any(UtilitiesSchedule.class)))
            .thenReturn(schedule);

        UtilitiesRangeBracket bracketToUpdate = new UtilitiesRangeBracket();
        bracketToUpdate.setRangeTopEnd(4.0);

        UtilitiesRangeBracket result = utilitiesScheduleService.updateRangeBrackets(1,bracketToUpdate);
        assertThat(result.getRangeTopEnd() == 4.0);
        assertThat(result.getId() == 1);
    }

    @Test
    public void getAllUtilitiesRangeBracketByScheduleId() throws Exception {
        UtilitiesSchedule schedule = new UtilitiesSchedule();
        schedule.setScheduleId(2);
        schedule.setMinimumCharge(10);
        UtilitiesRangeBracket bracket = new UtilitiesRangeBracket();
        bracket.setId(1);
        bracket.setRangeTopEnd(2.0);
        bracket.setSchedule(schedule);
        schedule.getUtilitiesRangeBracket().add(bracket);

        when(utilitiesScheduleRepository.getOne(any()))
            .thenReturn(schedule);

        Collection<UtilitiesRangeBracket> results = utilitiesScheduleService.getAllUtilitiesRangeBracketByScheduleId(2);
        assertThat(results.size() == schedule.getUtilitiesRangeBracket().size());

    }

    @Test
    public void getOneUtilitiesRangeBracket() throws Exception {
        UtilitiesRangeBracket bracket = new UtilitiesRangeBracket();
        bracket.setId(2);
        bracket.setRangeTopEnd(2.0);
        when(utilitiesRangeBracketRepository.getOne(any()))
            .thenReturn(bracket);

        UtilitiesRangeBracket result = utilitiesScheduleService.getOneUtilitiesRangeBracket(2);
        assertThat(result.getId() == 2);
        assertThat(result.getRangeTopEnd() == result.getRangeTopEnd());
    }

    @Test
    public void deleteRangeBrackets() throws Exception {
        UtilitiesRangeBracket bracket = new UtilitiesRangeBracket();
        bracket.setRangeTopEnd(2.0);
        when(utilitiesRangeBracketRepository.getOne(any()))
            .thenReturn(bracket);
        utilitiesScheduleService.deleteRangeBrackets(1);
        verify(utilitiesRangeBracketRepository).delete(any(Integer.class));
    }
}
