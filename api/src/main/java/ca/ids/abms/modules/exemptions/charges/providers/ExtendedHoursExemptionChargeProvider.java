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

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Extended Hours surcharge exemption applies the largest exemption only.
 */
@Component
public class ExtendedHoursExemptionChargeProvider implements ExemptionChargeProvider{
    private final LargestExemptionChargeMethod method;

    ExtendedHoursExemptionChargeProvider(final LargestExemptionChargeMethod method) {
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
          //  .map(e -> new ExemptionCharge(e.extendedHoursSurchargeExemption(), e.flightNoteChargeExemption()))
            .map(e -> new ExemptionCharge(e.extendedHoursSurchargeExemption(), e.flightNoteChargeExemption() + " extended"))
            .collect(Collectors.toList());

        // resolve exemption charge using largest exemption method
        ExemptionChargeMethodResult result = method.resolve(new ExemptionChargeMethodModel.Builder()
            .chargeCurrency(flightMovement.getExtendedHoursSurchargeCurrency())
            .chargeValue(flightMovement.getExtendedHoursSurcharge())
            .exemptionCharges(exemptionCharges).build());

        // return immediately if result is null as nothing to apply
        if (result == null) return;

        // apply resolved exemption result to provided flight movement, duplicates flight notes are ignored
        String note = "";
        if(result.getExemptCharge()!= null && result.getExemptCharge()!= 0 && result.getExemptNotes().size() != 0) {
			flightMovement.setExtendedHoursSurcharge(result.getAppliedCharge());
			flightMovement.setExemptExtendedHoursSurcharge(result.getExemptCharge());
			note = result.getExemptionPercentage() + "% " + result.getExemptNotes().get(0);
        }
        
        FlightNotesUtility.mergeFlightNotes(flightMovement, note);
      //  FlightNotesUtility.mergeFlightNotes(flightMovement, result.getExemptNotes());
    }
}
