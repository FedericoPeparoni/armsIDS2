package ca.ids.abms.modules.exemptions.charges.providers;

import ca.ids.abms.modules.exemptions.ExemptionType;
import ca.ids.abms.modules.exemptions.charges.methods.LargestExemptionChargeMethod;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

public class InternationalPaxExemptionChargeProviderTest {

    private InternationalPaxExemptionChargeProvider provider;

    @Before
    public void setup() {
        LargestExemptionChargeMethod method = new LargestExemptionChargeMethod();
        provider = new InternationalPaxExemptionChargeProvider(method);
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


        // CASE 2: flight movement is not modified if no international pax exemptions
        FlightMovement flightMovement = mockFlightMovement();
        provider.apply(flightMovement, Collections.singletonList(mockExemptionTypes(null)));

        assertThat(flightMovement.getInternationalPassengerCharges()).isEqualTo(123.0);
        assertThat(flightMovement.getExemptInternationalPassengerCharges()).isEqualTo(0.0);
        assertThat(flightMovement.getFlightNotes()).isEqualTo("MOCK FLIGHT MOVEMENT");


        // CASE 3: flight movement is modified with applied exemption and flight notes
        flightMovement = mockFlightMovement();
        provider.apply(flightMovement, Collections.singletonList(mockExemptionTypes(75.0)));

        assertThat(flightMovement.getInternationalPassengerCharges()).isEqualTo(30.75);
        assertThat(flightMovement.getExemptInternationalPassengerCharges()).isEqualTo(92.25);
        assertThat(flightMovement.getFlightNotes()).isEqualTo("MOCK FLIGHT MOVEMENT; MOCK INTERNATIONAL PAX CHARGE EXEMPTION");
    }

    private FlightMovement mockFlightMovement() {
        FlightMovement flightMovement = new FlightMovement();

        flightMovement.setInternationalPassengerCharges(123.0);
        flightMovement.setExemptInternationalPassengerCharges(0.0);
        flightMovement.setFlightNotes("MOCK FLIGHT MOVEMENT");

        return flightMovement;
    }

    private ExemptionType mockExemptionTypes(Double internationalPaxChargeExemption) {
        return new ExemptionType() {
            @Override
            public Double internationalPaxChargeExemption() {
                return internationalPaxChargeExemption;
            }

            @Override
            public String flightNoteChargeExemption() {
                return "MOCK INTERNATIONAL PAX CHARGE EXEMPTION";
            }
        };
    }
}
