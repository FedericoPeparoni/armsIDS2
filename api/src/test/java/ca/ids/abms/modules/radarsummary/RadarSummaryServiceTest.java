package ca.ids.abms.modules.radarsummary;

import ca.ids.abms.modules.aerodromes.AerodromeService;
import ca.ids.abms.modules.flightmovements.FlightMovementService;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementBuilderException;
import ca.ids.abms.modules.radarsummary.enumerators.RadarSummaryFormat;
import ca.ids.abms.modules.system.SystemConfiguration;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.util.billingcontext.BillingContext;
import ca.ids.abms.util.billingcontext.BillingContextKey;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by c.talpa on 05/01/2017.
 */
public class RadarSummaryServiceTest {

    private RadarSummaryRepository radarSummaryRepository;
    private RadarSummaryService radarSummaryService;
    private AerodromeService aerodromeService;

    private SystemConfigurationService systemConfigurationService;

    private SystemConfiguration systemConfiguration;
    private List<RadarSummary> radarSummaryList;

    @Before
    public void setup() {
        radarSummaryRepository = mock(RadarSummaryRepository.class);
        aerodromeService=mock(AerodromeService.class);
        systemConfigurationService=mock(SystemConfigurationService.class);

        FlightMovementService flightMovementService = mock(FlightMovementService.class);
        RadarSummaryWaypointUtility radarSummaryWaypointUtility = new RadarSummaryWaypointUtility(systemConfigurationService);
        radarSummaryService = new RadarSummaryService(flightMovementService, radarSummaryRepository, radarSummaryWaypointUtility);

        radarSummaryService.setSystemConfigurationService(systemConfigurationService);

        //Create some helper object
        systemConfiguration=new SystemConfiguration();

        // create a list of radar summary
        radarSummaryList=new ArrayList<>();
        RadarSummary radarSummary=new RadarSummary();
        radarSummary.setFlightIdentifier("TAL001");
        radarSummary.setDestinationAeroDrome("DSTX");
        radarSummaryList.add(radarSummary);
    }

    @After
    public void destroy() {
        BillingContext.clear();
    }

    @Test
    public void createRadarSummary() throws Exception {
        RadarSummary radarSummary = new RadarSummary();
        radarSummary.setDate(LocalDateTime.now());
        radarSummary.setDepartureAeroDrome("dep");
        radarSummary.setDestinationAeroDrome("dest");
        when(aerodromeService.checkAerodromeIdentifier(any())).thenReturn(radarSummary.getDepartureAeroDrome());
        when(radarSummaryRepository.save(any(RadarSummary.class))).thenReturn(radarSummary);

        RadarSummary result = radarSummaryService.create(radarSummary);
        assertThat(result.getDepartureAeroDrome()).isEqualTo(radarSummary.getDepartureAeroDrome());
    }

    @Test
    public void getAllRadarSummaries() {

        List<RadarSummary> radarSummaryList = Collections.singletonList(new RadarSummary());

        when(radarSummaryRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(radarSummaryList));
        Page<RadarSummary> results = radarSummaryService.findAll(mock(Pageable.class));

        assertThat(results.getTotalElements()).isEqualTo(radarSummaryList.size());
    }

    @Test
    public void getRadarSummaryById() {
        RadarSummary radarSummary = new RadarSummary();
        radarSummary.setId(1);

        when(radarSummaryRepository.findOne(1)).thenReturn(radarSummary);

        RadarSummary result = radarSummaryService.findOne(1);
        assertThat(result).isEqualTo(radarSummary);
    }


    @Test
    public void updateRadarSummary() throws Exception {
        RadarSummary existingRadarSummary = new RadarSummary();
        existingRadarSummary.setDepartureAeroDrome("dep");
        existingRadarSummary.setDestinationAeroDrome("dest");

        RadarSummary updateRadarSummary = new RadarSummary();
        updateRadarSummary.setDepartureAeroDrome("dep1");
        when(radarSummaryRepository.getOne(1)).thenReturn(existingRadarSummary);
        when(aerodromeService.checkAerodromeIdentifier(any())).thenReturn(existingRadarSummary.getDepartureAeroDrome());
        when(radarSummaryRepository.save(any(RadarSummary.class))).thenReturn(existingRadarSummary);

        RadarSummary result = radarSummaryService.update(1, updateRadarSummary);
        assertThat(result.getDepartureAeroDrome()).isEqualTo(updateRadarSummary.getDepartureAeroDrome());
    }

