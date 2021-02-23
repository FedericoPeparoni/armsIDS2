package ca.ids.abms.modules.system;

import java.util.Locale;

import javax.validation.constraints.NotNull;

public enum BillingOrgCode {
    /** Default/unspecified organization */
    DEFAULT,
    
    /** Botswana: Civil Aviation Authority of Botswana */
    CAAB,
    
    /** Zambia: Zambia Airports Corporation Limited */
    ZACL,
    
    /** Kenya: Kenya Civil Aviation Authority */
    KCAA,
    
    /** Curacao: Dutch Caribbean Air Navigation Service Provider */
    DC_ANSP,
    
    /** Venezuela: National Institute of Civil Aviation */
    INAC,
    
    /** Argentina: Empresa Argentina de Navegación Aérea */
    EANA,

    /** Trinidad and Tobago: Trinidad and Tobago Civil Aviation Authority */
    TTCAA,

    /** Suriname: Civil Aviation Suriname */
    CADSUR;


    /**
     * Returns the string used to represent this org in the database. This typically is the same
     * as the enum constant name, but with underscores replaced with dashes.
     */
    public String getDatabaseCode() {
        return this.databaseCode;
    }
    
    /**
     * Returns the string used to represent this org on the file system, e.g. in resources/reports directories.
     * This typically is the same as the enum constant name, but with underscores replaced with dashes, and
     * forced to lower case.
     */
    public String getFileCode() {
        return this.fileCode;
    }
    
    /**
     * Parse the org code from system_configurations table into an enum.
     * Unknown and NULL values are returned as {@link #DEFAULT}.
     */
    @NotNull
    public static BillingOrgCode safeParseDatabaseCode (final String databaseCode) {
        if (databaseCode != null) {
            for (final BillingOrgCode x: BillingOrgCode.values()) {
                if (x.getDatabaseCode().equals (databaseCode)) {
                    return x;
                }
            }
        }
        return BillingOrgCode.DEFAULT;
    }
    
    
    // -------------------------- private ----------------------
    private final String databaseCode;
    private final String fileCode;
    private BillingOrgCode() {
        this.databaseCode = this.name().replaceAll ("_", "-");
        this.fileCode = this.databaseCode.toLowerCase (Locale.US);
    }
    
}
