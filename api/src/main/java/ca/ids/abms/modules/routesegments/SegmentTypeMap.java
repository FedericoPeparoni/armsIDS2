package ca.ids.abms.modules.routesegments;

import ca.ids.abms.modules.common.enumerators.CrossingDistanceStrategy;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.enumerate.EnrouteChargesBasis;
import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementSource;
import com.vividsolutions.jts.geom.Geometry;

/**
 * Created by c.talpa on 25/02/2017.
 */
public class SegmentTypeMap {

    public static SegmentType mapCrossingDistanceStrategyToSegmentType(final CrossingDistanceStrategy crossingDistanceStrategy) {

        if (crossingDistanceStrategy == null)
            return null;

        SegmentType segmentType;
        switch (crossingDistanceStrategy) {
            case ATC_LOG:
                segmentType = SegmentType.ATC;
                break;
            case NOMINAL:
                segmentType = SegmentType.NOMINAL;
                break;
            case RADAR:
                segmentType = SegmentType.RADAR;
                break;
            case SCHEDULED:
                segmentType = SegmentType.SCHED;
                break;
            case TOWER_LOG:
                segmentType = SegmentType.TOWER;
                break;
            case USER:
                segmentType = SegmentType.USER;
                break;
            default:
                segmentType = null;
        }
        return segmentType;
    }

    public static SegmentType mapEnrouteCrossingBasisToSegmentType(final EnrouteChargesBasis enrouteChargesBasis) {

        if (enrouteChargesBasis == null)
            return null;

        SegmentType segmentType;
        switch (enrouteChargesBasis) {
            case ATC_LOG:
                segmentType = SegmentType.ATC;
                break;
            case MANUAL:
                segmentType = SegmentType.USER;
                break;
            case NOMINAL:
                segmentType = SegmentType.NOMINAL;
                break;
            case SCHEDULED:
                segmentType = SegmentType.SCHED;
                break;
            case RADAR_SUMMARY:
                segmentType = SegmentType.RADAR;
                break;
            case TOWER_LOG:
                segmentType = SegmentType.TOWER;
                break;
            default:
                segmentType = null;
        }
        return segmentType;
    }

    public static SegmentType mapFlightMovementSourceToSegmentType(final FlightMovementSource flightMovementSource){

        if (flightMovementSource == null)
            return null;

        SegmentType segmentType;
        switch (flightMovementSource){
            case ATC_LOG:
                segmentType = SegmentType.ATC;
                break;
            case MANUAL:
                segmentType = SegmentType.SCHED;
                break;
            case NETWORK:
                segmentType = SegmentType.SCHED;
                break;
            case RADAR_SUMMARY:
                segmentType = SegmentType.RADAR;
                break;
            case TOWER_LOG:
                segmentType = SegmentType.TOWER;
                break;
            default:
                segmentType = null;
        }
        return segmentType;
    }

    public static Geometry mapSegmentTypeToFlightMovementGeometry(final SegmentType segmentType, final FlightMovement flightMovement) {

        if (segmentType == null || flightMovement == null)
            return null;

        Geometry geometry;
        switch (segmentType) {
            case ATC:
                geometry = flightMovement.getAtcLogTrack();
                break;
            case SCHED:
                geometry = flightMovement.getFplRouteGeom();
                break;
            case RADAR:
                geometry = flightMovement.getRadarRoute();
                break;
            case TOWER:
                geometry = flightMovement.getTowerLogTrack();
                break;
            default:
                geometry = null;
        }
        return geometry;
    }

    private SegmentTypeMap() {
        throw new IllegalStateException("Utility class, do not instantiate a new instance.");
    }
}
