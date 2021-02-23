package ca.ids.abms.config.cache;

public class CacheNames {

    private CacheNames() {
        throw new IllegalStateException("Utility class, do not instantiate a new instance.");
    }

    public static final String CACHE_TRANSACTIONS = "transactions";

    // NAV DB
    public static final String NAVDB_IDENTS = "navDB.idents";
    public static final String NAVDB_COORDINATES = "navDB.coordinates";
    public static final String NAVDB_AIRSPACES = "navDB.airspaces";
    public static final String NAVDB_AIRSPACE_BY_ID = "navDB.airspaceById";
    public static final String NAVDB_AERODROME_PREFIXES = "navDB.aerodromePrefixes";
    public static final String NAVDB_FIRS_BY_LOCATION = "navDB.firsByLocation";
    public static final String NAVDB_AD_INSIDE_SOUTH_SUDAN = "navDB.adInsideSouthSudan";

}
