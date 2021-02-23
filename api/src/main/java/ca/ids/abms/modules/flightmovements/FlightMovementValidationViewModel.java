package ca.ids.abms.modules.flightmovements;

import ca.ids.abms.modules.flightmovements.category.FlightmovementCategory;
import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementStatus;
import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementType;
import ca.ids.abms.modules.flightmovements.enumerate.FlightmovementCategoryNationality;
import ca.ids.abms.modules.flightmovements.enumerate.FlightmovementCategoryScope;
import ca.ids.abms.modules.flightmovements.enumerate.FlightmovementCategoryType;
import ca.ids.abms.modules.flightmovementsbuilder.enumerate.FlightMovementValidatorIssue;
import ca.ids.abms.util.EnumUtils;
import ca.ids.abms.util.StringUtils;

import java.time.LocalDateTime;
import java.util.EnumSet;

/**
 * Created by c.talpa on 01/12/2016.
 */
public class FlightMovementValidationViewModel {

    private Integer flightMovementID;

    private FlightMovementStatus status;

    private FlightMovementType movementType;

    private EnumSet<FlightMovementValidatorIssue> issues;
    
    private String accountName;
    
    private String flightId;
    
    private String registration;
    
    private String departureTime;
    
    private LocalDateTime dayOfFlight;

    private FlightmovementCategory flightmovementCategory;
    
    private FlightmovementCategoryType flightmovementType;
    
    private FlightmovementCategoryScope flightmovementScope;
    
    private FlightmovementCategoryNationality flightmovementNationality;
    
    
    public Integer getFlightMovementID() {
        return flightMovementID;
    }

    public void setFlightMovementID(Integer flightMovementID) {
        this.flightMovementID = flightMovementID;
    }

    public FlightMovementStatus getStatus() {
        return status;
    }

    public void setStatus(FlightMovementStatus status) {
        this.status = status;
    }

   /* public FlightMovementType getMovementType() {
        return movementType;
    }

    public void setMovementType(FlightMovementType movementType) {
        this.movementType = movementType;
    }
*/
    public EnumSet<FlightMovementValidatorIssue> getIssues() {
        return  issues;
    }

    public void setIssues(EnumSet<FlightMovementValidatorIssue> issues) {
        this. issues = issues;
    }

    public void setIssuesString(String commaSeparetedIssues){

        if(StringUtils.isStringIfNotNull(commaSeparetedIssues)){
            issues = EnumUtils.convertStringToEnumSet(FlightMovementValidatorIssue.class, commaSeparetedIssues);
        }
    }

    public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getFlightId() {
		return flightId;
	}

	public void setFlightId(String flightId) {
		this.flightId = flightId;
	}

	public String getRegistration() {
		return registration;
	}

	public void setRegistration(String registration) {
		this.registration = registration;
	}

	public String getDepartureTime() {
		return departureTime;
	}

	public void setDepartureTime(String departureTime) {
		this.departureTime = departureTime;
	}

	public LocalDateTime getDayOfFlight() {
		return dayOfFlight;
	}

	public void setDayOfFlight(LocalDateTime dayOfFlight) {
		this.dayOfFlight = dayOfFlight;
	}

	public FlightmovementCategory getFlightmovementCategory() {
		return flightmovementCategory;
	}

	public void setFlightmovementCategory(FlightmovementCategory flightmovementCategory) {
		this.flightmovementCategory = flightmovementCategory;
	}

	public FlightmovementCategoryType getFlightmovementType() {
		return flightmovementType;
	}

	public void setFlightmovementType(FlightmovementCategoryType flightmovementType) {
		this.flightmovementType = flightmovementType;
	}

	public FlightmovementCategoryScope getFlightmovementScope() {
		return flightmovementScope;
	}

	public void setFlightmovementScope(FlightmovementCategoryScope flightmovementScope) {
		this.flightmovementScope = flightmovementScope;
	}

	public FlightmovementCategoryNationality getFlightmovementNationality() {
		return flightmovementNationality;
	}

	public void setFlightmovementNationality(FlightmovementCategoryNationality flightmovementNationality) {
		this.flightmovementNationality = flightmovementNationality;
	}

	public FlightMovementType getMovementType() {
		return movementType;
	}

	public void setMovementType(FlightMovementType movementType) {
		this.movementType = movementType;
	}

	@Override
    public String toString() {
        return "FlightMovementValidationViewModel{" +
            "flightMovementID=" + flightMovementID +
            ", movementType='" + flightmovementType + '\'' +
            '}';
    }
}

