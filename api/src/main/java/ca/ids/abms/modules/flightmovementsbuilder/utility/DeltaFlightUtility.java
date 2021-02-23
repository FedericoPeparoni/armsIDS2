package ca.ids.abms.modules.flightmovementsbuilder.utility;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementType;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementValidator;
import ca.ids.abms.modules.system.BillingOrgCode;
import ca.ids.abms.modules.system.SystemConfigurationService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.operation.linemerge.LineMerger;

import ca.ids.abms.modules.airspaces.AirspaceService;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.FlightMovementAerodromeService;
import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementSource;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementBuilderException;
import ca.ids.abms.modules.flightmovementsbuilder.utility.cache.vo.RouteCacheVO;
import ca.ids.abms.modules.flightmovementsbuilder.utility.cache.vo.RouteSegmentVO;
import ca.ids.abms.modules.flightmovementsbuilder.vo.DeltaFlightVO;
import ca.ids.abms.modules.flightmovementsbuilder.vo.RPAirspaceVO;
import ca.ids.abms.modules.routesegments.RouteSegment;
import ca.ids.abms.modules.routesegments.SegmentType;
import ca.ids.abms.modules.routesegments.SegmentTypeMap;
import ca.ids.abms.modules.unspecified.UnspecifiedDepartureDestinationLocation;
import ca.ids.abms.modules.unspecified.UnspecifiedDepartureDestinationLocationService;
import ca.ids.abms.modules.util.models.NavDBUtils;
import ca.ids.abms.util.GeometryUtils;
import ca.ids.abms.util.StringUtils;

@Component
public class DeltaFlightUtility {

	private static final Logger LOG = Logger.getLogger(DeltaFlightUtility.class);

    public static final String AERODROME = "AERODROME";
    public static final String ARRIVEAT = "ARRIVEAT";
    public static final String DEPARTUREAT = "DEPARTUREAT";

	private final UnspecifiedDepartureDestinationLocationService unspecifiedDepartureDestinationLocationService;
	private final AirspaceService airspaceService;
	private final NavDBUtils navDBUtils;
    private final RouteParserWrapper routeParserWrapper;
	private final FlightMovementAerodromeService flightMovementAerodromeService;
	private final SystemConfigurationService systemConfigurationService;
	private final FlightMovementValidator flightMovementValidator;

    public DeltaFlightUtility(
        final UnspecifiedDepartureDestinationLocationService unspecifiedDepartureDestinationLocationService,
        final AirspaceService airspaceService,
        final NavDBUtils navDBUtils,
        final RouteParserWrapper routeParserWrapper,
        final FlightMovementAerodromeService flightMovementAerodromeService,
        final SystemConfigurationService systemConfigurationService,
        final FlightMovementValidator flightMovementValidator
    ) {
		this.unspecifiedDepartureDestinationLocationService = unspecifiedDepartureDestinationLocationService;
		this.airspaceService = airspaceService;
		this.navDBUtils = navDBUtils;
		this.routeParserWrapper = routeParserWrapper;
		this.flightMovementAerodromeService =  flightMovementAerodromeService;
		this.systemConfigurationService = systemConfigurationService;
		this.flightMovementValidator = flightMovementValidator;
	}


    /**
     * A flight is a DeltaFlight if DOMESTIC CAAB flight movement and either item18 DEP/ or DEST/ field value matches
     * the DESIGNATOR_TIME_PATTERN
     */
    public Boolean isDeltaFlight(FlightMovement flightMovement) {
        return flightMovement != null
            && FlightMovementType.DOMESTIC.equals(getMovementType(flightMovement))
            && (isDeltaStop(flightMovement.getItem18Dep()) || isDeltaStop(flightMovement.getItem18Dest()))
            && systemConfigurationService.getBillingOrgCode() == BillingOrgCode.CAAB;
    }

