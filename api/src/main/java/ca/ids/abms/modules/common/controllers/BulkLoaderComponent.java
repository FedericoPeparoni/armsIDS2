package ca.ids.abms.modules.common.controllers;

import ca.ids.abms.modules.common.dto.BulkLoaderSummary;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

public interface BulkLoaderComponent {

    ResponseEntity<BulkLoaderSummary> upload (@RequestParam("file") final MultipartFile file, @RequestParam(required = false) Map<String, String> params);

    final static String DATA_IMPORT_SERVICE = "recordableRowCsvImporter";

    final static String ATC_LOG_LOADER = "atcMovementLogBulkLoader";

    final static String TOWER_LOG_LOADER = "towerMovementLogBulkLoader";

    final static String PASSENGER_CHARGE_RETURN_LOADER = "pscrBulkLoader";

    final static String PASSENGER_MANIFEST_LOADER = "manifestsBulkLoader";

    final static String RADAR_SUMMARY_LOADER = "radarSummaryLoader";

    final static String FLIGHT_SCHEDULE_LOADER = "flightScheduleLoader";

    final static String AIRCRAFT_REGISTRATION_LOADER = "aircraftRegistrationLoader";

}
