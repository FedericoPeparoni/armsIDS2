package ca.ids.abms.modules.atcmovements;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.ids.abms.modules.unspecified.UnspecifiedDepartureDestinationLocationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;

import ca.ids.abms.modules.aerodromes.AerodromeService;
import ca.ids.abms.modules.dataimport.DataImportService;
import ca.ids.abms.modules.dataimport.RejectableRowCsvImportServiceImpl;
import ca.ids.abms.modules.dataimport.XLSXToCSVConverter;
import ca.ids.abms.modules.flightmovements.FlightMovementService;
import ca.ids.abms.modules.system.SystemConfigurationService;

public class AtcMovementLogServiceTest {

    private AtcMovementLogService atcMovementLogService;
    private AtcMovementLogRepository atcMovementLogRepository;
    private FlightMovementService flightMovementService;
    private AerodromeService aerodromeService;
    private RejectableRowCsvImportServiceImpl<AtcMovementLogCsvViewModel> rejectableRowCsvImportService;

    private SystemConfigurationService systemConfigurationService;
    private List<AtcMovementLog> atcMovementLogHelperLst;

    @Before
    public void setup() {
        this.rejectableRowCsvImportService = new RejectableRowCsvImportServiceImpl<>();
        this.atcMovementLogRepository = mock(AtcMovementLogRepository.class);
        this.flightMovementService = mock(FlightMovementService.class);
        this.aerodromeService=mock(AerodromeService.class);
        this.systemConfigurationService=mock(SystemConfigurationService.class);
        this.atcMovementLogService = new AtcMovementLogService(aerodromeService, mock(AtcMovementLogDepartureEstimator.class),
            atcMovementLogRepository, flightMovementService, mock(UnspecifiedDepartureDestinationLocationService.class));

        when(flightMovementService.isIcaoAerodrome(anyObject())).thenReturn(true);

        // create a list of radar summary
        atcMovementLogHelperLst=new ArrayList<AtcMovementLog>();
        AtcMovementLog atcMovementLogHelper = new AtcMovementLog();
        atcMovementLogHelper.setFlightId("TAL001");
        atcMovementLogHelper.setDateOfContact(LocalDateTime.of(2017,03,20,23,50));
        atcMovementLogHelperLst.add(atcMovementLogHelper);
    }

