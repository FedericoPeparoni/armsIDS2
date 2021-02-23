package ca.ids.abms.modules.flightmovementsbuilder.utility.discount;

import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.FlightMovementRepository;

import com.google.common.collect.Multimap;
import edu.emory.mathcs.backport.java.util.Collections;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DiscountUtilityTest {

    private DiscountUtility discountUtility;

    private DiscountProvider providerA;

    private DiscountProvider providerB;
     private FlightMovementRepository flightMovementRepository;

    @Before
    public void setup() {

        // mock two providers
        providerA = mock(DiscountProvider.class);
        providerB = mock(DiscountProvider.class);
        flightMovementRepository = mock(FlightMovementRepository.class);
        // initialize utility from list of providers
        discountUtility = new DiscountUtility(Arrays.asList(providerA, providerB),flightMovementRepository);
    }

    @Test
    public void getArrivalChargeDiscountTest() {

        // mock discount providers and verify that two decimal place rounding is used
        when(providerA.getArrivalChargeDiscount(any(FlightMovement.class), anyString(), any(Multimap.class)))
            .thenReturn(0.75);
        when(providerB.getArrivalChargeDiscount(any(FlightMovement.class), anyString(), any(Multimap.class)))
            .thenReturn(0.5);
        assertThat(discountUtility.getArrivalChargeDiscount(new FlightMovement(), "TEST_AD"))
            .isEqualTo(0.38);

        // mock zero providers and verify that full amount is still charged
        assertThat(new DiscountUtility(Collections.emptyList(),flightMovementRepository)
            .getArrivalChargeDiscount(new FlightMovement(), "TEST_AD"))
            .isEqualTo(DiscountConstants.FULL_RATE);

        // mock discount providers and verify that null is ignored
        when(providerA.getArrivalChargeDiscount(any(FlightMovement.class), anyString(), any(Multimap.class)))
            .thenReturn(0.5);
        when(providerB.getArrivalChargeDiscount(any(FlightMovement.class), anyString(), any(Multimap.class)))
            .thenReturn(null);
        assertThat(discountUtility.getArrivalChargeDiscount(new FlightMovement(), "TEST_AD"))
            .isEqualTo(0.5);
    }
}
