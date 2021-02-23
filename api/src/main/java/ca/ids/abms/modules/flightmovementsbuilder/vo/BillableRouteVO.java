package ca.ids.abms.modules.flightmovementsbuilder.vo;

import ca.ids.abms.modules.flightmovements.enumerate.FlightmovementCategoryType;
import ca.ids.abms.modules.routesegments.SegmentType;
import com.vividsolutions.jts.geom.Geometry;

/**
 * Created by c.talpa on 10/03/2017.
 */
public class BillableRouteVO {

    private FlightmovementCategoryType flightMovementType;

    private SegmentType segmentType;

    private Double calculateDistance;

    private Geometry billableRoute;

    private String billableEntryPoint;

    private String billableExitPoint;

    private Double billableCrossingDist;



    public BillableRouteVO(){}

    public BillableRouteVO(BillableRouteVO obj){
        this.flightMovementType=obj.getFlightMovementType();
        this.segmentType=obj.getSegmentType();
        this.calculateDistance=obj.getCalculateDistance();
        this.billableRoute=obj.getBillableRoute();
        this.billableEntryPoint=obj.getBillableEntryPoint();
        this.billableExitPoint=obj.getBillableExitPoint();
        this.billableCrossingDist=obj.getBillableCrossingDist();
    }

    public FlightmovementCategoryType getFlightMovementType() {
        return flightMovementType;
    }

    public void setFlightMovementType(FlightmovementCategoryType flightMovementType) {
        this.flightMovementType = flightMovementType;
    }

    public SegmentType getSegmentType() {
        return segmentType;
    }

    public void setSegmentType(SegmentType segmentType) {
        this.segmentType = segmentType;
    }

    public Double getCalculateDistance() {
        return calculateDistance;
    }

    public void setCalculateDistance(Double calculateDistance) {
        this.calculateDistance = calculateDistance;
    }

    public Geometry getBillableRoute() {
        return billableRoute;
    }

    public void setBillableRoute(Geometry billableRoute) {
        this.billableRoute = billableRoute;
    }

    public String getBillableEntryPoint() {
        return billableEntryPoint;
    }

    public void setBillableEntryPoint(String billableEntryPoint) {
        this.billableEntryPoint = billableEntryPoint;
    }

    public String getBillableExitPoint() {
        return billableExitPoint;
    }

    public void setBillableExitPoint(String billableExitPoint) {
        this.billableExitPoint = billableExitPoint;
    }

    public Double getBillableCrossingDist() {
        return billableCrossingDist;
    }

    public void setBillableCrossingDist(Double billableCrossingDist) {
        this.billableCrossingDist = billableCrossingDist;
    }

    public Boolean isValid(){
        Boolean returnValue=Boolean.TRUE;

        if(this.flightMovementType==null){
            returnValue= Boolean.FALSE;
        }

        if(this.billableRoute==null){
            returnValue= Boolean.FALSE;
        }

        if(this.calculateDistance==null){
            returnValue= Boolean.FALSE;
        }

        return returnValue;
    }

    @Override
    public String toString() {
        return "BillableRouteVO{" +
            "flightMovementType=" + flightMovementType +
            ", segmentType=" + segmentType +
            ", calculateDistance=" + calculateDistance +
            ", billableRoute=" + billableRoute +
            ", billableEntryPoint='" + billableEntryPoint + '\'' +
            ", billableExitPoint='" + billableExitPoint + '\'' +
            ", billableCrossingDist=" + billableCrossingDist +
            '}';
    }
}