	/**
	 * If the arrival time for the one of the consecutive stops is less
	 * than the departure time of the previous stop or the actual departure time of the flight,
	 * or if arrival time is bigger then departure time for the stop,
	 * it means overnight stop.
	 *
	 * It is assumed that the length of the flight never exceeds 24 hours.
	 *
	 * @return First segment after overnight stop
	 */
	public RouteSegment getOvernightFirstSegment(FlightMovement flight){

		if(flight == null || flight.getItem18Dest() == null || flight.getItem18Dest().isEmpty()) {
            return null;
        }

		// get segments from the flight
		// find all stops from item 19 /dest
		if(Item18Parser.destFieldToMap(flight.getItem18Dest()) == null) {
            return null;
        }

		LinkedList<DeltaFlightVO> deltaDestList = new LinkedList<>(Item18Parser.destFieldToMap(flight.getItem18Dest()));

		if(deltaDestList == null || deltaDestList.isEmpty()){
			return null;
		}

		LinkedList<RouteSegment> routeSegmentList = new LinkedList<>(flight.getRouteSegments());
		if(routeSegmentList ==null  || routeSegmentList.isEmpty()){
		    return null;
		}

        for (DeltaFlightVO stop : deltaDestList) {
            Integer positionInList = deltaDestList.indexOf(stop);

            Integer arrivalAtInteger = stop.getArrivaAt() != null && !stop.getArrivaAt().isEmpty()
                ? Integer.parseInt(stop.getArrivaAt())
                : null;

            Integer departAtInteger = stop.getDepartAt() != null && !stop.getDepartAt().isEmpty()
                ? Integer.parseInt(stop.getDepartAt())
                : null;

		    //check the arrival and departure time for the same stop
            if(arrivalAtInteger != null && departAtInteger != null && (arrivalAtInteger >= departAtInteger)){

                return getRouteSegment(routeSegmentList, positionInList + 1, flight.getSource());
            }

            // If it is not the last segment in list
            if (positionInList != deltaDestList.size() - 1) {

                DeltaFlightVO nextFlight = deltaDestList.get(positionInList + 1);

                Integer nextFlightArrivalAtInteger = nextFlight.getArrivaAt() != null && !nextFlight.getArrivaAt().isEmpty()
                    ? Integer.parseInt(nextFlight.getArrivaAt())
                    : null;


                // check the arrival and departure time for the consecutive stops
                if (departAtInteger != null && nextFlightArrivalAtInteger != null &&
                    nextFlightArrivalAtInteger <= departAtInteger){
                        // overnight flight
                        // get the first segment
                    return getRouteSegment(routeSegmentList, positionInList, flight.getSource());
                }
            }
        }

		return null;
	}


	private RouteSegment getRouteSegment(LinkedList<RouteSegment> aRouteSegmentList, int index,
            FlightMovementSource type) {
	    RouteSegment routeSegment = null;
        SegmentType segmemtType = SegmentTypeMap.mapFlightMovementSourceToSegmentType(type);
        List<RouteSegment> routeSegmentLst = aRouteSegmentList.stream().filter(rs -> rs.getSegmentType().equals(segmemtType)).collect(Collectors.toList());
        if (index < routeSegmentLst.size()) {
            routeSegment = routeSegmentLst.get(index);
        }
        return routeSegment;
    }


    public Boolean isOvernightSegment(FlightMovement flight, RouteSegment segment){
		if(flight == null || segment == null)
			return false;

		RouteSegment first = getOvernightFirstSegment(flight);
		if(first == null){
			// no overnight segments in the flight
			return false;
		}

		return (segment.getSegmentNumber()!= null && first.getSegmentNumber() != null &&
				segment.getSegmentNumber() >= first.getSegmentNumber());
	}

	public RouteSegment getBillableOvernightSegment(FlightMovement flight){
		RouteSegment res = null;
		if(flight == null || flight.getRouteSegments() == null || flight.getRouteSegments().isEmpty()){
			return res;
		}
		RouteSegment first = getOvernightFirstSegment(flight);
		if(first == null){
			// no overnight segments in the flight
			return res;
		}

		// check all overnight stops and return the first one
		for(int i= first.getSegmentNumber(); i<flight.getRouteSegments().size();i++){
		    if (flight.getRouteSegments().get(i) != null && flight.getRouteSegments().get(i).getSegmentType().equals(SegmentType.SCHED) && 
		               ((StringUtils.isNotBlank(flight.getRouteSegments().get(i).getSegmentEndLabel()) && isMaintainedAd(flight.getRouteSegments().get(i).getSegmentEndLabel())) ||
		                (StringUtils.isNotBlank(flight.getRouteSegments().get(i).getSegmentStartLabel()) && isMaintainedAd(flight.getRouteSegments().get(i).getSegmentStartLabel()))
		               )
		        )
		     {
		        return flight.getRouteSegments().get(i);
		     }
		}

		return res;
	}

	public RouteSegment getBillableSegment(FlightMovement flight){
        RouteSegment res = null;
        if(flight == null || flight.getRouteSegments() == null || flight.getRouteSegments().isEmpty()){
            return res;
        }

        // check all stops and return the first one
        for(RouteSegment seg:flight.getRouteSegments()){
            
           if (seg != null && seg.getSegmentType().equals(SegmentType.SCHED) && 
               ((StringUtils.isNotBlank(seg.getSegmentEndLabel()) && isMaintainedAd(seg.getSegmentEndLabel())) ||
                (StringUtils.isNotBlank(seg.getSegmentStartLabel()) && isMaintainedAd(seg.getSegmentStartLabel()))
               )
              )
           {
                return seg;

            }
        }
        return res;
    }
	
