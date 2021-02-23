package ca.ids.abms.modules.dbqueries;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.GenericJDBCException;
import org.hibernate.exception.SQLGrammarException;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.transaction.annotation.Transactional;

/**
 * Utility class for evaluating SQL queries.
 * @author dpanech
 *
 */
public class DatabaseQueryEvaluator {
    
    public DatabaseQueryEvaluator (final SessionFactory sessionFactory, final String queryFilesLocation) {
        this.sessionFactory = sessionFactory;
        this.databaseQueryLoader = new DatabaseQueryLoader (queryFilesLocation);
    }

    /**
     * Execute the given query with the specified parameters.
     * 
     * @param name -- the query name; the qctual SQL file name will be constructed as <code>$queryFilesLocation/$name.sql</code>
     * @param params - map of parameter key/value pairs
     * 
     * @return the list of key/value maps of the results
     */
    @Transactional (readOnly = true)
    public DatabaseQueryResult query (final String name, final Map <String, Object> params) {
        final Session session = getCurrentSession();
        final DatabaseQueryLoader.NamedQuery namedQuery = databaseQueryLoader.load (name);
        try {
            final SQLQuery sqlQuery = session.createSQLQuery (namedQuery.expr);
            do_filterParams (params, namedQuery.requiredParameters).forEach((key, value)->{
                if (key != null) {
                    if (value == null) {
                        sqlQuery.setString(key, null);
                    }
                    else {
                        sqlQuery.setParameter(key, value);
                    }
                }
            });
            sqlQuery.setResultTransformer (AliasToEntityMapResultTransformer.INSTANCE);
            @SuppressWarnings("rawtypes")
            final List resultList = sqlQuery.list();
            @SuppressWarnings("unchecked")
            final List <Map <String, Object>> typedResultList = resultList;
            return new DatabaseQueryResult (typedResultList);
        }
        catch (SQLGrammarException|GenericJDBCException x) {
            throw new RuntimeException (x.getSQLException());
        }
    }
    
    // ------------------ private -------------------------

    /**
     * Create a map of parameters for the SQL statement by adding missing ones, etc.
     * 
     * @param params  -- parameters provided by user
     * @param requiredParams  -- parameters required by the SQL query
     * 
     * @return map of requiredParams mapped to user-provided values, or NULL values for the missing keys
     */
    private final Map <String, Object> do_filterParams (final Map <String, Object> params, final Set <String> requiredParams) {
        final Map <String, Object> filteredParams = new HashMap<>();
        requiredParams.stream().forEach (key -> {
            filteredParams.put (key, params.get (key));
        });
        return filteredParams;
    }

    /** Return current Hibernate session */
    private final Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }
    
    private final SessionFactory sessionFactory;
    private final DatabaseQueryLoader databaseQueryLoader;
    
}
