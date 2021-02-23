package ca.ids.abms.util.csv;

import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.util.ReflectionUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.function.Consumer;

/**
 * Provides capabilities for automatically mapping data classes for serialization into CSV.
 * <p>
 * Target types are parsed and inspected via reflection before being cached as {@link Property}
 * objects for later processing.
 * <p>
 * {@link Property} objects store metadata for the type's fields, including annotations used for
 * exclusion, naming, and transforming of field values during serialization.
 *
 * @param <T> The target type to map
 * @author Derek McKinnon
 * @see Property
 * @see ca.ids.abms.util.csv.annotations.CsvIgnore
 * @see ca.ids.abms.util.csv.annotations.CsvProperty
 * @see ca.ids.abms.util.csv.annotations.CsvPropertyOrder
 */
class AutoCsvMapper<T> {

    private final Collection<Property> properties;
    private final String dateFormat;
    private final String mtow;
    private final String distance;
    private final String formatCoordinates;
    private final String inverseRate;

    /**
     * Creates a new instance of the mapper.
     *
     * @param clazz The target type to map
     * @param systemConfigurationService SystemConfigurationService
     */
    AutoCsvMapper(Class<T> clazz, final SystemConfigurationService systemConfigurationService) {
        properties = PropertyParser.parseProperties(clazz);
        this.dateFormat = systemConfigurationService.getCurrentValue(SystemConfigurationItemName.DATE_FORMAT);
        this.mtow = systemConfigurationService.getCurrentValue(SystemConfigurationItemName.MTOW_UNIT_OF_MEASURE);
        this.distance = systemConfigurationService.getCurrentValue(SystemConfigurationItemName.DISTANCE_UNIT_OF_MEASURE);
        this.formatCoordinates = systemConfigurationService.getCurrentValue(SystemConfigurationItemName.FORMAT_COORDINATES);
        this.inverseRate = systemConfigurationService.getCurrentValue(SystemConfigurationItemName.INVERSE_CURRENCY_RATE);
    }

    private Collection<String> getPropertyValues(Collection<Property> properties, Object instance) {
        ArrayList<String> values = new ArrayList<>();

        properties.forEach(
            property -> values.addAll(getPropertyValues(property, instance))
        );

        return values;
    }

    @SuppressWarnings("squid:S3776")
    private Collection<String> getPropertyValues(Property property, Object instance) {
        Object value = ReflectionUtils.getFieldValue(instance, property.getFieldName(), Object.class);
        if (value == null) {
            return Collections.emptyList();
        }

        if (property.isNested()) {
            return getPropertyValues(property.getNestedProperties(), value);
        }

        if (property.isMtow() && this.mtow.equals("kg")) {
            value = (Double)value * 907.185;
            property.setPrecision(0);
        }

        if (property.isDistance() && this.distance.equals("nm")) {
            value = (Double)value / 1.852;
        }

        if (property.isDate() && value instanceof TemporalAccessor) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(this.dateFormat);
            value = formatter.format((TemporalAccessor) value);
        }

