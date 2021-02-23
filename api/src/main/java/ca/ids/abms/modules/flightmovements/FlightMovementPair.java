package ca.ids.abms.modules.flightmovements;

public class FlightMovementPair {

    private FlightMovement segmentOne;

    private FlightMovement segmentTwo;

    public FlightMovement getSegmentOne() {
        return segmentOne;
    }

    public void setSegmentOne(FlightMovement segmentOne) {
        this.segmentOne = segmentOne;
    }

    public FlightMovement getSegmentTwo() {
        return segmentTwo;
    }

    public void setSegmentTwo(FlightMovement segmentTwo) {
        this.segmentTwo = segmentTwo;
    }

    @Override
    public String toString() {
        return "FlightMovementPair{" +
            "segmentOne=" + segmentOne +
            ", segmentTwo='" + segmentTwo + '\'' +
            '}';
    }
}
