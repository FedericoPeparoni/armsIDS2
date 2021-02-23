package ca.ids.abms.modules.exemptions.charges.providers;

import ca.ids.abms.modules.exemptions.ExemptionType;
import ca.ids.abms.modules.exemptions.charges.methods.LargestExemptionChargeMethod;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

public class EnrouteExemptionChargeProviderTest {

    private EnrouteExemptionChargeProvider provider;

    @Before
    public void setup() {
        LargestExemptionChargeMethod method = new LargestExemptionChargeMethod();
        provider = new EnrouteExemptionChargeProvider(method);
    }

    @Test
    public void applyTest() {

        // CASE 1: throw IllegalParameterException when either parameter is undefined
        try {
            // noinspection ConstantConditions
            provider.apply(null, null);
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException ignored) {}

        try {
            // noinspection ConstantConditions
            provider.apply(new FlightMovement(), null);
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException ignored) {}

        try {
            // noinspection ConstantConditions
            provider.apply(null, Collections.emptyList());
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException ignored) {}


        // CASE 2: flight movement is not modified if no enroute exemptions
        FlightMovement flightMovement = mockFlightMovement();
        provider.apply(flightMovement, Collections.singletonList(mockExemptionTypes(null)));

        assertThat(flightMovement.getEnrouteCharges()).isEqualTo(123.0);
        assertThat(flightMovement.getExemptEnrouteCharges()).isEqualTo(0.0);
        assertThat(flightMovement.getFlightNotes()).isEqualTo("MOCK FLIGHT MOVEMENT");


        // CASE 3: flight movement is modified with applied exemption and flight notes
        flightMovement = mockFlightMovement();
        provider.apply(flightMovement, Collections.singletonList(mockExemptionTypes(75.0)));

        assertThat(flightMovement.getEnrouteCharges()).isEqualTo(30.75);
        assertThat(flightMovement.getExemptEnrouteCharges()).isEqualTo(92.25);
        assertThat(flightMovement.getFlightNotes()).isEqualTo("MOCK FLIGHT MOVEMENT; MOCK ENROUTE CHARGE EXEMPTION");
    }

    private FlightMovement mockFlightMovement() {
        FlightMovement flightMovement = new FlightMovement();

        flightMovement.setEnrouteCharges(123.0);
        flightMovement.setExemptEnrouteCharges(0.0);
        flightMovement.setFlightNotes("MOCK FLIGHT MOVEMENT");

        return flightMovement;
    }

    private ExemptionType mockExemptionTypes(Double enrouteChargeExemption) {
        return new ExemptionType() {
            @Override
            public Double enrouteChargeExemption() {
                return enrouteChargeExemption;
            }

            @Override
            public String flightNoteChargeExemption() {
                return "MOCK ENROUTE CHARGE EXEMPTION";
            }
        };
    }
}
