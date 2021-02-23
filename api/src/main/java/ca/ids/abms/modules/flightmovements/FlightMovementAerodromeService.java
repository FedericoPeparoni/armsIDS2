package ca.ids.abms.modules.flightmovements;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.modules.aerodromes.Aerodrome;
import ca.ids.abms.modules.aerodromes.AerodromeService;
import ca.ids.abms.modules.aerodromes.AerodromeCategory;
import ca.ids.abms.modules.airspaces.AirspaceService;
import ca.ids.abms.modules.flightmovements.enumerate.AdResolveType;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementBuilderException;
import ca.ids.abms.modules.flightmovementsbuilder.enumerate.FlightMovementBuilderIssue;
import ca.ids.abms.modules.flightmovementsbuilder.utility.Item18Parser;
import ca.ids.abms.modules.flightmovementsbuilder.vo.DeltaFlightVO;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.modules.unspecified.UnspecifiedDepartureDestinationLocation;
import ca.ids.abms.modules.unspecified.UnspecifiedDepartureDestinationLocationService;
import ca.ids.abms.modules.unspecified.UnspecifiedDepartureDestinationLocationStatus;
import ca.ids.abms.modules.util.models.ApplicationConstants;
import ca.ids.abms.modules.util.models.NavDBUtils;
import ca.ids.abms.modules.util.models.geometry.CoordinatesConversionUtility;
import ca.ids.abms.modules.util.models.geometry.CoordinatesVO;
import ca.ids.abms.util.GeometryUtils;
import ca.ids.abms.util.StringUtils;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.WKTReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FlightMovementAerodromeService {

    private static final Logger LOG = LoggerFactory.getLogger(FlightMovementAerodromeService.class);

    /**
     * System configuration setting value for determining domestic aerodrome identifiers by
     * aerodrome prefix against configured country code prefixes.
     */
    private static final String DETERMINED_BY_PREFIX = "Prefix";

    /**
     * System configuration setting value for determining domestic aerodrome identifiers by
     * aerodrome location within FIR.
     */
    private static final String DETERMINED_BY_LOCATION = "Location";

    /**
     * System configuration setting value for determining domestic aerodrome identifiers by
     * either {@link this#DETERMINED_BY_PREFIX} or {@link this#DETERMINED_BY_LOCATION}.
     */
    private static final String DETERMINED_BY_EITHER = "Either";
    
    private final AerodromeService aerodromeService;
    private final UnspecifiedDepartureDestinationLocationService unspecifiedDepartureDestinationLocationService;
    private final NavDBUtils navdbUtils;
    private final AirspaceService airspaceService;
    private final SystemConfigurationService systemConfigurationService;

    public FlightMovementAerodromeService(final AerodromeService aerodromeService,
                                          final UnspecifiedDepartureDestinationLocationService unspecifiedDepartureDestinationLocationService,
                                          final NavDBUtils navdbUtils,
                                          final AirspaceService airspaceService,
                                          final SystemConfigurationService systemConfigurationService) {
        this.aerodromeService = aerodromeService;
        this.unspecifiedDepartureDestinationLocationService = unspecifiedDepartureDestinationLocationService;
        this.navdbUtils = navdbUtils;
        this.airspaceService = airspaceService;
        this.systemConfigurationService = systemConfigurationService;
    }

    /**
     * Will check if {@code aerodromeIdentifier} is {@code 'ZZZZ'} or exists in either NAVDB or ABMSDB.
     *
     * Note, this method will return coordinates from ABMSDB Aerodromes if it does NOT exist in NAVDB. If you require
     * that the aerodrome name is returned instead, see {@link #checkAerodromeIdentifier(String, boolean)}.
     *
     * @param aerodromeIdentifier aerodrome name
     * @return resolved aerodrome identifier or coordinate
     */
    @Transactional(readOnly = true)
    public String checkAerodromeIdentifier(final String aerodromeIdentifier) {
        return this.checkAerodromeIdentifier(aerodromeIdentifier, true);
    }

    /**
     * Will check if {@code aerodromeIdentifier} is {@code 'ZZZZ'} or exists in either NAVDB or ABMSDB.
     *
     * @param aerodromeIdentifier aerodrome name
     * @param resolveToCoordinates resolve to coordinates if not in NAVDB
     * @return resolved aerodrome identifier or coordinate
     */
    @Transactional(readOnly = true)
    public String checkAerodromeIdentifier(final String aerodromeIdentifier, final boolean resolveToCoordinates) {
        return this.checkAerodromeIdentifier(aerodromeIdentifier, resolveToCoordinates, false);
    }

    /**
     * Will check if {@code aerodromeIdentifier} is {@code 'ZZZZ' or 'AFIL" (only for departure aerodromes)} or exists in either NAVDB or ABMSDB.
     *
     * @param aerodromeIdentifier aerodrome name
     * @param resolveToCoordinates resolve to coordinates if not in NAVDB
     * @param checkAfil check AFIL for departure aerodrome
     * @return resolved aerodrome identifier or coordinate
     */
    @Transactional(readOnly = true)
    public String checkAerodromeIdentifier(final String aerodromeIdentifier, final boolean resolveToCoordinates, final boolean checkAfil) {
        return aerodromeService.checkAerodromeIdentifier(aerodromeIdentifier, resolveToCoordinates, checkAfil);
    }

    @Transactional(readOnly = true)
    public Aerodrome getAeroDrome(String aeroDromeIdentifier){
        Aerodrome aerodrome = null;

        if(StringUtils.isNotBlank(aeroDromeIdentifier)){
            aerodrome = aerodromeService.findAeroDromeByAeroDromeName(aeroDromeIdentifier);
        }

        return aerodrome;
    }

    /**
     * Determine if supplied departure and destination aerodromes are a circular route.
     *
     * @param depAd departure aerodrome
     * @param item18Dep item 18 departure value
     * @param destAd destination aerodrome
     * @param item18Dest item 18 destination value
     * @return Boolean
     * @throws FlightMovementBuilderException flight movement builder exception
     */
    @Transactional(readOnly = true)
    public Boolean isCircularRoute(final String depAd, final String item18Dep, final String destAd, final String item18Dest) throws FlightMovementBuilderException {
        Boolean returnValue = Boolean.FALSE;

        if(StringUtils.isNotBlank(depAd) && StringUtils.isNotBlank(destAd)){
            String depAdChecked = resolveAerodrome(depAd, item18Dep, true);
            String destAdChecked = resolveAerodrome(destAd, item18Dest, false);

            if(StringUtils.isNotBlank(depAdChecked) && StringUtils.isNotBlank(destAdChecked) && depAdChecked.equalsIgnoreCase(destAdChecked)){
                returnValue = Boolean.TRUE;
            }
        }

        return returnValue;
    }

    /**
     * Will resolve aerodrome based on {@code item18} value if {@code aerodromeIdentifier} cannot be resolved.
     *
     * Note, this method will return coordinates from ABMSDB Aerodromes if it does NOT exist in NAVDB. If you require
     * that the aerodrome name is returned instead, see {@link #resolveAerodrome(String, String, boolean, boolean)}.
     *
     * @param aerodromeIdentifier aerodrome name
     * @param item18 aerodrome item 18
     * @param checkAfil check AFIL for departure aerodrome
     * @return resolved aerodrome identifier or coordinate
     * @throws FlightMovementBuilderException resolve aerodrome issue
     */
    @Transactional(readOnly = true)
    public String resolveAerodrome(final String aerodromeIdentifier, final String item18, final boolean checkAfil) throws FlightMovementBuilderException {
        return this.resolveAerodrome(aerodromeIdentifier, item18, true, checkAfil);
    }

    @Transactional(readOnly = true)
    public String resolveAerodrome(final String aerodromeIdentifier, final boolean checkAfil) {
        return aerodromeService.checkAerodromeIdentifier(aerodromeIdentifier, false, checkAfil);
    }

    /**
     * Will resolve aerodrome based on {@code item18} value if {@code aerodromeIdentifier} cannot be resolved.
     *
     * @param aerodromeIdentifier aerodrome name
     * @param item18 aerodrome item 18
     * @param resolveToCoordinates resolve to coordinates if not in NAVDB
     * @param checkAfil check AFIL for departure aerodrome
     * @return resolved aerodrome identifier or coordinate
     * @throws FlightMovementBuilderException resolve aerodrome issue
     */
    @SuppressWarnings("WeakerAccess")
    @Transactional(readOnly = true)
    public String resolveAerodrome(final String aerodromeIdentifier,
                                   final String item18,
                                   final boolean resolveToCoordinates,
                                   final boolean checkAfil) throws FlightMovementBuilderException {
        String aerodrome;

        if (StringUtils.isBlank(aerodromeIdentifier)) {
            LOG.debug("Aerodrome is null");
            return null;
        }

        aerodrome = checkAerodromeIdentifier(aerodromeIdentifier, resolveToCoordinates, checkAfil);

        if ((aerodrome == null || ApplicationConstants.PLACEHOLDER_ZZZZ.equals(aerodrome)
            || (ApplicationConstants.PLACEHOLDER_AFIL.equals(aerodrome) && checkAfil)) && StringUtils.isNotBlank(item18)) {

            String aerodromeIdentifierItem18 = Item18Parser.parseDesignator(item18);
            if (StringUtils.isNotBlank(aerodromeIdentifierItem18)) {
                //Check if a valid aerodrome identifier
                final String aerodromeDesignator = checkAerodromeIdentifier(aerodromeIdentifierItem18, resolveToCoordinates, checkAfil);

                if (aerodromeDesignator != null) {
                    if (ApplicationConstants.PLACEHOLDER_ZZZZ.equals(aerodromeDesignator)
                        || (ApplicationConstants.PLACEHOLDER_AFIL.equals(aerodromeDesignator) && checkAfil)) {

                        LOG.error("{} aerodrome identifier: {} and item18: {} ",
                            FlightMovementBuilderIssue.UNKNOWN_AERODROME_ITEM18, aerodromeIdentifier, item18);
                        throw new FlightMovementBuilderException(FlightMovementBuilderIssue.UNKNOWN_AERODROME_ITEM18);
                    } else {
                        aerodrome = aerodromeDesignator;
                    }
                } else {
                    //Check if it is present in unspecified location table
                    final UnspecifiedDepartureDestinationLocation unspecifiedLocation = unspecifiedDepartureDestinationLocationService.findTextIdentifier(aerodromeIdentifierItem18);
                    if (unspecifiedLocation != null) {
                        if (unspecifiedLocation.getAerodromeIdentifier() != null) {
                            // resolve unspecified by Aerodrome Identifier
                            aerodrome = unspecifiedLocation.getAerodromeIdentifier().getAerodromeName();
                        } else if (resolveToCoordinates) {
                            // resolve unspecified by Aerodrome Coordinates
                            aerodrome = CoordinatesConversionUtility.convertLatitudeLongitudeFromDecimalToDMS(unspecifiedLocation.getLatitude(), unspecifiedLocation.getLongitude());
                        } else {
                            aerodrome = aerodromeIdentifierItem18;
                        }
                    }
                    else {
                        // Create Unspecified by parse Item18
                        final String aerodromeLocation = createLocationMethodForUnspecifiedDepartureDestinationLocation(item18);

                        if(org.apache.commons.lang.StringUtils.isNotBlank(aerodromeLocation)) {
                            aerodrome = aerodromeLocation;
                        }
                    }
                }
            } else {
                aerodromeIdentifierItem18 = Item18Parser.parseCoordinates(item18);
                if(org.apache.commons.lang.StringUtils.isNotBlank(aerodromeIdentifierItem18)){
                   UnspecifiedDepartureDestinationLocation existingAerodrome = unspecifiedDepartureDestinationLocationService.findByTextIdentifierOrAerodromeIdentifier(aerodromeIdentifierItem18);

                   if (existingAerodrome != null) {
                       aerodrome = aerodromeIdentifierItem18;
                   } else {
                       // Create Unspecified by parse Item18
                       aerodrome = createLocationMethodForUnspecifiedDepartureDestinationLocation(aerodromeIdentifierItem18);
                   }
                } else {
                    LOG.error("{} aerodrome identifier: {} and item18: {} ",
                        FlightMovementBuilderIssue.UNKNOWN_AERODROME, aerodromeIdentifier, item18);
                    final String[] message = {
                        FlightMovementBuilderIssue.UNKNOWN_AERODROME.toValue(), aerodromeIdentifier, item18
                    };
                    throw new FlightMovementBuilderException(FlightMovementBuilderIssue.UNKNOWN_AERODROME,
                        org.apache.commons.lang.StringUtils.join(message, ' '));
                }
            }
        } else {
            // Check if the aerodrome can be found in the unspecified departure/destination table
            if (aerodrome == null) {
                UnspecifiedDepartureDestinationLocation existingAerodrome = unspecifiedDepartureDestinationLocationService.findByTextIdentifierOrAerodromeIdentifier(aerodromeIdentifier);

               if (existingAerodrome != null)  {
                   aerodrome = aerodromeIdentifier;
                }
            }
        }
        if (resolveToCoordinates) {
            aerodrome = resolveAnyLocationToDMS(aerodrome, aerodrome);
        }
        return aerodrome;
    }

    /**
     * Will resolve aerodrome based on {@code aerodromeIdentifierDelta}.
     *
     * Note, this method will return coordinates from ABMSDB Aerodromes if it does NOT exist in NAVDB. If you require
     * that the aerodrome name is returned instead, see {@link #resolveAerodromeFromDelta(String, boolean)}.
     *
     * @param aerodromeIdentifierDelta aerodrome from delta
     * @return resolved aerodrome identifier or coordinate
     * @throws FlightMovementBuilderException resolve aerodrome issue
     */
    @Transactional(readOnly = true)
    public String resolveAerodromeFromDelta(final String aerodromeIdentifierDelta) throws FlightMovementBuilderException {
        return this.resolveAerodromeFromDelta(aerodromeIdentifierDelta, true);
    }

    /**
     * Will resolve aerodrome based on {@code aerodromeIdentifierDelta}.
     *
     * @param aerodromeIdentifierDelta aerodrome from delta
     * @param resolveToCoordinates resolve to coordinates if not in NAVDB
     * @return resolved aerodrome identifier or coordinate
     * @throws FlightMovementBuilderException resolve aerodrome issue
     */
    @Transactional(readOnly = true)
    public String resolveAerodromeFromDelta(final String aerodromeIdentifierDelta, final boolean resolveToCoordinates) throws FlightMovementBuilderException {
        String aerodrome = null;
        if (StringUtils.isBlank(aerodromeIdentifierDelta)) {
            return null;
        }

        // check if a valid aerodrome identifier
        final String aerodromeDesignator = checkAerodromeIdentifier(aerodromeIdentifierDelta, resolveToCoordinates);

        // if not null and not ZZZZ set as aerodrome
        if (aerodromeDesignator != null) {
            if (ApplicationConstants.PLACEHOLDER_ZZZZ.equals(aerodromeDesignator)) {
                LOG.error("{} aerodrome identifier: {}",
                    FlightMovementBuilderIssue.UNKNOWN_AERODROME_ITEM18, aerodromeIdentifierDelta);
                throw new FlightMovementBuilderException(FlightMovementBuilderIssue.UNKNOWN_AERODROME_ITEM18);
            } else {
                aerodrome = aerodromeDesignator;
            }
        } else {
            // check if it is present in unspecified location table
            final UnspecifiedDepartureDestinationLocation unspecifiedLocation = unspecifiedDepartureDestinationLocationService.findTextIdentifier(aerodromeIdentifierDelta);
            if (unspecifiedLocation != null) {
                if (unspecifiedLocation.getAerodromeIdentifier() != null) {
                    // resolve unspecified by Aerodrome Identifier
                    aerodrome = unspecifiedLocation.getAerodromeIdentifier().getAerodromeName();
                } else {
                    // resolve unspecified by Aerdrome Coordinates
                    aerodrome = CoordinatesConversionUtility.convertLatitudeLongitudeFromDecimalToDMS(unspecifiedLocation.getLatitude(), unspecifiedLocation.getLongitude());
                }
            }
        }

        return aerodrome;
    }

    @Transactional(readOnly = true)
    public Boolean isCircularRouteDelta(String depAd, String item18Dep, String item18Dest) throws FlightMovementBuilderException{
    	Boolean result = Boolean.FALSE;
    	if(depAd == null || depAd.isEmpty() || item18Dest == null || item18Dest.isEmpty()){
    		return result;
    	}

    	String depAdChecked = resolveAerodrome(depAd, item18Dep, true);

    	// find destination
    	String destAdChecked = null;
        List<DeltaFlightVO> deltaDestList = Item18Parser.destFieldToMap(item18Dest);
		if(deltaDestList != null && !deltaDestList.isEmpty() &&
			deltaDestList.get(deltaDestList.size()-1) != null){
				destAdChecked = deltaDestList.get(deltaDestList.size()-1).getIdent();
		}

        if(StringUtils.isNotBlank(depAdChecked) && StringUtils.isNotBlank(destAdChecked)
        		&& depAdChecked.equalsIgnoreCase(destAdChecked)){
            result = Boolean.TRUE;
        }

    	return result;
    }

    /**
     * Find unspecified location by text identifier or name.
     *
     * @param location flight movement aerodrome
     * @return aerodrome identifier
     */
    @Transactional(readOnly = true)
    public String resolveAnyLocation(String location, final boolean resolveToCoordinates){
    	String result;

    	if(!StringUtils.isNotBlank(location)){
    		return null;
    	}

    	// check if it is the aerodrome from navdb or billing DB
    	result = checkAerodromeIdentifier(location, resolveToCoordinates);

    	if(result != null){
    		return result;
    	}

    	// check unspecified locations
        UnspecifiedDepartureDestinationLocation unspecifiedLocation = unspecifiedDepartureDestinationLocationService.findByTextIdentifierOrAerodromeIdentifier(location);
        if (unspecifiedLocation != null) {
            if (unspecifiedLocation.getAerodromeIdentifier() != null) {
                // resolve unspecified by Aerodrome Identifier
                result = unspecifiedLocation.getAerodromeIdentifier().getAerodromeName();
            } else {
                // resolve unspecified by Aerdrome Coordinates
                result = CoordinatesConversionUtility.convertLatitudeLongitudeFromDecimalToDMS(unspecifiedLocation.getLatitude(), unspecifiedLocation.getLongitude());
            }
        }
        return result;
    }

    /**
     * Convert a token to a degrees/minutes/seconds string.
     * <p>
     * The token can be:
     * <ul>
     *   <li>a DMS string, such as "1122N03344E", in which case it will be returned as is
     *   <li>an aerodrome name that matches the aerordomes table in ABMS DB
     *   <li>an aerodrome name/code that matches the unspecified locations table in ABMS DB
     *   <li>an aerodrome code that matches some record in NAVDB
     * </ul>
     * In all other cases, returns null
     */
    @Transactional(readOnly = true)
    public String resolveAnyLocationToDMS (final String location) {
        return resolveAnyLocationToDMS (location, null);
    }

    /**
     * Convert a token to a degrees/minutes/seconds string.
     * <p>
     * The token can be:
     * <ul>
     *   <li>a DMS string, such as "1122N03344E", in which case it will be returned as is
     *   <li>an aerodrome name that matches the aerordomes table in ABMS DB
     *   <li>an aerodrome name/code that matches the unespecified locations table in ABMS DB
     *   <li>an aerodrome code that matches some record in NAVDB
     * </ul>
     * In all other cases, returns the specified default value
     */
    @Transactional(readOnly = true)
    public String resolveAnyLocationToDMS (final String location, final String dflt) {
        if (location != null) {
            final String loc = location.trim();
            if (!loc.isEmpty()) {

                // If it's a coordinate, just return it
                Coordinate coord = GeometryUtils.parseAviationCoordinate (loc);
                if (coord != null) {
                    return GeometryUtils.formatAviationCoordinate (coord);
                }

                // If it's an airport from ABMS database, use its coordinates
                final Aerodrome aerodrome = this.aerodromeService.findAeroDromeByAeroDromeName (loc);
                if (aerodrome != null && aerodrome.getGeometry() != null) {
                    final Point centerPoint = aerodrome.getGeometry() instanceof Point ? (Point)aerodrome.getGeometry() : aerodrome.getGeometry().getCentroid();
                    coord = centerPoint.getCoordinate();
                    if (coord != null) {
                        return GeometryUtils.formatAviationCoordinate (coord);
                    }
                }

                // Look for it in unspecified locations, use it
                final UnspecifiedDepartureDestinationLocation unspecLoc = this.unspecifiedDepartureDestinationLocationService.findByTextIdentifierOrAerodromeIdentifier (loc);
                if (unspecLoc != null && unspecLoc.getLatitude() != null && unspecLoc.getLongitude() != null) {
                    return GeometryUtils.formatAviationCoordinate (unspecLoc.getLatitude(), unspecLoc.getLongitude());
                }

                // Otherwise, look for it in NAVDB
                final CoordinatesVO coordinatesVO = navdbUtils.getCoordinatesFromAirportNAVDB (loc);
                if (coordinatesVO != null && coordinatesVO.getLatitude() != null && coordinatesVO.getLongitude() != null) {
                    return GeometryUtils.formatAviationCoordinate (coordinatesVO.getLatitude(), coordinatesVO.getLongitude());
                }
            }
        }
        LOG.debug("No coordinates for {} aerodrome found", location);
        return dflt;
    }

    @Transactional(readOnly = true)
    public String resolveLocation(final String location) {
        String resolved = null;
        if (org.apache.commons.lang.StringUtils.isEmpty(location)) {
            LOG.debug("No coordinates for {} aerodrome found", location);
            return null;
        }

        final String loc = location.trim();
        if (!loc.isEmpty()) {
            resolved = location;
            // If it's a coordinate, just return it
            Coordinate coord = GeometryUtils.parseAviationCoordinate (loc);
            if (coord == null) {
                final Aerodrome aerodrome = this.aerodromeService.findAeroDromeByAeroDromeName (loc);
                if (aerodrome == null) {
                 // Look for it in unspecified locations, use it
                    final UnspecifiedDepartureDestinationLocation unspecLoc = this.unspecifiedDepartureDestinationLocationService.findByTextIdentifierOrAerodromeIdentifier (loc);
                    if (unspecLoc != null && unspecLoc.getLatitude() != null && unspecLoc.getLongitude() != null) {
                        resolved = GeometryUtils.formatAviationCoordinate (unspecLoc.getLatitude(), unspecLoc.getLongitude());
                    }
                }
            }
        }
        return resolved;
    }

    public Boolean isAerodromeDomestic(String identifier) {
        return isAerodromeDomestic(identifier, identifier, false);
    }

    public Boolean isAerodromeDomestic(final String identifier, final String adFromFlight) {
        return isAerodromeDomestic(identifier, adFromFlight, false);
    }
    
    /**
     * Aerodrome is considered DOMESTIC if it geographically belongs to the list of country airspace
     * OR aerodrome prefix matches country code prefixes.
     *
     * @param identifier aerodrome identifier to validate
     * @param adFromFlight original aerodrome identifier from the flight
     * @return Boolean return true if identifier domestic
     */
    @Transactional(readOnly = true)
    public Boolean isAerodromeDomestic(final String identifier, final String adFromFlight, final boolean checkAfil) {
        if (StringUtils.isBlank(identifier)) return false;

        String determinedBy = systemConfigurationService.getCurrentValue(SystemConfigurationItemName
            .AERODROME_NATIONALITY_DETERMINED_BY);

        // determined by setting MUST be configured at all times, do not continue if not found
        if (StringUtils.isBlank(determinedBy)) {
            LOG.error("System configuration setting 'Aerodrome nationality determined by' cannot be found");
            throw new IllegalStateException("System configuration setting 'Aerodrome nationality determined by' cannot be found");
        }

        // determined domestic aerodrome identifier by system configuration setting
        boolean isDomestic;
        switch (determinedBy) {
            case DETERMINED_BY_PREFIX:
                if (adFromFlight.equals(ApplicationConstants.PLACEHOLDER_ZZZZ) || (checkAfil && adFromFlight.equals(ApplicationConstants.PLACEHOLDER_AFIL))) {
                    isDomestic = true;
                } else {
                    isDomestic = aerodromeService.isDomesticAerodrome(identifier);
                }
                break;
            case DETERMINED_BY_EITHER:
                if (adFromFlight.equals(ApplicationConstants.PLACEHOLDER_ZZZZ) || (checkAfil && adFromFlight.equals(ApplicationConstants.PLACEHOLDER_AFIL))) {
                    isDomestic = true;
                } else {
                    isDomestic = aerodromeService.isDomesticAerodrome(identifier) || isIdentifierWithinFir(identifier);
                }
                break;
            case DETERMINED_BY_LOCATION:
            default:
                isDomestic = isIdentifierWithinFir(identifier);
        }

        return isDomestic;
    }

    private Boolean isIdentifierWithinFir(final String identifier) {

        // resolve identifier to coordinate and return true if no coordinates found
        // identifiers without coordinates end up in the unspecified departure and destination location
        // and are automatically assumed to be domestic identifiers from what I can tell refactoring the code for US 114729
        String coordinate = resolveAnyLocationToDMS(identifier);
        if (StringUtils.isBlank(coordinate)) return true;

        Coordinate coord = GeometryUtils.parseAviationCoordinate(coordinate);
        if (coord == null) return false;

        // get list of airspaces
        WKTReader wktr = new WKTReader();
        GeometryFactory geometry = new GeometryFactory();
        Point point = geometry.createPoint(coord);

        List<String> airspaceGeometry = this.airspaceService.getAllAirspaceGeometry();

        if (airspaceGeometry != null && !airspaceGeometry.isEmpty()) {

            for (String geo : airspaceGeometry) {
                try {
                    Geometry airspace = wktr.read(geo);
                    if (airspace != null && airspace.contains(point)) {
                        return true;
                    }
                } catch(Exception e) {
                    throw new CustomParametrizedException(String.format("Wrong WKT string: %s", geo),e);
                }
            }
        }

        return false;
    }

    private String createLocationMethodForUnspecifiedDepartureDestinationLocation(String item18) throws FlightMovementBuilderException {

    	String aerodrome = null;

    	if(StringUtils.isBlank(item18)) {
    		return aerodrome;
    	}

        // parse coordinates
        String aerodromeCoordinates = Item18Parser.parseCoordinates(item18);

        String aerodromeDesignator = Item18Parser.parseDesignator(item18);
        
        // 2019-09-18
        // Don't create unspecified location for Delta flights
        List<DeltaFlightVO> deltaStops = Item18Parser.destFieldToMap(aerodromeDesignator);
        if(deltaStops != null && !deltaStops.isEmpty()) {
            return aerodrome;
        }
        aerodrome = aerodromeDesignator;

        // create unspecified location
        UnspecifiedDepartureDestinationLocation unspecifiedDepartureDestinationLocation=null;
        if (StringUtils.isNotBlank(aerodromeDesignator)) {
            unspecifiedDepartureDestinationLocation = new UnspecifiedDepartureDestinationLocation();
            unspecifiedDepartureDestinationLocation.setTextIdentifier(aerodromeDesignator);
            unspecifiedDepartureDestinationLocation.setName(aerodromeDesignator);
            unspecifiedDepartureDestinationLocation.setLatitude(CoordinatesConversionUtility.convertLatitudeFromDMSToDecimal(aerodromeCoordinates));
            unspecifiedDepartureDestinationLocation.setLongitude(CoordinatesConversionUtility.convertLongitudeFromDMSToDecimal(aerodromeCoordinates));
            unspecifiedDepartureDestinationLocation.setMaintained(Boolean.FALSE);
            unspecifiedDepartureDestinationLocation.setStatus(UnspecifiedDepartureDestinationLocationStatus.SYS_GENERATED);
            unspecifiedDepartureDestinationLocationService.create(unspecifiedDepartureDestinationLocation, aerodromeDesignator);
        } else {
            LOG.warn("I can't create unspecified departure destination location for missing information !!!");
            throw  new FlightMovementBuilderException(FlightMovementBuilderIssue.MISSING_INFORMATION_FOR_CREATE_UNSPECIFIED_LOCATION);
        }

        return aerodrome;
    }

    public void createUnspecifiedDepDestByAerodrome(String aerodromeDesignator) throws FlightMovementBuilderException {

        // create unspecified location
        UnspecifiedDepartureDestinationLocation unspecifiedDepartureDestinationLocation=null;
        if (StringUtils.isNotBlank(aerodromeDesignator)) {
            if (!unspecifiedDepartureDestinationLocationService.aerodromeExists(aerodromeDesignator)) {
                unspecifiedDepartureDestinationLocation = new UnspecifiedDepartureDestinationLocation();
                unspecifiedDepartureDestinationLocation.setTextIdentifier(aerodromeDesignator);
                unspecifiedDepartureDestinationLocation.setName(aerodromeDesignator);
                unspecifiedDepartureDestinationLocation.setMaintained(Boolean.FALSE);
                unspecifiedDepartureDestinationLocation.setStatus(UnspecifiedDepartureDestinationLocationStatus.SYS_GENERATED);
                unspecifiedDepartureDestinationLocationService.create(unspecifiedDepartureDestinationLocation, aerodromeDesignator);
            }
        } else {
            LOG.warn("I can't create unspecified departure destination location for missing information !!!");
            throw  new FlightMovementBuilderException(FlightMovementBuilderIssue.MISSING_INFORMATION_FOR_CREATE_UNSPECIFIED_LOCATION);
        }
    }

    /**
     * Check and add aerodrome for unspecified locations.
     *
     * @param aerodromeIdentifier flight movement aerodrome
     * @throws FlightMovementBuilderException flight movement builder exception
     */
    public void checkAddAerodromeForUnspecified(final String aerodromeIdentifier) throws FlightMovementBuilderException {
        String aerodrome = aerodromeService.checkAerodromeIdentifier(aerodromeIdentifier);
        if (aerodrome == null) {
            if (!StringUtils.isNotBlank(aerodromeIdentifier) || (StringUtils.isNotBlank(aerodromeIdentifier)
                    && aerodromeIdentifier.equalsIgnoreCase(ApplicationConstants.PLACEHOLDER_ZZZZ))) {
                LOG.error("{} aerodrome identifier: {} ",
                    FlightMovementBuilderIssue.UNKNOWN_AERODROME, aerodromeIdentifier);
                final String[] message = {FlightMovementBuilderIssue.UNKNOWN_AERODROME.toValue(), aerodromeIdentifier};
                throw new FlightMovementBuilderException(FlightMovementBuilderIssue.UNKNOWN_AERODROME,
                    org.apache.commons.lang.StringUtils.join(message, ' '));
            } else{
                createUnspecifiedDepDestByAerodrome(aerodromeIdentifier);
            }
        }
    }

    /**
     * Find aerodrome category for the location.
     *
     * @param location flight movement aerodrome
     * @param item18 item 18 field
     * @param locationType - DEP,DEST,DELTA_DEST
     * @return aerodrome category
     */
    @Transactional(readOnly = true)
    public AerodromeCategory resolveLocationToAdCategory(String location, String item18, AdResolveType locationType){

    	if (location == null)
    		return null;

    	// check if location is an aerodrome from Aerodrome table
    	Aerodrome ad = aerodromeService.findAeroDromeByAeroDromeName(location);
    	if (ad != null) {
    		return ad.getAerodromeCategory();
    	}

    	// check if location is ZZZZ or AFIL for departure aerodrome
        if (!location.equalsIgnoreCase(ApplicationConstants.PLACEHOLDER_ZZZZ)
            && !(location.equalsIgnoreCase(ApplicationConstants.PLACEHOLDER_AFIL) && locationType.equals(AdResolveType.AD_TYPE_DEPARTURE))
            && !locationType.equals(AdResolveType.AD_TYPE_DELTA_DESTINATION)) {
            return null;
        }

        String adName;
        // get identifier from item18
        if (locationType.equals(AdResolveType.AD_TYPE_DESTINATION) || locationType.equals(AdResolveType.AD_TYPE_DEPARTURE)) {
            adName = Item18Parser.getFirstAerodrome(item18);
        } else {
            adName = location;
        }

        if (adName == null) {
            return null;
        }

        // check in aerodrome table
        ad = aerodromeService.findAeroDromeByAeroDromeName(adName);
        if (ad != null) {
            return ad.getAerodromeCategory();
        }

        // the name is not in aerodrome table, but maybe we can find ICAO aerodrome identifier in the unspecified locations table
        UnspecifiedDepartureDestinationLocation unspecifiedLocation = unspecifiedDepartureDestinationLocationService.findByTextIdentifierOrAerodromeIdentifier(adName);
        if (unspecifiedLocation != null && unspecifiedLocation.getAerodromeIdentifier() != null ) {
           return unspecifiedLocation.getAerodromeIdentifier().getAerodromeCategory();
        }

    	return null;
    }

    @Transactional(readOnly = true)
    public boolean isIcaoAerodrome (final String aerodromeIdentifier) {
        return aerodromeService.isIcaoAerodrome(aerodromeIdentifier);
    }

    @Transactional(readOnly = true)
    public boolean isAerodromeManned(String aerodromeIdentifier) {
        return aerodromeService.isMannedAerodrome(aerodromeIdentifier);
    }

    @Transactional(readOnly = true)
    public List<String> getFirsByLocation(String coordinate){
    	Coordinate coord = GeometryUtils.parseAviationCoordinate (coordinate);
        GeometryFactory gf = new GeometryFactory();
        Point p = gf.createPoint(coord);
        String s = p.toText();
    	return navdbUtils.getFirsByLocation(s);
    }

    @Transactional(readOnly = true)
    public Boolean isAdInsideSouthSudan(String adCode) {
       	return navdbUtils.isAdInsideSouthSudan(adCode);
    }

    @Transactional(readOnly = true)
    public Aerodrome findAeroDromeByAeroDromeName (String loc) {
    	return aerodromeService.findAeroDromeByAeroDromeName(loc);
    }

    @Transactional(readOnly = true)
    public UnspecifiedDepartureDestinationLocation findUnspecifiedDepartureDestinationLocation(String loc) {
    	return unspecifiedDepartureDestinationLocationService.findByTextIdentifierOrAerodromeIdentifier (loc);
    }
    
    public boolean isAdDomesticButOutsideAirspace(String adCode) {
        boolean result = false;
        if(StringUtils.isNotBlank(adCode) &&
           ( isAerodromeDomestic(adCode) && !isIdentifierWithinFir(adCode)))
               result = true;
        return result;
    }
}
