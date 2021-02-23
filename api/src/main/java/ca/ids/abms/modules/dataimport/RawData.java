package ca.ids.abms.modules.dataimport;

import static ca.ids.abms.util.StringUtils.abbrev;

public class RawData {

    public static RawData createRow(final String[] result, final String initialData) {
        return new RawData(result, initialData, false);
    }

    public static RawData createAnEmptyRow() {
        return new RawData(null, null, false);
    }

    public static RawData createTheLastRow() {
        return new RawData(null, null, true);
    }

    public String[] getResult() {
        return result;
    }

    public boolean isEmptyLine() {
        return this.result == null || result.length == 0;
    }

    public boolean isEndOfData() {
        return this.endOfData;
    }

    public String getInitialData () {
        return initialData;
    }

    private String[] result;
    private String initialData;
    private boolean endOfData;

    private RawData(final String[] result, final String initialData, final boolean endOfData) {
        this.result = result;
        this.initialData = initialData;
        this.endOfData = endOfData;
    }

    @Override
    public String toString() {
        return "RawData{" +
            "result[" + (result != null ? result.length : 0) +
            "], initialData=\"" + abbrev (initialData) + "\"" +
            ", emptyLine=" + isEmptyLine() +
            ", endOfData=" + endOfData +
            '}';
    }
}
