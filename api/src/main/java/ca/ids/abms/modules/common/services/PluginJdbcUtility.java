package ca.ids.abms.modules.common.services;

import ca.ids.abms.modules.plugins.enumerators.PluginSqlAction;
import ca.ids.abms.modules.util.models.ModelUtils;
import ca.ids.abms.util.StringUtils;
import ca.ids.abms.util.jdbc.AutoRowMapper;
import ca.ids.abms.util.jdbc.PluginSqlStatement;
import org.apache.commons.lang.IllegalClassException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import javax.persistence.*;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

@SuppressWarnings("WeakerAccess")
public class PluginJdbcUtility<T, ID extends Serializable> {

    private static final String IDENTIFIER_NAME_DEFAULT = "id";

    private final Class<T> clazz;

    private Field identifierField;

    private String identifierName;

    private final RowMapper<T> rowMapper;

    private String resourceName;

    private String statementDelete;

    private String statementInsert;

    private String statementSelectAll;

    private String statementSelectOne;

    private String statementSelectMaxId;

    private String statementUpdate;

    public PluginJdbcUtility(Class<T> clazz) {
        this(clazz, new AutoRowMapper<>(clazz));
    }

    public PluginJdbcUtility(Class<T> clazz, RowMapper<T> rowMapper) {
        this.clazz = clazz;
        this.rowMapper = rowMapper;
        this.setDefaults();
    }

    /**
     * Set identifier name used in sql queries.
     *
     * @param identifierName aka id name used in sql statements
     */
    public void setIdentifierName(String identifierName) {
        this.identifierName = identifierName;
    }

    /**
     * Get the identifier name from the class id annotation or default to id.
     *
     * @return identifier name from annotation @Id or `id`
     */
    public String getIdentifierName() {
        return identifierName;
    }

    /**
     * Set resource name used in sql queries.
     *
     * @param resourceName aka table name used in sql statements
     */
    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    /**
     * Gets the resource name from class table annotation or simple name as snake case.
     *
     * @return resource name from annotation @Table.name() or clazz.getSimpleName
     */
    public String getResourceName() {
        return resourceName;
    }

    /**
     * Row Mapper used to map results to `T` object.
     * Default row mapper is `AutoRowMapper<T>`.
     *
     * @return row mapper for `T`
     */
    public RowMapper<T> getRowMapper() {
        return rowMapper;
    }

    /**
     * Plugin Sql Statement for delete one query by id.
     *
     * @param id of `T` type entity
     * @return Delete Plugin Sql Statement
     */
    public PluginSqlStatement deleteStatement(ID id) {

        // create plugin sql statement and return
        PluginSqlStatement statement = new PluginSqlStatement();

        statement.setAction(PluginSqlAction.DELETE);
        statement.setStatement(statementDelete);
        statement.setResource(resourceName);
        statement.setParams(new MapSqlParameterSource(IDENTIFIER_NAME_DEFAULT, id));

        return statement;
    }

    /**
     * Plugin Sql Statement for find all query.
     *
     * @return Select Plugin Sql Statement
     */
    public PluginSqlStatement findAllStatement() {

        // create plugin sql statement and return
        PluginSqlStatement statement = new PluginSqlStatement();

        statement.setAction(PluginSqlAction.SELECT);
        statement.setStatement(statementSelectAll);
        statement.setResource(resourceName);

        return statement;
    }

    /**
     * Plugin Sql Statement for select one query by id.
     *
     * @param id of `T` type entity
     * @return Select Plugin Sql Statement
     */
    public PluginSqlStatement findOneStatement(ID id) {

        // create plugin sql statement and return
        PluginSqlStatement statement = new PluginSqlStatement();

        statement.setAction(PluginSqlAction.SELECT);
        statement.setStatement(statementSelectOne);
        statement.setResource(resourceName);
        statement.setParams(new MapSqlParameterSource(IDENTIFIER_NAME_DEFAULT, id));

        return statement;
    }

    /**
     * Plugin Sql Statement to find max id.
     *
     * @param columnName is name of id column
     * @return Select Plugin Sql Statement
     */
    public PluginSqlStatement findMaxIdStatement(String columnName) {

        // create plugin sql statement and return
        PluginSqlStatement statement = new PluginSqlStatement();

        statement.setAction(PluginSqlAction.SELECT);
        statement.setStatement(statementSelectMaxId);
        statement.setResource(resourceName);
        statement.setParams(new MapSqlParameterSource(IDENTIFIER_NAME_DEFAULT, columnName));

        return statement;
    }

