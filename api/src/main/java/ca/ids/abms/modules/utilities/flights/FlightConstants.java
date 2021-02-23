package ca.ids.abms.modules.utilities.flights;

class FlightConstants {

    /**
     * The number of hours to allow departure time to overlap within a 24 hour window used when determining if
     * date of flight or date of contact from departure time and contact time.
     *
     * Value should only ever be between 0 and 6.
     *
     * See also TFS US100976.
     */
    static final int DEP_TIME_BUFFER = 4;

    private FlightConstants() {
        throw new IllegalStateException("Constants class, do not instantiate a new instance.");
    }
}
