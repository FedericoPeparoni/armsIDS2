package ca.ids.abms.modules.localaircraftregistry;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import ca.ids.abms.modules.common.dto.BulkLoaderSummary;
import ca.ids.abms.modules.common.services.AbstractDataImporter;
import ca.ids.abms.modules.dataimport.CsvImportServiceImp;
import ca.ids.abms.modules.dataimport.DataImportService;
import ca.ids.abms.modules.dataimport.DataImportType;
import ca.ids.abms.modules.system.SystemConfiguration;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.uploadedfiles.UploadedFileService;

@Component(DataImportType.Values.LOCAL_AIRCRAFT_REGISTRY)
public class LocalAircraftRegistryImporter extends AbstractDataImporter {

    private static final DataImportType DATA_IMPORT_TYPE = DataImportType.LOCAL_AIRCRAFT_REGISTRY;

    private static final String LOCAL_AIRCRAFT_REGISTRY_UPLOAD_LOCATION = "Local aircraft registry file location";

    private static final Logger LOG = LoggerFactory.getLogger(LocalAircraftRegistryImporter.class);

    private SystemConfigurationService systemConfigurationService;

    private static final String DATA_IMPORT_SERVICE = "recordableRowCsvImporter";
    private static final String LOCAL_AIRCRAFT_REGISTRY_LOADER = "localAircraftRegistryLoader";

    @Autowired
    @Qualifier(DATA_IMPORT_SERVICE)
    private CsvImportServiceImp<LocalAircraftRegistryCsvViewModel> dataImportService;

    @Qualifier(LOCAL_AIRCRAFT_REGISTRY_LOADER)
    private final LocalAircraftRegistryLoader bulkLoader;

    public LocalAircraftRegistryImporter(final UploadedFileService uploadedFileService,
                                         final SystemConfigurationService systemConfigurationService,
                                         final CsvImportServiceImp<LocalAircraftRegistryCsvViewModel> dataImportService,
                                         final LocalAircraftRegistryLoader bulkLoader) {

        super(uploadedFileService, DATA_IMPORT_TYPE);
        this.systemConfigurationService = systemConfigurationService;
        this.dataImportService = dataImportService;
        this.bulkLoader = bulkLoader;
    }

    /**
     * Get Local Aircraft Registry location string from system configuration.
     *
     * @return local aircraft registry location string
     */
    @Override
    public String getLocation() {
        SystemConfiguration item = systemConfigurationService.getOneByItemName(LOCAL_AIRCRAFT_REGISTRY_UPLOAD_LOCATION);
        if (item != null)
            return item.getCurrentValue();
        else
            return null;
    }

    /**
     * Parse file as local aircraft registry modal and load into local aircraft registry bulk loader.
     *
     * @param file file to bulk upload
     * @return bulk loader summary
     */
    @Override
    public BulkLoaderSummary upload(File file) {
        List<LocalAircraftRegistryCsvViewModel> result;
        try {
            result = dataImportService.parseFromFile(file,
                DataImportService.STRATEGY.BIND_BY_POSITION, LocalAircraftRegistryCsvViewModel.class);
        } catch (Exception ex) {
            LOG.error("Cannot read the file {} because: {}", file.getName(), ex.getMessage());
            return null;
        }
        return bulkLoader.bulkLoad(result, file);
    }

}
