package ca.ids.abms.modules.flightmovements;

import ca.ids.abms.modules.aircraft.AircraftRegistration;
import ca.ids.abms.modules.aircraft.AircraftRegistrationService;
import ca.ids.abms.modules.aircraft.AircraftType;
import ca.ids.abms.modules.aircraft.AircraftTypeService;
import ca.ids.abms.modules.exemptions.FlightNotesUtility;
import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementSource;
import ca.ids.abms.modules.flightmovementsbuilder.utility.Item18Field;
import ca.ids.abms.modules.flightmovementsbuilder.utility.Item18Parser;
import ca.ids.abms.modules.unspecifiedaircraft.UnspecifiedAircraftType;
import ca.ids.abms.modules.unspecifiedaircraft.UnspecifiedAircraftTypeService;
import ca.ids.abms.modules.unspecifiedaircraft.UnspecifiedAircraftTypeStatus;
import ca.ids.abms.modules.util.models.ApplicationConstants;
import ca.ids.abms.util.StringUtils;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by c.talpa on 28/03/2017.
 */
@Service
@Transactional
public class FlightMovementAircraftService {

    private static final Logger LOG = LoggerFactory.getLogger(FlightMovementAircraftService.class);

    private final AircraftTypeService aircraftTypeService;
    private final AircraftRegistrationService aircraftRegistrationService;
    private final UnspecifiedAircraftTypeService unspecifiedAircrafttypeService;
    private final FlightMovementRepository flightMovementRepository;

    FlightMovementAircraftService(
        final AircraftTypeService aircraftTypeService,
        final AircraftRegistrationService aircraftRegistrationService,
        final UnspecifiedAircraftTypeService unspecifiedAircrafttypeService,
        final FlightMovementRepository flightMovementRepository
    ) {
        this.aircraftTypeService = aircraftTypeService;
        this.aircraftRegistrationService = aircraftRegistrationService;
        this.unspecifiedAircrafttypeService = unspecifiedAircrafttypeService;
        this.flightMovementRepository = flightMovementRepository;
    }

    @SuppressWarnings("squid:S2259")
    public String checkAndResolveAircraftType(FlightMovement flightMovement) {
        LOG.warn("Check and Resolve AircraftType RegistrationNumber: {}, AircraftType: {}, OtherInfo: {}, DateOfFlight : {}",
        		flightMovement.getItem18RegNum(), flightMovement.getAircraftType(), flightMovement.getOtherInfo(),
        		flightMovement.getDateOfFlight());
        String aircraftTypeResolve = null;

        if (flightMovement.getSource() != null && flightMovement.getSource() == FlightMovementSource.MANUAL && flightMovement.getAircraftType() != null) {
            return flightMovement.getAircraftType();
        }

        if (StringUtils.isNotBlank(flightMovement.getItem18RegNum())) {
             // get aircraft number from registration
            AircraftRegistration aircraftRegistration = aircraftRegistrationService
                    .findAircraftRegistrationByRegistrationNumber(flightMovement.getItem18RegNum(), flightMovement.getDateOfFlight());
            if (aircraftRegistration != null && aircraftRegistration.getAircraftType() != null) {
                aircraftTypeResolve = aircraftRegistration.getAircraftType().getAircraftType();
            } else {
                LOG.warn("Registration  is NULL, I try to resolve by aircraft type filed.");
            }
        }

        // registration is missing
        if (aircraftTypeResolve == null && StringUtils.isNotBlank(flightMovement.getAircraftType())) {
            if (flightMovement.getAircraftType().equalsIgnoreCase(ApplicationConstants.PLACEHOLDER_ZZZZ)) {
                LOG.warn("Aircraft type is an unspecified aircraft type : {} ", flightMovement.getAircraftType());
                String typeFromItem18 = null;
                // aircraft type is an unspecified aircraft type - get the value
                // from item 18 TYP/
                // FROM ICAO: When ZZZZ is present in Item 9, then Item 18 shall
                // contain the type(s)
                // of aircraft preceded if necessary without a space by
                // number(s) of aircraft and separated
                // by one space
                // Check other info
                if (StringUtils.isNotBlank(flightMovement.getOtherInfo())) {
                    typeFromItem18 = findItem18AircraftType(flightMovement.getOtherInfo());
                }

                // If not found in other info, check item18AircraftType field
                if (typeFromItem18 == null && StringUtils.isNotBlank(flightMovement.getItem18AircraftType())) {
                    typeFromItem18 = findItem18AircraftType(flightMovement.getItem18AircraftType());
                }

                if (StringUtils.isNotBlank(typeFromItem18)) {
                    if (aircraftTypeService.findByAircraftType(typeFromItem18) != null) {
                        // found standard aircraft type
                    	aircraftTypeResolve = typeFromItem18;
                    } else {
                        // try to find it in the table of unspecified aircraft
                        // types
                        UnspecifiedAircraftType unspecifiedAircraftType = unspecifiedAircrafttypeService
                                .findTextIdentifier(typeFromItem18);

                        if (unspecifiedAircraftType != null &&
                        		unspecifiedAircraftType.getMTOW() != null &&
                        		unspecifiedAircraftType.getMTOW() > 0.0 &&
                        		StringUtils.isNotBlank(unspecifiedAircraftType.getAircraftType()))
                        {
                           	aircraftTypeResolve = unspecifiedAircraftType.getAircraftType();
                        } else {
                        	// there is no unspecified aircraft type record or MTOW is unknown
                        	// or aircraft type is unknown
                           	// try to find aircraft type from the previous flight
                        	if(StringUtils.isNotBlank(flightMovement.getItem18RegNum()) && flightMovement.getDateOfFlight() != null) {
                        		FlightMovement previousFlight = flightMovementRepository.findLatestByItem18RegAircraftTypeKnown(flightMovement.getItem18RegNum(), flightMovement.getDateOfFlight());
                        		if(previousFlight != null) {
                        			flightMovement.setAircraftType(previousFlight.getAircraftType());
                        			flightMovement.setActualMtow( previousFlight.getActualMtow());
                        			aircraftTypeResolve = previousFlight.getAircraftType();

                        			// add flight notes
                        			String note="aircraft type ZZZZ changed to " + previousFlight.getAircraftType();
                                    FlightNotesUtility.mergeFlightNotes(flightMovement, note);
                        		}
                         	}

                        	if(StringUtils.isBlank(aircraftTypeResolve) && unspecifiedAircraftType == null) {
                        		// create new unspecified Aircraft type
                            	unspecifiedAircraftType = new UnspecifiedAircraftType();
                            	unspecifiedAircraftType.setTextIdentifier(typeFromItem18);
                            	unspecifiedAircraftType.setStatus(UnspecifiedAircraftTypeStatus.SYS_GENERATED);
                           		unspecifiedAircraftType.setAircraftType(null);
                            	unspecifiedAircraftType.setMTOW(null);
                            	unspecifiedAircrafttypeService.create(unspecifiedAircraftType);
                            	aircraftTypeResolve = StringUtils.strip(flightMovement.getAircraftType());
                        	}
                        }
                    }
                } else {
                    LOG.error("Error aircraft type is ZZZZ, but there isn't information in item 18");
                }
            } else { // aircraft type defined in item 9
                // check if it is a known type
                if (aircraftTypeService.findByAircraftType(flightMovement.getAircraftType()) != null) {
                    aircraftTypeResolve = StringUtils.strip(flightMovement.getAircraftType());
                } else {
                    // aircraft type is an unnknown one
                    LOG.error("Error aircraft type is unknown!");
                }
            }
        }

        return aircraftTypeResolve;
    }

