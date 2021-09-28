package ca.ids.abms.modules.exemptions.charges.methods;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExemptionChargeMethodResult {

    /**
     * Value of charge that has been applied.
     */
    private final Double appliedCharge;

    /**
     * Value of charge that has been exempted.
     */
    private final Double exemptCharge;

    /**
     * Percentage of charge that has been exempted.
     */
    private final Double exemptionPercentage;
    
    /**
     * Exemption notes that have been found.
     */
    private final List<String> exemptNotes;

    private ExemptionChargeMethodResult(
        final Double appliedCharge,
        final Double exemptCharge,
        final Double exemptionPercentage,
        final List<String> exemptNotes
    ) {
        this.appliedCharge = appliedCharge;
        this.exemptCharge = exemptCharge;
        this.exemptNotes = exemptNotes;
        this.exemptionPercentage = exemptionPercentage;
    }

    public Double getAppliedCharge() {
        return appliedCharge;
    }

    public Double getExemptCharge() {
        return exemptCharge;
    }

    public Double getExemptionPercentage() {
        return exemptionPercentage;
    }    
    
    public List<String> getExemptNotes() {
        return exemptNotes;
    }

    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExemptionChargeMethodResult that = (ExemptionChargeMethodResult) o;
        return Objects.equals(appliedCharge, that.appliedCharge) &&
            Objects.equals(exemptCharge, that.exemptCharge) &&
            Objects.equals(exemptNotes, that.exemptNotes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appliedCharge, exemptCharge, exemptNotes);
    }

    @Override
    public String toString() {
        return "ExemptionChargeMethodResult{" +
            "appliedCharge=" + appliedCharge +
            ", exemptCharge=" + exemptCharge +
            ", exemptNotes=" + exemptNotes +
            '}';
    }

    static class Builder {

        private Double appliedCharge = null;
        private Double exemptCharge = null;
        private Double exemptionPercentage = null;
        private List<String> exemptNotes = new ArrayList<>();

        Builder setAppliedCharge(final Double appliedCharge) {
            this.appliedCharge = appliedCharge;
            return this;
        }

        Builder setExemptCharge(final Double exemptCharge) {
            this.exemptCharge = exemptCharge;
            return this;
        }

        Builder setExemptionPercentage(final Double exemptionPercentage) {
            this.exemptionPercentage = exemptionPercentage;
            return this;
        }
        
        Builder setExemptNotes(final List<String> exemptNotes) {
            this.exemptNotes = exemptNotes;
            return this;
        }

        ExemptionChargeMethodResult build() {
            return new ExemptionChargeMethodResult(appliedCharge, exemptCharge, exemptionPercentage, exemptNotes);
        }
    }
}
