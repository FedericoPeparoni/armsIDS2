package ca.ids.abms.modules.flightmovements;

import java.util.HashSet;
import java.util.Set;

public class FlightMovementListChargesCalculationResult {
	
	private Set<Integer> successfullyCalculated = new HashSet<Integer>(0);
	
	private Set<Integer> unsuccessfullyCalculated = new HashSet<Integer>(0);

	public Set<Integer> getSuccessfullyCalculated() {
		return successfullyCalculated;
	}

	public Set<Integer> getUnsuccessfullyCalculated() {
		return unsuccessfullyCalculated;
	}
	
	public void addSuccessfullyCalculated(Integer flightMovementId) {
		successfullyCalculated.add(flightMovementId);
	}
	
	public void addUnSuccessfullyCalculated(Integer flightMovementId) {
		unsuccessfullyCalculated.add(flightMovementId);
	}	
}