    @Test
    public void deleteRadarSummary() {
        radarSummaryService.delete(1);
        verify(radarSummaryRepository).delete(any(Integer.class));
    }

    @Test
    public void testFindRadarSummaryByLogicalKey(){

        final RadarSummary resultRecord = this.radarSummaryList.get(0);

        // Exact match exists
        when(radarSummaryRepository.findByLogicalKeyExact(any(), any(), any(), any())).thenReturn(resultRecord);
        assertThat(radarSummaryService.findByLogicalKey ("TAL001", "AAAA", LocalDateTime.now(), "1234").getDestinationAeroDrome()).isEqualTo("DSTX");

        // Exact match doesn't exist, and time sensitivity option doesn't exist
        when(systemConfigurationService.getOneByItemName(SystemConfigurationItemName.DEP_TIME_RANGE_MIN)).thenReturn(null);
        when(radarSummaryRepository.findByLogicalKeyExact(any(),any(),any(),any())).thenReturn(null);
        assertThat (radarSummaryService.findByLogicalKey ("TAL001", "AAAA", LocalDateTime.now(), "1234")).isNull();

        // Exact match doesn't exist, and time sensitivity option is 0
        systemConfiguration.setCurrentValue("0");
        when(systemConfigurationService.getOneByItemName(SystemConfigurationItemName.DEP_TIME_RANGE_MIN)).thenReturn (systemConfiguration);
        when(radarSummaryRepository.findByLogicalKeyExact(any(),any(),any(),any())).thenReturn(null);
        assertThat (radarSummaryService.findByLogicalKey ("TAL001", "AAAA", LocalDateTime.now(), "1234")).isNull();

        // Exact match doesn't exist, and time sensitivity option is < 0
        systemConfiguration.setCurrentValue("-1");
        when(systemConfigurationService.getOneByItemName(SystemConfigurationItemName.DEP_TIME_RANGE_MIN)).thenReturn (systemConfiguration);
        when(radarSummaryRepository.findByLogicalKeyExact(any(),any(),any(),any())).thenReturn(null);
        assertThat (radarSummaryService.findByLogicalKey ("TAL001", "AAAA", LocalDateTime.now(), "1234")).isNull();

        // Exact match doesn't exist, and fuzzy match also doesn't exist
        systemConfiguration.setCurrentValue("-1");
        when(systemConfigurationService.getOneByItemName(SystemConfigurationItemName.DEP_TIME_RANGE_MIN)).thenReturn (systemConfiguration);
        when(radarSummaryRepository.findByLogicalKeyExact(any(), any(), any(), any())).thenReturn(null);
        when(radarSummaryRepository.findByLogicalKeyFuzzy(any(), any(), any(), any(), any())).thenReturn(null);
        assertThat (radarSummaryService.findByLogicalKey ("TAL001", "AAAA", LocalDateTime.now(), "1234")).isNull();

        // Exact match doesn't exist; but fuzzy match does
        systemConfiguration.setCurrentValue("10");
        when(systemConfigurationService.getOneByItemName(SystemConfigurationItemName.DEP_TIME_RANGE_MIN)).thenReturn (systemConfiguration);
        when(radarSummaryRepository.findByLogicalKeyExact(any(), any(), any(), any())).thenReturn (null);
        when(radarSummaryRepository.findByLogicalKeyFuzzy(any(), any(), any(), any(), any())).thenReturn (resultRecord);
        assertThat(radarSummaryService.findByLogicalKey ("TAL001", "AAAA", LocalDateTime.now(), "1234").getDestinationAeroDrome()).isEqualTo("DSTX");

    }

    @Test
    public void createIgnoreRadarWaypointsTest() throws FlightMovementBuilderException {
        RadarSummary create = mockRadarSummary("2350", "WYPNTA", "WYPNTB", "WYPNTC", "WYPNTD");
        create.setRoute("WYPNTX WYPNTY WYPNTZ");
        create.setFirEntryTime("1200");
        create.setFirEntryPoint("WYPNTX");
        create.setFirExitTime("1300");
        create.setFirExitPoint("WYPNTZ");

        when(radarSummaryRepository.save(any(RadarSummary.class))).thenAnswer(i -> i.getArguments()[0]);
        when(systemConfigurationService.getValue(SystemConfigurationItemName.RADAR_SUMMARY_FORMAT))
            .thenReturn(RadarSummaryFormat.INDRA_REC.getName());

        BillingContext.put(BillingContextKey.MERGE_WAYPOINTS, Boolean.TRUE);
        RadarSummary result = radarSummaryService.create(create);

        // waypoints should not match route and fir points when ignoring waypoints
        assertThat(result.getRoute()).isEqualTo("WYPNTX WYPNTY WYPNTZ");
        assertThat(result.getFirEntryPoint()).isEqualTo("WYPNTX");
        assertThat(result.getFirEntryTime()).isEqualTo("1200");
        assertThat(result.getFirExitPoint()).isEqualTo("WYPNTZ");
        assertThat(result.getFirExitTime()).isEqualTo("1300");
        assertThat(result.getWaypoints().size()).isEqualTo(4);
    }

