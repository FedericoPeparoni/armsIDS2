package ca.ids.abms.util.csv;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ca.ids.abms.util.ReflectionUtils;
import ca.ids.abms.util.csv.annotations.*;
import org.apache.commons.lang.StringUtils;

import static ca.ids.abms.util.ReflectionUtils.hasMatchingAnnotation;

/**
 * A static utility class for parsing a type's {@link Field}s into {@link Property} objects.
 *
 * @author Derek McKinnon
 */
public final class PropertyParser {

    private static final Pattern dynamicHeaderPattern = Pattern.compile("#\\{(.*)\\}");

    // Private constructor to prevent instantiation
    private PropertyParser() {
    }

    /**
     * Examines a given type with reflection to return a {@link Collection} of
     * {@link Property} objects that encapsulate {@link Field}s and optional
     * annotation information on them.
     *
     * @param clazz The target type to inspect
     * @return The collection of properties
     */
    static Collection<Property> parseProperties(Class<?> clazz) {
        Stream<Property> stream = ReflectionUtils.getDeclaredFields(clazz).stream()
            .filter(field -> !hasMatchingAnnotation(field, CsvIgnore.class, CsvIgnore::value))
            .map(PropertyParser::parseProperty);

        CsvPropertyOrder annotation = clazz.getDeclaredAnnotation(CsvPropertyOrder.class);

        if (annotation == null) {
            return stream.collect(Collectors.toList());
        }

        if (annotation.alphabetical()) {
            return stream
                .sorted((p1, p2) -> p1.getFieldName().compareTo(p2.getFieldName()))
                .collect(Collectors.toList());
        }

        String[] order = annotation.value();

        HashMap<String, Property> unorderedProperties = new HashMap<>();
        stream.forEach(p -> unorderedProperties.put(p.getFieldName(), p));

        ArrayList<Property> orderedProperties = new ArrayList<>();

        for (String fieldName : order) {
            Property property = unorderedProperties.remove(fieldName);
            if (property != null) {
                orderedProperties.add(property);
            }
        }

        // Add any remaining properties that were not mentioned in the order
        orderedProperties.addAll(unorderedProperties.values());

        return orderedProperties;
    }

    private static Property parseProperty(Field field) {
        Property property = new Property();
        property.setField(field);
        property.setFieldName(field.getName());
        property.setHeaderName(StringUtils.capitalize(field.getName().replaceAll(String.format("%s","(?<=[^A-Z])(?=[A-Z])"), " ")));

        if (ReflectionUtils.isCollection(field)) {
            property.setIsCollection(true);

            Class<?> clazz = ReflectionUtils.getGenericType(field);
            property.getNestedProperties().addAll(parseProperties(clazz));
        }

        CsvProperty annotation = field.getDeclaredAnnotation(CsvProperty.class);
        if (annotation != null) {
            if (annotation.nested() && !property.isCollection()) {
                Class<?> nestedType = field.getType();
                property.getNestedProperties().addAll(parseProperties(nestedType));
            } else {
                property.setHeaderName(annotation.value().isEmpty() ? property.getHeaderName() : annotation.value());
                property.setPrecision(annotation.precision() <= -1 ? null : annotation.precision());
                property.setDateTimeFormat(annotation.dateFormat().isEmpty() ? null : annotation.dateFormat());
                property.setDate(annotation.date());
                property.setDateTime(annotation.dateTime());
                property.setMtow(annotation.mtow());
                property.setDistance(annotation.distance());
                property.setLatitude(annotation.latitude());
                property.setLongitude(annotation.longitude());
                property.setInverse(annotation.inverse());
            }
        }

        return property;
    }

    /**
     * Computes the header names of all properties, expanding on nested properties
     * if present.
     *
     * @param properties The collection of properties to traverse
     * @return The collection of headers
     */
    public static Collection<String> getHeaders(Collection<Property> properties) {
        return getHeaders(properties, null);
    }

    /**
     * Computes the header names of all properties, expanding on nested properties
     * if present.
     *
     * @param properties   The collection of properties to traverse
     * @param placeholders The map of header name placeholders (can be null)
     * @return The collection of headers
     */
    static Collection<String> getHeaders(Collection<Property> properties, Map<String, String> placeholders) {
        ArrayList<String> headers = new ArrayList<>();

        properties.forEach(property -> headers.addAll(getHeaders(property, placeholders)));

        return headers;
    }

    private static Collection<String> getHeaders(Property property, Map<String, String> placeholders) {
        if (property.isNested()) {
            return getHeaders(property.getNestedProperties(), placeholders);
        }

        String headerName = property.getHeaderName();

        if (placeholders != null) {
            Matcher matcher = dynamicHeaderPattern.matcher(headerName);

            if (matcher.matches()) {
                String key = matcher.group(1);
                headerName = Optional.ofNullable(placeholders.get(key)).orElse(headerName);
            }
        }

        return Collections.singletonList(headerName);
    }
}
