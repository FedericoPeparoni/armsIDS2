package ca.ids.abms.modules.radarsummary.enumerators;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum RadarSummaryFormat {
    EUROCAT_A("EUROCAT-A"),
    INDRA_REC("INDRA-REC"),
    RAYTHEON_A("RAYTHEON-A"),
    INTELCAN_A("INTELCAN-A"),
    LEONARDO("LEONARDO");

    private String name;

    RadarSummaryFormat(final String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static RadarSummaryFormat forName(final String name) {
        if (name != null)
            return map.get(name.toUpperCase());
        else
            return null;
    }

    private static final Map<String, RadarSummaryFormat> map =
        new HashMap<>();

    static {
        for (RadarSummaryFormat value : EnumSet.allOf(RadarSummaryFormat.class)) {
            map.put(value.getName().toUpperCase(), value);
        }
    }
}
