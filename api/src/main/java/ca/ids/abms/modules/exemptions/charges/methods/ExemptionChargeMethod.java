package ca.ids.abms.modules.exemptions.charges.methods;

public interface ExemptionChargeMethod {

    /**
     * Implement applied charge, exempt charge and flight notes for provided charge value and exemption charges.
     */
    ExemptionChargeMethodResult resolve(final ExemptionChargeMethodModel model);
}
