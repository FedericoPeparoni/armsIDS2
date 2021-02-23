package ca.ids.abms.modules.flightmovementsbuilder;

import ca.ids.abms.config.error.CustomRuntimeException;
import ca.ids.abms.config.error.ErrorDTO;
import ca.ids.abms.config.error.RejectedReasons;
import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementStatus;
import ca.ids.abms.modules.flightmovementsbuilder.enumerate.FlightMovementBuilderIssue;

/**
 * Created by c.talpa on 15/03/2017.
 */
public class FlightMovementBuilderException extends Exception implements CustomRuntimeException {

    private static final long serialVersionUID = 1L;

	private String message;

    private String param;

    private FlightMovementBuilderIssue flightMovementBuilderIssue;

    //Constructor that accepts a FlightMovementIssue
    public FlightMovementBuilderException(FlightMovementBuilderIssue flightMovementBuilderIssue)
    {
        this(flightMovementBuilderIssue,null,null);
    }

    //Constructor that accepts a FlightMovementIssue and message
    public FlightMovementBuilderException(FlightMovementBuilderIssue flightMovementBuilderIssue,String message)
    {
        this(flightMovementBuilderIssue,message,null);
    }

    //Constructor that accepts a FlightMovementIssue, message, and params
    public FlightMovementBuilderException(FlightMovementBuilderIssue flightMovementBuilderIssue, String message,String param)
    {
        super(message);
        this.flightMovementBuilderIssue= flightMovementBuilderIssue;
        this.param=param;
        this.message=message;
    }

    //Constructor that accepts a FlightMovementStatus
    public FlightMovementBuilderException(FlightMovementStatus flightMovementStatus) {
        this(mapStatusToBuilderIssue(flightMovementStatus));
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public FlightMovementBuilderIssue getFlightMovementBuilderIssue() {
        return flightMovementBuilderIssue;
    }

    public void setFlightMovementBuilderIssue(FlightMovementBuilderIssue flightMovementBuilderIssue) {
        this.flightMovementBuilderIssue = flightMovementBuilderIssue;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public ErrorDTO getErrorDTO() {
        final ErrorDTO.Builder errorBuilder = new ErrorDTO.Builder(this.flightMovementBuilderIssue.toValue());

        if (this.message != null) {
            errorBuilder.appendDetails(this.message);
        } else {
            errorBuilder.appendDetails(RejectedReasons.FLIGHT_MOVEMENT_BUILDER_ERROR.toValue());
        }

        if (this.param != null) {
            errorBuilder.addInvalidField(flightMovementBuilderIssue.getClass(), this.param, this.message);
        }

        errorBuilder.addRejectedReason(RejectedReasons.FLIGHT_MOVEMENT_BUILDER_ERROR);

        return errorBuilder.build();
    }

    /**
     * This will map a FlightMovementStatus to its appropriate FlightMovementBuilderIssue counterpart.
     * @param flightMovementStatus FlightMovementStatus to map
     * @return counterpart FlightMovementBuilderIssue
     */
    private static FlightMovementBuilderIssue mapStatusToBuilderIssue(FlightMovementStatus flightMovementStatus) {
        if (flightMovementStatus == FlightMovementStatus.CANCELED) {
            return FlightMovementBuilderIssue.FLIGHT_MOVEMENT_CANCELLED;
        } else if (flightMovementStatus == FlightMovementStatus.DELETED) {
        	return FlightMovementBuilderIssue.FLIGHT_MOVEMENT_DELETED;
        } else if (flightMovementStatus == FlightMovementStatus.INVOICED) {
            return FlightMovementBuilderIssue.FLIGHT_MOVEMENT_INVOICED;
        } else if (flightMovementStatus == FlightMovementStatus.DECLINED) {
            return FlightMovementBuilderIssue.FLIGHT_MOVEMENT_DECLINED;
        } else {
            return FlightMovementBuilderIssue.FLIGHT_MOVEMENT_NOT_EDITABLE;
        }
    }
}

