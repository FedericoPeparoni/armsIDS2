package ca.ids.abms.modules.aircraft;

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

@Component(DataImportType.Values.AIRCRAFT_REGISTRATION)
public class AircraftRegistrationImporter extends AbstractDataImporter {

    public static final DataImportType DATA_IMPORT_TYPE = DataImportType.AIRCRAFT_REGISTRATION;

    public static final String AIRCRAFT_REGISTRATION_LOCATION = "Aircraft registration file location";

    private final Logger LOG = LoggerFactory.getLogger(AircraftRegistrationImporter.class);

    private SystemConfigurationService systemConfigurationService;

    @Autowired
    @Qualifier(DATA_IMPORT_SERVICE)
    private CsvImportServiceImp<AircraftRegistrationCsvViewModel> dataImportService;

    @Qualifier(AIRCRAFT_REGISTRATION_LOADER)
    private final AircraftRegistrationLoader bulkLoader;

    public AircraftRegistrationImporter(final UploadedFileService uploadedFileService,
                                final SystemConfigurationService systemConfigurationService,
                                final CsvImportServiceImp<AircraftRegistrationCsvViewModel> dataImportService,
                                final AircraftRegistrationLoader bulkLoader) {
        super(uploadedFileService, DATA_IMPORT_TYPE);
        this.systemConfigurationService = systemConfigurationService;
        this.dataImportService = dataImportService;
        this.bulkLoader = bulkLoader;
    }

    /**
     * Get aircraft registration location string from system configuration.
     *
     * @return aircraft registration location string
     */
    @Override
    public String getLocation() {
        SystemConfiguration item = systemConfigurationService.getOneByItemName(AIRCRAFT_REGISTRATION_LOCATION);
        if (item != null)
            return item.getCurrentValue();
        else
            return null;
    }

    /**
     * Parse file as aircraft registration modal and load into aircraft registration bulk loader.
     *
     * @param file file to bulk upload
     * @return bulk loader summary
     */
    @Override
    public BulkLoaderSummary upload(File file) {
        List<AircraftRegistrationCsvViewModel> result;
        try {
            result = dataImportService.parseFromFile(file,
                DataImportService.STRATEGY.BIND_BY_POSITION, AircraftRegistrationCsvViewModel.class);
        } catch (Exception ex) {
            LOG.error("Cannot read the file {} because: {}", file.getName(), ex.getMessage());
            return null;
        }
        return bulkLoader.bulkLoad(result, file);
    }
}
