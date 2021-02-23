package ca.ids.abms.modules.radarsummary;

import ca.ids.abms.modules.common.dto.BulkLoaderSummary;
import ca.ids.abms.modules.common.services.AbstractDataImporter;
import ca.ids.abms.modules.dataimport.CsvImportServiceImp;
import ca.ids.abms.modules.dataimport.DataImportService;
import ca.ids.abms.modules.dataimport.DataImportType;
import ca.ids.abms.modules.radarsummary.enumerators.RadarSummaryFormat;
import ca.ids.abms.modules.system.SystemConfiguration;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.uploadedfiles.UploadedFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static ca.ids.abms.modules.common.controllers.BulkLoaderComponent.*;

@Component(DataImportType.Values.RADAR)
public class RadarSummaryImporter extends AbstractDataImporter {

    private static final DataImportType DATA_IMPORT_TYPE = DataImportType.RADAR;

    private static final String RADAR_UPLOAD_LOCATION = "Radar summary file location";
    private static final String RADAR_UPLOAD_ADDRESS = "Radar summary IP address";
    private static final String RADAR_UPLOAD_PORT = "Radar summary port number";
    private static final String RADAR_FILE_FORMAT = "Radar flight strip format";

    private SystemConfigurationService systemConfigurationService;

    @Autowired
    @Qualifier(DATA_IMPORT_SERVICE)
    private CsvImportServiceImp<RadarSummaryCsvViewModel> dataImportService;

    @Qualifier(RADAR_SUMMARY_LOADER)
    private final RadarSummaryLoader bulkLoader;

    private final List<RadarSummaryRejectableCsvParser> rejectableCsvParsers;

    public RadarSummaryImporter(
        final UploadedFileService uploadedFileService, final SystemConfigurationService systemConfigurationService,
        final CsvImportServiceImp<RadarSummaryCsvViewModel> dataImportService, final RadarSummaryLoader bulkLoader,
        final List<RadarSummaryRejectableCsvParser> rejectableCsvParsers
    ) {
        super(uploadedFileService, DATA_IMPORT_TYPE);
        this.systemConfigurationService = systemConfigurationService;
        this.dataImportService = dataImportService;
        this.bulkLoader = bulkLoader;
        this.rejectableCsvParsers = rejectableCsvParsers;
    }

    /**
     * Get radar summary location string from system configuration.
     *
     * @return radar summary location string
     */
    @Override
    public String getLocation() {
        SystemConfiguration item = systemConfigurationService.getOneByItemName(RADAR_UPLOAD_LOCATION);
        if (item != null)
            return item.getCurrentValue();
        else
            return null;
    }

    /**
     * Get radar summary file format from system configuration.
     *
     * @return radar summary file format name
     */
    private RadarSummaryFormat getRadarFormat() {
        SystemConfiguration item = systemConfigurationService.getOneByItemName(RADAR_FILE_FORMAT);
        if (item != null && item.getCurrentValue() != null && !item.getCurrentValue().isEmpty())
            return RadarSummaryFormat.forName(item.getCurrentValue());
        else if (item != null && item.getDefaultValue() != null && !item.getDefaultValue().isEmpty())
            return RadarSummaryFormat.forName(item.getDefaultValue());
        else
            return RadarSummaryFormat.RAYTHEON_A;
    }

    /**
     * Get radar summary address for TCP/IP Socket to bind onto. Used in combination with a MOXA serial to
     * USB converter.
     *
     * @return address to bind socket onto, returns null if not set in system configuration
     */
    @Override
    public String getSocketAddress() {
        SystemConfiguration item = systemConfigurationService.getOneByItemName(RADAR_UPLOAD_ADDRESS);
        if (item != null)
            return item.getCurrentValue();
        else
            return null;
    }

    /**
     * Get radar summary port for TCP/IP Socket to bind onto. Used in combination with a MOXA serial to USB
     * converter.
     *
     * @return port to bind socket onto, returns 0 if not set in system configuration
     */
    @Override
    public int getSocketPort() {
        SystemConfiguration item = systemConfigurationService.getOneByItemName(RADAR_UPLOAD_PORT);
        if (item != null)
            return Integer.parseInt(item.getCurrentValue());
        else
            return 0;
    }

    /**
     * Parse file as radar summary modal and load into radar summary bulk loader. Format is defined in
     * system configuration and existing radar routes are merged.
     *
     * @param file file to bulk upload
     * @return bulk loader summary
     */
    @Override
    public BulkLoaderSummary upload(final File file) throws IOException {

        // look up rejectable csv parser by radar summary format
        RadarSummaryRejectableCsvParser parser = findRejectableCsvParser(this.getRadarFormat());

        // if no parser, use generic data import service and bind by position strategy
        List<RadarSummaryCsvViewModel> result = parser == null
            ? dataImportService.parseFromFile(file, DataImportService.STRATEGY.BIND_BY_POSITION, RadarSummaryCsvViewModel.class)
            : parser.parseFile(file);

        return bulkLoader.bulkLoad(result, file);
    }

    /**
     * Parse file as radar summary modal and load into radar summary bulk loader. Format is defined in
     * system configuration and existing radar routes are merged.
     *
     * @param multipartFile multipart file to bulk upload
     * @return bulk loader summary
     */
    public BulkLoaderSummary upload(final MultipartFile multipartFile) throws IOException {
        // US100976: merge waypoints by default
        return this.upload(multipartFile, this.getRadarFormat(), Boolean.TRUE);
    }

    /**
     * Parse file as radar summary modal and load into radar summary bulk loader.
     *
     * @param multipartFile multipart file to bulk upload
     * @param format multipart file radar format
     * @param mergeWaypoints true if merging existing waypoints
     * @return bulk loader summary
     */
    public BulkLoaderSummary upload(final MultipartFile multipartFile, final RadarSummaryFormat format, final Boolean mergeWaypoints) throws IOException {

        // look up rejectable csv parser by radar summary format
        RadarSummaryRejectableCsvParser parser = findRejectableCsvParser(format);

        // if no parser, use generic data import service and bind by position strategy
        List<RadarSummaryCsvViewModel> result = parser == null
            ? dataImportService.parseFromMultipartFile(multipartFile, DataImportService.STRATEGY.BIND_BY_POSITION, RadarSummaryCsvViewModel.class)
            : parser.parseFile(multipartFile);

        return bulkLoader.bulkLoad(result, multipartFile, mergeWaypoints);
    }

    private RadarSummaryRejectableCsvParser findRejectableCsvParser(final RadarSummaryFormat radarSummaryFormat) {

        // look up rejectable csv parser by radar summary format
        RadarSummaryRejectableCsvParser result = null;
        for (RadarSummaryRejectableCsvParser rejectableCsvParser : rejectableCsvParsers) {
            if (radarSummaryFormat == rejectableCsvParser.getRadarSummaryFormat()) {
                result = rejectableCsvParser;
                break;
            }
        }
        return result;
    }
}
