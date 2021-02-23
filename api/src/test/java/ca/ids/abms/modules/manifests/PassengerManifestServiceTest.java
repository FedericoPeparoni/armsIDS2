package ca.ids.abms.modules.manifests;

import ca.ids.abms.modules.aircraft.AircraftType;
import ca.ids.abms.modules.aircraft.AircraftTypeRepository;

import ca.ids.abms.modules.dataimport.CsvImportServiceImp;
import ca.ids.abms.modules.dataimport.DataImportTestPojo;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.*;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class PassengerManifestServiceTest {

    private PassengerManifestService passengerManifestService;
    private PassengerManifestRepository passengerManifestRepository;
    private AircraftTypeRepository  aircraftTypeRepository;

    private CsvImportServiceImp<DataImportTestPojo> dataImportServiceCsv;

    @Test
    public void testConversionValidData() throws Exception {
        String csvMsg=readFileContentFromResource("/dataimport/csv-05-passenger-manifest-data.txt");
        List<?> result = dataImportServiceCsv.parseColumnCsvToObject(csvMsg, DataImportTestPojo.class);
        assertNotNull(result);
        assertTrue(result.size()>0);
    }

    @Before
    public void setup() {
        this.dataImportServiceCsv = new CsvImportServiceImp<>();
        this.aircraftTypeRepository = mock(AircraftTypeRepository.class);
        this.passengerManifestRepository = mock(PassengerManifestRepository.class);
        this.passengerManifestService = new PassengerManifestService(passengerManifestRepository,
            aircraftTypeRepository);
    }

    @Test
    public void create() throws Exception {
        AircraftType aircraftType = new AircraftType();
        aircraftType.setId(1);

        PassengerManifest manifest = new PassengerManifest();
        manifest.setDocumentNumber(2);
        manifest.setAircraftType(aircraftType);

        when(aircraftTypeRepository.getOne(any(Integer.class))).thenReturn(aircraftType);
        when(passengerManifestRepository.saveAndFlush(any(PassengerManifest.class))).thenReturn(manifest);

        PassengerManifest result = passengerManifestService.create(manifest);
        assertThat(result.getDocumentNumber() == manifest.getDocumentNumber());
        assertThat(result.getAircraftType().getId() == manifest.getAircraftType().getId());
    }

    @Test
    public void update() throws Exception {
        AircraftType aircraftType = new AircraftType();
        aircraftType.setId(1);

        PassengerManifest manifest = new PassengerManifest();
        manifest.setDocumentNumber(2);
        manifest.setAircraftType(aircraftType);

        AircraftType otherAircraftType = new AircraftType();
        otherAircraftType.setId(2);

        when(aircraftTypeRepository.getOne(any(Integer.class))).thenReturn(otherAircraftType);
        when(passengerManifestRepository.getOne(any(Integer.class))).thenReturn(manifest);

        PassengerManifest itemToUpdate = new PassengerManifest();
        itemToUpdate.setDocumentNumber(2);
        itemToUpdate.setAircraftType(otherAircraftType);
        itemToUpdate.setFlightId("123");
        when(passengerManifestRepository.saveAndFlush(any(PassengerManifest.class))).thenReturn(itemToUpdate);

        PassengerManifest result = passengerManifestService.update(2, itemToUpdate);
        assertThat(result.getDocumentNumber() == 2);
        assertThat(result.getAircraftType().getId() == 2);
        assertThat(result.getFlightId().equals("123"));
    }

    @Test
    public void findAll() throws Exception {
        AircraftType aircraftType = new AircraftType();
        aircraftType.setId(1);

        PassengerManifest manifest = new PassengerManifest();
        manifest.setDocumentNumber(2);
        manifest.setAircraftType(aircraftType);

        List<PassengerManifest> items = Collections.singletonList(manifest);

        when(passengerManifestRepository.findAll(any(Pageable.class)))
            .thenReturn(new PageImpl<>(items));

        final Sort sortingOpts = new Sort(
            new Sort.Order(Sort.Direction.DESC, "dateOfFlight"),
            new Sort.Order(Sort.Direction.ASC, "flightId"));
        Pageable pageable = new PageRequest(1, 20, sortingOpts);

        Page<PassengerManifest> results = passengerManifestService.findAll(pageable, false);
        assertThat(items.size() == results.getTotalElements());
        assertThat(items.get(0).getDocumentNumber() == results.getContent().get(0).getDocumentNumber());
        assertThat(items.get(0).getAircraftType().getId() == results.getContent().get(0).getAircraftType().getId());
    }

    @Test
    public void getOne() throws Exception {
        AircraftType aircraftType = new AircraftType();
        aircraftType.setId(1);

        PassengerManifest manifest = new PassengerManifest();
        manifest.setDocumentNumber(2);
        manifest.setAircraftType(aircraftType);

        when(passengerManifestRepository.getOne(any())).thenReturn(manifest);

        PassengerManifest result = passengerManifestService.getOne(1);
        assertThat(result.getDocumentNumber() == 2);
        assertThat(result.getAircraftType().getId() ==  1);
    }

    @Test
    public void delete() throws Exception {
        PassengerManifest item = new PassengerManifest();
        item.setDocumentNumber(2);
        when(passengerManifestRepository.getOne(any()))
            .thenReturn(item);
        passengerManifestService.delete(1);
        verify(passengerManifestRepository).delete(any(Integer.class));
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
