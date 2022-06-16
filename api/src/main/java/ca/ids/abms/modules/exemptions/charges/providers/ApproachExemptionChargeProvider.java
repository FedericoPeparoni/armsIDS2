package ca.ids.abms.modules.exemptions.charges.providers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import ca.ids.abms.modules.exemptions.ExemptionType;
import ca.ids.abms.modules.exemptions.FlightNotesUtility;
import ca.ids.abms.modules.exemptions.charges.ExemptionCharge;
import ca.ids.abms.modules.exemptions.charges.methods.ExemptionChargeMethodModel;
import ca.ids.abms.modules.exemptions.charges.methods.ExemptionChargeMethodResult;
import ca.ids.abms.modules.exemptions.charges.methods.LargestExemptionChargeMethod;
import ca.ids.abms.modules.flightmovements.FlightMovement;

/**
 * Approach charge exemption applies the largest exemption only.
 */
@Component
public class ApproachExemptionChargeProvider implements ExemptionChargeProvider {

    private final LargestExemptionChargeMethod method;

    ApproachExemptionChargeProvider(final LargestExemptionChargeMethod method) {
        this.method = method;
    }

    /**
     * Apply exemptions to flight movement using largest exemption defined. All flight notes are
     * applied where exemption is defined.
     */
    public void apply(final FlightMovement flightMovement, final Collection<ExemptionType> exemptions) {
        Preconditions.checkArgument(flightMovement != null && exemptions != null);

        // map exemption types using applicable values to exemption charge object
        Collection<ExemptionCharge> exemptionCharges = exemptions.stream().filter(Objects::nonNull)
        	.map(e -> new ExemptionCharge(e.approachChargeExemption(), e.flightNoteChargeExemption() + " approach"))
            .collect(Collectors.toList());
        double charge = 0d;
        if (flightMovement.getApproachChargesWithoutDiscount() == null) {
        	charge = flightMovement.getApproachCharges();
        	if (flightMovement.getExemptApprochCharges() != null)
        		charge += flightMovement.getExemptApprochCharges();
        }else {
        	charge = flightMovement.getApproachChargesWithoutDiscount();
        }
        
        // resolve exemption charge using largest exemption method
        ExemptionChargeMethodResult result = method.resolve(new ExemptionChargeMethodModel.Builder()
            .chargeCurrency(flightMovement.getApproachChargesCurrency())
            .chargeValue(charge)
            .exemptionCharges(exemptionCharges).build());

        // return immediately if result is null as nothing to apply
        if (result == null) return;
        if(flightMovement.getFlightNotes().contains("OutageApproach")){
        	List<String> notesList = new ArrayList<String>(Arrays.asList(flightMovement.getFlightNotes().split(";")));
        	for(String s : notesList) {
        		if(s.contains("OutageApproach")) {
        			String[] stringOutageApproach = s.split("%");
        			if (Double.valueOf(stringOutageApproach[0]) < result.getExemptionPercentage()) {
        				String note = "";
        		        if(result.getExemptNotes().size() !=0 && result.getExemptCharge() != null && result.getExemptCharge() != 0) {
        					flightMovement.setApproachCharges(result.getAppliedCharge());
        					flightMovement.setExemptApprochCharges(result.getExemptCharge());
        		        	note = result.getExemptionPercentage()+"% "+ result.getExemptNotes().get(0);	
        		        }else if (result.getExemptCharge() != null)
        		        	flightMovement.setExemptApprochCharges(result.getExemptCharge());
        		        
        		        FlightNotesUtility.mergeFlightNotes(flightMovement, note);
        			}
        			break;
        		}
        	}
        }else {
        	String note = "";
            if(result.getExemptNotes().size() !=0 && result.getExemptCharge() != null && result.getExemptCharge() != 0) {
    			flightMovement.setApproachCharges(result.getAppliedCharge());
    			flightMovement.setExemptApprochCharges(result.getExemptCharge());
            	note = result.getExemptionPercentage()+"% "+ result.getExemptNotes().get(0);	
            }else if (result.getExemptCharge() != null)
            	flightMovement.setExemptApprochCharges(result.getExemptCharge());
            
            FlightNotesUtility.mergeFlightNotes(flightMovement, note);
        }

        
        
    }
}
