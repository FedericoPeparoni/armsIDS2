package ca.ids.abms.modules.dataimport;

import ca.ids.abms.modules.atcmovements.AtcMovementLogBulkLoader;
import ca.ids.abms.modules.atcmovements.AtcMovementLogCsvViewModel;
import ca.ids.abms.modules.atcmovements.AtcMovementLogImporter;
import ca.ids.abms.modules.charges.PassengerServiceChargeReturnImporter;
import ca.ids.abms.modules.common.dto.BulkLoaderSummary;
import ca.ids.abms.modules.common.enumerators.ItemLoaderResult;
import ca.ids.abms.modules.common.services.AbstractBulkLoader;
import ca.ids.abms.modules.common.services.AbstractDataImporter;
import ca.ids.abms.modules.flightmovements.FlightMovementService;
import ca.ids.abms.modules.locking.JobLockingService;
import ca.ids.abms.modules.radarsummary.RadarSummaryImporter;
import ca.ids.abms.modules.system.SystemConfiguration;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.modules.towermovements.TowerMovementLogImporter;
import ca.ids.abms.modules.uploadedfiles.UploadedFileService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.FileFilter;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({AbstractDataImporter.class, AbstractBulkLoader.class})
public class DataImportProcessorTest {

    @Mock
    private UploadedFileService uploadedFileService;

    @Mock
    private SystemConfigurationService systemConfigurationService;

    @Mock
    private CsvImportServiceImp<AtcMovementLogCsvViewModel> atcMovementLogCsvViewModelCsvImportServiceImp;

    @Mock
    private AtcMovementLogBulkLoader atcMovementLogBulkLoader;

    @Mock
    private FlightMovementService flightMovementService;

    private AtcMovementLogImporter atcMovementLogImporter;

    @Mock
    private PassengerServiceChargeReturnImporter passengerServiceChargeReturnImporter;

    @Mock
    private RadarSummaryImporter radarSummaryImporter;

    @Mock
    private TowerMovementLogImporter towerMovementLogImporter;

    @Mock
    private File directory;

    @Mock
    private File file;

    private DataImportProcessor dataImportProcessor;

    private BulkLoaderSummary bulkLoaderSummary;

    private List<BulkLoaderSummary> bulkLoaderSummaries;

    private SystemConfiguration systemConfiguration;

    private List<AtcMovementLogCsvViewModel> atcMovementLogCsvViewModels;

    @Mock
    private JobLockingService jobLockingService;

