package ca.ids.abms.util.csv;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

/**
 * Stores metadata for a field in a class, including annotations used for
 * ordering, exclusion, naming, and transforming of field values during serialization.
 *
 * @author Derek McKinnon
 * @see ca.ids.abms.util.csv.annotations.CsvIgnore
 * @see ca.ids.abms.util.csv.annotations.CsvProperty
 * @see ca.ids.abms.util.csv.annotations.CsvPropertyOrder
 */
public class Property {

    private Field                field;
    private String               fieldName;
    private String               headerName;
    private boolean              isCollection;
    private Integer              precision;
    private String               dateTimeFormat;
    private boolean              date;
    private boolean              dateTime;
    private boolean              mtow;
    private boolean              distance;
    private boolean              latitude;
    private boolean              longitude;
    private boolean              inverse;
    private Collection<Property> nestedProperties;

    /**
     * @return The {@link Field} object for the field.
     */
    public Field getField() {
        return field;
    }

    /**
     * Sets the {@link Field} object for the field.
     *
     * @param field The field
     */
    public void setField(Field field) {
        this.field = field;
    }

    /**
     * @return The name of the field
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Sets the name of the field.
     *
     * @param fieldName The name of the field
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * @return The name of the header for the field
     */
    public String getHeaderName() {
        return headerName;
    }

    /**
     * Sets the header for the field.
     *
     * @param headerName The name of the header for the field
     */
    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }

    /**
     * @return The nested {@link Property} objects, if any
     */
    public Collection<Property> getNestedProperties() {
        return nestedProperties == null ? (nestedProperties = new ArrayList<>()) : nestedProperties;
    }

    /**
     * Sets the collection of nested properties.
     *
     * @param nestedProperties The collection of nested properties
     */
    public void setNestedProperties(Collection<Property> nestedProperties) {
        this.nestedProperties = nestedProperties;
    }

    /**
     * @return A value indicating whether or not this {@link Property} represents
     * a collection of objects.
     */
    public boolean isCollection() {
        return isCollection;
    }

    /**
     * Sets value indicating whether or not this {@link Property} represents
     * a collection of objects.
     *
     * @param collection A value indicating whether or not this {@link Property} represents
     *                   a collection of objects.
     */
    public void setIsCollection(boolean collection) {
        isCollection = collection;
    }

    /**
     * @return An optional value indicating the precision of the field, if any
     */
    public Optional<Integer> getPrecision() {
        return Optional.ofNullable(precision);
    }

    /**
     * Sets the precision of the field.
     *
     * @param precision The precision of the field
     */
    public void setPrecision(Integer precision) {
        this.precision = precision;
    }

    /**
     * @return The date/time format of the field, if any
     */
    public Optional<String> getDateTimeFormat() {
        return Optional.ofNullable(dateTimeFormat);
    }

    /**
     * Sets the date/time value of the field.
     *
     * @param dateTimeFormat The date/time value of the field
     */
    public void setDateTimeFormat(String dateTimeFormat) {
        this.dateTimeFormat = dateTimeFormat;
    }

    /**
     * @return A value indicating whether or not this field represents
     * a nested object that should be expanded.
     */
    public boolean isNested() {
        return nestedProperties != null && !nestedProperties.isEmpty();
    }

    public boolean isDate() {
        return date;
    }

    public void setDate(boolean date) {
        this.date = date;
    }

    public boolean isMtow() {
        return mtow;
    }

    public void setMtow(boolean mtow) {
        this.mtow = mtow;
    }

    public boolean isDistance() {
        return distance;
    }

    public void setDistance(boolean distance) {
        this.distance = distance;
    }

    public boolean isDateTime() {
        return dateTime;
    }

    public void setDateTime(boolean dateTime) {
        this.dateTime = dateTime;
    }

    public boolean isLatitude() {
        return latitude;
    }

    public void setLatitude(boolean latitude) {
        this.latitude = latitude;
    }

    public boolean isLongitude() {
        return longitude;
    }

    public void setLongitude(boolean longitude) {
        this.longitude = longitude;
    }

    public boolean isInverse() {
        return inverse;
    }

    public void setInverse(boolean inverse) {
        this.inverse = inverse;
    }
}
