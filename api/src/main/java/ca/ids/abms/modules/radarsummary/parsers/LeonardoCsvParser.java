package ca.ids.abms.modules.radarsummary.parsers;

import ca.ids.abms.modules.radarsummary.FlightTravelCategory;
import ca.ids.abms.modules.radarsummary.RadarSummaryCsvViewModel;
import ca.ids.abms.modules.radarsummary.RadarSummaryRejectableCsvParser;
import ca.ids.abms.modules.radarsummary.enumerators.RadarSummaryFormat;
import ca.ids.abms.util.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

@Component
public class LeonardoCsvParser extends RadarSummaryRejectableCsvParser {

    private static final String VALUE_DELIMITER = ",";
    private static final String LINE_INDICATOR = null;
    private static final String FORMAT_HOUR_SINGLE = "yyyy-MM-dd H:mm";
    private static final String FORMAT_HOUR_DOUBLE = "yyyy-MM-dd HH:mm";
    private static final String FORMAT_SECONDS ="yyyy-MM-dd HH:mm:ss";

    /**
     * Use to parse Leonardo csv line items from a file.
     */
    public LeonardoCsvParser() {
        super(RadarSummaryFormat.LEONARDO, VALUE_DELIMITER, LINE_INDICATOR);
    }
  
    /**
     * Parse line of fields and map to RadarSummaryCsvViewModel to be added to list of parsed lines.
     * Any exceptions will be added as a rejected item.
     */
    public RadarSummaryCsvViewModel parseFields(String[] fields) {
        RadarSummaryCsvViewModel radarSummaryCSV = new RadarSummaryCsvViewModel();

        String flightDate = null;
        String flightTime = null;

        radarSummaryCSV.setFlightId(StringUtils.stripToNull(fields[0]));
        radarSummaryCSV.setAircraftType(StringUtils.stripToNull(fields[1]));
        radarSummaryCSV.setRegistration(StringUtils.stripToNull(fields[2]));
        // Flight date 3
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMAT_HOUR_DOUBLE);
        if (StringUtils.isNotBlank(fields[3])) {
            String departureDateTime = fields[3].trim();
                  
            if (departureDateTime.length() == 15) {
                formatter = DateTimeFormatter.ofPattern(FORMAT_HOUR_SINGLE);
            } else if(departureDateTime.length() == 19) {
                formatter = DateTimeFormatter.ofPattern(FORMAT_SECONDS);
            }
            LocalDate localDate = LocalDate.parse(departureDateTime, formatter);
            LocalTime localTime = LocalTime.parse(departureDateTime, formatter);
            
            flightDate = localDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            flightTime = localTime.format(DateTimeFormatter.ofPattern("HHmm"));
        }

        radarSummaryCSV.setDate(flightDate);
        radarSummaryCSV.setDayOfFlight(flightDate);
        radarSummaryCSV.setDepartureTime(flightTime);
        
        radarSummaryCSV.setDepartureAeroDrome(StringUtils.stripToNull(fields[4]));
        radarSummaryCSV.setDepartureTime(StringUtils.stripToNull(fields[5]));
       
        radarSummaryCSV.setDestinationAeroDrome(StringUtils.stripToNull(fields[8]));
        
       
        radarSummaryCSV.setFlightTravelCategory(resolveFlightTravelCategory(StringUtils.stripToNull(fields[27])));
        
        radarSummaryCSV.setFlightRule(StringUtils.stripToNull(fields[24]));
        
        // route is set from internal route field
        radarSummaryCSV.setRoute(StringUtils.stripToNull(fields[18]));
        radarSummaryCSV.setFlightType(StringUtils.stripToNull(fields[25]));
        
        // inbound point
        String point = StringUtils.stripToNull(fields[10]);
        String coordinate = StringUtils.stripToNull(fields[11]);
       if(validatePoint(point) == null) {
            radarSummaryCSV.setFirEntryPoint(coordinate);
        } else {
            radarSummaryCSV.setFirEntryPoint(point);
        } 
        radarSummaryCSV.setEntryCoordinate(coordinate);
        
