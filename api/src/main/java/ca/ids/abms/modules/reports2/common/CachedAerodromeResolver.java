package ca.ids.abms.modules.reports2.common;

import java.util.HashMap;
import java.util.Map;

public class CachedAerodromeResolver {
    
    private final ReportHelper reportHelper;
    private final Map <String, String> resolvedCodeCache = new HashMap<>();

    public CachedAerodromeResolver (final ReportHelper reportHelper) {
        this.reportHelper = reportHelper;
    }
    
    public String resolve (final String icaoCode, final String item18Code) {
        final StringBuilder keyBuf = new StringBuilder (16);
        if (icaoCode != null) {
            keyBuf.append (icaoCode);
        }
        keyBuf.append ("$");
        if (item18Code != null) {
            keyBuf.append (item18Code);
        }
        final String key = keyBuf.toString();
        String resolvedCode = resolvedCodeCache.get (key);
        if (resolvedCode == null) {
            resolvedCode = reportHelper.findAerodromeCode(icaoCode, item18Code);
            resolvedCodeCache.put (key, resolvedCode);
        }
        return resolvedCode;
    }
    
}