    /**
     * Return value of identifier from object.
     *
     * @param object to evaluate
     * @return object identifier value
     */
    @SuppressWarnings("unchecked")
    public ID identifierValue(T object) {

        if (object == null)
            throw new IllegalArgumentException("`object` argument must NOT be null");

        if (identifierField == null)
            throw new IllegalStateException("Cannot perform method as object must contain field with `@Id` " +
                "annotation or `id` field name.");

        try {
            return (ID) identifierField.get(object);
        } catch (IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Plugin Sql Statement for insert query by object fields.
     *
     * @param object to insert
     * @return Insert Plugin Sql Statement
     */
    public PluginSqlStatement insertStatement(T object) {
        PluginSqlStatement statement = new PluginSqlStatement();

        statement.setAction(PluginSqlAction.INSERT);
        statement.setStatement(statementInsert);
        statement.setResource(resourceName);
        statement.setParams(this.objectParams(object));

        return statement;
    }

    /**
     * Merge source object into target object excluding null and default properties. If {@code Field} object is
     * enforcing Java language access control and the underlying field is inaccessible, the field is ignored.
     *
     * @param source from object
     * @param target to object
     *
     * @throws IllegalClassException
     *         if this {@code Class} represents an abstract class,
     *         an interface, an array class, a primitive type, or void;
     *         or if the class has no nullary constructor;
     *         or if the instantiation fails for some other reason.
     */
    public void mergeOnly(T source, T target) {
        try {
            T defaults = clazz.newInstance();
            List<String> includedFields = new ArrayList<>();
            for (Field field : source.getClass().getDeclaredFields()) {
                if (isMergeOnlyField(field, source, target, defaults))
                    includedFields.add(field.getName());
            }
            ModelUtils.mergeOnly(source, target, includedFields.toArray(new String[0]));
        } catch (IllegalAccessException | InstantiationException ex) {
            throw new IllegalClassException(ex.getMessage());
        }
    }

    /**
     * Plugin Sql Statement for update query by object fields.
     *
     * @param object to update
     * @return Update Plugin Sql Statement
     */
    public PluginSqlStatement updateStatement(T object) {
        PluginSqlStatement statement = new PluginSqlStatement();

        statement.setAction(PluginSqlAction.UPDATE);
        statement.setStatement(statementUpdate);
        statement.setResource(resourceName);
        statement.setParams(this.objectParams(object));

        return statement;
    }

    /**
     * Class table name is `@Table(name)` else default to snake case of class simple name.
     *
     * @return evaluated table name
     */
    private String classTableName() {
        if (clazz.isAnnotationPresent(Table.class)
            && !clazz.getAnnotation(Table.class).name().isEmpty())
            return clazz.getAnnotation(Table.class).name();
        else
            return StringUtils.camelCaseToSnakeCase(clazz.getSimpleName());
    }

    /**
     * Field column name is `@Column(name)` else default to snake case of field name.
     *
     * @param field to evaluate
     * @return field column name
     */
    protected String fieldColumnName(Field field) {
        if (field.isAnnotationPresent(Column.class) && !field.getAnnotation(Column.class).name().isEmpty()) {
            return field.getAnnotation(Column.class).name();
        } else {
            return StringUtils.camelCaseToSnakeCase(field.getName());
        }
    }

    /**
     * Field param name is `fieldColumnName` without any spaces.
     *
     * @param field to evaluate
     * @return field param name
     */
    protected String fieldParamName(Field field) {
        return fieldColumnName(field)
            .replaceAll("[-\\s\\.\\$\\+\\_\\(\\)]","")
            .replaceAll("\\%", "Pct");
    }

    /**
     * Evaluate if field should be included in merge only. True if field value is
     * not null, default value or target value.
     *
     * @param field to evaluate
     * @param source source object
     * @param target target object
     * @param defaults default object
     *
     * @return true if field should be included in merge
     */
    private Boolean isMergeOnlyField(Field field, T source, T target, T defaults) {
        try {
            field.setAccessible(true);
            Object sourceField = field.get(source);
            if (sourceField != null && !sourceField.equals(field.get(defaults)) && !sourceField.equals(field.get(target)))
                return true;
        } catch (IllegalAccessException ex) {
            // silently ignore fields that aren't accessible
        }
        return false;
    }

    /**
     * Return true if field is to be persisted.
     *
     * @param field to evaluate
     * @return true if persistent field
     */
    protected Boolean persistField(Field field) {
        // skip over fields with `@Transient` annotation or static as they are not to be persisted
        return !field.isAnnotationPresent(Transient.class)
            && !Modifier.isStatic(field.getModifiers());
    }

    /**
     * Map object fields to Sql Parameters to use in Plugin Sql Statements.
     *
     * @param object to map fields
     * @return Map Sql Parameters Source
     */
    protected MapSqlParameterSource objectParams(T object) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        for (Field field : object.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                params.addValue(this.fieldParamName(field), field.get(object));
            } catch (IllegalAccessException ex) {
                // ignore fields that are inaccessible
            }
        }
        return params;
    }

