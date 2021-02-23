package ca.ids.abms.modules.radarsummary;

import ca.ids.abms.modules.common.mappers.CustomDataMapper;
import ca.ids.abms.util.StringUtils;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by c.talpa on 13/01/2017.
 */
@Component("RadarSummaryCSVMapping")
public class RadarSummaryCSVMapping implements CustomDataMapper {

    private static final Logger LOG = LoggerFactory.getLogger(RadarSummaryCSVMapping.class);

    private static final short COLUMN_SIZE = 16;

    private static final short FLIGHT_TRAVEL_CATEGORY = 0;
    private static final short FLIGHT_IDENTIFIER = 1;
    private static final short DATE = 2;
    private static final short REGISTRATION = 3;
    private static final short FLIGHT_TYPE = 4;
    private static final short FLIGHT_RULE = 5;
    private static final short AIRCRAFT_TYPE = 6;

    private static final short DEST_AD = 9;
    private static final short DEST_TIME = 10;
    private static final short DEP_AD = 11;
    private static final short DEP_TIME = 12;

    private static final short ENTRY_POINT = 14;
    private static final short ENTRY_TIME = 15;

    private static final String WHITE_SPACE = " ";

    public RadarSummaryCsvViewModel convertStringArrayIntoCsvModel(String[] record) throws CsvRequiredFieldEmptyException, CsvConstraintViolationException, CsvDataTypeMismatchException {
        RadarSummaryCsvViewModel radarSummary = null;

        if(record!=null && record.length > COLUMN_SIZE) {
            radarSummary = new RadarSummaryCsvViewModel();

            radarSummary.setFlightTravelCategory(StringUtils.checkAndTrimString(record[FLIGHT_TRAVEL_CATEGORY]));
            radarSummary.setFlightId(StringUtils.checkAndTrimString(record[FLIGHT_IDENTIFIER]));
            radarSummary.setDate(StringUtils.checkAndTrimString(record[DATE]));
            radarSummary.setRegistration(checkRegistrationFromCsvFile(record[REGISTRATION]));
            radarSummary.setFlightType(StringUtils.checkAndTrimString(record[FLIGHT_TYPE]));
            radarSummary.setFlightRule(StringUtils.checkAndTrimString(record[FLIGHT_RULE]));
            radarSummary.setAircraftType(StringUtils.checkAndTrimString(record[AIRCRAFT_TYPE]));
            radarSummary.setDestinationAeroDrome(StringUtils.checkAndTrimString(record[DEST_AD]));
            radarSummary.setDepartureTime(StringUtils.checkAndTrimString(record[DEP_TIME]));
            radarSummary.setDepartureAeroDrome(StringUtils.checkAndTrimString(record[DEP_AD]));
            radarSummary.setDestTime(StringUtils.checkAndTrimString(record[DEST_TIME]));
            radarSummary.setFirEntryPoint(StringUtils.checkAndTrimString(record[ENTRY_POINT]));
            radarSummary.setFirEntryTime(StringUtils.checkAndTrimString(record[ENTRY_TIME]));
            radarSummary.setRoute(calculationRouteFromCsvFile(record));
            radarSummary.setFirExitPoint(checkExitPointFromCsvFile(record));
            radarSummary.setFirExitTime(checkExitTimeFromCsvFile(record));

            LOG.debug("Import RadarSummary from CSV file: " + radarSummary);
        }

        return radarSummary;
    }

    @Deprecated
    public List<RadarSummaryCsvViewModel> radarSummaryMapFromRecordList(List<String[]> recordList)
        throws CsvRequiredFieldEmptyException, CsvConstraintViolationException, CsvDataTypeMismatchException {
        List<RadarSummaryCsvViewModel> radarSummaryList=null;

        if(recordList!=null && recordList.size()>0){
            radarSummaryList=new ArrayList<>();
            Iterator<String[]> iterator = recordList.iterator();

            while (iterator.hasNext()) {
                radarSummaryList.add(convertStringArrayIntoCsvModel(iterator.next()));
            }
        }
        return radarSummaryList;
    }

    private String checkRegistrationFromCsvFile(String csvRegistration) {
        String registration = StringUtils.checkAndTrimString(csvRegistration);

        if(StringUtils.isStringIfNotNull(registration)) {
            return registration.split(StringUtils.WHITE_SPACE_REGEX)[0];
        }

        return null;
    }

    private String checkExitPointFromCsvFile(String[] record) {
        String result = null;

        for(int i = ENTRY_POINT; i < record.length; i = i + 2) {
            String exitPoint = StringUtils.checkAndTrimString(record[i]);
            if(StringUtils.isStringIfNotNull(exitPoint)) {
                result = exitPoint;
            } else {
                break;
            }
        }
        return result;
    }

    private String checkExitTimeFromCsvFile(String[] record) {
        String result = null;

        for(int i = ENTRY_TIME; i < record.length; i = i + 2){
            String exitTime = StringUtils.checkAndTrimString(record[i]);
            if(StringUtils.isStringIfNotNull(exitTime)){
                result = exitTime;
            } else {
                break;
            }
        }
        return result;
    }

    private String calculationRouteFromCsvFile(String[] record) {
        String route = null;

        if(record != null && record.length >= ENTRY_POINT) {
            route = "";
            String fixName = null;
            for(int i = ENTRY_POINT; i < record.length; i = i + 2){
                fixName = StringUtils.checkAndTrimString(record[i]);
                if(StringUtils.isStringIfNotNull(fixName)) {
                    route += fixName + WHITE_SPACE;
                } else {
                    break;
                }
            }
        }
        return StringUtils.checkAndTrimString(route);
    }
}
