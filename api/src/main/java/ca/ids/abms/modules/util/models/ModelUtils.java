package ca.ids.abms.modules.util.models;

import java.beans.FeatureDescriptor;
import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Stream;

import com.google.common.collect.Sets;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import ca.ids.abms.config.error.StaleVersionException;
import ca.ids.abms.modules.util.models.annotations.MergeOnNull;

public final class ModelUtils {

    // private ctor to prevent instantiation
    private ModelUtils() {
    }

    /**
     * Merges the non-{@code null} properties of two objects of the same type together.
     *
     * @param source         the object to pull properties from
     * @param target         the object to fill properties into
     * @param excludedFields an array of field names that will not have data copied from
     */
    public static void merge(Object source, Object target, String... excludedFields) {
        if (source != null && target != null) {
            ModelUtils.checkVersionIfComparables(source, target);
            excludedFields = Stream.concat(Arrays.stream(getNullFieldNames(source)), Arrays.stream(excludedFields)).toArray(String[]::new);

            BeanUtils.copyProperties(source, target, excludedFields);
        }
    }

    /**
     * Merges the non-{@code null} properties of two objects of the same type together.
     *
     * @param source         the object to pull properties from
     * @param target         the object to fill properties into
     * @param includedFields an array of field names that will have data copied from
     */
    public static void mergeOnly(Object source, Object target, String... includedFields) {
        if (source != null && target != null) {
            ModelUtils.checkVersionIfComparables(source, target);
            String[] excludedFields = Stream.concat(
                Arrays.stream(getNullFieldNames(source)), Arrays.stream(getExcludedFieldNames(source, includedFields))
            ).toArray(String[]::new);

            BeanUtils.copyProperties(source, target, excludedFields);
        }
    }

    /**
     * Merges properties of two objects.
     *
     * @param source         the object to pull properties from
     * @param target         the object to fill properties into
     * @param excludedFields an array of field names that will be excluded from merging
     */
    public static void mergeExcept(Object source, Object target, String... excludedFields) {
        if (source != null && target != null) {
            ModelUtils.checkVersionIfComparables(source, target);
            BeanUtils.copyProperties(source, target, excludedFields);
        }
    }

    /**
     * Get object property field names where value is `null` AND `@MergeOnNull(true)`
     * is not present.
     *
     * Adapted from: http://stackoverflow.com/a/32066155
     *
     * @param source object to check
     * @return field names to exclude
     */
    private static String[] getNullFieldNames(Object source) {
        BeanWrapper wrappedSource = new BeanWrapperImpl(source);

        return Stream.of(wrappedSource.getPropertyDescriptors())
            .map(FeatureDescriptor::getName)
            .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null
                && !mergeOnNull(source, propertyName))
            .toArray(String[]::new);
    }

    private static String[] getExcludedFieldNames(Object source, String[] includedFields) {
        BeanWrapper wrappedSource = new BeanWrapperImpl(source);

        HashSet<String> includedPropertyNames = Sets.newHashSet(includedFields);

        return Stream.of(wrappedSource.getPropertyDescriptors())
            .map(FeatureDescriptor::getName)
            .filter(propertyName -> !includedPropertyNames.contains(propertyName))
            .toArray(String[]::new);
    }

    public static void checkVersionIfComparables(final Object source, final Object target) {
        if (source instanceof Versioned && target != null && (source.getClass().equals(target.getClass())
            || source.getClass().equals(target.getClass().getSuperclass()))) {
            final Comparable srcVersion = ((Versioned)source).getVersion();
            final Comparable tarVersion = ((Versioned)target).getVersion();
            if (srcVersion != null && tarVersion != null && srcVersion.compareTo(tarVersion) < 0) {
                throw new StaleVersionException(
                    "The object" +
                    " " + source.getClass().getSimpleName() + " " +
                    "has a stale version" +
                    " " + srcVersion + " " +
                    "against the current version" +
                    " " + tarVersion);
            }
        }
    }

    /**
     * Check if property value is annotated with `@MergeOnNull(true)`.
     *
     * @param source object containing property
     * @param name property name to check
     * @return true if `@MergeOnNull(true)`
     */
    private static boolean mergeOnNull(Object source, String name) {

        // try and get property annotation `MergeOnNull` value, default false
        boolean mergeOnNull = false;
        try {
            if (source.getClass().getDeclaredField(name).isAnnotationPresent(MergeOnNull.class))
                mergeOnNull = source.getClass().getDeclaredField(name).getAnnotation(MergeOnNull.class).value();
        } catch (NoSuchFieldException ignored) {
            // ignore exception and continue
        }
        return mergeOnNull;
    }
}
