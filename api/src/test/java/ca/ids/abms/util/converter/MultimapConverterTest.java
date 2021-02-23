package ca.ids.abms.util.converter;

import com.google.common.collect.*;
import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.failBecauseExceptionWasNotThrown;

public class MultimapConverterTest {

    @Test
    public void joinTest() {

        // build string integer multimap
        Multimap<String, Integer> multimap = ImmutableListMultimap.<String, Integer>builder()
            .put("ABC", 1)
            .put("XYZ", 999)
            .build();

        // apply join to map and verify results with default separators
        assertThat(MultimapConverter.join().apply(multimap))
            .isEqualTo("ABC->1,XYZ->999");

        // apply join to map and verify results with custom separators
        assertThat(MultimapConverter.join("|", "=").apply(multimap))
            .isEqualTo("ABC=1|XYZ=999");

        // join empty map into string and verify null is returned
        assertThat(MultimapConverter.join().apply(ImmutableListMultimap.of()))
            .isEqualTo(null);
    }

    @Test
    public void splitTest() {

        // split string into map using default separators and verify
        Multimap<String, Integer> multimap = MultimapConverter.split()
            .apply("ABC->1,XYZ->999");

        assertThat(multimap.size())
            .isEqualTo(2);
        assertThat(multimap.containsEntry("ABC", 1))
            .isTrue();
        assertThat(multimap.containsEntry("XYZ", 999))
            .isTrue();

        // split string into map using custom separators and verify
        multimap = MultimapConverter.split("|", "=")
            .apply("ABC=1|XYZ=999");

        assertThat(multimap.size())
            .isEqualTo(2);
        assertThat(multimap.containsEntry("ABC", 1))
            .isTrue();
        assertThat(multimap.containsEntry("XYZ", 999))
            .isTrue();

        // split empty string into map and verify
        multimap = MultimapConverter.split()
            .apply(" ");

        assertThat(multimap.isEmpty())
            .isTrue();

        // split duplicates into map and verify
        multimap = MultimapConverter.split()
            .apply("ABC->100,XYZ->100,ABC->50");

        assertThat(multimap.size())
            .isEqualTo(3);
        assertThat(multimap.get("ABC").size())
            .isEqualTo(2);
        assertThat(multimap.get("XYZ").size())
            .isEqualTo(1);
        assertThat(multimap.containsEntry("ABC", 100))
            .isTrue();
        assertThat(multimap.containsEntry("XYZ", 100))
            .isTrue();
        assertThat(multimap.containsEntry("ABC", 50))
            .isTrue();
    }

    @Test
    public void exceptionTest() {

        // assert that IllegalArgumentException thrown when join is applied to `null`
        try {
            MultimapConverter.join().apply(null);
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException ex) {
            assertThat(ex.getMessage())
                .isEqualTo("Argument 'multimap' cannot be null.");
        }

        // assert that IllegalArgumentException thrown when split is applied to `null`
        try {
            MultimapConverter.split().apply(null);
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException ex) {
            assertThat(ex.getMessage())
                .isEqualTo("Argument 'str' cannot be null.");
        }
    }
}
