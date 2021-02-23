package ca.ids.abms.modules.airspaces;

import ca.ids.abms.modules.util.models.NavDBUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class AirspaceServiceTest {

    private AirspaceRepository airspaceRepository;

    private AirspaceService airspaceService;

    private NavDBUtils navDBUtils;


    @Before
    public void setup() {

        airspaceRepository = mock(AirspaceRepository.class);
        navDBUtils= mock(NavDBUtils.class);
        airspaceService = new AirspaceService(airspaceRepository,navDBUtils);
    }


    @Test
    public void getAirspaceById() throws Exception {
        Airspace airspace = new Airspace();
        airspace.setId(1);


        when(navDBUtils.getAirspaceNavdbById(any())).thenReturn(airspace);

        Airspace result = navDBUtils.getAirspaceNavdbById(1);
        assertThat(result).isEqualTo(airspace);
    }

    @Test
    public void getAllAirspaces() throws Exception {
        List<Airspace> airspaces = Collections.singletonList(new Airspace());

        when(navDBUtils.getAllAirspacesNavdb()).thenReturn(new ArrayList<>(airspaces));

        List<Airspace> results = airspaceService.getAllFromNavDb(mock(Pageable.class));

        assertThat(results.size()).isEqualTo(airspaces.size());
    }

    @Test
    public void navdbNameMappedToAirspaceFullName() throws Exception {

        Airspace airspace = new Airspace();
        airspace.setAirspaceFullName("OTTAWA, ON");
        List<Airspace> airspaces = Collections.singletonList(airspace);

        when(navDBUtils.getAllAirspacesNavdb()).thenReturn(airspaces);

        List<Airspace> a = airspaceService.getAllFromNavDb(mock(Pageable.class));

        assertThat(a.get(0).getAirspaceFullName()).isEqualTo("OTTAWA, ON");
    }

}