        String firEntryDateTime = StringUtils.strip(fields[12]);
        if (firEntryDateTime != null) {
            if (firEntryDateTime.length() == 15) {
                formatter = DateTimeFormatter.ofPattern(FORMAT_HOUR_SINGLE);
            } else if(firEntryDateTime.length() == 19) {
                formatter = DateTimeFormatter.ofPattern(FORMAT_SECONDS);
            }
        
            LocalTime time = LocalTime.parse(firEntryDateTime, formatter);
        
            flightTime = time.format(DateTimeFormatter.ofPattern("HHmm"));
        
            radarSummaryCSV.setFirEntryTime(flightTime);
        }
        // outbound point
        point = StringUtils.stripToNull(fields[14]);
        coordinate = StringUtils.stripToNull(fields[15]);
        if(validatePoint(point) == null) {
            radarSummaryCSV.setFirExitPoint(coordinate);
        } else {
            radarSummaryCSV.setFirExitPoint(point);
        }
        radarSummaryCSV.setExitCoordinate(coordinate);
        
        radarSummaryCSV.setFirEntryFlightLevel(convertFlightLevel(StringUtils.stripToNull(fields[13])));
        radarSummaryCSV.setFirExitFlightLevel(convertFlightLevel(StringUtils.stripToNull(fields[17])));
               
        String firExitDateTime = StringUtils.stripToNull(fields[16]);
        if(firExitDateTime != null) {
            if (firExitDateTime.length() == 15) {
                formatter = DateTimeFormatter.ofPattern(FORMAT_HOUR_SINGLE);
            } else if(firExitDateTime.length() == 19) {
                formatter = DateTimeFormatter.ofPattern(FORMAT_SECONDS);
            }
            LocalTime time = LocalTime.parse(firExitDateTime, formatter);
        
            flightTime = time.format(DateTimeFormatter.ofPattern("HHmm"));
        
            radarSummaryCSV.setFirExitTime(flightTime);
        }
       
        radarSummaryCSV.setWakeTurb(StringUtils.stripToNull(fields[27]));
        
        // set requested flight level
        radarSummaryCSV.setFlightLevel(convertFlightLevel(StringUtils.stripToNull(fields[19])));
        
        radarSummaryCSV.setSegment(1);
        

        return radarSummaryCSV;
    }

    private String resolveFlightTravelCategory(final String flightTravelCategory) {
        if(flightTravelCategory == null) {
            return flightTravelCategory;
        }
        
        if (flightTravelCategory.equals("0")) {
            return FlightTravelCategory.ARRIVAL.getValue();
        }

        if (flightTravelCategory.equals("1")) {
            return FlightTravelCategory.OVERFLIGHT.getValue();
        }

        if (flightTravelCategory.equals("2")) {
            return FlightTravelCategory.DEPARTURE.getValue();
        }

        if (flightTravelCategory.equals("3")) {
            return FlightTravelCategory.DOMESTIC.getValue();
        }

        return null;
    }

    
    @SuppressWarnings("unused")
    private String resolveFirEntryPoint(final String entryPoint, final String depAerodrome, final String flightTravelCategory) {
        if (StringUtils.isNotBlank(validatePoint(entryPoint))) {           
            return entryPoint;
        } else if (StringUtils.isNotBlank(flightTravelCategory)
            && (flightTravelCategory.equals(FlightTravelCategory.DEPARTURE.getValue()) || flightTravelCategory.equals(FlightTravelCategory.DOMESTIC.getValue()))) {
            return depAerodrome;
        }
        return null;
    }

    @SuppressWarnings("unused")
    private String resolveFirExitPoint(final String exitPoint, final String destAerodrome, final String flightTravelCategory) {
        if (StringUtils.isNotBlank(validatePoint(exitPoint))) {
            return exitPoint;
        } else if (StringUtils.isNotBlank(flightTravelCategory)
            && (flightTravelCategory.equals(FlightTravelCategory.ARRIVAL.getValue()) || flightTravelCategory.equals(FlightTravelCategory.DOMESTIC.getValue()))) {
            return destAerodrome;
        }
        return null;
    }
    
    private String validatePoint(String point) {
        
            //exclude DYINB, DYOUT, DRDGE, RYWnn, LLnnn, and nnNnn.
        if(point != null && 
                (point.equalsIgnoreCase("DYINB") || 
                    point.equalsIgnoreCase("DYOUT") ||
                    Pattern.compile("RYW[0-9]{2}").matcher(point).matches() ||
                    Pattern.compile("LL[0-9]{3}").matcher(point).matches() ||
                    Pattern.compile("[0-9]{2}N[0-9]{2}").matcher(point).matches())
                )
            return null;
            
        return point;
    }
    
    private String convertFlightLevel(String fl) {
        if(StringUtils.isBlank(fl))
            return fl;
        // Convert to integer
        Integer feet = Integer.parseInt(fl);
        if(feet == null)
            return fl;
        
        return  "F" + String.format("%03d", feet/100);
    }
}
