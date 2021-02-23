package ca.ids.abms.modules.radarsummary;

import java.util.ArrayList;

import org.hamcrest.core.Every;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by s.craymer on 18/08/2017.
 */
public class FlightTravelCategoryTest {

    private ArrayList<FlightTravelCategory> flightTravelCategories;

    @Before
    public void setup() {
        flightTravelCategories = new ArrayList<>();
    }

    @Test
    public void mapToValueCaseInsensitiveTest(){
        flightTravelCategories.add(FlightTravelCategory.mapFromValue("overflight"));
        flightTravelCategories.add(FlightTravelCategory.mapFromValue("OVERFLIGHT"));
        flightTravelCategories.add(FlightTravelCategory.mapFromValue("overFlight"));
        Assert.assertThat(flightTravelCategories, Every.everyItem(Is.is(FlightTravelCategory.OVERFLIGHT)));
    }

    @Test
    public void mapToValueOverflightTest() {
        flightTravelCategories.add(FlightTravelCategory.mapFromValue("OVR"));
        flightTravelCategories.add(FlightTravelCategory.mapFromValue("OVERFLIGHT"));
        Assert.assertThat(flightTravelCategories, Every.everyItem(Is.is(FlightTravelCategory.OVERFLIGHT)));
    }

    @Test
    public void mapToValueDomesticTest() {
        flightTravelCategories.add(FlightTravelCategory.mapFromValue("DOM"));
        flightTravelCategories.add(FlightTravelCategory.mapFromValue("DOMESTIC"));
        Assert.assertThat(flightTravelCategories, Every.everyItem(Is.is(FlightTravelCategory.DOMESTIC)));
    }

    @Test
    public void mapToValueDepartureTest() {
        flightTravelCategories.add(FlightTravelCategory.mapFromValue("DEP"));
        flightTravelCategories.add(FlightTravelCategory.mapFromValue("DEPARTURE"));
        flightTravelCategories.add(FlightTravelCategory.mapFromValue("OUT"));
        flightTravelCategories.add(FlightTravelCategory.mapFromValue("OUTGOING"));
        Assert.assertThat(flightTravelCategories, Every.everyItem(Is.is(FlightTravelCategory.DEPARTURE)));
    }

    @Test
    public void mapToValueArrivalTest() {
        flightTravelCategories.add(FlightTravelCategory.mapFromValue("ARR"));
        flightTravelCategories.add(FlightTravelCategory.mapFromValue("ARRIVAL"));
        flightTravelCategories.add(FlightTravelCategory.mapFromValue("INB"));
        flightTravelCategories.add(FlightTravelCategory.mapFromValue("INCOMING"));
        Assert.assertThat(flightTravelCategories, Every.everyItem(Is.is(FlightTravelCategory.ARRIVAL)));
    }
}