    @Test
    public void testBulkLoad() throws Exception {

        /* Test if the service is able to parse the CSV file */
        String content = readFileContentFromResource("/dataimport/csv-06-atc-movements-log.txt");
        MockMultipartFile file = new MockMultipartFile("data", "filename.txt", "text/plain", content.getBytes());
        List<AtcMovementLogCsvViewModel> result = rejectableRowCsvImportService.parseFromMultipartFile(file,
            DataImportService.STRATEGY.BIND_BY_POSITION, AtcMovementLogCsvViewModel.class);

        assertNotNull(result);
        assertTrue(result.size() == 2); // asserts that header row is ignored
        assertThat(result.get(0).getDateOfContact()).isEqualTo("1-Sep-15");
        assertThat(result.get(0).getFirEntryTime()).isEqualTo("19:55");
        assertThat(result.get(1).getDateOfContact()).isEqualTo("02-Sep-15");
        assertThat(result.get(1).getFirExitTime()).isNullOrEmpty();

        /* Test if the service is able to parse the XLSX file */
        InputStream fileInputStream = readFileInputStreamFromResource("/dataimport/excel-01-atc-movement-log.xlsx");
        file = new MockMultipartFile("data", "filename.xlsx", XLSXToCSVConverter.APPLICATION_XLXS, fileInputStream);
        result = rejectableRowCsvImportService.parseFromMultipartFile(file,
            DataImportService.STRATEGY.BIND_BY_POSITION, AtcMovementLogCsvViewModel.class);

        assertNotNull(result);
        assertTrue(result.size() == 5); // asserts that header and empty rows are ignored
        assertThat(result.get(0).getDateOfContact()).isEqualTo("1-Feb-17");
        assertThat(result.get(0).getFirEntryTime()).isEqualTo("0726");
        assertThat(result.get(1).getDateOfContact()).isEqualTo("1-Feb-17");
        assertThat(result.get(1).getFirExitTime()).isNullOrEmpty();
        assertThat(result.get(2).getDateOfContact()).isEqualTo("2-Feb-17");
        assertThat(result.get(2).getDepartureTime()).isEqualTo("0804");

        /* Test if the service is able to update and create items */

        AtcMovementLog itemToUpdate = new AtcMovementLog();
        itemToUpdate.setId(3);
        itemToUpdate.setFlightId("AAA01");
        itemToUpdate.setDateOfContact(LocalDateTime.of(16,12,24,0,0,0));
        itemToUpdate.setFirExitTime("2030");
        itemToUpdate.setDepartureAerodrome("CAP");
        itemToUpdate.setDestinationAerodrome("YOW");

        AtcMovementLog itemToCreate = new AtcMovementLog();
        itemToCreate.setId(3);
        itemToCreate.setFlightId("BBB02");
        itemToCreate.setDateOfContact(LocalDateTime.of(17,01,24,0,0,0));
        itemToCreate.setFirExitTime("0955");
        itemToCreate.setDepartureAerodrome("CAP");
        itemToCreate.setDestinationAerodrome("YOW");

        List<AtcMovementLog> items = new ArrayList<>();
        items.add(itemToUpdate);
        items.add(itemToCreate);
        when(aerodromeService.checkAerodromeIdentifier(any())).thenReturn(itemToCreate.getDepartureAerodrome());
        when(atcMovementLogRepository.findByFlightIdAndDateOfContact(
            "AAA01", LocalDate.of(16,12,24)))
            .thenReturn(itemToUpdate);
        when(atcMovementLogRepository.findByFlightIdAndDateOfContact("BBB02", LocalDate.of(17,01,24)))
            .thenReturn(null);

        atcMovementLogService.createOrUpdate(itemToUpdate);
        atcMovementLogService.createOrUpdate(itemToCreate);

        verify(atcMovementLogRepository, times(2)).saveAndFlush((any(AtcMovementLog.class)));
    }

    @Test
    public void create() throws Exception {
        AtcMovementLog item = new AtcMovementLog();
        item.setDepartureAerodrome("CAP");
        item.setDestinationAerodrome("YOW");
        when(aerodromeService.checkAerodromeIdentifier(any())).thenReturn(item.getDepartureAerodrome());
        when(atcMovementLogRepository.saveAndFlush(any(AtcMovementLog.class))).thenReturn(item);

        AtcMovementLog result = atcMovementLogService.create(item);
         assertThat(result.getDepartureAerodrome()).isEqualTo(item.getDepartureAerodrome());
    }

    @Test
    public void update() throws Exception {
        AtcMovementLog item = new AtcMovementLog();
        item.setId(3);
        item.setDepartureAerodrome("CAP");
        item.setDestinationAerodrome("YOW");
        when(aerodromeService.checkAerodromeIdentifier(any())).thenReturn(item.getDepartureAerodrome());
        when(atcMovementLogRepository.getOne(any(Integer.class))).thenReturn(item);

        AtcMovementLog itemUpdated = new AtcMovementLog();
        itemUpdated.setId(3);
        itemUpdated.setDepartureAerodrome("CAP");
        itemUpdated.setDestinationAerodrome("YUL");

        when(atcMovementLogRepository.saveAndFlush(any(AtcMovementLog.class)))
            .thenReturn(itemUpdated);

        AtcMovementLog result = atcMovementLogService.update(3, itemUpdated);
        assertThat(result.getId()).isEqualTo(3);
        assertThat(result.getDestinationAerodrome()).isEqualTo("YUL");
    }

    @Test
    public void findAll() throws Exception {
        AtcMovementLog item = new AtcMovementLog();
        item.setId(3);

        List<AtcMovementLog> items = Collections.singletonList(item);

        when(atcMovementLogRepository.findAll(any(Pageable.class)))
            .thenReturn(new PageImpl<>(items));

        final Sort sortingOpts = new Sort(
            new Sort.Order(Sort.Direction.DESC, "dayOfFlight"),
            new Sort.Order(Sort.Direction.DESC, "departureTime"));
        Pageable pageable = new PageRequest(1, 20, sortingOpts);

        Page<AtcMovementLog> results = atcMovementLogService.findAll(pageable, null, null, null);
        assertThat(((long)items.size()) == results.getTotalElements());
    }