	/**
	 * Aerodrome is maintained if it is a real aerodrome with ICAO id from navdb
	 * or if it is in the list of unspecifyed locations, but has a flag maintained as true
	 * @param ident
	 * @return
	 */
	private Boolean isMaintainedAd(String ident){
		if(ident == null || ident.isEmpty())
			return false;

		//if aerodrome is in navdb - it is maintaned
		if(navDBUtils.checkIdentFromAirportNAVDB(ident)){
			return true;
		} else {
			UnspecifiedDepartureDestinationLocation unad =
					unspecifiedDepartureDestinationLocationService.findTextIdentifier(ident);
			if(unad != null && unad.getMaintained()){
				return true;
			}
		}

		return false;
	}



    /**
     * If the departure time for the one of the consecutive stops is less
     * than the departure time of the previous stop or the actual departure time of the flight,
     * it means overnight stop.
     * It is assumed that the length of the flight never exceeds 24 hours.
     *
     * @return A not-null RouteCacheVO
     */
    public RouteCacheVO getDeltaRouteSegmentList(final FlightMovement flight, final String depAd){
        final RouteCacheVO routeCacheVO = new RouteCacheVO();
		if(flight != null) {
            
            //resolve Departure/destination ad

            // try to get destination from item18, which is normal for the delta flights,
            // if no destination is obtained from item18 - use regular destination from FPL
            String destAd = this.getDeltaDestination(flight.getItem18Dest(), AERODROME);
            if (destAd == null) {
                destAd = flight.getDestAd();
            }

            LinkedList<RouteSegmentVO> routeSegments = null;

            // to biuld route segments build one by one from the available stop locations
            List<DeltaFlightVO> deltaDestList = Item18Parser.destFieldToMap(flight.getItem18Dest());

            LinkedList<RPAirspaceVO> rpAirspaceVOList = new LinkedList<>(airspaceService.getLinkedListAllAirspaceGeometry());
            RouteCacheVO segmentRouteCacheVO = null;
            
            try {
                String depDMS = flightMovementAerodromeService.resolveAnyLocationToDMS(depAd);
                String destDMS = null;
                Double crossingDist = 0.0;
                LineMerger merger = new LineMerger();

                if (deltaDestList != null && !deltaDestList.isEmpty()) {
                    routeSegments = new LinkedList<>();
                    String startAd = depAd;
                    for (DeltaFlightVO stop : deltaDestList) {
                        destDMS = flightMovementAerodromeService.resolveAnyLocationToDMS(stop.getIdent());                    

                        if (destDMS != null) {
                            segmentRouteCacheVO = routeParserWrapper.getRouteInformationByRouteParser(depDMS, destDMS, "DCT", flight.getCruisingSpeedOrMachNumber(), flight.getEstimatedElapsedTime(), SegmentType.SCHED, rpAirspaceVOList,null);

                            if (segmentRouteCacheVO != null && segmentRouteCacheVO.getRouteSegmentList() != null && !segmentRouteCacheVO.getRouteSegmentList().isEmpty()) {

                             // use aerodrome names instead of coords for delta flight map labels
                                for (RouteSegmentVO rs : segmentRouteCacheVO.getRouteSegmentList()) {
                                    rs.setSegmentStartLabel(startAd);
                                    rs.setSegmentEndLabel(stop.getIdent());
                                }
                                
                                routeSegments.addAll(segmentRouteCacheVO.getRouteSegmentList());
                                if (segmentRouteCacheVO.getDistance() != null) {
                                    crossingDist = crossingDist + segmentRouteCacheVO.getDistance();
                                }
                                if(segmentRouteCacheVO.getRoute() != null) {
                                    merger.add(segmentRouteCacheVO.getRoute());
                                 }
                            }
                            depDMS = destDMS;
                            startAd=stop.getIdent();
                        }
                    }

                    // Segments for the delta flight stops are returned one by one for each stop.
                    // We have to set the correct numbers in the order of the stops for future usage
                    int segmentNumber = 1;
                    for (RouteSegmentVO rs : routeSegments) {
                        if (rs != null) {
                            rs.setSegmentNumber(segmentNumber);
                            segmentNumber = segmentNumber + 1;
                        }
                    }
                }
                @SuppressWarnings("unchecked")
                // If the route has loops LineMerger will return multiple LineStrings which have to be converted to a MULTISTRING
                
                Collection<LineString>  geometries = merger.getMergedLineStrings();
                Geometry geom = GeometryUtils.lineStringToMultiLineString(geometries);
                
                routeCacheVO.setDistance(routeParserWrapper.roundTotalDistance(crossingDist));
                routeCacheVO.setRoute(geom);
                routeCacheVO.setRouteSegmentList(routeSegments);
                routeCacheVO.setDestAd(destAd);
                routeCacheVO.setValid(true);
            } catch (FlightMovementBuilderException e) {
                LOG.error(e);
            }
        }
	    return routeCacheVO;
	}

