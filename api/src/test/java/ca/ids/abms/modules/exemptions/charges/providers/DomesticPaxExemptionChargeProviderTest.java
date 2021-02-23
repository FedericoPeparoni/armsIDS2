package ca.ids.abms.modules.exemptions.charges.providers;

import ca.ids.abms.modules.exemptions.ExemptionType;
import ca.ids.abms.modules.exemptions.charges.methods.LargestExemptionChargeMethod;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

public class DomesticPaxExemptionChargeProviderTest {

    private DomesticPaxExemptionChargeProvider provider;

    @Before
    public void setup() {
        LargestExemptionChargeMethod method = new LargestExemptionChargeMethod();
        provider = new DomesticPaxExemptionChargeProvider(method);
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


        // CASE 2: flight movement is not modified if no domestic pax exemptions
        FlightMovement flightMovement = mockFlightMovement();
        provider.apply(flightMovement, Collections.singletonList(mockExemptionTypes(null)));

        assertThat(flightMovement.getDomesticPassengerCharges()).isEqualTo(123.0);
        assertThat(flightMovement.getExemptDomesticPassengerCharges()).isEqualTo(0.0);
        assertThat(flightMovement.getFlightNotes()).isEqualTo("MOCK FLIGHT MOVEMENT");


        // CASE 3: flight movement is modified with applied exemption and flight notes
        flightMovement = mockFlightMovement();
        provider.apply(flightMovement, Collections.singletonList(mockExemptionTypes(75.0)));

        assertThat(flightMovement.getDomesticPassengerCharges()).isEqualTo(30.75);
        assertThat(flightMovement.getExemptDomesticPassengerCharges()).isEqualTo(92.25);
        assertThat(flightMovement.getFlightNotes()).isEqualTo("MOCK FLIGHT MOVEMENT; MOCK DOMESTIC PAX CHARGE EXEMPTION");
    }

    private FlightMovement mockFlightMovement() {
        FlightMovement flightMovement = new FlightMovement();

        flightMovement.setDomesticPassengerCharges(123.0);
        flightMovement.setExemptDomesticPassengerCharges(0.0);
        flightMovement.setFlightNotes("MOCK FLIGHT MOVEMENT");

        return flightMovement;
    }

    private ExemptionType mockExemptionTypes(Double domesticPaxChargeExemption) {
        return new ExemptionType() {
            @Override
            public Double domesticPaxChargeExemption() {
                return domesticPaxChargeExemption;
            }

            @Override
            public String flightNoteChargeExemption() {
                return "MOCK DOMESTIC PAX CHARGE EXEMPTION";
            }
        };
    }
}