        if (property.isDateTime() && value instanceof TemporalAccessor) {
            String dateTimeFormat = this.dateFormat + " HH:mm";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormat);
            value = formatter.format((TemporalAccessor) value);
        }

        if (property.isLatitude()) {
            value = convertCoordinates((Double)value, 0, " ", "latitude");
        }

        if (property.isLongitude()) {
            value = convertCoordinates((Double)value, 2, "", "longitude");
        }

        String stringValue = String.valueOf(value);

        Optional<Integer> precision = property.getPrecision();
        if (precision.isPresent()) {
            DecimalFormat format = new DecimalFormat();
            format.setMinimumFractionDigits(precision.get());
            format.setMaximumFractionDigits(precision.get());

            if (property.isInverse() && this.inverseRate.equalsIgnoreCase("t")) {
                stringValue = format.format(1 / (Double)value);
            } else {
                stringValue = format.format(value);
            }
        }

        Optional<String> format = property.getDateTimeFormat();
        if (format.isPresent()) {
            if (value instanceof TemporalAccessor) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format.get());
                stringValue = formatter.format((TemporalAccessor) value);
            } else if (value instanceof Date) {
                SimpleDateFormat formatter = new SimpleDateFormat(format.get());
                stringValue = formatter.format((Date) value);
            } else if (value instanceof Calendar) {
                SimpleDateFormat formatter = new SimpleDateFormat(format.get());
                formatter.setCalendar((Calendar) value);
                stringValue = formatter.format(((Calendar) value).getTime());
            }
        }

        return Collections.singletonList(stringValue);
    }

    /**
     * Computes the CSV header row and supplies it to the callback.
     *
     * @param callback A handler for consumption of the row
     */
    void mapHeaders(Consumer<Collection<String>> callback) {
        mapHeaders(callback, null);
    }

    /**
     * Computes the CSV header row and supplies it to the callback.
     *
     * @param callback     A handler for consumption of the row
     * @param placeholders A map of header name placeholders
     */
    void mapHeaders(Consumer<Collection<String>> callback, Map<String, String> placeholders) {
        callback.accept(PropertyParser.getHeaders(properties, placeholders));
    }

    /**
     * Computes the row(s) for an instance of the mapped type and supplies the result
     * to the callback.
     *
     * @param data     An instance of the mapped type
     * @param callback A handler for the consumption of the data
     */
    void mapData(T data, Consumer<Collection<String>> callback, boolean export) {
        ArrayList<String> rowTemplate = new ArrayList<>();

        Property collectionProperty = null;
        int collectionPosition = 0;

        for (Property property : properties) {
            if (!property.isCollection()) {
                Collection<String> collection = getPropertyValues(property, data);
                if (export && collection.isEmpty()) {
                    collection = Collections.singletonList("");
                }
                rowTemplate.addAll(collection);
                continue;
            }

            if (collectionProperty == null) {
                collectionProperty = property;
                collectionPosition = rowTemplate.size();
            }
        }

        if (collectionProperty == null) {
            callback.accept(rowTemplate);

            return;
        }

        Collection collection = ReflectionUtils.getFieldValue(data, collectionProperty.getFieldName(), Collection.class);

        for (Object item : collection) {
            ArrayList<String> row = new ArrayList<>(rowTemplate);

            row.addAll(
                collectionPosition,
                getPropertyValues(collectionProperty.getNestedProperties(), item)
            );

            callback.accept(row);
        }
    }

    private String convertCoordinates(double val, int round, String separator, String coordinate) {
        if (this.formatCoordinates.equals("degrees minutes seconds")) {

            double deg = Math.abs(val);
            // get component deg
            int degrees = (int) Math.floor(deg);
            // get component min
            int minutes = (int) Math.floor((deg * 3600) / 60) % 60;
            // get component sec & round/right-pad
            double seconds = round > 0 ? deg * 3600 % 60 : Math.round((deg * 3600 % 60));

            // check for rounding up
            if (seconds == 60) {
                seconds = 0;
                minutes++;
            }

            if (minutes == 60) {
                minutes = 0;
                degrees++;
            }

            // left-pad with leading zeros
            String coordinateString = ("000" + degrees);
            String degreesString = coordinateString.substring(coordinateString.length() - 3);
            // left-pad with leading zeros
            coordinateString = ("00" + minutes);
            String minutesString = coordinateString.substring(coordinateString.length() - 2);

            String secondsString;
            if (round > 0) {
                secondsString = String.format("%.2f", seconds);
            } else {
                secondsString = String.format("%.0f", seconds);
            }

            if (seconds < 10) {
                secondsString = "0" + secondsString;
            }

            String coordinates = String.format("%s%s%s%s%s", degreesString, separator, minutesString, separator, secondsString);
            return getLatLong(coordinates, coordinate, val);

        } else {
            return String.format("%.5f", val);
        }
    }

    private String getLatLong(String coordinates, String coordinate, double val) {
        if (coordinate.equals("latitude")) {
            return String.format("%s%s", coordinates.substring(1), (val < 0 ? 'S' : 'N'));
        } else {
            return String.format("%s%s", coordinates, (val < 0 ? 'W' : 'E'));
        }
    }
}
