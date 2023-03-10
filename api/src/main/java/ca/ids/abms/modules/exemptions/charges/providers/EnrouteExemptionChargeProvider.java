package ca.ids.abms.modules.exemptions.charges.providers;

import ca.ids.abms.modules.exemptions.ExemptionType;
import ca.ids.abms.modules.exemptions.FlightNotesUtility;
import ca.ids.abms.modules.exemptions.charges.ExemptionCharge;
import ca.ids.abms.modules.exemptions.charges.methods.ExemptionChargeMethodModel;
import ca.ids.abms.modules.exemptions.charges.methods.ExemptionChargeMethodResult;
import ca.ids.abms.modules.exemptions.charges.methods.LargestExemptionChargeMethod;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import com.google.common.base.Preconditions;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Enroute charge exemption applies the largest exemption only.
 */
@Component
public class EnrouteExemptionChargeProvider  implements ExemptionChargeProvider{

    private final LargestExemptionChargeMethod method;

    EnrouteExemptionChargeProvider(final LargestExemptionChargeMethod method) {
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
            .map(e -> new ExemptionCharge(e.enrouteChargeExemption(), e.flightNoteChargeExemption() + " enroute"))
            .collect(Collectors.toList());
        
        Double costEnroute = 0d;
        DecimalFormat df = new DecimalFormat("#.##");
        
        //Art. 17
        if(flightMovement.getEnrouteCostToMinimum() == null) {
        	costEnroute = flightMovement.getBillableCrossingCost();
        }else {
        	String cost = df.format(flightMovement.getEnrouteCostToMinimum());
        	flightMovement.setEnrouteCostToMinimum(Double.valueOf(cost.replace(",", ".")));
        	 costEnroute = flightMovement.getBillableCrossingCost() + flightMovement.getEnrouteCostToMinimum();
        }
        
        // resolve exemption charge using largest exemption method
        ExemptionChargeMethodResult result = method.resolve(new ExemptionChargeMethodModel.Builder()
        	.chargeCurrency(flightMovement.getEnrouteResultCurrency())
            .chargeValue(costEnroute) 
            .exemptionCharges(exemptionCharges).build());

        if (result == null) {
        	flightMovement.setEnrouteCharges(costEnroute);
        	flightMovement.setExemptEnrouteCharges(0d);
        	return;
		}
		String note = "";
		flightMovement.setEnrouteCharges(result.getAppliedCharge());
		if (result.getExemptNotes().size() != 0 && result.getExemptCharge() != null && result.getExemptCharge() != 0) {
			// apply resolved exemption result to provided flight movement, duplicates
			// flight notes are ignored
			flightMovement.setExemptEnrouteCharges(result.getExemptCharge());
			note = result.getExemptionPercentage() + "% " + result.getExemptNotes().get(0);
		}else if(result.getExemptCharge() != null)
			flightMovement.setExemptEnrouteCharges(result.getExemptCharge());
		
		FlightNotesUtility.mergeFlightNotes(flightMovement, note);

    }
}
