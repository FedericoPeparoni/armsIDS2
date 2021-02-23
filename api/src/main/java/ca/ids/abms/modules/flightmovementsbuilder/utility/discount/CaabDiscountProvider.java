package ca.ids.abms.modules.flightmovementsbuilder.utility.discount;

import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.FlightMovementRepository;
import ca.ids.abms.modules.system.BillingOrgCode;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.util.models.DateTimeUtils;
import com.google.common.collect.Multimap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;

@Component
public class CaabDiscountProvider implements DiscountProvider {
    private static final Logger LOG = LoggerFactory.getLogger(CaabDiscountProvider.class);

    private final FlightMovementRepository flightMovementRepository;
    private final SystemConfigurationService systemConfigurationService;

    CaabDiscountProvider(
        final FlightMovementRepository flightMovementRepository,
        final SystemConfigurationService systemConfigurationService
    ) {
        this.flightMovementRepository = flightMovementRepository;
        this.systemConfigurationService = systemConfigurationService;
    }

    /**
     * This method returns 0.5 (50%) if the given flight movement aerodrome charge can be eligible to get a discount of
     * 50%, else 1.0 (100%) is returned indicating that the flight movement aerodrome charge should be charged the
     * full amount.
     *
     * According the policy of CAAB, this discount applies to any registered flight that lands at the same destination
     * two or more times in one calendar day that was not scheduled (FlightType = 'N'). The account type must be
     * `General Aviation` `Charter` to apply.
     */
    @Override
    public Double getArrivalChargeDiscount(final FlightMovement flightMovement, final String arrivalAd,
                                           final Multimap<String, Integer> previousStops) {
        if (systemConfigurationService.getBillingOrgCode() != BillingOrgCode.CAAB)
            return DiscountConstants.FULL_RATE;

        // verify if flight movement can have discount applied
        if (flightMovement.getDateOfFlight() == null || arrivalAd == null || !isFlightMovementApplicable(flightMovement)) {
            LOG.debug("Cannot calculate CAAB Discount, flightType={}, regNum={}, arrivalAd={}",
                flightMovement.getMovementType(), flightMovement.getItem18RegNum(), arrivalAd);
            return DiscountConstants.FULL_RATE;
        }

        // if previous stop already charged, return discount as no need to query previous flight movements
        if (previousStops.containsKey(arrivalAd))
            return CaabDiscountConstants.ARRIVAL_DISCOUNT_RATE;

        // determine appropriate arrival date
        LocalTime depTime = DateTimeUtils.convertStringToLocalTime(flightMovement.getDepTime());
        LocalTime arrivalTime = DateTimeUtils.convertStringToLocalTime(flightMovement.getArrivalTime());

        LocalDateTime dateOfArrival;
        if (arrivalTime != null && depTime != null && arrivalTime.isBefore(depTime))
            dateOfArrival = flightMovement.getDateOfFlight().plusDays(1);
        else
            dateOfArrival = flightMovement.getDateOfFlight();

        // find previous flight movement where flight arrives at same aerodrome on the same day
        FlightMovement priorArrivalCharge = null;
        if(StringUtils.isNotBlank(flightMovement.getItem18RegNum())) {
            priorArrivalCharge = flightMovementRepository.findPriorArrivalCharge(
                    flightMovement.getFlightId(), flightMovement.getDateOfFlight(), flightMovement.getDepTime(),
                    flightMovement.getDepAd(), flightMovement.getItem18RegNum(), flightMovement.getAccount().getId(),
                    flightMovement.getFlightType(), arrivalAd, dateOfArrival, dateOfArrival.minusDays(1));
        }
        // apply discount if previous stop found, else charge full rate
        return priorArrivalCharge != null
            ? CaabDiscountConstants.ARRIVAL_DISCOUNT_RATE : DiscountConstants.FULL_RATE;
    }

    /**
     * Valid for discount only if flight movement type is non-scheduled, general or other (FlightType = 'N', 'G', 'X')
     * and account type is `General Aviation` or `Charter`.
     */
    private Boolean isFlightMovementApplicable(final FlightMovement flightMovement) {

        // validate that required data is not null
        if (flightMovement.getFlightType() == null ||
            flightMovement.getAccount() == null ||
            flightMovement.getAccount().getAccountType() == null ||
            flightMovement.getAccount().getAccountType().getName() == null) {
            LOG.warn("No Account.AccountType and/or FlightType associated with the flight movement '{}'", flightMovement);
            return false;
        }

        // verify non-scheduled, general or other flight type and charter or general aviation account type
        return isAccountApplicable(flightMovement) && isFlightTypeApplicable(flightMovement);
    }

    /**
     * Valid for discount if flight movement account type is `General Aviation` or `Charter`.
     */
    private Boolean isAccountApplicable(final FlightMovement flightMovement) {

        String accountType = flightMovement.getAccount().getAccountType().getName();

        return accountType.equalsIgnoreCase("Charter")
            || accountType.equalsIgnoreCase("GeneralAviation");
    }

    /**
     * Valid for discount if flight movement type is non-scheduled, general
     * or other (FlightType = 'N', 'G', 'X').
     */
    private Boolean isFlightTypeApplicable(final FlightMovement flightMovement) {

        String flightType = flightMovement.getFlightType().toUpperCase(Locale.US);

        return flightType.contains("N")
            || flightType.contains("G")
            || flightType.contains("X");
    }
}