    @Test
    public void createRadarWaypointsTest() throws FlightMovementBuilderException {
        RadarSummary create = mockRadarSummary("2350", "WYPNTA", "WYPNTB", "WYPNTC", "WYPNTD");
        create.setRoute("WYPNTX WYPNTY WYPNTZ");
        create.setFirEntryTime("1200");
        create.setFirEntryPoint("WYPNTX");
        create.setFirExitTime("1300");
        create.setFirExitPoint("WYPNTZ");

        when(radarSummaryRepository.save(any(RadarSummary.class))).thenAnswer(i -> i.getArguments()[0]);
        when(systemConfigurationService.getValue(SystemConfigurationItemName.RADAR_SUMMARY_FORMAT))
            .thenReturn(RadarSummaryFormat.INDRA_REC.getName());

        BillingContext.put(BillingContextKey.MERGE_WAYPOINTS, Boolean.TRUE);
        RadarSummary result = radarSummaryService.create(create, false, null);

        // waypoints should overwrite route and fir points when ignoring waypoints false
        assertThat(result.getRoute()).isEqualTo("WYPNTA WYPNTB WYPNTC WYPNTD");
        assertThat(result.getFirEntryPoint()).isEqualTo("WYPNTA");
        assertThat(result.getFirEntryTime()).isEqualTo("2350");
        assertThat(result.getFirExitPoint()).isEqualTo("WYPNTD");
        assertThat(result.getFirExitTime()).isEqualTo("0020");
        assertThat(result.getWaypoints().size()).isEqualTo(4);

        create = mockRadarSummary("2350", "WYPNTA", "WYPNTB", "WYPNTC", "WYPNTD");
        create.setWaypoints(null);

        result = radarSummaryService.create(create, false, null);

        // waypoints should not overwrite route and fir points when ignoring waypoints false and waypoints empty
        // this is to prevent overwritting route and fir points when waypoints are not supplied from radar summary
        assertThat(result.getRoute()).isEqualTo("WYPNTA WYPNTB WYPNTC WYPNTD");
        assertThat(result.getFirEntryPoint()).isEqualTo("WYPNTA");
        assertThat(result.getFirEntryTime()).isEqualTo("2350");
        assertThat(result.getFirExitPoint()).isEqualTo("WYPNTD");
        assertThat(result.getFirExitTime()).isEqualTo("0020");
        assertThat(result.getWaypoints().size()).isEqualTo(0);
    }

    @Test
    public void updateIgnoreRadarWaypointsTest() throws FlightMovementBuilderException {
        RadarSummary update = mockRadarSummary("2350", "WYPNTA", "WYPNTB", "WYPNTC", "WYPNTD");
        RadarSummary existing = mockRadarSummary("2358", "WYPNTC", "WYPNTD", "WYPNTE");

        when(radarSummaryRepository.getOne(anyInt())).thenReturn(existing);
        when(radarSummaryRepository.save(any(RadarSummary.class))).thenAnswer(i -> i.getArguments()[0]);
        when(systemConfigurationService.getValue(SystemConfigurationItemName.RADAR_SUMMARY_FORMAT))
            .thenReturn(RadarSummaryFormat.INDRA_REC.getName());

        BillingContext.put(BillingContextKey.MERGE_WAYPOINTS, Boolean.TRUE);
        RadarSummary result = radarSummaryService.update(1, update);

        assertThat(result.getRoute()).isEqualTo("WYPNTA WYPNTB WYPNTC WYPNTD");
        assertThat(result.getFirEntryPoint()).isEqualTo("WYPNTA");
        assertThat(result.getFirEntryTime()).isEqualTo("2350");
        assertThat(result.getFirExitPoint()).isEqualTo("WYPNTD");
        assertThat(result.getFirExitTime()).isEqualTo("0020");
        assertThat(result.getWaypoints().size()).isEqualTo(4);
    }

