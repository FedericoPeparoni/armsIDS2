package ca.ids.abms.modules.dbqueries;

import java.util.Map;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * A service for executing names queries. The queries are stored in "dbqueries/" directory
 * (/etc/config/db/api/ in Linux); query names map to file names under that location.
 */
@Service
public class DatabaseQueryService {

    public DatabaseQueryService (final SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Execute the given query with the specified parameters.
     *
     * @param name -- the query name. For example "examples/users" query will load a file named
     *                <code>classpath:/dbqueries/examples/users.sql</code> (under Eclipse) or
     *                <code>/etc/abms/api/dbqueries/examples/users.sql</code> (in Linux).
     * @param params - map of parameter key/value pairs
     *
     * @return the list of key/value maps of the results
     */
    @Transactional (readOnly = true)
    public DatabaseQueryResult query (final String name, final Map <String, Object> params) {
        init();
        return databaseQueryEvaluator.query (name, params);
    }

    private synchronized void init() {
        if (databaseQueryEvaluator == null) {
            databaseQueryEvaluator = new DatabaseQueryEvaluator (sessionFactory, queryFilesLocation);
        }
    }

    @Value("${abms.dbqueries.queryFilesLocation}")
    private String queryFilesLocation;

    private final SessionFactory sessionFactory;
    private DatabaseQueryEvaluator databaseQueryEvaluator = null;
}
