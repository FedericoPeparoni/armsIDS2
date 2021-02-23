package ca.ids.abms.util.stringmatcher;

import ca.ids.abms.modules.unspecified.UnspecifiedDepartureDestinationLocation;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class StringMatcherService {

    private static final Logger LOG = LoggerFactory.getLogger(StringMatcherService.class);

    private final StringMatcherTargetMapper stringMatcherTargetMapper;

    private final StringMatcherUtility stringMatcherUtility;

    public StringMatcherService(
        StringMatcherTargetMapper stringMatcherTargetMapper,
        StringMatcherUtility stringMatcherUtility
    ) {
        this.stringMatcherTargetMapper = stringMatcherTargetMapper;
        this.stringMatcherUtility = stringMatcherUtility;
    }

    /**
     * Find the top match, if any, from a list of objects.
     * A single string is used to match against an object and its specified attributes
     *
     * @param stringToMatch in list of strings;
     * @param uDDLs are the class to search, in this case UnspecifiedDepartureDestinationLocations;
     * @param matchThreshold the minimum score required for a successful match;
     * @param matchTargets is the number of targets on the class that are used to match against
     * @return matched object if any;
     */
    public UnspecifiedDepartureDestinationLocation getTopMatch(
        String stringToMatch,
        List<UnspecifiedDepartureDestinationLocation> uDDLs,
        Integer matchThreshold,
        Integer matchTargets
    ) {
        List<StringMatcherTarget> stringMatcherTargets = new ArrayList<>();

        for (UnspecifiedDepartureDestinationLocation uDDL : uDDLs) {
            stringMatcherTargets.add(stringMatcherTargetMapper.toStringMatcherTarget(uDDL));
        }

        StringMatcherTarget topMatch = getTopMatchFromTargets(
            stringToMatch,
            stringMatcherTargets,
            matchThreshold,
            matchTargets
        );

        if (topMatch != null) {
            return (UnspecifiedDepartureDestinationLocation) topMatch.getObject();
        } else {
            return null;
        }

    }

    /**
     * Find the top match, if any, from a list of strings.
     * A single string is used to match against an object and its specified attributes
     *
     * @param stringToMatch in list of strings;
     * @param stringMatcherTargets are the list of mapped string matcher target objects
     * @param matchThreshold the minimum score required for a successful match;
     * @param matchTargets is the number of targets on the class that are used to match against
     * @return top match if found
     */
    private StringMatcherTarget getTopMatchFromTargets(
        String stringToMatch,
        List<StringMatcherTarget> stringMatcherTargets,
        Integer matchThreshold,
        Integer matchTargets
    )  {
        List<String> stringMatcherTargetStrings;

        LOG.debug("getTopMatchFromTargets: string to match: {}, string match targets: {}, match threshold: {}, match targets: {}",
            stringToMatch,
            stringMatcherTargets != null ? stringMatcherTargets.size() : 0,
            matchThreshold,
            matchTargets
        );

        // do not attempt to find matching string target if null or empty
        if (stringMatcherTargets == null || stringMatcherTargets.isEmpty())
            return null;

        // if multiple match targets are specified, add each to the list of strings to match against
        if (matchTargets == 2) {
            stringMatcherTargetStrings = stringMatcherTargets
                .stream()
                .flatMap(sMT -> Stream.of(sMT.getStringTargetOne(), sMT.getStringTargetTwo()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        } else {
            stringMatcherTargetStrings = stringMatcherTargets
                .stream()
                .map(StringMatcherTarget::getStringTargetOne)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        }

        // attempt to find top match or set to null if no elements found
        ExtractedResult topMatch;
        try {
            topMatch = stringMatcherUtility.getTopMatch(stringToMatch, stringMatcherTargetStrings);
        } catch (final NoSuchElementException ex) {
            topMatch = null;
        }

        // match must have a score greater than the match threshold
        if (topMatch != null && topMatch.getScore() > matchThreshold) {
            for (StringMatcherTarget sMT : stringMatcherTargets) {
                if (sMT.getStringTargetOne().equalsIgnoreCase(topMatch.getString())
                    || (sMT.getStringTargetTwo() != null && sMT.getStringTargetTwo().equalsIgnoreCase(topMatch.getString())
                )) {
                    LOG.debug("getTopMatch found result. Match string: {}, Match score: {}", topMatch.getString(), topMatch.getScore());
                    return sMT;
                }
            }
        }

        return  null;
    }
}