    @Test
    public void updateMergeRadarWaypointsTest() throws FlightMovementBuilderException {
        RadarSummary update = mockRadarSummary("2340", "WYPNTA", "WYPNTB", "WYPNTC", "WYPNTD");
        RadarSummary existing = mockRadarSummary("2358", "WYPNTC", "WYPNTD", "WYPNTE");

        when(radarSummaryRepository.getOne(anyInt())).thenReturn(existing);
        when(radarSummaryRepository.save(any(RadarSummary.class))).thenAnswer(i -> i.getArguments()[0]);
        when(systemConfigurationService.getValue(SystemConfigurationItemName.RADAR_SUMMARY_FORMAT))
            .thenReturn(RadarSummaryFormat.INDRA_REC.getName());

        // by default, billing context should merge
        RadarSummary result = radarSummaryService.update(1, update);

        assertThat(result.getRoute()).isEqualTo("WYPNTA WYPNTB WYPNTC WYPNTD WYPNTE");
        assertThat(result.getFirEntryPoint()).isEqualTo("WYPNTA");
        assertThat(result.getFirEntryTime()).isEqualTo("2340");
        assertThat(result.getFirExitPoint()).isEqualTo("WYPNTE");
        assertThat(result.getFirExitTime()).isEqualTo("0018");
        assertThat(result.getWaypoints().size()).isEqualTo(5);
    }

    @Test
    public void updateOverwriteRadarWaypointsTest() throws FlightMovementBuilderException {
        RadarSummary update = mockRadarSummary("2350", "WYPNTA", "WYPNTB", "WYPNTC", "WYPNTD");
        RadarSummary existing = mockRadarSummary("2358", "WYPNTC", "WYPNTD", "WYPNTE");

        when(radarSummaryRepository.getOne(anyInt())).thenReturn(existing);
        when(radarSummaryRepository.save(any(RadarSummary.class))).thenAnswer(i -> i.getArguments()[0]);
        when(systemConfigurationService.getValue(SystemConfigurationItemName.RADAR_SUMMARY_FORMAT))
            .thenReturn(RadarSummaryFormat.INDRA_REC.getName());

        BillingContext.put(BillingContextKey.MERGE_WAYPOINTS, Boolean.FALSE);
        RadarSummary result = radarSummaryService.update(1, update);

        assertThat(result.getRoute()).isEqualTo("WYPNTA WYPNTB WYPNTC WYPNTD");
        assertThat(result.getFirEntryPoint()).isEqualTo("WYPNTA");
        assertThat(result.getFirEntryTime()).isEqualTo("2350");
        assertThat(result.getFirExitPoint()).isEqualTo("WYPNTD");
        assertThat(result.getFirExitTime()).isEqualTo("0020");
        assertThat(result.getWaypoints().size()).isEqualTo(4);
    }

    private RadarSummary mockRadarSummary(String entryTime, String... points) {

        RadarSummary result = new RadarSummary();

        result.setId(0);
        result.setFlightIdentifier("ABC123");
        result.setDayOfFlight(LocalDate.of(2018, 12, 30).atStartOfDay());

        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HHmm");
        LocalTime time = LocalTime.parse(entryTime, timeFormat);
        LocalDateTime entryDateTime = result.getDayOfFlight()
            .plusHours(time.getHour())
            .plusMinutes(time.getMinute());

        result.setFirEntryPoint(points[0]);
        result.setFirEntryTime(entryTime);
        result.setFirExitPoint(points[points.length - 1]);
        result.setFirExitTime(entryDateTime.plusMinutes(10 * (points.length - 1)).format(timeFormat));

        StringJoiner route = new StringJoiner(" ");
        List<RadarSummaryWaypoint> waypoints = new ArrayList<>();
        for (int i = 0; i < points.length; i++) {
            route.add(points[i]);
            waypoints.add(mockRadarSummaryWaypoint(entryDateTime.plusMinutes(i * 10), points[i]));
        }
        result.setRoute(route.toString());
        result.setWaypoints(waypoints);

        return result;
    }

    private RadarSummaryWaypoint mockRadarSummaryWaypoint(final LocalDateTime dateTime, final String point) {
        RadarSummaryWaypoint waypoint = new RadarSummaryWaypoint();
        waypoint.setDateTime(dateTime);
        waypoint.setPoint(point);
        return waypoint;
    }
}
