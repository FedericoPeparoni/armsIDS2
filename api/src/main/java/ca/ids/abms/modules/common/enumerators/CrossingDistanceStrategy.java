package ca.ids.abms.modules.common.enumerators;

import com.google.common.base.Preconditions;

/**
 * How to calculate crossing distances. This is used by the system configuration item 'Crossing distance precedence'
 */
public enum CrossingDistanceStrategy {
    
    SMALLEST ("smallest"),
    LARGEST ("largest"),
    SCHEDULED ("scheduled"),
    RADAR ("radar"),
    NOMINAL ("nominal"),
    ATC_LOG ("atc log"),
    TOWER_LOG ("tower log"),
    USER ("user");
    
    /**
     * Parse a string that had been loaded from the database (system configuration); return null on error.
     */
    public static CrossingDistanceStrategy tryParseDatabaseString (final String dbString) {
        if (dbString != null) {
            for (final CrossingDistanceStrategy strategy: CrossingDistanceStrategy.values()) {
                if (strategy.dbString.equals(dbString)) {
                    return strategy;
                }
            }
        }
        return null;
    }
    
    /**
     * Parse a string that had been loaded from the database (system configuration); throw a NullPointerException on error.
     */
    public static CrossingDistanceStrategy parseDatabaseString (final String dbString) {
        Preconditions.checkNotNull (dbString);
        final CrossingDistanceStrategy result = tryParseDatabaseString (dbString);
        Preconditions.checkArgument (result != null);
        return result;
    }
    
    /**
     * Returns the string representation of this constant that should be used when saving these values to the database.
     */
    public String toDatabaseString() {
        return dbString;
    }
    
    // ----------------------- private -------------------
    
    private CrossingDistanceStrategy (final String dbString) {
        this.dbString = dbString;
    }
    
    private final String dbString;
}
