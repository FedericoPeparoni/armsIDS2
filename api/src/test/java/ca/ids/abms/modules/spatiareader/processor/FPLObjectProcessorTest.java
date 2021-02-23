package ca.ids.abms.modules.spatiareader.processor;

import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.FlightMovementService;
import ca.ids.abms.modules.locking.JobLockingService;
import ca.ids.abms.modules.spatiareader.FlightPlansService;
import ca.ids.abms.modules.spatiareader.dto.FplObjectDto;
import ca.ids.abms.modules.spatiareader.entity.FplObject;
import ca.ids.abms.modules.spatiareader.mapper.CplFplMapper;
import ca.ids.abms.modules.system.SystemConfiguration;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.util.converter.JSR310DateConverters;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FPLObjectProcessorTest {

    @Mock
    private SystemConfigurationService systemConfigurationService;

    @Mock
    private FlightMovementService flightMovementService;

    private CplFplMapper cplFplMapper;

    @Mock
    private FlightPlansService flightPlansService;

    private FPLObjectProcessor fplObjectProcessor;

    @Mock
    private JobLockingService jobLockingService;

    private SystemConfiguration systemConfiguration;
    
    private static TimeZone savedTimeZone;
    
    @BeforeClass
    public static void init() {
        savedTimeZone = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }
    
    @AfterClass
    public static void cleanup() {
        TimeZone.setDefault(savedTimeZone);
    }

    @Before
    public void setup() throws Exception {
        cplFplMapper = new CplFplMapperImpl();
        fplObjectProcessor = new FPLObjectProcessor(flightMovementService, systemConfigurationService, cplFplMapper,
            flightPlansService, true, jobLockingService);

        systemConfiguration = new SystemConfiguration();
        systemConfiguration.setId(1);
        systemConfiguration.setItemName(FPLObjectProcessor.LAST_TIMESTAMP_ITEM_NAME);
        systemConfiguration.setDefaultValue("2017-01-01 00:00:00");

        when(systemConfigurationService.getOneByItemName(FPLObjectProcessor.LAST_TIMESTAMP_ITEM_NAME))
            .thenReturn(systemConfiguration);

        when(flightPlansService.getFplObjectsStartingFromDate(makeLocalDate("2017-01-01 00:00:00")))
            .thenReturn(getFlightObjects());

        when(flightPlansService.thereIsACronosPluginEnabled())
            .thenReturn(true);

        when(flightMovementService.createUpdateFlightMovementFromSpatia(any(FplObjectDto.class), eq (false)))
            .thenReturn(new FlightMovement());
        
        when(systemConfigurationService.update(systemConfiguration)).thenReturn (systemConfiguration);

        doNothing().when(jobLockingService).check(anyString(), anyString());
        doNothing().when(jobLockingService).complete(anyString(), anyString());
    }

    @Test
    public void testProcessing() {

        fplObjectProcessor.doProcess();
        verify(systemConfigurationService, times(2)).update(systemConfiguration);
        assertThat(systemConfiguration.getCurrentValue()).isEqualTo ("2017-04-05 03:02:01");

    }
    @Test
    public void testProcessingGenericError() throws Exception{

        when(flightMovementService.calculateFlightMovementFromFplObject(any(FlightMovement.class))).thenThrow(new RuntimeException("Generic error") );

        fplObjectProcessor.doProcess();
        verify(systemConfigurationService, times(2)).update(systemConfiguration);
        assertThat(systemConfiguration.getCurrentValue()).isEqualTo ("2017-04-05 03:02:01");

    }
    private List<FplObject> getFlightObjects() {
        ArrayList<FplObject> list = new ArrayList<>();
        FplObject fplObject = new FplObject();
        fplObject.setFlightId("TEST01");
        fplObject.setChildMessageMaxCatalogueDate(makeDate("2017-02-03 10:20:30"));
        list.add(fplObject);
        FplObject fplObject2 = new FplObject();
        fplObject2.setFlightId("TEST02");
        fplObject2.setChildMessageMaxCatalogueDate(makeDate("2017-04-05 03:02:01"));
        list.add(fplObject2);
        return list;
    }


    private Date makeDate(final String dateText) throws RuntimeException {
        Date date;
        try {
            date = new SimpleDateFormat(JSR310DateConverters.DEFAULT_PATTERN_DATE_TIME).parse(dateText);
        } catch (ParseException pe) {
            throw new RuntimeException("Warning! the default date time pattern has been changed and the configurations should be reviewed! "
                + "The code depends on this information and doesn't have to change!", pe);
        }
        return date;
    }

    private LocalDateTime makeLocalDate (final String localDateText) throws RuntimeException {
        try {
            return JSR310DateConverters.convertStringToLocalDateTime(localDateText, JSR310DateConverters.DEFAULT_PATTERN_DATE_TIME);
        } catch (DateTimeParseException dtpe) {
            throw new RuntimeException("Warning! the default date time pattern has been changed and the configurations should be reviewed! "
                + "The code depends on this information and doesn't have to change!", dtpe);

        }
    }

    public class CplFplMapperImpl extends CplFplMapper {
        @Override
        public FplObjectDto toFplDto(FplObject entity) {
            if ( entity == null ) {
                return null;
            }
            final FplObjectDto fplObjectDto_ = new FplObjectDto();
            fplObjectDto_.setChildMessageMaxCatalogueDate( mapJavaDateToLocalDateTime( entity.getChildMessageMaxCatalogueDate() ) );
            fplObjectDto_.setCatalogueFplObjectId( entity.getCatalogueFplObjectId() );
            fplObjectDto_.setCataloguePrcStatus( entity.getCataloguePrcStatus() );
            fplObjectDto_.setFlightId( entity.getFlightId() );
            fplObjectDto_.setFlightRules( entity.getFlightRules() );
            fplObjectDto_.setFlightType( entity.getFlightType() );
            fplObjectDto_.setAircraftType( entity.getAircraftType() );
            fplObjectDto_.setWakeTurb( entity.getWakeTurb() );
            fplObjectDto_.setDepartureAd( entity.getDepartureAd() );
            fplObjectDto_.setDepartureTime( entity.getDepartureTime() );
            fplObjectDto_.setMsgDepartureTime( entity.getMsgDepartureTime() );
            fplObjectDto_.setSpeed( entity.getSpeed() );
            fplObjectDto_.setFlightLevel( entity.getFlightLevel() );
            fplObjectDto_.setRoute( entity.getRoute() );
            fplObjectDto_.setDestinationAd( entity.getDestinationAd() );
            fplObjectDto_.setTotalEet( entity.getTotalEet() );
            fplObjectDto_.setArrivalAd( entity.getArrivalAd() );
            fplObjectDto_.setArrivalTime( entity.getArrivalTime() );
            fplObjectDto_.setOtherInfo( entity.getOtherInfo() );
            fplObjectDto_.setDayOfFlight( mapJavaDateToLocalDate( entity.getDayOfFlight() ) );
            fplObjectDto_.setRawFpl( entity.getRawFpl() );
            return fplObjectDto_;
        }
    }
}
