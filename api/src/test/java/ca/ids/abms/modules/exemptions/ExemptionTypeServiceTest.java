package ca.ids.abms.modules.exemptions;

import ca.ids.abms.modules.countries.Country;
import ca.ids.abms.modules.countries.CountryRepository;
import ca.ids.abms.modules.currencies.*;
import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.exemptions.charges.providers.ExemptionChargeProvider;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.FlightMovementRepository;
import ca.ids.abms.modules.system.SystemConfiguration;
import ca.ids.abms.modules.system.SystemConfigurationRepository;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.modules.transactions.TransactionRepository;
import ca.ids.abms.modules.util.models.CurrencyUtils;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExemptionTypeServiceTest {

    private static final LocalDateTime BILLING_DATE = LocalDate.of(2019, 1, 1).atStartOfDay();
    private static final HashMap<String, CurrencyExchangeRate> RATES_TO_USD = buildExchangeRatesToUSD();

    private ExemptionTypeService exemptionTypeService;

    @Before
    public void setup() {
        CurrencyRepository currencyRepository = mock(CurrencyRepository.class);
        CurrencyExchangeRateRepository currencyExchangeRateRepository = mock(CurrencyExchangeRateRepository.class);
        SystemConfigurationRepository systemConfigurationRepository = mock(SystemConfigurationRepository.class);
        TransactionRepository transactionRepository = mock(TransactionRepository.class);
        FlightMovementRepository flightMovementRepository = mock(FlightMovementRepository.class);
        CountryRepository countryRepository = mock(CountryRepository.class);

        CurrencyUtils currencyUtils = new CurrencyUtils(
            new CurrencyService(currencyRepository),
            new CurrencyExchangeRateService(currencyRepository, currencyExchangeRateRepository, null),
            new SystemConfigurationService(systemConfigurationRepository, transactionRepository, flightMovementRepository, countryRepository));

        this.exemptionTypeService = new ExemptionTypeService(currencyUtils,
            Collections.singletonList(mockExemptionChargeProvider()),
            null,
            Collections.singletonList(mockExemptionTypeProvider()));

        when(currencyRepository.getANSPCurrency()).thenReturn(RATES_TO_USD.get("BWP").getCurrency());
        when(currencyRepository.findByCurrencyCode(eq("USD"))).thenReturn(RATES_TO_USD.get("USD").getCurrency());
        when(currencyRepository.findByCurrencyCode(eq("CAD"))).thenReturn(RATES_TO_USD.get("CAD").getCurrency());
        when(currencyRepository.findByCurrencyCode(eq("BWP"))).thenReturn(RATES_TO_USD.get("BWP").getCurrency());
        when(currencyExchangeRateRepository.getApplicableCurrencyExchangeRateToUsd(eq(1), any(LocalDateTime.class)))
            .thenReturn(buildRates(RATES_TO_USD.get("USD")));
        when(currencyExchangeRateRepository.getApplicableCurrencyExchangeRateToUsd(eq(2), any(LocalDateTime.class)))
            .thenReturn(buildRates(RATES_TO_USD.get("BWP")));
        when(currencyExchangeRateRepository.getApplicableCurrencyExchangeRateToUsd(eq(3), any(LocalDateTime.class)))
            .thenReturn(buildRates(RATES_TO_USD.get("CAD")));

        mockSysConfig(systemConfigurationRepository, SystemConfigurationItemName.INTERNATIONAL_PAX_FEE_CURRENCY, "CAD");
        mockCurrencyConfig(systemConfigurationRepository, SystemConfigurationItemName.AIR_NAVIGATION_CHARGES_CURRENCY);
        mockCurrencyConfig(systemConfigurationRepository, SystemConfigurationItemName.DOMESTIC_PAX_FEE_CURRENCY);
        mockCurrencyConfig(systemConfigurationRepository, SystemConfigurationItemName.INTERNATIONAL_PAX_FEE_CURRENCY);
        mockCurrencyConfig(systemConfigurationRepository, SystemConfigurationItemName.DOMESTIC_AERODROME_CHARGES_CURRENCY);
        mockCurrencyConfig(systemConfigurationRepository, SystemConfigurationItemName.DOMESTIC_APPROACH_CHARGES_CURRENCY);
        mockCurrencyConfig(systemConfigurationRepository, SystemConfigurationItemName.DOMESTIC_LATE_ARRIVAL_DEPARTURE_CHARGES_CURRENCY);
        mockCurrencyConfig(systemConfigurationRepository, SystemConfigurationItemName.DOMESTIC_EXTENDED_HOURS_SURCHARGE_CURRENCY);
        mockCurrencyConfig(systemConfigurationRepository, SystemConfigurationItemName.REGIONAL_AERODROME_CHARGES_CURRENCY);
        mockCurrencyConfig(systemConfigurationRepository, SystemConfigurationItemName.REGIONAL_APPROACH_CHARGES_CURRENCY);
        mockCurrencyConfig(systemConfigurationRepository, SystemConfigurationItemName.REGIONAL_LATE_ARRIVAL_DEPARTURE_CHARGES_CURRENCY);
        mockCurrencyConfig(systemConfigurationRepository, SystemConfigurationItemName.REGIONAL_EXTENDED_HOURS_SURCHARGE_CURRENCY);
        mockCurrencyConfig(systemConfigurationRepository, SystemConfigurationItemName.INTERNATIONAL_AERODROME_CHARGES_CURRENCY);
        mockCurrencyConfig(systemConfigurationRepository, SystemConfigurationItemName.INTERNATIONAL_APPROACH_CHARGES_CURRENCY);
        mockCurrencyConfig(systemConfigurationRepository, SystemConfigurationItemName.INTERNATIONAL_LATE_ARRIVAL_DEPARTURE_CHARGES_CURRENCY);
        mockCurrencyConfig(systemConfigurationRepository, SystemConfigurationItemName.INTERNATIONAL_EXTENDED_HOURS_SURCHARGE_CURRENCY);
    }

    @Test
    public void resolveFlightMovementExemptionsTest() {

        final FlightMovement flightMovement = mockFlightMovement();
        this.exemptionTypeService.resolveFlightMovementExemptions(flightMovement);

        assertThat(flightMovement.getEnrouteCharges()).isEqualTo(0.0);
        assertThat(flightMovement.getLateArrivalCharges()).isEqualTo(1.4);
        assertThat(flightMovement.getLateDepartureCharges()).isEqualTo(1.6);
        assertThat(flightMovement.getParkingCharges()).isEqualTo(3.0);
        assertThat(flightMovement.getApproachCharges()).isEqualTo(5.3);
        assertThat(flightMovement.getAerodromeCharges()).isEqualTo(0.2);
        assertThat(flightMovement.getDomesticPassengerCharges()).isEqualTo(0.5);
        assertThat(flightMovement.getInternationalPassengerCharges()).isEqualTo(2.1);

        assertThat(flightMovement.getExemptEnrouteCharges()).isEqualTo(1.0);
        assertThat(flightMovement.getExemptApprochCharges()).isEqualTo(0.0);
        assertThat(flightMovement.getExemptParkingCharges()).isEqualTo(0.0);
        assertThat(flightMovement.getExemptLateArrivalCharges()).isEqualTo(0.0);
        assertThat(flightMovement.getExemptLateDepartureCharges()).isEqualTo(0.0);
        assertThat(flightMovement.getExemptDomesticPassengerCharges()).isEqualTo(0.0);
        assertThat(flightMovement.getExemptInternationalPassengerCharges()).isEqualTo(0.0);
        assertThat(flightMovement.getExemptAerodromeCharges()).isEqualTo(0.0);

        assertThat(flightMovement.getTotalCharges()).isEqualTo(14.1);
        assertThat (flightMovement.getFlightNotes()).isEqualTo("MOCK FLIGHT NOTE; MOCK EXEMPTION TYPE");
    }

    private static List<CurrencyExchangeRate> buildRates (final CurrencyExchangeRate cer) {
        final List<CurrencyExchangeRate> items = new ArrayList<>(1);
        items.add(cer);
        return items;
    }

    private static HashMap<String, CurrencyExchangeRate> buildExchangeRatesToUSD() {
        final Country usa = new Country();
        usa.setId(1);
        usa.setCountryCode("USA");
        usa.setCountryName("United States of America");

        final Country bwa = new Country();
        bwa.setId(2);
        bwa.setCountryCode("BWA");
        bwa.setCountryName("Botsawana");

        final Country can = new Country();
        can.setId(3);
        can.setCountryCode("CAN");
        can.setCountryName("Canada");

        final Currency usd = new Currency();
        usd.setId(1);
        usd.setCurrencyCode("USD");
        usd.setDecimalPlaces(2);
        usd.setSymbol("$");
        usd.setCountryCode(usa);

        final Currency bwp = new Currency();
        bwp.setId(2);
        bwp.setCurrencyCode("BWP");
        bwp.setDecimalPlaces(2);
        bwp.setSymbol("B");
        bwp.setCountryCode(bwa);

        final Currency cad = new Currency();
        cad.setId(3);
        cad.setCurrencyCode("CAD");
        cad.setDecimalPlaces(2);
        cad.setSymbol("$");
        cad.setCountryCode(can);

        final CurrencyExchangeRate usdRate = new CurrencyExchangeRate();
        usdRate.setCurrency(usd);
        usdRate.setId(1);
        usdRate.setExchangeRate(1.0);
        usdRate.setExchangeRateValidFromDate(BILLING_DATE.minusDays(1));
        usdRate.setExchangeRateValidToDate(BILLING_DATE.plusDays(1));

        final CurrencyExchangeRate bwpRate = new CurrencyExchangeRate();
        bwpRate.setCurrency(bwp);
        bwpRate.setId(2);
        bwpRate.setExchangeRate(1.5);
        bwpRate.setExchangeRateValidFromDate(BILLING_DATE.minusDays(1));
        bwpRate.setExchangeRateValidToDate(BILLING_DATE.plusDays(1));

        final CurrencyExchangeRate cadRate = new CurrencyExchangeRate();
        cadRate.setCurrency(cad);
        cadRate.setId(3);
        cadRate.setExchangeRate(0.5);
        cadRate.setExchangeRateValidFromDate(BILLING_DATE.minusDays(1));
        cadRate.setExchangeRateValidToDate(BILLING_DATE.plusDays(1));

        final HashMap<String, CurrencyExchangeRate> rates = new HashMap<>(3);
        rates.put("USD", usdRate);
        rates.put("BWP", bwpRate);
        rates.put("CAD", cadRate);
        return rates;
    }

    private static void mockSysConfig(final SystemConfigurationRepository repository, final String name, final String value) {
        SystemConfiguration systemConfiguration = new SystemConfiguration();

        systemConfiguration.setItemName(name);
        systemConfiguration.setCurrentValue(value);

        when(repository.getOneByItemName(eq(name))).thenReturn(systemConfiguration);
    }

    private static void mockCurrencyConfig(final SystemConfigurationRepository repository, final String name) {
        mockSysConfig(repository, name, "ANSP");
    }

    private static FlightMovement mockFlightMovement() {
        final FlightMovement fm = new FlightMovement();

        fm.setFlightId("ABC123");
        fm.setItem18RegNum("ABCDE");

        fm.setDepAd("ABCD");
        fm.setDestAd("WXYZ");

        fm.setDepTime("0900");
        fm.setDateOfFlight(BILLING_DATE);
        fm.setBillingDate(BILLING_DATE);

        fm.setEnrouteCharges(1.0);
        fm.setLateArrivalCharges(1.4);
        fm.setLateDepartureCharges(1.6);
        fm.setParkingCharges(3.0);
        fm.setApproachCharges(5.3);
        fm.setAerodromeCharges(0.2);
        fm.setDomesticPassengerCharges(0.5);
        fm.setInternationalPassengerCharges(2.1);

        fm.setExemptEnrouteCharges(0.0);
        fm.setExemptLateArrivalCharges(0.0);
        fm.setExemptLateDepartureCharges(0.0);
        fm.setExemptParkingCharges(0.0);
        fm.setExemptApprochCharges(0.0);
        fm.setExemptAerodromeCharges(0.0);
        fm.setExemptDomesticPassengerCharges(0.0);
        fm.setExemptInternationalPassengerCharges(0.0);

        fm.setTotalCharges(15.1);
        fm.setFlightNotes("MOCK FLIGHT NOTE");

        return fm;
    }

    private static ExemptionType mockExemptionType() {

        return new ExemptionType() {
            @Override
            public Double enrouteChargeExemption() {
                return 100.0;
            }

            @Override
            public String flightNoteChargeExemption() {
                return "MOCK EXEMPTION TYPE";
            }
        };
    }

    private static ExemptionTypeProvider mockExemptionTypeProvider() {
        return new ExemptionTypeProvider() {
            @Override
            public Collection<ExemptionType> findApplicableExemptions(FlightMovement flightMovement) {
                return Collections.singletonList(mockExemptionType());
            }
        };
    }

    private static ExemptionChargeProvider mockExemptionChargeProvider() {
        // following mock exemption type definition, enroute should eb fully exempt
        return (flightMovement, exemptions) -> {
            flightMovement.setExemptEnrouteCharges(flightMovement.getEnrouteCharges());
            flightMovement.setEnrouteCharges(0.0);
            FlightNotesUtility.mergeFlightNotes(flightMovement, "MOCK EXEMPTION TYPE");
        };
    }
}
