package ca.ids.abms.modules.flight;

import ca.ids.abms.modules.common.dto.BulkLoaderSummary;
import ca.ids.abms.modules.common.services.AbstractDataImporter;
import ca.ids.abms.modules.dataimport.CsvImportServiceImp;
import ca.ids.abms.modules.dataimport.DataImportService;
import ca.ids.abms.modules.dataimport.DataImportType;
import ca.ids.abms.modules.system.SystemConfiguration;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.uploadedfiles.UploadedFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

import static ca.ids.abms.modules.common.controllers.BulkLoaderComponent.*;

@Component(DataImportType.Values.FLIGHT_SCHEDULE)
public class FlightScheduleImporter extends AbstractDataImporter {

    public static final DataImportType DATA_IMPORT_TYPE = DataImportType.FLIGHT_SCHEDULE;

    public static final String FLIGHT_SCHEDULE_LOCATION = "Flight schedule file location";

    private final Logger LOG = LoggerFactory.getLogger(FlightScheduleImporter.class);

    private SystemConfigurationService systemConfigurationService;

    @Autowired
    @Qualifier(DATA_IMPORT_SERVICE)
    private CsvImportServiceImp<FlightScheduleCsvViewModel> dataImportService;

    @Qualifier(FLIGHT_SCHEDULE_LOADER)
    private final FlightScheduleLoader bulkLoader;

    public FlightScheduleImporter(final UploadedFileService uploadedFileService,
                                final SystemConfigurationService systemConfigurationService,
                                final CsvImportServiceImp<FlightScheduleCsvViewModel> dataImportService,
                                final FlightScheduleLoader bulkLoader) {
        super(uploadedFileService, DATA_IMPORT_TYPE);
        this.systemConfigurationService = systemConfigurationService;
        this.dataImportService = dataImportService;
        this.bulkLoader = bulkLoader;
    }

    /**
     * Get flight schedule location string from system configuration.
     *
     * @return flight schedule location string
     */
    @Override
    public String getLocation() {
        SystemConfiguration item = systemConfigurationService.getOneByItemName(FLIGHT_SCHEDULE_LOCATION);
        if (item != null)
            return item.getCurrentValue();
        else
            return null;
    }

    /**
     * Parse file as flight schedule modal and load into flight schedule bulk loader.
     *
     * @param file file to bulk upload
     * @return bulk loader summary
     */
    @Override
    public BulkLoaderSummary upload(File file) {
        List<FlightScheduleCsvViewModel> result;
        try {
            result = dataImportService.parseFromFile(file,
                DataImportService.STRATEGY.BIND_BY_POSITION, FlightScheduleCsvViewModel.class);
        } catch (Exception ex) {
            LOG.error("Cannot read the file {} because: {}", file.getName(), ex.getMessage());
            return null;
        }
        return bulkLoader.bulkLoad(result, file);
    }
}