    /**
     * Set resource, identifier, and statement field values.
     */
    private void setDefaults() {

        // identifier and resource values must be set before statements
        // identifier field must be set before identifier name
        this.setIdentifierField();
        this.setIdentifierName();
        this.setResourceName();

        // set each statement used using identifier and resource name
        this.setStatementDelete();
        this.setStatementInsert();
        this.setStatementSelectAll();
        this.setStatementSelectOne();
        this.setStatementSelectMaxId();
        this.setStatementUpdate();
    }

    /**
     * Set resource identifier field from `@Id` annotation value or default to `id` field if exists.
     */
    private void setIdentifierField() {

        // get identified (aka @Id) field from annotation
        Field newIdentifierField = null;
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                newIdentifierField = field;
                break;
            } else if (newIdentifierField == null && IDENTIFIER_NAME_DEFAULT.equals(field.getName())) {
                newIdentifierField = field;
            }
        }

        // set identifier field and set accessible if not null for use in `identifierValue(object)`
        identifierField = newIdentifierField;
        if (identifierField != null)
            identifierField.setAccessible(true);
    }

    /**
     * Set resource identifier name from `@Id` annotation value or default to `id`.
     */
    private void setIdentifierName() {

        // find column name from identifier field if exists, else set to default
        if (identifierField != null) {
            this.identifierName = this.fieldColumnName(identifierField);
        } else {
            this.identifierName = IDENTIFIER_NAME_DEFAULT;
        }
    }

    /**
     * Set resource name from `@Table.name()` annotation value or class simple name
     * as snake case.
     *
     * @throws IllegalStateException when annotation name value is empty and anonymous class
     */
    private void setResourceName() {

        // get resource (aka table) name from annotation or snake case of class name
        String newResourceName = this.classTableName();

        // if resource is empty, must be anonymous class without table annotation
        if (newResourceName.isEmpty())
            throw new IllegalStateException("Expected " + clazz + " annotation " + Table.class.toGenericString() + " with `name` value to be present or be a non anonymous class.");

        // set resource name
        this.resourceName = newResourceName;
    }

    /**
     * Set delete statement from resource and identifier name. ONLY supports MSSQL.
     */
    private void setStatementDelete() {
        statementDelete = "DELETE FROM [" + resourceName + "] WHERE [" + identifierName + "] = :" + IDENTIFIER_NAME_DEFAULT;
    }

    /**
     * Set insert statement from resource and identifier names using entity fields as columns. ONLY supports MSSQL.
     */
    private void setStatementInsert() {

        // loop through each field and append to column and param names
        StringJoiner columnNames = new StringJoiner(", ");
        StringJoiner paramNames = new StringJoiner(", ");
        for (Field field : clazz.getDeclaredFields()) {

            // skip over identifier fields with generated value, expect them to be database generated
            // @GeneratedValue is a hibernate annotation and not supported in this class
            if ((field.isAnnotationPresent(Id.class) && field.isAnnotationPresent(GeneratedValue.class)) ||
                (!persistField(field)))
                continue;

            // append field column and param names
            columnNames.add("[" + this.fieldColumnName(field) + "]");
            paramNames.add(":" + this.fieldParamName(field));
        }

        statementInsert = "INSERT INTO [" + resourceName + "] (" + columnNames.toString()
            + ") VALUES (" + paramNames.toString() + ")";
    }

    /**
     * Set select all statement from resource name. ONLY supports MSSQL.
     */
    private void setStatementSelectAll() {
        statementSelectAll = "SELECT * FROM [" + resourceName + "]";
    }

    /**
     * Set select one statement from resource and identifier names. ONLY supports MSSQL.
     */
    private void setStatementSelectOne() {
        statementSelectOne = "SELECT * FROM [" + resourceName + "] WHERE [" + identifierName + "] = :" + IDENTIFIER_NAME_DEFAULT;
    }

    /**
     * Set select one statement from resource and identifier names. ONLY supports MSSQL.
     */
    private void setStatementSelectMaxId() {
        statementSelectMaxId = "SELECT MAX(" +  identifierName + ") FROM [" + resourceName + "]";
    }

    /**
     * Set update statement from resource and identifier names using entity fields as columns. ONLY supports MSSQL.
     */
    private void setStatementUpdate() {

        // loop through each field and append to set values and condition
        StringJoiner condition = new StringJoiner(" AND ");
        StringJoiner setValues = new StringJoiner(", ");
        for (Field field : clazz.getDeclaredFields()) {

            if (!persistField(field))
                continue;

            if (field.isAnnotationPresent(Id.class)) {
                condition.add("[" + this.fieldColumnName(field) + "] = :" + this.fieldParamName(field));
            } else {
                setValues.add("[" + this.fieldColumnName(field) + "] = :" + this.fieldParamName(field));
            }
        }

        statementUpdate = "UPDATE [" + resourceName + "] SET " + setValues.toString() + " WHERE " + condition.toString();
    }
}
