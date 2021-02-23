package ca.ids.abms.modules.towermovements;

import ca.ids.abms.modules.common.enumerators.FlightCategory;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TowerMovementLogMapperTest {

    private TowerMovementLogMapper mapper;

    @Before
    public void setup() {
        mapper = new TowerMovementLogMapperImpl();
    }

    @Test
    public void defaultValuesTest() {

        TowerMovementLogCsvViewModel viewModel = new TowerMovementLogCsvViewModel();

        // validate that values are defined when view model values are null
        TowerMovementLog result = mapper.toModel(viewModel);
        assertThat(result.getFlightCategory()).isEqualTo(FlightCategory.NON_SCH);
        assertThat(result.getFlightCrew()).isEqualTo(0);
        assertThat(result.getPassengers()).isEqualTo(0);

        // validate that values are not overwritten when view model values are defined
        viewModel.setFlightCategory("SCH");
        viewModel.setFlightCrew(1);
        viewModel.setPassengers(1);
        result = mapper.toModel(viewModel);
        assertThat(result.getFlightCategory()).isEqualTo(FlightCategory.SCH);
        assertThat(result.getFlightCrew()).isEqualTo(1);
        assertThat(result.getPassengers()).isEqualTo(1);
    }
}
