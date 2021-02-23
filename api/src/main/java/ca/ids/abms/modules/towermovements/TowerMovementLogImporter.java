package ca.ids.abms.modules.towermovements;

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

import static ca.ids.abms.modules.common.controllers.BulkLoaderComponent.DATA_IMPORT_SERVICE;
import static ca.ids.abms.modules.common.controllers.BulkLoaderComponent.TOWER_LOG_LOADER;

@Component(DataImportType.Values.TOWER)
public class TowerMovementLogImporter extends AbstractDataImporter {

    public static final DataImportType DATA_IMPORT_TYPE = DataImportType.TOWER;

    public static final String TOWER_UPLOAD_LOCATION = "Tower log file location";

    private final Logger LOG = LoggerFactory.getLogger(TowerMovementLogImporter.class);

    private SystemConfigurationService systemConfigurationService;

    @Autowired
    @Qualifier(DATA_IMPORT_SERVICE)
    private CsvImportServiceImp<TowerMovementLogCsvViewModel> dataImportService;

    @Qualifier(TOWER_LOG_LOADER)
    private final TowerMovementLogBulkLoader bulkLoader;

    public TowerMovementLogImporter(final UploadedFileService uploadedFileService,
                                    final SystemConfigurationService systemConfigurationService,
                                    final CsvImportServiceImp<TowerMovementLogCsvViewModel> dataImportService,
                                    final TowerMovementLogBulkLoader bulkLoader) {
        super(uploadedFileService, DATA_IMPORT_TYPE);
        this.systemConfigurationService = systemConfigurationService;
        this.dataImportService = dataImportService;
        this.bulkLoader = bulkLoader;
    }

    /**
     * Get tower movement log location string from system configuration.
     *
     * @return tower movement log location string
     */
    @Override
    public String getLocation() {
        SystemConfiguration item = systemConfigurationService.getOneByItemName(TOWER_UPLOAD_LOCATION);
        if (item != null)
            return item.getCurrentValue();
        else
            return null;
    }

    /**
     * Parse file as tower movement log modal and load into tower movement log bulk loader.
     *
     * @param file file to bulk upload
     * @return bulk loader summary
     */
    @Override
    public BulkLoaderSummary upload(File file) {
        List<TowerMovementLogCsvViewModel> result;
        try {
            result = dataImportService.parseFromFile(file,
                DataImportService.STRATEGY.BIND_BY_POSITION, TowerMovementLogCsvViewModel.class);
        } catch (Exception ex) {
            LOG.error("Cannot read the file {} because: {}", file.getName(), ex.getMessage());
            return null;
        }
        return bulkLoader.bulkLoad(result, file);
    }
}