    @SuppressWarnings("squid:S2259")
    public String checkAndResolveItem18AircraftType(
        final String aircraftType,
        final String otherInfo,
        String item18AircraftType
    ) {
        LOG.warn("Check and Resolve Item 18 AircraftType, AircraftType: {}, OtherInfo: {}", aircraftType, otherInfo);

        if (aircraftType == null || aircraftType.equalsIgnoreCase(ApplicationConstants.PLACEHOLDER_ZZZZ)) {
            return null;
        }

        String item18AircraftTypeResolve = null;
        if (StringUtils.isNotBlank(otherInfo)) {

            // FROM ICAO: When ZZZZ is present in Item 9, then Item 18 contains the type(s)
            String aircraftTypeItem18 = findItem18AircraftType(otherInfo);
            if (StringUtils.isNotBlank(aircraftTypeItem18)) {
                item18AircraftTypeResolve = aircraftTypeItem18;
            }
        }

        if (item18AircraftTypeResolve == null && StringUtils.isNotBlank(item18AircraftType)) {

            // Check for item18AircraftType value
            String aircraftTypeItem18 = findItem18AircraftType(item18AircraftType);
            if (StringUtils.isNotBlank(aircraftTypeItem18)) {
                item18AircraftTypeResolve = aircraftTypeItem18;
            }
        }

        return item18AircraftTypeResolve;
    }

    public AircraftType findAircaftType(final String aircraftType) {
        return aircraftTypeService.findByAircraftType(aircraftType);
    }

    /**
     * Get the UnspecifiedAircraftType by the unique text identifier.
     * @param textIdentifier unique text identifier
     * @return single unspecified aircraft type
     */
    public UnspecifiedAircraftType findUnspecifiedAircraftType(final String textIdentifier) {
        return unspecifiedAircrafttypeService.findTextIdentifier(textIdentifier);
    }

    public AircraftRegistration findAircraftRegistration(final String registrationNumber, LocalDateTime date) {
        return aircraftRegistrationService.findAircraftRegistrationByRegistrationNumber(registrationNumber, date);
    }

    public boolean startsWithAircraftRegistrationPrefix(String registrationNumber) {
        return aircraftRegistrationService.startsWithAircraftRegistrationPrefix(registrationNumber);
    }

    public String findItem18AircraftType(String item18) {
        String otherInfoTYP = null;
        if (item18 != null) {
            otherInfoTYP = org.apache.commons.lang.StringUtils.rightPad(
                item18, item18.length() + 1
            );
        }

        if (otherInfoTYP != null && !otherInfoTYP.toUpperCase().startsWith("TYP/")) {
            otherInfoTYP = "TYP/" + otherInfoTYP;
        }

        return Item18Parser.parse(otherInfoTYP, Item18Field.TYP);
    }
}
