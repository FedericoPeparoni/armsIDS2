package ca.ids.abms.modules.exemptions.charges;

import java.util.Objects;

public class ExemptionCharge {

    /**
     * Exemption to apply to charge.
     */
    private final Double chargeExemption;

    /**
     * Flight note that applies to the charge exemption.
     */
    private final String flightNote;

    public ExemptionCharge(Double chargeExemption, String flightNote) {
        this.chargeExemption = chargeExemption;
        this.flightNote = flightNote;
    }

    public Double getChargeExemption() {
        return chargeExemption;
    }

    public String getFlightNote() {
        return flightNote;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExemptionCharge that = (ExemptionCharge) o;
        return Objects.equals(chargeExemption, that.chargeExemption) &&
            Objects.equals(flightNote, that.flightNote);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chargeExemption, flightNote);
    }

    @Override
    public String toString() {
        return "ExemptionCharge{" +
            "chargeExemption=" + chargeExemption +
            ", flightNote='" + flightNote + '\'' +
            '}';
    }
}
