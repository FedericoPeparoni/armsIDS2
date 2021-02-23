package ca.ids.abms.util;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMultimap;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collector;

/**
 * Add Java 8 stream support for Guava Multimap classes.
 */
public class MultimapUtil {

    /**
     * Java 8 support for collecting map as immutableListMultimap. Can be replaced by Guava 21.0
     * `ImmutableListMultimap.toImmutableListMultimap` method.
     */
    public static <T, K, V> Collector<T, ImmutableMultimap.Builder<K, V>, ImmutableMultimap<K, V>> toImmutableListMultimap(
        Function<? super T, ? extends K> keyMapper,
        Function<? super T, ? extends V> valueMapper
    ) {
        BiConsumer<ImmutableMultimap.Builder<K, V>, T> accumulator = (b, t) -> b
            .put(keyMapper.apply(t), valueMapper.apply(t));
        return Collector.of(ImmutableListMultimap.Builder::new, accumulator,
            (l, r) -> l.putAll(r.build()),ImmutableMultimap.Builder::build);
    }

    private MultimapUtil() {
        throw new IllegalStateException("Utility class, do not instantiate a new instance.");
    }
}
