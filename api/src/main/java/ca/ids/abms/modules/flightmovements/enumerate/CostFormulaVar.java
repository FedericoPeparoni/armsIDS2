package ca.ids.abms.modules.flightmovements.enumerate;

/**
 *
 * @author heskina
 * Billing formulas may contain the following names for the variables
 *
 *   MTOW 			    - mtow based on aircraft type or registration ID
 *   SchedCrossDist		- FIR crossing distance
 *   AvgMassFactor        (AMF, based on MTOW category)
 *   AccountDiscount      (based on account)
 *   EntriesNumber        (number of flight entries into FIR, based on radar or flight plan)
 *   FirEntryFee          (cost per FIR entry based on account)
 *   ApproachFee          (approach fee based on Aerodrome Category, flight time and MTOW)
 *   AerodromeFee         (aerodrome fee based on Aerodrome Category, flight time and MTOW)
 *   DFACTOR              distance in Nm divided by 100 rounded to two places of decimal
 *   WFACTOR              square root of MTOW in KG divided by 20,000 rounded to two places of decimal

 */
public enum CostFormulaVar {
	MTOW("MTOW"),
	SCHEDCROSSDIST("CrossDist"),
	AVGMASSFACTOR("AvgMassFactor"),
	ACCOUNTDISCOUNT("AccountDiscount"),
	ENTRIESNUMBER("EntriesNumber"),
	FIRENTRYFEE("FirEntryFee"),
    APPROACHFEE("ApproachFee"),
    AERODROMEFEE("AerodromeFee"),
    DFACTOR("DFACTOR"),
    WFACTOR("WFACTOR");

	private String value;

	CostFormulaVar(String value){
        this.value=value;
    }

	/** Variable name for including in the variable map before passing to the evaluator */
    public String varName() {
        return value;
    }

    /**
     * Variable name as it should appear in formula text, i.e., "[varName]"
     * <p>
     * <b>WARNING</b>: this is meant to be used only in unit tests! Use {@link #varName()} instead in most cases.
     */
    public String varInlineName() {
        return "[" + value + "]";
    }


    @Override
    public String toString() {
        return "CostFormulaVar{" +
            "value='" + value + '\'' +
            '}';
    }

}