    /**
     * Route for the delta/charter flights consists of multiple stops which are listed in item 18 DEST/ field
     * Destination aerodrome field contains the first stop, it is not the real end point of flight.
     * Real destination aerodrome is the last stop from item18 DEST/
     * Example 1: departure ad - FBKE, destination ad - ZZZZ
     * item 18:  DEST/XAKANAKA0915/0930 FBKR0940/0955 FBKE1055
     * The real route: FBKE -> XAKANAKA ->FBKR ->FBKE
     *
     * Example 2: departure ad - FBKE, destination ad - FBSV
     * item 18:  DEST/FBSV0950/1000 SHINDI1030/1040 FBCO1045/1055 XAKANAKA1105/1115 FBMN1135
     * The real route: FBKE -> FBSV -> SHINDI -> FBCO -> XAKANAKA ->FBMN
     *
     * @param fm
     * @return
     */
    public String constructDeltaRoute(FlightMovement fm){
    	String route = "";
    	if(fm == null)
    	    return route;
    	
    	if(fm.getFplRoute() != null  && !fm.getFplRoute().isEmpty()){
    		route = fm.getFplRoute();
    	}

    	if(fm.getDeltaFlight() != null && !fm.getDeltaFlight()){
    		return route;
    	}

    	if(fm.getItem18Dest() != null && !fm.getItem18Dest().isEmpty()) {
    		List<DeltaFlightVO> deltaDestList = Item18Parser.destFieldToMap(fm.getItem18Dest());
    		if(deltaDestList != null && !deltaDestList.isEmpty())	{
    			// the last stop in item 18 is a real destination aerodrome
    			if(deltaDestList.size() < 2){
    				return route;
    			} else {
    				for(int i = 0; i<deltaDestList.size()-1;i++){

    					if(deltaDestList.get(i) != null && deltaDestList.get(i).getIdent() != null){

    						String location = this.flightMovementAerodromeService.resolveAnyLocation(deltaDestList.get(i).getIdent(), true);
    						String locDMS = flightMovementAerodromeService.resolveAnyLocationToDMS(location);
    			            if(StringUtils.isNotBlank(locDMS)){
    			            	route = route + " " + location;
    			            }
    					}
    				}
    			}
    		}
    	}
    	return route;
    }

    /**
     * The real destination aerodrome for the delta/charter flight is the last stop from Item18 DEST/
     *
     * @return
     */
    public String getDeltaDestination(String item18Dest, String field){
    	String deltaDest = null;
    	if(item18Dest == null || item18Dest.isEmpty())
    		return null;

    	List<DeltaFlightVO> deltaDestList = Item18Parser.destFieldToMap(item18Dest);
		if(deltaDestList != null && !deltaDestList.isEmpty() &&
			deltaDestList.get(deltaDestList.size()-1) != null){
			    if (field.equals(AERODROME)) {
			        deltaDest = deltaDestList.get(deltaDestList.size()-1).getIdent();
			    } else if (field.equals(ARRIVEAT)) {
			        deltaDest = deltaDestList.get(deltaDestList.size()-1).getArrivaAt();
			    } else if (field.equals(DEPARTUREAT)) {
			        deltaDest = deltaDestList.get(deltaDestList.size()-1).getDepartAt();
			    }
		}
		return deltaDest;
    }

    /**
     * Get flight movement type from field OR validate using FlightMovementValidator if necessary.
     */
    private FlightMovementType getMovementType(final FlightMovement flightMovement) {
        if (flightMovement != null && flightMovement.getMovementType() != null)
            return flightMovement.getMovementType();
        else
            return flightMovementValidator.validateFlightMovementType(flightMovement)
                .getMovementType();
    }

    /**
     * Check if provided item18 DEP/ or DEST/ value contains delta stops.
     */
    private Boolean isDeltaStop(final String item18Field) {
        if (item18Field == null || item18Field.isEmpty())
            return false;

        final List<DeltaFlightVO> deltaStops = Item18Parser.destFieldToMap(item18Field);
        return deltaStops != null && !deltaStops.isEmpty();
    }
}
