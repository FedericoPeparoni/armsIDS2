package ca.ids.abms.util;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class MiscUtils {
    
    /** Returns the first non-null argument */
    @SafeVarargs
    public static <T> T nvl (final T... values) {
        for (int i = 0; i < values.length; ++i) {
            if (values[i] != null)
                return values[i];
        }
        return null;
    }
    
    /** Returns the first non-empty string argument */
    public static String evl (final String... values) {
        for (int i = 0; i < values.length; ++i) {
            if (StringUtils.isNotEmpty(values[i]))
                return values[i];
        }
        return null;
    }
    
    /**
     * Construct a page from a list of results.
     * The returned page will contain a sublist according to the pageable parameter.
     */
    public static <T> Page <T> page (final List <T> fullContent, final Pageable pageable) {
        final int startIndex = Math.min (fullContent.size(), pageable.getPageNumber() * pageable.getPageSize());
        final int endIndex = Math.min (fullContent.size(), startIndex + pageable.getPageSize());
        final List <T> content = fullContent.subList (startIndex, endIndex);
        return new PageImpl <T> (content, pageable, fullContent.size());
    }

    /**
     * Check whether 2 ints are equal; can handle NULL values correctly (as not equal)
     */
    public static boolean integerEquals (final Integer a, final Integer b) {
        if (a != null && b != null && a.equals(b)) {
            return true;
        }
        return false;
    }
}
