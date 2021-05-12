package ca.ids.abms.modules.unifiedtaxes;

import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.aircraft.AircraftRegistration;

public class UnifiedTaxInvoiceError {

    public Account account() {
        return account;
    }
    public AircraftRegistration aircraftRegistration() {
        return aircraftRegistration;
    }

    public String reason() {
        return reason;
    }
	
    public UnifiedTaxInvoiceError (
            final Account account,
            final AircraftRegistration aircraftRegistration,
            final String reason) {
        this.account = account;
        this.aircraftRegistration = aircraftRegistration;
        this.reason = reason;
    }
	
    public String toString() {
    	String accountName = account!=null?account.getName():"";
    	String registrationNumber = aircraftRegistration!=null?aircraftRegistration.getRegistrationNumber():"";
    	return accountName+"#"+registrationNumber+"#"+reason;
    }
    
    // ---------------- private ---------
    private final Account account;
    private final AircraftRegistration aircraftRegistration;
	private final String reason;
}