    @Test
    public void getOne() throws Exception {
        AtcMovementLog item = new AtcMovementLog();
        item.setId(3);

        when(atcMovementLogRepository.getOne(any())).thenReturn(item);

        AtcMovementLog result = atcMovementLogService.getOne(3);
        assertThat(result.getId()).isEqualTo(3);
    }

    @Test
    public void delete() throws Exception {
        AtcMovementLog item = new AtcMovementLog();
        item.setId(3);
        when(atcMovementLogRepository.getOne(any()))
            .thenReturn(item);
        atcMovementLogService.delete(3);
        verify(atcMovementLogRepository).delete(any(Integer.class));
    }



    @Test
    public void testFindAtcMovementLogByUniqueKey(){
        // Test Case 1
        String flightId=null;
        LocalDateTime dateOfContact=null;
        String firEntryTime=null;
        String firMidTime=null;
        String firExitTime=null;
        AtcMovementLog atcMovementLogResult= atcMovementLogService.findAtcMovementLogByUniqueKey(flightId,dateOfContact,firEntryTime,firMidTime,firExitTime);
        Assert.assertTrue(atcMovementLogResult==null);

        // Test Case 2
        flightId="TAL001";
        dateOfContact=LocalDateTime.of(2017,03,20,23,50);
        firEntryTime=null;
        firMidTime=null;
        firExitTime=null;
        when(atcMovementLogRepository.findByFlightIdAndDateOfContactAndFirEntryTimeAndFirMidTimeAndFirExitTimeOrderByDateOfContactDescFirEntryTimeDesc(eq(flightId),eq(dateOfContact),eq(firEntryTime),eq(firMidTime),eq(firExitTime))).thenReturn(atcMovementLogHelperLst);
        atcMovementLogResult= atcMovementLogService.findAtcMovementLogByUniqueKey(flightId,dateOfContact,firEntryTime,firMidTime,firExitTime);
        Assert.assertTrue(atcMovementLogResult!=null);
        Assert.assertTrue(atcMovementLogResult.getFlightId().equalsIgnoreCase(atcMovementLogHelperLst.get(0).getFlightId()));

        // Test Case 3:  We have all information but we have any AtcMovementLog
        flightId="TAL001";
        dateOfContact=LocalDateTime.of(2017,03,20,23,50);
        firEntryTime="1234";
        firMidTime=null;
        firExitTime=null;
        atcMovementLogResult= atcMovementLogService.findAtcMovementLogByUniqueKey(flightId,dateOfContact,firEntryTime,firMidTime,firExitTime);
        Assert.assertTrue(atcMovementLogResult==null);

        // Test Case 4:  We have all information but we have any RadarSummary
        flightId="TAL001";
        dateOfContact=LocalDateTime.of(2017,03,20,23,50);
        firEntryTime="1234";
        firMidTime="1334";
        firExitTime=null;
        atcMovementLogResult= atcMovementLogService.findAtcMovementLogByUniqueKey(flightId,dateOfContact,firEntryTime,firMidTime,firExitTime);
        Assert.assertTrue(atcMovementLogResult==null);

        // Test Case 5:  We have all information but we have any RadarSummary
        flightId="TAL001";
        dateOfContact=LocalDateTime.of(2017,03,20,23,50);
        firEntryTime="1234";
        firMidTime="1334";
        firExitTime="1434";
        atcMovementLogResult= atcMovementLogService.findAtcMovementLogByUniqueKey(flightId,dateOfContact,firEntryTime,firMidTime,firExitTime);
        Assert.assertTrue(atcMovementLogResult==null);
    }


    private String readFileContentFromResource(String path) throws Exception {
        InputStream fileInputStream = readFileInputStreamFromResource(path);
        return rejectableRowCsvImportService.readFileContent(fileInputStream);
    }

    private InputStream readFileInputStreamFromResource(String path) throws Exception {
        ClassPathResource cpr = new ClassPathResource(path);
        return cpr.getInputStream();
    }
}
