package ca.ids.abms.modules.dbqueries;

import java.util.List;
import java.util.Map;

/**
 * Results of a stored database query.
 *
 */
public final class DatabaseQueryResult {
    
    public List <Map <String, Object>> data() {
        return data;
    }

    /** Constructor */
    DatabaseQueryResult (final List <Map <String, Object>> data) {
        this.data = data;
    }
    
    private final List <Map <String, Object>> data;

}
