package ca.ids.abms.util.converter;

public enum DateTimeParserStrategy {

    DATE_PATTERNS_YEAR_FIRST(new String[]{
        "yyyy-MMM-dd", "yyyy-MMM-d", "yy-MMM-dd", "yy-MMM-d", "yy-MM-dd", "yyyy-MM-dd",
        "yyyyMMdd", "yyyy MM dd", "yyyy MMM dd", "yyyy MMM d",
        "yyMMdd", "yy MM dd", "yy MMM dd", "yy MMM d",
        "yyyy/MMM/dd", "yyyy/MMM/d", "yy/MMM/dd", "yy/MMM/d", "yy/MM/dd", "yyyy/MM/dd"
    }),
    DATE_PATTERNS_YEAR_LAST(new String[]{
        "dd-MMM-yyyy", "d-MMM-yyyy", "dd-MMM-yy", "d-MMM-yy", "dd-MM-yyyy", "dd-MM-yy",
        "ddMMyyyy", "dd MM yyyy", "dd MMM yyyy", "d MMM yyyy",
        "ddMMyy", "dd MM yy", "dd MMM yy", "d MMM yy",
        "dd/MMM/yyyy", "d/MMM/yyyy", "dd/MMM/yy", "d/MMM/yy", "dd/MM/yyyy", "dd/MM/yy"
    }),
    DATE_PATTERNS_ALL(new String[]{
            "yyyy-MMM-dd", "yyyy-MMM-d", "yy-MMM-dd", "yy-MMM-d", "yy-MM-dd", "yyyy-MM-dd",
            "yyyyMMdd", "yyyy MM dd", "yyyy MMM dd", "yyyy MMM d",
            "yyMMdd", "yy MM dd", "yy MMM dd", "yy MMM d",
            "yyyy/MMM/dd", "yyyy/MMM/d", "yy/MMM/dd", "yy/MMM/d", "yy/MM/dd", "yyyy/MM/dd",
            "dd-MMM-yyyy", "d-MMM-yyyy", "dd-MMM-yy", "d-MMM-yy", "dd-MM-yyyy", "dd-MM-yy",
            "ddMMyyyy", "dd MM yyyy", "dd MMM yyyy", "d MMM yyyy",
            "ddMMyy", "dd MM yy", "dd MMM yy", "d MMM yy",
            "dd/MMM/yyyy", "d/MMM/yyyy", "dd/MMM/yy", "d/MMM/yy", "dd/MM/yyyy", "dd/MM/yy"
        }),
    TIME_PATTERNS(new String[]{
        "HH:mm", "HH.mm", "HHmm", "HH mm",
        "H:mm", "H.mm", "Hmm", "H mm",
        "hh:mm a", "hh.mm a", "hhmm a", "hh mm a",
        "h:mm a", "h.mm a", "hmm a", "h mm a",
        "HH:mm:ss", "HH.mm.ss", "HHmmss", "HH mm ss",
        "H:mm:ss", "H.mm.ss", "Hmmss", "H mm ss",
        "hh:mm:ss a", "hh.mm.ss a", "hhmmss a", "hh mm ss a",
        "h:mm:ss a", "h.mm.ss a", "hmmss a", "h mm ss a"
    });

    
    
    private final String patterns[];

    DateTimeParserStrategy(final String patterns[]) {
        this.patterns = patterns;
    }

    public String[] getPatterns() { return patterns; }

}
