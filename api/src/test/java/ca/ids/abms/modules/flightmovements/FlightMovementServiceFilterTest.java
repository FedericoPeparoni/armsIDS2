package ca.ids.abms.modules.flightmovements;

import ca.ids.abms.modules.aerodromeoperationalhours.AerodromeOperationalHoursService;
import ca.ids.abms.modules.aircraft.AircraftTypeService;
import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementStatus;
import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementType;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementBuilder;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementMerge;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementValidator;
import ca.ids.abms.modules.flightmovementsbuilder.enumerate.FlightMovementValidatorIssue;
import ca.ids.abms.modules.flightmovementsbuilder.utility.DeltaFlightUtility;
import ca.ids.abms.modules.flightmovementsbuilder.utility.FlightMovementBuilderUtility;
import ca.ids.abms.modules.flightmovementsbuilder.utility.ThruFlightPlanUtility;
import ca.ids.abms.modules.plugins.PluginService;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.transactions.TransactionService;
import ca.ids.abms.modules.util.models.CurrencyUtils;
import ca.ids.abms.modules.util.models.WhitelistingUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FlightMovementServiceFilterTest {

    private FlightMovementService flightMovementService;

    private FlightMovementRepository flightMovementRepository;

    @Before
    public void setup() {

        flightMovementRepository = mock(FlightMovementRepository.class);
        FlightMovementAerodromeService flightMovementAerodromeService = mock(FlightMovementAerodromeService.class);
        AircraftTypeService aircraftTypeService = mock(AircraftTypeService.class);
        FlightMovementBuilder flightMovementBuilder = mock(FlightMovementBuilder.class);
        FlightMovementValidator flightMovementValidator = mock(FlightMovementValidator.class);
        FlightMovementBuilderUtility flightMovementBuilderUtility = mock(FlightMovementBuilderUtility.class);
        ThruFlightPlanUtility thruFlightPlanUtility = mock(ThruFlightPlanUtility.class);

        FlightMovementMerge flightMovementMerge = new FlightMovementMerge(
            mock(DeltaFlightUtility.class), mock(ThruFlightPlanUtility.class));

        flightMovementService = new FlightMovementService(flightMovementRepository, flightMovementAerodromeService,
            aircraftTypeService, flightMovementBuilder, flightMovementValidator, flightMovementBuilderUtility,
            thruFlightPlanUtility, mock(SystemConfigurationService.class), flightMovementMerge,
            mock(FlightMovementRepositoryUtility.class), mock(AerodromeOperationalHoursService.class),
            mock(CurrencyUtils.class), mock(TransactionService.class), mock(WhitelistingUtils.class), mock(PluginService.class));
    }

    @Test
    public void getFlightMovementByMovementStatus() {

        List<FlightMovement> fml = new ArrayList<>();
        FlightMovement flightMovement = new FlightMovement();
        flightMovement.setFlightId("TEST2");
        flightMovement.setMovementType(FlightMovementType.DOMESTIC);
        flightMovement.setStatus(FlightMovementStatus.INCOMPLETE);
        fml.add(flightMovement);
        Page<FlightMovement> fmlPage = new PageImpl<>(fml);

        when(flightMovementRepository.findAllByStatus(any(Pageable.class), any(FlightMovementStatus.class)))
                .thenReturn(fmlPage);

        Page<FlightMovement> flightMovementResult = flightMovementService.findAllFlightMovementByStatus("INCOMPLETE",
                mock(Pageable.class));
        assertThat(flightMovementResult.getNumberOfElements()).isEqualTo(fmlPage.getNumberOfElements());
    }

    @Test
    public void getFlightMovementByIssue() {

        List<FlightMovement> fml = new ArrayList<>();
        FlightMovement flightMovement = new FlightMovement();
        flightMovement.setFlightId("TEST2");
        flightMovement.setMovementType(FlightMovementType.DOMESTIC);
        flightMovement.setStatus(FlightMovementStatus.INCOMPLETE);
        flightMovement.setResolutionErrors(FlightMovementValidatorIssue.MISSING_MTOW.toValue());
        fml.add(flightMovement);
        Page<FlightMovement> fmlPage = new PageImpl<>(fml);

        when(flightMovementRepository.findAllByResolutionErrorsContaining(any(), any(Pageable.class)))
                .thenReturn(fmlPage);

        Page<FlightMovement> flightMovementResult = flightMovementService
                .findAllFlightMovementByIssue(mock(Pageable.class), "MISSING_MTOW");
        assertThat(flightMovementResult.getNumberOfElements()).isEqualTo(fmlPage.getNumberOfElements());
    }
}
