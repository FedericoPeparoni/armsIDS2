package ca.ids.abms.modules.atcmovements;

import static ca.ids.abms.modules.common.controllers.BulkLoaderComponent.ATC_LOG_LOADER;
import static ca.ids.abms.modules.common.controllers.BulkLoaderComponent.DATA_IMPORT_SERVICE;

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

@Component(DataImportType.Values.ATC)
public class AtcMovementLogImporter extends AbstractDataImporter {

    public static final DataImportType DATA_IMPORT_TYPE = DataImportType.ATC;

    public static final String ATC_UPLOAD_LOCATION = "ATC log file location";

    private final Logger LOG = LoggerFactory.getLogger(AtcMovementLogImporter.class);

    private SystemConfigurationService systemConfigurationService;

    @Autowired
    @Qualifier(DATA_IMPORT_SERVICE)
    private CsvImportServiceImp<AtcMovementLogCsvViewModel> dataImportService;

    @Qualifier(ATC_LOG_LOADER)
    private final AtcMovementLogBulkLoader bulkLoader;

    public AtcMovementLogImporter(final UploadedFileService uploadedFileService,
                                  final SystemConfigurationService systemConfigurationService,
                                  final CsvImportServiceImp<AtcMovementLogCsvViewModel> dataImportService,
                                  final AtcMovementLogBulkLoader bulkLoader) {
        super(uploadedFileService, DATA_IMPORT_TYPE);
        this.systemConfigurationService = systemConfigurationService;
        this.dataImportService = dataImportService;
        this.bulkLoader = bulkLoader;
    }

    /**
     * Get atc movement log location string from system configuration.
     *
     * @return atc movement log location string
     */
    @Override
    public String getLocation() {
        SystemConfiguration item = systemConfigurationService.getOneByItemName(ATC_UPLOAD_LOCATION);
        if (item != null)
            return item.getCurrentValue();
        else
            return null;
    }

    /**
     * Parse file as atc movement log modal and load into atc movement log bulk loader.
     *
     * @param file file to bulk upload
     * @return bulk loader summary
     */
    @Override
    public BulkLoaderSummary upload(File file) {
        List<AtcMovementLogCsvViewModel> result;
        try {
            result = dataImportService.parseFromFile(file,
                DataImportService.STRATEGY.BIND_BY_POSITION, AtcMovementLogCsvViewModel.class);
        } catch (Exception ex) {
            LOG.error("Cannot read the file {} because: {}", file.getName(), ex.getMessage());
            return null;
        }
        return bulkLoader.bulkLoad(result, file);
    }
}
