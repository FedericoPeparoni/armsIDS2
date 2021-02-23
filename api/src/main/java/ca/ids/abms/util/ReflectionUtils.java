package ca.ids.abms.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.FeatureDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * A static utility class that provides helper methods
 * for working with reflection without worrying about
 * exceptions or boilerplate.
 *
 * @author Derek McKinnon
 */
public final class ReflectionUtils {

    private static final Logger log = LoggerFactory.getLogger(ReflectionUtils.class);

    // Private ctor to prevent instantiation
    private ReflectionUtils() {
    }

    /**
     * Creates a new instance of the target class.
     * This class must have a no-argument constructor.
     *
     * @param clazz The target class type
     * @return An {@link Optional<T>} containing the new instance or null on failure
     */
    public static <T> Optional<T> newInstance(Class<T> clazz) {
        try {
            return Optional.ofNullable(clazz.newInstance());
        } catch (Exception e) {
            log.error("Could not create instance of class");
            log.error("Stack Trace: ", e);

            return Optional.empty();
        }
    }

    /**
     * Returns a {@link Collection} of {@link Field}s in the class.
     */
    public static Collection<Field> getDeclaredFields(Class<?> clazz) {
        return Arrays.asList(clazz.getDeclaredFields());
    }

    /**
     * Retrieves a {@link Field} object by name from the class.
     *
     * @param clazz     The target class type to inspect
     * @param fieldName The name of the field
     * @return An {@link Optional<Field>} containing the field instance or null on failure
     */
    public static Optional<Field> getDeclaredField(Class<?> clazz, String fieldName) {
        try {
            return Optional.ofNullable(clazz.getDeclaredField(fieldName));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Returns an {@link Object} value for the named field in a given instance.
     *
     * @param instance  The target class instance
     * @param fieldName The field name to retrieve the value from
     * @return The value stored in the field
     */
    public static Optional<Object> getFieldValue(Object instance, String fieldName) {
        try {
            Field field = getDeclaredField(instance.getClass(), fieldName).orElseThrow(Exception::new);
            field.setAccessible(true);

            return Optional.ofNullable(field.get(instance));
        } catch (Exception e) {
            log.error("Could not get field: {}", fieldName);
            log.error("Stack Trace: ", e);

            return Optional.empty();
        }
    }

    /**
     * Returns the value for the named field in a given instance, cast to the desired type.
     *
     * @param instance  The target class instance
     * @param fieldName The field name to retrieve the value from
     * @return The value stored in the field
     */
    public static <T> T getFieldValue(Object instance, String fieldName, Class<T> clazz) {
        Optional<Object> value = getFieldValue(instance, fieldName);
        return clazz.cast(value.orElse(null));
    }

    /**
     * Sets the specified value for the named field in a given instance.
     *
     * @param instance  The target class instance
     * @param fieldName The field name to set the value in
     * @param value     The value to store in the field
     */
    public static void setFieldValue(Object instance, String fieldName, Object value) {
        getDeclaredField(instance.getClass(), fieldName)
            .ifPresent(field -> {
                try {
                    field.setAccessible(true);
                    field.set(instance, value);
                } catch (Exception e) {
                    log.error("Could not set field: {} with value: {}", fieldName, value);
                    log.error("Stack Trace: ", e);
                }
            });
    }

    /**
     * Checks if a {@link Class} has the specified {@link Annotation} and if the annotation matches
     * the supplied {@link Predicate}.
     *
     * @param clazz     The class type to inspect
     * @param type      The target annotation type
     * @param predicate A function that verifies the matched annotation
     */
    public static <A extends Annotation> boolean hasMatchingAnnotation(Class<?> clazz, Class<A> type, Predicate<A> predicate) {
        A annotation = clazz.getDeclaredAnnotation(type);

        return annotation != null && predicate.test(annotation);
    }

    /**
     * Checks if a {@link Field} has the specified {@link Annotation} and if the annotation matches
     * the supplied {@link Predicate}.
     *
     * @param field     The field to inspect
     * @param type      The target annotation type
     * @param predicate A function that verifies the matched annotation
     */
    public static <A extends Annotation> boolean hasMatchingAnnotation(Field field, Class<A> type, Predicate<A> predicate) {
        A annotation = field.getDeclaredAnnotation(type);

        return annotation != null && predicate.test(annotation);
    }

    /**
     * Checks if a {@link Field} represents a {@link Collection}
     *
     * @param field The field to inspect
     */
    public static boolean isCollection(Field field) {
        return Collection.class.isAssignableFrom(field.getType());
    }

    /**
     * Gets the generic type of a Field
     *
     * @param field The field to inspect
     */
    public static Class<?> getGenericType(Field field) {
        Type type = field.getGenericType();

        if (type instanceof ParameterizedType) {
            Type[] types = ((ParameterizedType) type).getActualTypeArguments();
            type = types[0];
        }

        return (Class<?>) type;
    }

    public static void merge(Object source, Object target, String... excludedFields) {
        excludedFields = Stream.concat(Arrays.stream(getNullPropertyNames(source)), Arrays.stream(excludedFields)).toArray(String[]::new);

        BeanUtils.copyProperties(source, target, excludedFields);
    }

    // Copied from: http://stackoverflow.com/a/32066155
    private static String[] getNullPropertyNames(Object source) {
        final BeanWrapper wrappedSource = new BeanWrapperImpl(source);
        return Stream.of(wrappedSource.getPropertyDescriptors())
            .map(FeatureDescriptor::getName)
            .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null)
            .toArray(String[]::new);
    }
}
