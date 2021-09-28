package ca.ids.abms.modules.exemptions.charges.providers;

import ca.ids.abms.modules.currencies.Currency;
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

@Component
public class UnifiedTaxExemptionChargeProvider {

    private final LargestExemptionChargeMethod method;

    UnifiedTaxExemptionChargeProvider(final LargestExemptionChargeMethod method) {
        this.method = method;
    }
	
    public ExemptionChargeMethodResult apply(double unifiedTaxChargeValue, final Currency currency, final Collection<ExemptionType> exemptions) {
        Preconditions.checkArgument(exemptions != null);

        // map exemption types using applicable values to exemption charge object
        Collection<ExemptionCharge> exemptionCharges = exemptions.stream().filter(Objects::nonNull)
            .map(e -> new ExemptionCharge(e.unifiedTaxExemption(), e.flightNoteChargeExemption()))
            .collect(Collectors.toList());

        // resolve exemption charge using largest exemption method
        ExemptionChargeMethodResult result = method.resolve(new ExemptionChargeMethodModel.Builder()
            .chargeCurrency(currency)
            .chargeValue(unifiedTaxChargeValue)
            .exemptionCharges(exemptionCharges).build());

        // return immediately if result is null as nothing to apply
        return result;
    }
    
}
