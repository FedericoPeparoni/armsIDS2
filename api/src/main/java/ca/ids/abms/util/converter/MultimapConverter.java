package ca.ids.abms.util.converter;

import ca.ids.abms.util.MultimapUtil;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * `com.google.common.collect.Multimap` conversion utility class. Uses `ListMultimap`
 * to maintain iteration order when mapping from `String` to `Multimap`.
 */
public class MultimapConverter {

    private static final String DEFAULT_ENTRY_SEPARATOR = ",";

    private static final String DEFAULT_VALUE_SEPARATOR = "->";

    /**
     * Apply a multimap to join into ',' separated entries of '->' separated key-value pairs as a string.
     *
     * @return string representation of map or null if map is empty
     * @throws IllegalArgumentException when applied map is null
     */
    public static Function<Multimap<String, Integer>, String> join() {
        return join(DEFAULT_ENTRY_SEPARATOR, DEFAULT_VALUE_SEPARATOR);
    }

    /**
     * Apply a multimap to join into delimiter separated entries of delimiter separated key-value pairs as a string.
     *
     * @param entrySeparator delimiter for each entry
     * @param valueSeparator delimiter for key-value pairs
     * @return string representation of map or null if map is empty
     * @throws IllegalArgumentException when applied map is null
     */
    public static Function<Multimap<String, Integer>, String> join(final String entrySeparator, final String valueSeparator) {
        return multimap -> multimapToString(multimap, entrySeparator, valueSeparator);
    }

    /**
     * Apply a string of ',' separated entries of '->' separated key-value pairs into a multimap.
     *
     * @return map representation of string or empty map if string is empty
     * @throws IllegalArgumentException when applied string is null
     */
    public static Function<String, Multimap<String, Integer>> split() {
        return split(DEFAULT_ENTRY_SEPARATOR, DEFAULT_VALUE_SEPARATOR);
    }

    /**
     * Apply a string of delimited separated entries of delimited key-value pairs into a multimap.
     *
     * @param entrySeparator delimiter for each entry
     * @param valueSeparator delimiter for key-value pairs
     * @return map representation of string or empty map if string is empty
     * @throws IllegalArgumentException when applied string is null
     */
    public static Function<String, Multimap<String, Integer>> split(final String entrySeparator, final String valueSeparator) {
        return str -> stringToMultimap(str, entrySeparator, valueSeparator);
    }

    private static String multimapToString(Multimap<String, Integer> multimap, final String entrySeparator, final String valueSeparator) {
        if (multimap == null)
            throw new IllegalArgumentException("Argument 'multimap' cannot be null.");

        if (multimap.isEmpty())
            return null;

        return multimap.entries().stream()
            .map(e -> e.getKey() + valueSeparator + e.getValue())
            .collect(Collectors.joining(entrySeparator));
    }

    private static Multimap<String, Integer> stringToMultimap(final String str, final String entrySeparator, final String valueSeparator) {
        if (str == null)
            throw new IllegalArgumentException("Argument 'str' cannot be null.");

        if (str.trim().isEmpty())
            return ImmutableMultimap.of();
        
        return Stream.of(str.split(Pattern.quote(entrySeparator)))
            .map(e -> e.split(Pattern.quote(valueSeparator), 2))
            .collect(MultimapUtil.toImmutableListMultimap(e -> e[0], e -> e.length > 1 ? Integer.parseInt(e[1]) : 0));
    }

    private MultimapConverter() {
        throw new IllegalStateException("Utility class, do not instantiate a new instance.");
    }
}
