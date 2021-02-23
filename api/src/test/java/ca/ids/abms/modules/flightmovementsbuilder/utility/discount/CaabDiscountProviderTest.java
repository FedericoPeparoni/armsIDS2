package ca.ids.abms.modules.flightmovementsbuilder.utility.discount;

import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.accounts.AccountType;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.FlightMovementRepository;
import ca.ids.abms.modules.system.BillingOrgCode;
import ca.ids.abms.modules.system.SystemConfigurationService;
import com.google.common.collect.ImmutableListMultimap;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

public class CaabDiscountProviderTest {

    private FlightMovementRepository flightMovementRepository;

    private SystemConfigurationService systemConfigurationService;

    private CaabDiscountProvider caabDiscountProvider;

    @Before
    public void setup() {

        flightMovementRepository = mock(FlightMovementRepository.class);
        when(flightMovementRepository
            .findPriorArrivalCharge(anyString(), any(LocalDateTime.class), anyString(), anyString(), anyString(),
                anyInt(), anyString(), anyString(), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(validFlightMovement());

        systemConfigurationService = mock(SystemConfigurationService.class);
        when(systemConfigurationService.getBillingOrgCode())
            .thenReturn(BillingOrgCode.CAAB);

        caabDiscountProvider = new CaabDiscountProvider(flightMovementRepository, systemConfigurationService);
    }

    @Test
    public void discountAppliedTest() {

        // verify that discount rate is returned when prior flight movement charged full amount
        FlightMovement flightMovement = validFlightMovement();
        assertThat(caabDiscountProvider.getArrivalChargeDiscount(flightMovement, "ABCD", ImmutableListMultimap.of()))
            .isEqualTo(CaabDiscountConstants.ARRIVAL_DISCOUNT_RATE);

        // verify that full rate is returned when prior flight movement not charged full amount
        assertThat(caabDiscountProvider.getArrivalChargeDiscount(flightMovement, "LMNO", ImmutableListMultimap.of()))
            .isEqualTo(CaabDiscountConstants.ARRIVAL_DISCOUNT_RATE);

        // verify that full rate is returned when prior flight movement not charged full amount
        when(flightMovementRepository
            .findPriorArrivalCharge(anyString(), any(LocalDateTime.class), anyString(), anyString(), anyString(),
                anyInt(), anyString(), anyString(), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(null);
        assertThat(caabDiscountProvider.getArrivalChargeDiscount(flightMovement, "ABCD", ImmutableListMultimap.of()))
            .isEqualTo(DiscountConstants.FULL_RATE);

        // verify that discount rate is returned when prior stop charged full amount
        assertThat(caabDiscountProvider.getArrivalChargeDiscount(flightMovement, "ABCD", ImmutableListMultimap.of("ABCD", 100)))
            .isEqualTo(CaabDiscountConstants.ARRIVAL_DISCOUNT_RATE);

        // verify that full rate is returned when prior stop not charged full amount
        assertThat(caabDiscountProvider.getArrivalChargeDiscount(flightMovement, "ABCD", ImmutableListMultimap.of("ABCD", 50)))
            .isEqualTo(CaabDiscountConstants.ARRIVAL_DISCOUNT_RATE);
    }

    @Test
    public void dateOfFlightTest() {

        // verify that current day is used for date of flight if arrival is greater then departure time
        FlightMovement flightMovement = validFlightMovement();
        caabDiscountProvider.getArrivalChargeDiscount(flightMovement, "ABCD", ImmutableListMultimap.of());

        verify(flightMovementRepository, times(1)).findPriorArrivalCharge(anyString(),
            any(LocalDateTime.class), anyString(), anyString(), anyString(), anyInt(), anyString(), anyString(),
            eq(flightMovement.getDateOfFlight()), eq(flightMovement.getDateOfFlight().minusDays(1)));

        // verify that next day is used for date of flight if arrival is less then departure time
        flightMovement.setDepTime("1000");
        flightMovement.setArrivalTime("0800");
        caabDiscountProvider.getArrivalChargeDiscount(flightMovement, "ABCD", ImmutableListMultimap.of());

        verify(flightMovementRepository, times(1)).findPriorArrivalCharge(anyString(),
            any(LocalDateTime.class), anyString(), anyString(), anyString(), anyInt(), anyString(), anyString(),
            eq(flightMovement.getDateOfFlight().plusDays(1)), eq(flightMovement.getDateOfFlight()));
    }

    @Test
    public void isOrganizationCaabTest() {

        // verify that discount rate is used when organization is CAAB
        assertThat(caabDiscountProvider.getArrivalChargeDiscount(validFlightMovement(), "ABCD", ImmutableListMultimap.of()))
            .isEqualTo(CaabDiscountConstants.ARRIVAL_DISCOUNT_RATE);

        // verify that full rate is returned when active organization is NOT CAAB
        for (BillingOrgCode code : BillingOrgCode.values()) {
            if (code == BillingOrgCode.CAAB)
                continue;
            when(systemConfigurationService.getBillingOrgCode())
                .thenReturn(code);
            assertThat(caabDiscountProvider.getArrivalChargeDiscount(validFlightMovement(), "ABCD", ImmutableListMultimap.of()))
                .isEqualTo(DiscountConstants.FULL_RATE);
        }
    }

    @Test
    public void isFlightMovementApplicableTest() {

        // verify that discount rate is returned when valid flight movement
        FlightMovement flightMovement = validFlightMovement();
        assertThat(caabDiscountProvider.getArrivalChargeDiscount(flightMovement, "ABCD", ImmutableListMultimap.of()))
            .isEqualTo(CaabDiscountConstants.ARRIVAL_DISCOUNT_RATE);
        flightMovement.setFlightType("G");
        assertThat(caabDiscountProvider.getArrivalChargeDiscount(flightMovement, "ABCD", ImmutableListMultimap.of()))
            .isEqualTo(CaabDiscountConstants.ARRIVAL_DISCOUNT_RATE);
        flightMovement.setFlightType("X");
        assertThat(caabDiscountProvider.getArrivalChargeDiscount(flightMovement, "ABCD", ImmutableListMultimap.of()))
            .isEqualTo(CaabDiscountConstants.ARRIVAL_DISCOUNT_RATE);

        // verify that full rate is returned when invalid flight movement
        flightMovement.setFlightType("S");
        assertThat(caabDiscountProvider.getArrivalChargeDiscount(flightMovement, "ABCD", ImmutableListMultimap.of()))
            .isEqualTo(DiscountConstants.FULL_RATE);
        flightMovement.setFlightType("M");
        assertThat(caabDiscountProvider.getArrivalChargeDiscount(flightMovement, "ABCD", ImmutableListMultimap.of()))
            .isEqualTo(DiscountConstants.FULL_RATE);
        flightMovement.setItem18RegNum(null);
        assertThat(caabDiscountProvider.getArrivalChargeDiscount(flightMovement, "ABCD", ImmutableListMultimap.of()))
            .isEqualTo(DiscountConstants.FULL_RATE);

    }

    private FlightMovement validFlightMovement() {

        AccountType accountType = new AccountType();
        accountType.setId(1);
        accountType.setName("Charter");

        Account account = new Account();
        account.setId(1);
        account.setName("TEST_ACCOUNT_NAME");
        account.setAccountType(accountType);

        FlightMovement flightMovement = new FlightMovement();
        flightMovement.setFlightType("N");
        flightMovement.setAccount(account);
        flightMovement.setDateOfFlight(LocalDate.now().atStartOfDay());
        flightMovement.setDepTime("0800");
        flightMovement.setArrivalTime("1000");
        flightMovement.setArrivalChargeDiscounts(ImmutableListMultimap.of(
            "ABCD", 100,
            "WXYZ", 100,
            "ABCD", 50,
            "LMNO", 50));

        flightMovement.setItem18RegNum("TESTREG");
        return flightMovement;
    }
}
