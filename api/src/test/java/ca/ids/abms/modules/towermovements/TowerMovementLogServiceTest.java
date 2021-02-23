package ca.ids.abms.modules.towermovements;

import ca.ids.abms.modules.aerodromes.AerodromeService;
import ca.ids.abms.modules.dataimport.CsvImportServiceImp;
import ca.ids.abms.modules.dataimport.DataImportService;
import ca.ids.abms.modules.flightmovements.FlightMovementService;
import ca.ids.abms.modules.unspecified.UnspecifiedDepartureDestinationLocationService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.*;
import org.springframework.mock.web.MockMultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class TowerMovementLogServiceTest {

    private TowerMovementLogService towerMovementLogService;
    private TowerMovementLogRepository towerMovementLogRepository;
    private FlightMovementService flightMovementService;
    private TowerMovementLogMapper towerMovementLogMapper;
    private AerodromeService aerodromeService;
    private CsvImportServiceImp<TowerMovementLogCsvViewModel> dataImportServiceCsv;

    @Before
    public void setup() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        this.towerMovementLogRepository = mock(TowerMovementLogRepository.class);
        this.flightMovementService= mock(FlightMovementService.class);
        this.dataImportServiceCsv = new CsvImportServiceImp<>();
        aerodromeService=mock(AerodromeService.class);
        Class<TowerMovementLogMapper> clazz = (Class<TowerMovementLogMapper>) Class.forName("ca.ids.abms.modules.towermovements.TowerMovementLogMapperImpl");
        this.towerMovementLogMapper = clazz.newInstance();
        this.towerMovementLogService = new TowerMovementLogService(aerodromeService, flightMovementService,
            mock(TowerMovementLogDepartureEstimator.class), towerMovementLogRepository,
            mock(UnspecifiedDepartureDestinationLocationService.class));

        when(flightMovementService.isIcaoAerodrome(anyObject())).thenReturn(true);
    }

    @Test
    public void create() throws Exception {
        TowerMovementLog item = new TowerMovementLog();
        item.setDepartureAerodrome("CAP");
        item.setDestinationAerodrome("YOW");
        when(aerodromeService.checkAerodromeIdentifier(any())).thenReturn(item.getDepartureAerodrome());
        when(towerMovementLogRepository.saveAndFlush(any(TowerMovementLog.class))).thenReturn(item);

        TowerMovementLog result = towerMovementLogService.create(item);
         assertThat(result.getDepartureAerodrome()).isEqualTo(item.getDepartureAerodrome());
    }

    @Test
    public void update() throws Exception {
        TowerMovementLog item = new TowerMovementLog();
        item.setId(3);
        item.setDepartureAerodrome("CAP");
        item.setDestinationAerodrome("YOW");
        when(aerodromeService.checkAerodromeIdentifier(any())).thenReturn(item.getDepartureAerodrome());
        when(towerMovementLogRepository.getOne(any(Integer.class))).thenReturn(item);

        TowerMovementLog itemUpdated = new TowerMovementLog();
        itemUpdated.setId(3);
        itemUpdated.setDepartureAerodrome("CAP");
        itemUpdated.setDestinationAerodrome("YUL");

        when(towerMovementLogRepository.saveAndFlush(any(TowerMovementLog.class)))
            .thenReturn(itemUpdated);

        TowerMovementLog result = towerMovementLogService.update(3, itemUpdated);
        assertThat(result.getId()).isEqualTo(3);
        assertThat(result.getDestinationAerodrome()).isEqualTo("YUL");
    }

    @Test
    public void findAll() throws Exception {
        TowerMovementLog item = new TowerMovementLog();
        item.setId(3);

        List<TowerMovementLog> items = Collections.singletonList(item);

        when(towerMovementLogRepository.findAll(any(Pageable.class)))
            .thenReturn(new PageImpl<>(items));

        final Sort sortingOpts = new Sort(
            new Sort.Order(Sort.Direction.DESC, "dayOfFlight"),
            new Sort.Order(Sort.Direction.DESC, "departureTime"));
        Pageable pageable = new PageRequest(1, 20, sortingOpts);

        Page<TowerMovementLog> results = towerMovementLogService.findAll(pageable, null, null, null);
        assertThat(((long)items.size()) == results.getTotalElements());
    }

    @Test
    public void getOne() throws Exception {
        TowerMovementLog item = new TowerMovementLog();
        item.setId(3);

        when(towerMovementLogRepository.getOne(any())).thenReturn(item);

        TowerMovementLog result = towerMovementLogService.getOne(3);
        assertThat(result.getId()).isEqualTo(3);
    }

    @Test
    public void delete() throws Exception {
        TowerMovementLog item = new TowerMovementLog();
        item.setId(3);
        when(towerMovementLogRepository.getOne(any()))
            .thenReturn(item);
        towerMovementLogService.delete(3);
        verify(towerMovementLogRepository).delete(any(Integer.class));
    }

    @Test
    public void testBulkLoad() throws Exception {
        String csvMsg = readFileContentFromResource("/dataimport/csv-07-tower-movements-log.txt");
        MockMultipartFile file = new MockMultipartFile("data", "filename.txt", "text/plain", csvMsg.getBytes());
        List<TowerMovementLogCsvViewModel> result = dataImportServiceCsv.parseFromMultipartFile(file,
            DataImportService.STRATEGY.BIND_BY_POSITION, TowerMovementLogCsvViewModel.class);

        /* Test if the service is able to parse the CSV file */
        assertNotNull(result);
        assertTrue(result.size()==2);
        assertThat(result.get(0).getDateOfContact()).isEqualTo("1-Sep-15");
        assertThat(result.get(0).getDestinationContactTime()).isEqualTo("23:13");
        assertThat(result.get(1).getDateOfContact()).isEqualTo("02-Dec-16");
        assertThat(result.get(1).getDepartureContactTime()).isEqualTo("4:40");

        /* Test if the service is able to update and create items */

        TowerMovementLog itemToUpdate = new TowerMovementLog();
        itemToUpdate.setId(3);
        itemToUpdate.setFlightId("AAA01");
        itemToUpdate.setDateOfContact(LocalDateTime.of(16,12,24,0,0,0));
        itemToUpdate.setDestinationContactTime("2030");
        itemToUpdate.setDepartureAerodrome("CAP");
        itemToUpdate.setDestinationAerodrome("YOW");

        TowerMovementLog itemToCreate = new TowerMovementLog();
        itemToCreate.setId(4);
        itemToCreate.setFlightId("BBB02");
        itemToCreate.setDateOfContact(LocalDateTime.of(17,01,24,0,0,0));
        itemToCreate.setDestinationContactTime("0955");
        itemToCreate.setDepartureAerodrome("CAP");
        itemToCreate.setDestinationAerodrome("YOW");

        List<TowerMovementLog> items = new ArrayList<>();
        items.add(itemToUpdate);
        items.add(itemToCreate);
        when(aerodromeService.checkAerodromeIdentifier(any())).thenReturn(itemToCreate.getDepartureAerodrome());
        when(towerMovementLogRepository.findByFlightId("AAA01")).thenReturn(itemToUpdate);
        when(towerMovementLogRepository.getOne(3)).thenReturn(itemToCreate);
        when(towerMovementLogRepository.findByFlightId("BBB02")).thenReturn(null);

        towerMovementLogService.createOrUpdateByFlightId(itemToUpdate);
        towerMovementLogService.createOrUpdateByFlightId(itemToCreate);

        verify(towerMovementLogRepository, times(2)).saveAndFlush(any(TowerMovementLog.class));

    }

    private String readFileContentFromResource(String path) throws Exception {
        InputStream fileInputStream = readFileInputStreamFromResource(path);
        String data = dataImportServiceCsv.readFileContent(fileInputStream);
        return data;
    }

    private InputStream readFileInputStreamFromResource(String path) throws Exception {
        ClassPathResource cpr = new ClassPathResource(path);
        return cpr.getInputStream();
    }
}
