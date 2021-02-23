package ca.ids.abms.modules.radarsummary.parsers;

import ca.ids.abms.modules.radarsummary.FlightTravelCategory;
import ca.ids.abms.modules.radarsummary.RadarSummaryCsvViewModel;
import ca.ids.abms.modules.radarsummary.RadarSummaryLoader;
import ca.ids.abms.modules.radarsummary.RadarSummaryRejectableCsvParser;
import ca.ids.abms.modules.radarsummary.enumerators.RadarSummaryFormat;
import ca.ids.abms.util.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Component
public class IntelcanAParser extends RadarSummaryRejectableCsvParser {

    private static final String VALUE_DELIMITER = " ";
    private static final String LINE_INDICATOR = null;
    private static final Logger LOG = LoggerFactory.getLogger(IntelcanAParser.class);

    /**
     * Use to parse Intelcan-A csv line items from a file.
     */
    public IntelcanAParser() {
        super(RadarSummaryFormat.INTELCAN_A, VALUE_DELIMITER, LINE_INDICATOR);
    }

    /**
     * Parse line of fields and map to RadarSummaryCsvViewModel to be added to list of parsed lines.
     * Any exceptions will be added as a rejected item.
     */
    protected RadarSummaryCsvViewModel parseFields(String[] fields) {
        RadarSummaryCsvViewModel radarSummaryCSV = new RadarSummaryCsvViewModel();
        String flightDate = null;
        String flightTime = null;

         if (StringUtils.isNotBlank(fields[10])) {
            flightDate = fields[10].trim();
        }
        if(StringUtils.isNotBlank(fields[0]))
            radarSummaryCSV.setFlightId(fields[0].trim());
        if(StringUtils.isNotBlank(fields[1]))
            radarSummaryCSV.setRegistration(fields[1].trim());
        if(StringUtils.isNotBlank(fields[2]))
            radarSummaryCSV.setAircraftType(fields[2].trim());
        if(StringUtils.isNotBlank(fields[3]))
            radarSummaryCSV.setDepartureAeroDrome(fields[3].trim());
        if (StringUtils.isNotBlank(fields[4]))
            radarSummaryCSV.setDepartureTime(fields[4].trim());
        if(StringUtils.isNotBlank(fields[5]))
            radarSummaryCSV.setDestinationAeroDrome(fields[5].trim());
        if(StringUtils.isNotBlank(fields[6]))
            radarSummaryCSV.setFlightRule(fields[6].trim());
        if(StringUtils.isNotBlank(fields[8]))
            radarSummaryCSV.setFlightTravelCategory(resolveFlightTravelCategory(fields[8].trim()));
        
        radarSummaryCSV.setDate(flightDate);
        radarSummaryCSV.setDayOfFlight(flightDate);
        radarSummaryCSV.setFirEntryPoint(fields[12]);
        radarSummaryCSV.setFirEntryTime(fields[13]);
        radarSummaryCSV.setFirExitPoint(fields[14]);
        radarSummaryCSV.setFirExitTime(fields[15]);

        radarSummaryCSV.setSegment(1);
        
        return radarSummaryCSV;
    }

    private String resolveFlightTravelCategory(String flightTravelCategory) {
        if(flightTravelCategory == null)
            return null;
        
        if (flightTravelCategory.equals("OVF")) {
            return FlightTravelCategory.OVERFLIGHT.getValue();
        }

        if (flightTravelCategory.equals("ARR")) {
            return FlightTravelCategory.ARRIVAL.getValue();
        }

        if (flightTravelCategory.equals("DEP")) {
            return FlightTravelCategory.DEPARTURE.getValue();
        }

        return null;
    }
}
