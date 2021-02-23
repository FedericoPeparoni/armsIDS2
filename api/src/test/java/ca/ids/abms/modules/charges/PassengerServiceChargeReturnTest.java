package ca.ids.abms.modules.charges;

import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.modules.accounts.AccountService;
import ca.ids.abms.modules.dataimport.CsvImportServiceImp;
import ca.ids.abms.modules.flightmovements.FlightMovementService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class PassengerServiceChargeReturnTest {
    private PassengerServiceChargeReturnRepository passengerServiceChargeReturnRepository;
    private PassengerServiceChargeReturnService passengerServiceChargeReturnService;
    private CsvImportServiceImp<PassengerServiceChargeReturnCsvViewModel> dataImportServiceCsv;
    private FlightMovementService flightMovementService;
    private PassengerServiceChargeReturn passengerServiceChargeReturn;
    private AccountService accountService;
    private List<PassengerServiceChargeReturn> list;

    @Before
    public void setup() {
        passengerServiceChargeReturnRepository = mock(PassengerServiceChargeReturnRepository.class);
        this.dataImportServiceCsv = new CsvImportServiceImp<>();
        flightMovementService=mock(FlightMovementService.class);
        accountService=mock(AccountService.class);
        passengerServiceChargeReturnService = new PassengerServiceChargeReturnService(passengerServiceChargeReturnRepository,
                                                            flightMovementService, accountService);

        passengerServiceChargeReturn= new PassengerServiceChargeReturn();
        passengerServiceChargeReturn.setFlightId("TALP01");

        list = new ArrayList<>();
        list.add(passengerServiceChargeReturn);

    }

    @Test
    public void getAllPassengerServiceChargeReturns() {
        List<PassengerServiceChargeReturn> passengerServiceChargeReturns = Collections.singletonList(new PassengerServiceChargeReturn());

        when(passengerServiceChargeReturnRepository.findAll(any(FiltersSpecification.class), any(Pageable.class)))
        .thenReturn(new PageImpl<>(passengerServiceChargeReturns));

        Page<PassengerServiceChargeReturn> results = passengerServiceChargeReturnService.findAll(mock(Pageable.class), false, "", null);

        assertThat(results.getTotalElements()).isEqualTo(passengerServiceChargeReturns.size());
    }

    @Test
    public void getPassengerServiceChargeReturnById() {
        PassengerServiceChargeReturn passengerServiceChargeReturn = new PassengerServiceChargeReturn();
        passengerServiceChargeReturn.setId(1);

        when(passengerServiceChargeReturnRepository.getOne(any()))
        .thenReturn(passengerServiceChargeReturn);

        PassengerServiceChargeReturn result = passengerServiceChargeReturnService.getOne(1);
        assertThat(result).isEqualTo(passengerServiceChargeReturn);
    }

    @Test
    public void createPassengerServiceChargeReturn() throws Exception {
        PassengerServiceChargeReturn passengerServiceChargeReturn = new PassengerServiceChargeReturn();
        passengerServiceChargeReturn.setFlightId("int_arr_fn()");
        when(passengerServiceChargeReturnRepository.save(any(PassengerServiceChargeReturn.class)))
        .thenReturn(passengerServiceChargeReturn);

        PassengerServiceChargeReturn result = passengerServiceChargeReturnService.create(passengerServiceChargeReturn);
        assertThat(result.getFlightId()).isEqualTo(passengerServiceChargeReturn.getFlightId());
    }

    @Test
    public void deletePassengerServiceChargeReturn() {
        passengerServiceChargeReturnService.delete(1);
        verify(passengerServiceChargeReturnRepository).delete(any(Integer.class));
    }

    @Test
    public void testFindByUniqueKey(){
        //TestCase 1
        String flightID="  ";
        LocalDateTime dayOfFlight=null;
        String departureTime = null;
        PassengerServiceChargeReturn chargeReturn=passengerServiceChargeReturnService.findByUniqueKey(flightID,dayOfFlight,departureTime);
        Assert.assertNull(chargeReturn);

        //TestCase 2
        flightID="TALP01";
        dayOfFlight=null;
        departureTime = null;
        chargeReturn=passengerServiceChargeReturnService.findByUniqueKey(flightID,dayOfFlight,departureTime);
        Assert.assertNull(chargeReturn);

        //TestCase 3
        flightID="TALP01";
        dayOfFlight=LocalDateTime.now();
        departureTime = null;
        when(passengerServiceChargeReturnRepository.findByFlightIdAndDayOfFlight("TALP01",dayOfFlight)).thenReturn(list);
        chargeReturn=passengerServiceChargeReturnService.findByUniqueKey(flightID,dayOfFlight,departureTime);
        Assert.assertTrue(chargeReturn.getFlightId().equalsIgnoreCase("TALP01"));

      //TestCase 4
        flightID="TALP01";
        dayOfFlight=LocalDateTime.now();
        departureTime = "1230";
        when(passengerServiceChargeReturnRepository.findByFlightIdAndDayOfFlightAndDepartureTime("TALP01",dayOfFlight,"1230")).thenReturn(list);
        chargeReturn=passengerServiceChargeReturnService.findByUniqueKey(flightID,dayOfFlight,departureTime);
        Assert.assertTrue(chargeReturn.getFlightId().equalsIgnoreCase("TALP01"));
    }

    @Test
    public void testBulkLoad() throws Exception {
        ClassPathResource cpr = new ClassPathResource("/dataimport/csv-08-passenger-service-charge-return-data.txt");
        InputStream fileInputStream = cpr.getInputStream();
        String csvMsg = dataImportServiceCsv.readFileContent(fileInputStream);

        MockMultipartFile file = new MockMultipartFile("data", "filename.txt", "text/plain", csvMsg.getBytes());

        List<PassengerServiceChargeReturnCsvViewModel> result = dataImportServiceCsv.parseFromMultipartFile(
                file, PassengerServiceChargeReturnCsvViewModel.class);

        /* Test if the service is able to parse the CSV file */
        assertNotNull(result);
        assertEquals(2, result.size());
        assertThat(result.get(0).getDayOfFlight()).isEqualTo("01/03/2017");
        assertThat(result.get(0).getFlightId()).isEqualTo("BP043");
        assertThat(result.get(0).getJoiningPassengers()).isEqualTo(35);
        assertThat(result.get(0).getChargeableDomesticPassengers()).isEqualTo(35);

        assertThat(result.get(1).getDayOfFlight()).isEqualTo("02/03/2017");
        assertThat(result.get(1).getFlightId()).isEqualTo("BP042");
        assertThat(result.get(1).getJoiningPassengers()).isEqualTo(32);
        assertThat(result.get(1).getChildren()).isEqualTo(1);
        assertThat(result.get(1).getChargeableItlPassengers()).isEqualTo(31);
    }
}
