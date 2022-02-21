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
 * Aerodrome charge exemption applies the largest exemption only.
 */
@Component
public class AerodromeExemptionChargeProvider implements ExemptionChargeProvider {

    private final LargestExemptionChargeMethod method;

    AerodromeExemptionChargeProvider(final LargestExemptionChargeMethod method) {
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
            .map(e -> new ExemptionCharge(e.aerodromeChargeExemption(), e.flightNoteChargeExemption() + " aerodrome"))
            .collect(Collectors.toList());

        // resolve exemption charge using largest exemption method
        ExemptionChargeMethodResult result = method.resolve(new ExemptionChargeMethodModel.Builder()
            .chargeCurrency(flightMovement.getAerodromeChargesCurrency())
            .chargeValue(flightMovement.getAerodromeCharges())
            .exemptionCharges(exemptionCharges).build());

        // return immediately if result is null as nothing to apply
        if (result == null) return;

     // apply resolved exemption result to provided flight movement, duplicates flight notes are ignored
        String note = "";
        if(result.getExemptNotes().size() !=0 && result.getExemptCharge() != null && result.getExemptCharge() != 0) {
        flightMovement.setAerodromeCharges(result.getAppliedCharge());
        flightMovement.setExemptAerodromeCharges(result.getExemptCharge());
        note = result.getExemptionPercentage()+"% "+ result.getExemptNotes().get(0);
        }
        FlightNotesUtility.mergeFlightNotes(flightMovement, note);
    }
}
