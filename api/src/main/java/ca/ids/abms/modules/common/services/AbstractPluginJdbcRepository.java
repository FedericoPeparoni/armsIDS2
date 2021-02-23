package ca.ids.abms.modules.common.services;

import ca.ids.abms.modules.cachedevents.CachedEventMetadata;
import ca.ids.abms.modules.cachedevents.enumerators.CachedEventAction;
import ca.ids.abms.modules.cachedevents.enumerators.CachedEventType;
import ca.ids.abms.modules.plugins.enumerators.PluginSqlAction;
import ca.ids.abms.util.jdbc.PluginKeyHolder;
import ca.ids.abms.util.jdbc.PluginSqlStatement;
import ca.ids.abms.util.jdbc.annotations.HandleEmptyResult;
import ca.ids.spring.cache.exceptions.CacheableRuntimeException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public abstract class AbstractPluginJdbcRepository<T, ID extends Serializable> {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private PluginJdbcUtility<T, ID> pluginJdbcUtility;

    public AbstractPluginJdbcRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                                        PluginJdbcUtility<T, ID> pluginJdbcUtility) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.pluginJdbcUtility = pluginJdbcUtility;
    }

    /**
     * Delete object from database by identifier.
     *
     * @param id identifier to delete
     */
    public void delete(ID id) {
        save(pluginJdbcUtility.deleteStatement(id));
    }

    /**
     * Select query expected to return a list of results.
     *
     * @return list of `T` type
     */
    public List<T> findAll() {
        return select(pluginJdbcUtility.findAllStatement());
    }

    /**
     * Select query expected to return one result.
     *
     * @param id of `T` type entity
     * @return `T` type
     */
    public T findOne(ID id) {
        return selectOne(pluginJdbcUtility.findOneStatement(id));
    }

    /**
     * Select query expected to return max id.
     *
     * @param columnName
     * @return `T` type
     */
    public Double findMaxId(String columnName) {
        return selectMaxId(pluginJdbcUtility.findMaxIdStatement(columnName));
    }

    /**
     * Persist and return new object.
     *
     * @param object to insert
     * @return inserted object
     */
    public T insert(T object) {

        // object cannot be null to insert
        if (object == null)
            throw new IllegalArgumentException("`object` argument must NOT be null");

        // insert object into database
        PluginKeyHolder key = save(pluginJdbcUtility.insertStatement(object));

        // find and return newly created object by identifier
        return findOne(keyValue(key));
    }

    /**
     * Persist and return updated object.
     *
     * @param object to update
     * @return updated object
     */
    public T update(T object) {

        // object cannot be null to update
        if (object == null)
            throw new IllegalArgumentException("`object` argument must NOT be null");

        // find existing entry by object's identifier
        ID identifier = pluginJdbcUtility.identifierValue(object);
        T existingObject = findOne(identifier);

        // existing entry cannot be null
        if (existingObject == null)
            throw new IllegalStateException("Cannot update " + pluginJdbcUtility.getResourceName() +
                " as existing entry could not be found with identifier value of " + identifier);

        // merge only non default values into exiting object
        pluginJdbcUtility.mergeOnly(object, existingObject);

        // update existing object in database
        PluginKeyHolder key = save(pluginJdbcUtility.updateStatement(existingObject));

        // find and return newly updated object by identifier
        return findOne(keyValue(key));
    }

    /**
     * Select query expected to return a list of results.
     *
     * @param statement plugin sql statement with SELECT action
     * @return list of `T` type
     */
    protected List<T> select(PluginSqlStatement statement) {

        if (statement.getAction() != PluginSqlAction.SELECT)
            throw new IllegalArgumentException("Expected statement action value of SELECT");

        return namedParameterJdbcTemplate.query(
            statement.getStatement(),
            statement.getParams(),
            pluginJdbcUtility.getRowMapper());
    }

    /**
     * Select query expected to return a list of results.
     *
     * @param statement plugin sql statement with SELECT action
     * @return list of `T` type
     */
    @HandleEmptyResult
    protected Double selectMaxId(PluginSqlStatement statement) {

        if (statement.getAction() != PluginSqlAction.SELECT)
            throw new IllegalArgumentException("Expected statement action value of SELECT");

        return namedParameterJdbcTemplate.queryForObject(
            statement.getStatement(),
            statement.getParams(),
            Double.class
        );
    }

    /**
     * Select query expected to return one result.
     *
     * @param statement plugin sql statement with SELECT action
     * @return `T` type;
     */
    @HandleEmptyResult
    protected T selectOne(PluginSqlStatement statement) {

        if (statement.getAction() != PluginSqlAction.SELECT)
            throw new IllegalArgumentException("Expected statement action value of SELECT");

        return namedParameterJdbcTemplate.queryForObject(
            statement.getStatement(),
            statement.getParams(),
            pluginJdbcUtility.getRowMapper());
    }

    /**
     * Run sql statement using a plugin sql statement object for INSERT, UPDATE, and DELETE actions.
     *
     * @param statement plugin sql statement with INSERT, UPDATE, or DELETE action
     * @return key identifier if any returned
     */
    protected PluginKeyHolder save(PluginSqlStatement statement) {

        if (statement.getAction() != PluginSqlAction.INSERT &&
            statement.getAction() != PluginSqlAction.UPDATE &&
            statement.getAction() != PluginSqlAction.DELETE)
            throw new IllegalArgumentException("Expected statement action value of INSERT, UPDATE, or DELETE");

        PluginKeyHolder keyHolder = new PluginKeyHolder();

        try {
            namedParameterJdbcTemplate.update(statement.getStatement(), statement.getParams(), keyHolder);
        } catch (Exception e) {
            throw new CacheableRuntimeException(new CachedEventMetadata[] {
                new CachedEventMetadata(
                    CachedEventType.SQL,
                    CachedEventAction.from(statement.getAction()),
                    statement.getResource(),
                    statement.sqlToString())
            }, e);
        }

        return keyHolder;
    }

    @SuppressWarnings("unchecked")
    private ID keyValue(PluginKeyHolder key) {
        return (ID) key.getKeyAsObject();
    }
}
