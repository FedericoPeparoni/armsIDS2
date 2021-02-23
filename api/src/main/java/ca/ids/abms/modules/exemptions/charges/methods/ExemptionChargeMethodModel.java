package ca.ids.abms.modules.exemptions.charges.methods;

import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.exemptions.charges.ExemptionCharge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class ExemptionChargeMethodModel {

    private final Currency chargeCurrency;
    private final Double chargeValue;
    private final Collection<ExemptionCharge> exemptionCharges;

    private ExemptionChargeMethodModel(
        final Currency chargeCurrency,
        final Double chargeValue,
        final Collection<ExemptionCharge> exemptionCharges
    ) {
        this.chargeCurrency = chargeCurrency;
        this.chargeValue = chargeValue;
        this.exemptionCharges = exemptionCharges;
    }

    Currency getChargeCurrency() {
        return chargeCurrency;
    }

    Double getChargeValue() {
        return chargeValue;
    }

    Collection<ExemptionCharge> getExemptionCharges() {
        return exemptionCharges;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExemptionChargeMethodModel that = (ExemptionChargeMethodModel) o;
        return Objects.equals(chargeCurrency, that.chargeCurrency) &&
            Objects.equals(chargeValue, that.chargeValue) &&
            Objects.equals(exemptionCharges, that.exemptionCharges);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chargeCurrency, chargeValue, exemptionCharges);
    }

    @Override
    public String toString() {
        return "ExemptionChargeMethodModel{" +
            "chargeCurrency=" + chargeCurrency +
            ", chargeValue=" + chargeValue +
            ", exemptionCharges=" + exemptionCharges +
            '}';
    }

    public static class Builder {

        private Currency chargeCurrency = null;
        private Double chargeValue = null;
        private Collection<ExemptionCharge> exemptionCharges = new ArrayList<>();

        public Builder chargeCurrency(final Currency chargeCurrency) {
            this.chargeCurrency = chargeCurrency;
            return this;
        }

        public Builder chargeValue(final Double chargeValue) {
            this.chargeValue = chargeValue;
            return this;
        }

        public Builder exemptionCharges(final Collection<ExemptionCharge> exemptionCharges) {
            this.exemptionCharges = exemptionCharges;
            return this;
        }

        public ExemptionChargeMethodModel build() {
            return new ExemptionChargeMethodModel(chargeCurrency, chargeValue, exemptionCharges);
        }
    }
}
