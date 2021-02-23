package ca.ids.abms.plugins.amhs;

public enum AmhsMessageType {
    UNKNOWN (AmhsMessageCategory.UNKNOWN),
    FPL (AmhsMessageCategory.FPL),
    DLA (AmhsMessageCategory.FPL),
    CNL (AmhsMessageCategory.FPL),
    DEP (AmhsMessageCategory.FPL),
    ARR (AmhsMessageCategory.FPL),
    CHG (AmhsMessageCategory.FPL);
    
    AmhsMessageType (final AmhsMessageCategory category) {
        this.category = category;
    }
    
    public AmhsMessageCategory getCategory() {
        return category;
    }
    
    private final AmhsMessageCategory category;
}
