package ca.ids.abms.modules.common.controllers;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import ca.ids.abms.modules.common.dto.UploadReportViewModel;

public interface UploadReportComponent{

    ResponseEntity<List<UploadReportViewModel>> upload(final MultipartFile file);

    ResponseEntity<List<UploadReportViewModel>> upload(final MultipartFile file, final LocalDateTime startDate);

    ResponseEntity<List<UploadReportViewModel>> preUpload(final MultipartFile file, final LocalDateTime startDate);

    final static String DATA_IMPORT_SERVICE = "recordableRowCsvImporter";

    final static String FLIGHT_SCHEDULE_LOADER = "flightScheduleLoader";

    final static String AIRCRAFT_REGISTRATION_LOADER = "aircraftRegistrationLoader";
}
