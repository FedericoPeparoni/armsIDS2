package ca.ids.abms.util.stringmatcher;

import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StringMatcherUtility {

    public ExtractedResult getTopMatch(String stringToMatch, List<String> stringsToMatchAgainst) {
        return FuzzySearch.extractOne(stringToMatch, stringsToMatchAgainst);
    }
}
