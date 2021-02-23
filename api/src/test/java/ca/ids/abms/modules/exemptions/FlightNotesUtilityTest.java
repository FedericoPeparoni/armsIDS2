package ca.ids.abms.modules.exemptions;

import org.junit.Test;
import static ca.ids.abms.modules.exemptions.FlightNotesUtility.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

public class FlightNotesUtilityTest {

    @Test
    public void testParseFlightNotes() {
        assertThat (parseFlightNotes (null)).isEmpty();
        assertThat (parseFlightNotes ("")).isEmpty();
        assertThat (parseFlightNotes ("   ")).isEmpty();
        assertThat (parseFlightNotes ("  ;; ;  ")).isEmpty();
        assertThat (parseFlightNotes ("   ;;  aaa  bbb  ; ;"))
            .containsExactly("aaa  bbb");
        assertThat (parseFlightNotes ("aaa;bbb;aaa;aaa;ccc;bbb"))
            .containsExactly("aaa", "bbb", "ccc");
    }

    @Test
    public void testMergeFlightNotes() {
        assertThat (mergeFlightNotes ("", Arrays.asList ("aaa", "bbb", "ccc")))
            .isEqualTo("aaa; bbb; ccc");
        assertThat (mergeFlightNotes ("  ; ", Arrays.asList ("aaa", "bbb", "ccc")))
            .isEqualTo("aaa; bbb; ccc");
        assertThat (mergeFlightNotes ("ccc;zzz", Arrays.asList ("aaa", "bbb", "ccc")))
            .isEqualTo("ccc; zzz; aaa; bbb");
        assertThat (mergeFlightNotes ("aaa AAA; bbb BBB", Arrays.asList ("bbb BBB", "ccc CCC")))
            .isEqualTo("aaa AAA; bbb BBB; ccc CCC");
    }
}
