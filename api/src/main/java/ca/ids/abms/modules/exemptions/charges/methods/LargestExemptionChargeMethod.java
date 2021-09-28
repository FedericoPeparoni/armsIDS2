package ca.ids.abms.modules.exemptions.charges.methods;

import ca.ids.abms.modules.exemptions.charges.ExemptionCharge;
import ca.ids.abms.modules.util.models.Calculation;
import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of exemption charge method that only applies the largest charge
 * exemption. All flight note with defined charge exemptions are still applied.
 */
@Component
public class LargestExemptionChargeMethod implements ExemptionChargeMethod {

    /**
     * Resolve applied charge by largest exemption value and all flight notes where exemption is defined.
     */
    public ExemptionChargeMethodResult resolve(final ExemptionChargeMethodModel model) {
        Preconditions.checkArgument(model != null);

        // exemptions must be defined, otherwise there is nothing to resolve
        if (model.getExemptionCharges() == null || model.getExemptionCharges().isEmpty())
            return null;

        double percentage = 0;
        List<String> flightNotes = new ArrayList<>();

        // loop through each exemption charge and apply if defined
        // only highest charge exemption is used
        for (ExemptionCharge exemption : model.getExemptionCharges()) {

            Double chargeExemption = exemption.getChargeExemption();

            // skip if charge exemption is not defined
            if (chargeExemption == null)
                continue;

            // only apply charge exemption if largest
            if (chargeExemption > percentage)
                percentage = chargeExemption;

            // only apply note if not null or blank
            String flightNote = exemption.getFlightNote();
            if (StringUtils.isNotBlank(flightNote))
                flightNotes.add(flightNote);
        }

        // determine decimal place precision from currency or default to two decimal places
        Integer precision = model.getChargeCurrency() == null || model.getChargeCurrency().getDecimalPlaces() == null
            ? 2 : model.getChargeCurrency().getDecimalPlaces();

        // calculate applied and exempt value from charge value and percentage
        Double chargeValue = model.getChargeValue();
        Double appliedCharge = calculateCharge(chargeValue, percentage, precision);
        Double exemptCharge = appliedCharge == null ? null : Calculation.operation(
            chargeValue, appliedCharge, Calculation.MathOperator.SUBTRACT, precision);

        return new ExemptionChargeMethodResult.Builder()
            .setAppliedCharge(appliedCharge)
            .setExemptCharge(exemptCharge)
            .setExemptionPercentage(percentage)
            .setExemptNotes(flightNotes)
            .build();
    }

    /**
     * Calculate new charge by multiplying value by inverse percentage. Only positive values
     * are allowed and any negative will be interpreted as positive.
     */
    private Double calculateCharge(final Double charge, final Double percentage, final Integer precision) {
        if (charge == null || percentage == null) return null;

        return Calculation.operation(Math.abs(charge), 1 - (Math.abs(percentage) / 100),
            Calculation.MathOperator.MULTIPLY, precision);
    }
}
