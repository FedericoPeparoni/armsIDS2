package ca.ids.abms.modules.route;

import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.modules.flightmovementsbuilder.utility.ThruFlightPlanUtility;
import ca.ids.abms.modules.system.SystemConfigurationService;

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

public class NominalRouteServiceTest {

    private NominalRouteRepository nominalRouteRepository;
    private NominalRouteService nominalRouteService;
    private SystemConfigurationService systemConfigurationService;

    @Before
    public void setup() {

        ThruFlightPlanUtility thruFlightPlanUtility = mock(ThruFlightPlanUtility.class);

        nominalRouteRepository = mock(NominalRouteRepository.class);
        BiDirectionalNominalRouteRepository biDirectionalNominalRouteRepository = mock(BiDirectionalNominalRouteRepository.class);

        nominalRouteService = new NominalRouteService(biDirectionalNominalRouteRepository, nominalRouteRepository,
            thruFlightPlanUtility,systemConfigurationService);
    }

    @Test
    public void createNominalRoute() {
        NominalRoute nominalRoute = new NominalRoute();
        nominalRoute.setStatus(NominalRouteStatus.MANUAL.getValue());
        when(nominalRouteRepository.save(any(NominalRoute.class))).thenReturn(nominalRoute);

        NominalRoute result = nominalRouteService.create(nominalRoute, true);
        assertThat(result.getStatus()).isEqualTo(nominalRoute.getStatus());
    }

    @Test
    public void deleteUser() {
        nominalRouteService.delete(1);
        verify(nominalRouteRepository).delete(any(Integer.class));
    }

    @Test
    public void getAllNominalRoutes() {
        List<NominalRoute> nominalRoutes = Collections.singletonList(new NominalRoute());

        // noinspection unchecked
        when(nominalRouteRepository.findAll(any(FiltersSpecification.class), any(Pageable.class))).thenReturn(new PageImpl<>(nominalRoutes));

        Page<NominalRoute> results = nominalRouteService.findAll(mock(Pageable.class), null);

        assertThat(results.getTotalElements()).isEqualTo(nominalRoutes.size());
    }

    @Test
    public void getNominalRouteById() {
        NominalRoute nominalRoute = new NominalRoute();
        nominalRoute.setId(1);

        when(nominalRouteRepository.getOne(any())).thenReturn(nominalRoute);

        NominalRoute result = nominalRouteService.getOne(1);
        assertThat(result).isEqualTo(nominalRoute);
    }

    @Test
    public void updateNominalRoute() {
        NominalRoute existingNominalRoute = new NominalRoute();
        existingNominalRoute.setStatus(NominalRouteStatus.MANUAL.getValue());

        NominalRoute nominalRoute = new NominalRoute();
        nominalRoute.setStatus(NominalRouteStatus.CALCULATED.getValue());

        when(nominalRouteRepository.getOne(any())).thenReturn(existingNominalRoute);

        when(nominalRouteRepository.save(any(NominalRoute.class))).thenReturn(existingNominalRoute);

        NominalRoute result = nominalRouteService.update(1, nominalRoute, false);

        assertThat(result.getStatus()).isEqualTo(NominalRouteStatus.CALCULATED.getValue());
    }
}
