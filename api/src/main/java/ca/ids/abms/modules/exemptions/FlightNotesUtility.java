package ca.ids.abms.modules.exemptions;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import ca.ids.abms.modules.flightmovements.FlightMovement;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

public final class FlightNotesUtility {

    /**
     * Merge a list of notes into an existing semicolon-separated notes field.
     * Preserves order, but ignores duplicates.
     */
    static String mergeFlightNotes(final String oldNotesStr, final Collection<String> newNotes) {
        final Set <String> notes = FlightNotesUtility.parseFlightNotes (oldNotesStr);
        notes.addAll (newNotes);
        return StringUtils.join(notes.iterator(), "; ");
    }

    /**
     * Merge a list of notes into an existing flight movement notes field.
     * Preserves order, but ignores duplicates.
     */
    public static void mergeFlightNotes(final FlightMovement flightMovement, final Collection<String> notes) {
        Preconditions.checkArgument(flightMovement != null);

        // return immediately if notes is empty as nothing to add
        if (notes == null || notes.isEmpty()) return;

        // merge existing flight movement notes with new notes
        String result = mergeFlightNotes(flightMovement.getFlightNotes(), notes);

        // update flight movement flight notes field with result
        flightMovement.setFlightNotes(result);
    }

    /**
     * Merge a list of notes into an existing flight movement notes field.
     * Preserves order, but ignores duplicates.
     */
    public static void mergeFlightNotes(final FlightMovement flightMovement, final String... notes) {
        Preconditions.checkArgument(flightMovement != null);

        // return immediately if notes is empty as nothing to add
        if (notes == null || notes.length == 0) return;

        // merge existing flight movement notes with new notes
        mergeFlightNotes(flightMovement, Arrays.asList(notes));
    }

    // ----------------- private --------------

    /**
     * Parse semicolon-separated flight notes string into a set of distinct strings
     */
    static Set<String> parseFlightNotes (final String str) {
        if (StringUtils.isNotBlank(str)) {
            return Arrays.stream (SEP.split (str))
                    .filter (StringUtils::isNotBlank)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }
        return new LinkedHashSet<>();
    }
    private static final Pattern SEP = Pattern.compile("\\s*;+\\s*");

    private FlightNotesUtility() {}
}
