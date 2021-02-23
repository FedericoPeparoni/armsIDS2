package ca.ids.abms.modules.radarsummary.parsers;

import ca.ids.abms.modules.radarsummary.RadarSummaryCsvViewModel;
import ca.ids.abms.modules.radarsummary.RadarSummaryRejectableCsvParser;
import ca.ids.abms.modules.radarsummary.enumerators.RadarSummaryFormat;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@SuppressWarnings("unused")
public class EurocatACsvParser extends RadarSummaryRejectableCsvParser {

    private static final String VALUE_DELIMITER = "\\|";

    private static final String LINE_INDICATOR = "^\\|.*$";

    /**
     * Use to parse Eurocat csv line items from a file.
     */
    public EurocatACsvParser() {
        super(RadarSummaryFormat.EUROCAT_A, VALUE_DELIMITER, LINE_INDICATOR);
    }

    /**
     * Parse line of fields and map to RadarSummaryCsvViewModel to be added to list of parsed lines.
     * Any exceptions will be added as a rejected item.
     */
    protected RadarSummaryCsvViewModel parseFields(String[] fields) {
        RadarSummaryCsvViewModel radarSummaryCSV = new RadarSummaryCsvViewModel();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyMMdd");
        LocalDate localDate = LocalDate.parse(fields[9].trim(), formatter);
        String strDate = localDate.format(formatter2);

        radarSummaryCSV.setDate(strDate);
        radarSummaryCSV.setFlightId(fields[1].trim());
        radarSummaryCSV.setDayOfFlight(strDate);
        radarSummaryCSV.setDepartureTime(getDepTime(fields[6]));
        radarSummaryCSV.setDestTime(null);
        radarSummaryCSV.setRegistration(fields[2].trim());
        radarSummaryCSV.setAircraftType(fields[3].trim());
        radarSummaryCSV.setDepartureAeroDrome(fields[4].trim());
        radarSummaryCSV.setDestinationAeroDrome(fields[5].trim());
        radarSummaryCSV.setRoute(fields[15].replace("+", " ").trim());
        final String[] entryPoint = getPointSafety(fields[7]);
        radarSummaryCSV.setFirEntryPoint(entryPoint[0]);
        radarSummaryCSV.setFirEntryTime(entryPoint[1]);
        final String[] exitPoint = getPointSafety(fields[8]);
        radarSummaryCSV.setFirExitPoint(exitPoint[0]);
        radarSummaryCSV.setFirExitTime(exitPoint[1]);
        radarSummaryCSV.setFlightRule(fields[11].trim());
        radarSummaryCSV.setFlightType(fields[12].trim());
        radarSummaryCSV.setFlightTravelCategory(fields[13].trim());

        radarSummaryCSV.setSegment(1);
        
        return radarSummaryCSV;
    }

    private String[] getPointSafety (final String field) {
        final String[] result = new String[2];
        if (field != null) {
            final String[] pointData = StringUtils.split(field,'/');
            if (pointData.length > 0) {
                result[0] = pointData[0].trim();
            }
            if (pointData.length > 1) {
                result[1] = pointData[1].trim();
            }
        }
        return result;
    }

    private String getDepTime(String field) {
        String depTime = field != null ? field.trim() : "";
        if (depTime.length() >= 4) {
            depTime = depTime.substring(0, 4);
        }
        return depTime;
    }
}
