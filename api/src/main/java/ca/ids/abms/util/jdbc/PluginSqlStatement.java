package ca.ids.abms.util.jdbc;

import ca.ids.abms.modules.cachedevents.CachedEventMetadata;
import ca.ids.abms.modules.cachedevents.enumerators.CachedEventAction;
import ca.ids.abms.modules.cachedevents.enumerators.CachedEventType;
import ca.ids.abms.modules.plugins.enumerators.PluginSqlAction;
import ca.ids.abms.modules.util.models.DateTimeUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.*;

public class PluginSqlStatement {

    private String statement;

    private MapSqlParameterSource params;

    private PluginSqlAction action;

    private String resource;

    public PluginSqlStatement() {}

    public PluginSqlStatement(PluginSqlAction action, String resource, String statement, MapSqlParameterSource params) {
        this.statement = statement;
        this.params = params;
        this.action = action;
        this.resource = resource;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String query) {
        this.statement = query;
    }

    public MapSqlParameterSource getParams() {
        return params;
    }

    public void setParams(MapSqlParameterSource params) {
        this.params = params;
    }

    public PluginSqlAction getAction() {
        return action;
    }

    public void setAction(PluginSqlAction action) {
        this.action = action;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    /**
     * sqlToString
     *
     * @return String representation of SQL statement with interpolated params;
     */
    public String sqlToString() {
        String result = statement;
        if (params == null || params.getValues().size() <= 0) {
            return result;
        }

        // sort from largest to smallest so keys don't conflict
        // `DocumentNo` and `No` would conflict if `No` key was replaced first
        Comparator<String> comparator = (o1, o2) -> o2.length() - o1.length() != 0 ? o2.length() - o1.length() : o1.compareTo(o2);
        SortedMap<String, Object> paramMap = new TreeMap<>(comparator);
        paramMap.putAll(params.getValues());

        // loop through each param and replace all in resulting statement
        for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
            result = replaceAllParam(entry.getKey(), entry.getValue(), result);
        }

        return result;
    }

    @Override
    public String toString() {
        return "PluginSqlStatement{" +
            "statement='" + statement + '\'' +
            ", params=" + params +
            ", action=" + action +
            ", resource=" + resource +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PluginSqlStatement that = (PluginSqlStatement) o;
        return Objects.equals(statement, that.statement) &&
            Objects.equals(params, that.params) &&
            Objects.equals(action, this.action) &&
            Objects.equals(resource, this.resource);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statement, params, action, resource);
    }

    /**
     * Generate Cached Event Metadata from Plugin Sql Statements. Returns null
     * if no sqlStatement values supplied.
     *
     * @param sqlStatement plugin sql statements to generate metadata from
     * @return Array of Cached Event Metadata
     */
    public static CachedEventMetadata[] sqlToMetadata(PluginSqlStatement... sqlStatement) {
        if ( sqlStatement == null )
            return null;

        CachedEventMetadata[] result = new CachedEventMetadata[sqlStatement.length];
        int i = 0;
        for ( PluginSqlStatement statement : sqlStatement ) {
            result[i] = new CachedEventMetadata(
                CachedEventType.SQL,
                CachedEventAction.from(statement.getAction()),
                statement.getResource(),
                statement.sqlToString());
            i++;
        }

        return result;
    }

    /**
     * Replace all param in statement.
     *
     * @param key key of param
     * @param value value of param
     * @param sqlStatement sql statement
     * @return resulting sql statement
     */
    private String replaceAllParam(String key, Object value, String sqlStatement) {
        Class paramClass = value != null ? value.getClass() : null;
        if (paramClass == null) {
            sqlStatement = sqlStatement.replaceAll(String.format(":%s", key),
                // replace with null keyword
                "null");
        } else if (paramClass.equals(Boolean.class)) {
            sqlStatement = sqlStatement.replaceAll(
                String.format(":%s", key),
                // format TRUE as 1 and FALSE as 0, MSSQL BIT type
                (Boolean) value ? "1" : "0"
            );
        } else if (paramClass.equals(String.class)) {
            sqlStatement = sqlStatement.replaceAll(
                String.format(":%s", key),
                // wrap params in quotes
                String.format("'%s'", value)
            );
        } else if (paramClass.equals(Date.class)) {
            sqlStatement = sqlStatement.replaceAll(
                String.format(":%s", key),
                // format for mssql and wrap in quotes -- other databases will need to be handled
                String.format("'%s'", DateTimeUtils.dateToMSSqlDateTime(value))
            );
        } else if (paramClass.equals(LocalDateTime.class)) {
            sqlStatement = sqlStatement.replaceAll(
                String.format(":%s", key),
                // format for mssql and wrap in quotes -- other databases will need to be handled
                String.format("'%s'", DateTimeUtils.dateToMSSqlDateTime((LocalDateTime) value))
            );
        } else {
            sqlStatement = sqlStatement.replaceAll(
                String.format(":%s", key),
                value.toString()
            );
        }
        return sqlStatement;
    }
}