    @Before
    public void setup() throws Exception {

        // for testing abstract data importer
        atcMovementLogImporter = new AtcMovementLogImporter(uploadedFileService, systemConfigurationService,
            atcMovementLogCsvViewModelCsvImportServiceImp, atcMovementLogBulkLoader);

        // the rest of the importers are mock
        dataImportProcessor = new DataImportProcessor(systemConfigurationService, atcMovementLogImporter, passengerServiceChargeReturnImporter,
            radarSummaryImporter, towerMovementLogImporter, jobLockingService);

        doNothing().when(jobLockingService).check(anyString(), anyString());
        doNothing().when(jobLockingService).complete(anyString(), anyString());

        /* -------- MOCK IMPORTER METHODS -------- */

        bulkLoaderSummary = new BulkLoaderSummary();
        bulkLoaderSummary.incrementProcessed();
        bulkLoaderSummary.incrementProcessed();
        bulkLoaderSummary.incrementRejected();
        bulkLoaderSummary.update(flightMovementService, ItemLoaderResult.CREATED);
        bulkLoaderSummary.update(flightMovementService, ItemLoaderResult.UPDATED);

        bulkLoaderSummaries = new ArrayList<>();
        bulkLoaderSummaries.add(bulkLoaderSummary);

        when(passengerServiceChargeReturnImporter.doImport()).thenReturn(bulkLoaderSummaries);
        when(radarSummaryImporter.doImport()).thenReturn(bulkLoaderSummaries);
        when(towerMovementLogImporter.doImport()).thenReturn(bulkLoaderSummaries);

        /* -------- MOCK SYSTEM CONFIGURATION METHODS -------- */

        systemConfiguration = new SystemConfiguration();
        systemConfiguration.setId(1);
        systemConfiguration.setItemName(AtcMovementLogImporter.ATC_UPLOAD_LOCATION);
        systemConfiguration.setDefaultValue("file:///c:/testing");
        systemConfiguration.setCurrentValue("file:///c:/testing");

        when(systemConfigurationService.getOneByItemName(eq(AtcMovementLogImporter.ATC_UPLOAD_LOCATION)))
            .thenReturn(systemConfiguration);

        systemConfiguration = new SystemConfiguration();
        systemConfiguration.setId(2);
        systemConfiguration.setItemName(SystemConfigurationItemName.AUTOMATED_UPLOAD_ENABLED);
        systemConfiguration.setDefaultValue("t");
        systemConfiguration.setCurrentValue("t");

        when(systemConfigurationService.getOneByItemName(eq(SystemConfigurationItemName.AUTOMATED_UPLOAD_ENABLED)))
            .thenReturn(systemConfiguration);

        systemConfiguration = new SystemConfiguration();
        systemConfiguration.setId(3);
        systemConfiguration.setItemName(SystemConfigurationItemName.AUTOMATED_UPLOAD_SCHEDULE);
        systemConfiguration.setDefaultValue("0 0 * * * ?");
        systemConfiguration.setCurrentValue("0 0 * * * ?");

        when(systemConfigurationService.getOneByItemName(eq(SystemConfigurationItemName.AUTOMATED_UPLOAD_SCHEDULE)))
            .thenReturn(systemConfiguration);

        /* -------- MOCK DIRECTORY AND FILE CLASSES -------- */

        when(file.getName()).thenReturn("test.csv");
        when(file.getPath()).thenReturn("file:///c:/testing");
        when(file.lastModified()).thenReturn(1509651444L);
        when(file.canRead()).thenReturn(true);
        when(file.isFile()).thenReturn(true);
        when(file.exists()).thenReturn(true);

        File[] files = new File[]{ file };

        when(directory.exists()).thenReturn(true);
        when(directory.canRead()).thenReturn(true);
        when(directory.isDirectory()).thenReturn(true);
        when(directory.listFiles(any(FileFilter.class))).thenReturn(files);

        whenNew(File.class).withParameterTypes(URI.class).withArguments(any(URI.class))
            .thenReturn(directory);

        mockStatic(Files.class);
        when(Files.probeContentType(any(Path.class))).thenReturn("text/csv");

        /* -------- MOCK DATA IMPORT SERVICE METHODS -------- */

        AtcMovementLogCsvViewModel atcMovementLogCsvViewModel = new AtcMovementLogCsvViewModel();
        atcMovementLogCsvViewModel.setId(1);
        atcMovementLogCsvViewModel.setFlightId("ABC123");

        atcMovementLogCsvViewModels = new ArrayList<>();
        atcMovementLogCsvViewModels.add(atcMovementLogCsvViewModel);

        when(atcMovementLogCsvViewModelCsvImportServiceImp.parseFromFile(any(File.class),
            eq(DataImportService.STRATEGY.BIND_BY_POSITION), eq(AtcMovementLogCsvViewModel.class)))
            .thenReturn(atcMovementLogCsvViewModels);

        /* -------- MOCK UPLOADED FILE SERVICE METHODS -------- */

        when(uploadedFileService.shouldProcessFile(eq("test.csv"), eq(DataImportType.ATC), eq(1509651444L)))
            .thenReturn(true);

        /* -------- MOCK BULK LOAD METHODS -------- */

        when(atcMovementLogBulkLoader.bulkLoad(eq(atcMovementLogCsvViewModels), eq(file)))
            .thenReturn(bulkLoaderSummary);
    }

    @Test
    public void testProcessing() throws Exception{

        // assert that doProcess completes without any exceptions
        dataImportProcessor.doProcess();

        // verify that importers are called no more then once
        verify(passengerServiceChargeReturnImporter, times(1))
            .doImport();
        verify(radarSummaryImporter, times(1))
            .doImport();
        verify(towerMovementLogImporter, times(1))
            .doImport();

        // verify that system configuration service is called once for atc
        verify(systemConfigurationService, times(1))
            .getOneByItemName(eq(AtcMovementLogImporter.ATC_UPLOAD_LOCATION));

        // verify that import service parse from file is called once
        verify(atcMovementLogCsvViewModelCsvImportServiceImp, times(1))
            .parseFromFile(any(File.class), eq(DataImportService.STRATEGY.BIND_BY_POSITION),
                eq(AtcMovementLogCsvViewModel.class));

        // verify that bulk load method is called once for atc
        verify(atcMovementLogBulkLoader, times(1))
            .bulkLoad(eq(atcMovementLogCsvViewModels), any(File.class));
    }
}
