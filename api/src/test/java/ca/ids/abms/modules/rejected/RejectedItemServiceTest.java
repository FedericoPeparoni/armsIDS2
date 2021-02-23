package ca.ids.abms.modules.rejected;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ca.ids.abms.config.security.SecurityUtils;
import ca.ids.abms.modules.airspaces.AirspaceService;
import ca.ids.abms.modules.currencies.CurrencyService;
import ca.ids.abms.modules.users.User;
import ca.ids.abms.modules.users.UserService;
import ca.ids.abms.util.billingcontext.BillingContextUtility;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;

import ca.ids.abms.config.error.ErrorDTO;
import ca.ids.abms.config.error.RejectedException;
import ca.ids.abms.config.error.RejectedReasons;
import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.accounts.AccountService;
import ca.ids.abms.modules.atcmovements.AtcMovementLog;
import ca.ids.abms.modules.atcmovements.AtcMovementLogBulkLoader;
import ca.ids.abms.modules.atcmovements.AtcMovementLogCsvViewModel;
import ca.ids.abms.modules.atcmovements.AtcMovementLogMapper;
import ca.ids.abms.modules.atcmovements.AtcMovementLogService;
import ca.ids.abms.modules.charges.PassengerServiceChargeReturn;
import ca.ids.abms.modules.charges.PassengerServiceChargeReturnCsvViewModel;
import ca.ids.abms.modules.charges.PassengerServiceChargeReturnLoader;
import ca.ids.abms.modules.charges.PassengerServiceChargeReturnMapper;
import ca.ids.abms.modules.charges.PassengerServiceChargeReturnService;
import ca.ids.abms.modules.common.dto.BulkLoaderSummary;
import ca.ids.abms.modules.common.dto.ItemLoaderObserver;
import ca.ids.abms.modules.common.services.AbstractBulkLoader;
import ca.ids.abms.modules.common.services.DateTimeConfigurationHandler;
import ca.ids.abms.modules.common.services.EntityValidator;
import ca.ids.abms.modules.common.services.MovementLogItemsResolver;
import ca.ids.abms.modules.dataimport.RejectableRowCsvImportServiceImpl;
import ca.ids.abms.modules.flightmovements.FlightMovementService;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementBuilderException;
import ca.ids.abms.modules.flightmovementsbuilder.enumerate.FlightMovementBuilderIssue;
import ca.ids.abms.modules.flightmovementsbuilder.utility.FlightMovementBuilderUtility;
import ca.ids.abms.modules.radarsummary.FlightTravelCategory;
import ca.ids.abms.modules.radarsummary.FlightTravelCategoryMapper;
import ca.ids.abms.modules.radarsummary.RadarSummary;
import ca.ids.abms.modules.radarsummary.RadarSummaryCsvViewModel;
import ca.ids.abms.modules.radarsummary.RadarSummaryDepartureEstimator;
import ca.ids.abms.modules.radarsummary.RadarSummaryLoader;
import ca.ids.abms.modules.radarsummary.RadarSummaryMapper;
import ca.ids.abms.modules.radarsummary.RadarSummaryService;
import ca.ids.abms.modules.rejected.impl.RejectedItemHandlerForAtc;
import ca.ids.abms.modules.rejected.impl.RejectedItemHandlerForFPL;
import ca.ids.abms.modules.rejected.impl.RejectedItemHandlerForPSCR;
import ca.ids.abms.modules.rejected.impl.RejectedItemHandlerForRadar;
import ca.ids.abms.modules.rejected.impl.RejectedItemHandlerForTower;
import ca.ids.abms.modules.spatiareader.entity.FplObject;
import ca.ids.abms.modules.spatiareader.mapper.CplFplMapper;
import ca.ids.abms.modules.system.SystemConfigurationRepository;
import ca.ids.abms.modules.towermovements.TowerMovementLog;
import ca.ids.abms.modules.towermovements.TowerMovementLogBulkLoader;
import ca.ids.abms.modules.towermovements.TowerMovementLogCsvViewModel;
import ca.ids.abms.modules.towermovements.TowerMovementLogMapper;
import ca.ids.abms.modules.towermovements.TowerMovementLogService;
import ca.ids.abms.modules.uploadedfiles.UploadedFileService;

@RunWith(MockitoJUnitRunner.class)
public class RejectedItemServiceTest {

    //// Define the Generic components ////

    private List<AbstractRejectedItemHandler> handlers = new ArrayList<>();
    private DateTimeConfigurationHandler dateTimeConfigurationHandler;
    final private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private MovementLogItemsResolver movementLogItemsResolver;
    @Mock
    private EntityValidator validator;
    @Mock
    private SystemConfigurationRepository systemConfigurationRepository;

    //// Define the Rejected item components ////

    private RejectedItemHandlerChain rejectedItemHandlerChain;

    @Mock
    private RejectedItemRepository rejectedItemRepository;
    @InjectMocks
    private RejectedItemService rejectedItemService;
    @Mock
    private UploadedFileService uploadedFileService;

    @InjectMocks
    private BillingContextUtility threadLocalVariableUtility;
    @Mock
    private CurrencyService currencyService;
    @Mock AirspaceService airspaceService;

    //// Define the ATC Movement log components ////

    private AbstractBulkLoader atcLoader;
    private RejectedItemHandlerForAtc rejAtc;
    private AtcMovementLogMapper atcMovementLogMapper = Mappers.getMapper(AtcMovementLogMapper.class);
    @Mock
    public AtcMovementLogService atcMovementLogService;
    @Mock
    private RejectableRowCsvImportServiceImpl<AtcMovementLogCsvViewModel> atcImportService;

    //// Define the Tower Movement log components ////

    private AbstractBulkLoader towerLoader;
    private RejectedItemHandlerForTower rejTower;
    @InjectMocks
    private TowerMovementLogMapper towerMovementLogMapper = Mappers.getMapper(TowerMovementLogMapper.class);
    @Mock
    public TowerMovementLogService towerMovementLogService;
    @Mock
    private RejectableRowCsvImportServiceImpl<TowerMovementLogCsvViewModel> towerImportService;

    //// Define the Radar Summary components ////

    private AbstractBulkLoader radarLoader;
    private RejectedItemHandlerForRadar rejRadar;
    @InjectMocks
    private RadarSummaryMapper radarMovementLogMapper = Mappers.getMapper(RadarSummaryMapper.class);
    @Mock
    public RadarSummaryService radarSummaryService;
    @Mock
    private RejectableRowCsvImportServiceImpl<RadarSummaryCsvViewModel> radarImportService;
    @Mock
    private FlightTravelCategoryMapper flightTravelCategoryMapper;

    //// Define the Passenger Service Charge Returns components ////

    private AbstractBulkLoader pscrLoader;
    private RejectedItemHandlerForPSCR rejPSCR;
    private PassengerServiceChargeReturnMapper pscrMapper = Mappers.getMapper(PassengerServiceChargeReturnMapper.class);
    @Mock
    public PassengerServiceChargeReturnService pscrService;
    @Mock
    private RejectableRowCsvImportServiceImpl<PassengerServiceChargeReturnCsvViewModel> pscrImportService;

    //// Define the Flight movement object components ////

    private RejectedItemHandlerForFPL rejFPL;
    private CplFplMapper fplMapper = Mappers.getMapper(CplFplMapper.class);
    @Mock
    public FlightMovementService fplService;
    @Mock
    public FlightMovementBuilderUtility flightMovementBuilderUtility;

    //// Define the Account object components ////

    private Account account;
    @Mock
    private AccountService accountService;

    //// Define the User object components ////
    @Mock
    private UserService userService;

    @Mock
    private RadarSummaryDepartureEstimator radarSummaryDepartureEstimator;

    private final ErrorDTO GENERIC_ERROR = new ErrorDTO.Builder().setErrorMessage("Validation Error").appendDetails("Test").build();


    @Before
    public void setup() {
        //// Initialize the Generic components ////

        MockitoAnnotations.initMocks(this);
        dateTimeConfigurationHandler = new DateTimeConfigurationHandler(systemConfigurationRepository);
        rejectedItemService = new RejectedItemService(rejectedItemRepository);

        //// Initialize the ATC Movement log components ////
        atcLoader = new AtcMovementLogBulkLoader(rejectedItemService, validator, uploadedFileService, atcMovementLogMapper, atcMovementLogService, threadLocalVariableUtility);
            atcMovementLogMapper.setDateTimeConfigurationHandler(dateTimeConfigurationHandler);
        rejAtc = new RejectedItemHandlerForAtc(validator, atcImportService,
            atcMovementLogMapper, atcMovementLogService, objectMapper);
        handlers.add(rejAtc);

        //// Initialize the Tower Movement log components ////
        towerLoader = new TowerMovementLogBulkLoader(rejectedItemService, validator, uploadedFileService, towerMovementLogMapper, towerMovementLogService, threadLocalVariableUtility);
        towerMovementLogMapper.setDateTimeConfigurationHandler(dateTimeConfigurationHandler);
        rejTower = new RejectedItemHandlerForTower(validator, towerImportService,
            towerMovementLogMapper, towerMovementLogService, objectMapper);
        handlers.add(rejTower);

        //// Initialize the Radar Summary components ////
        radarLoader = new RadarSummaryLoader(rejectedItemService, validator, uploadedFileService, radarMovementLogMapper, radarSummaryService, threadLocalVariableUtility, radarSummaryDepartureEstimator);
        rejRadar = new RejectedItemHandlerForRadar(validator, radarImportService,
            radarMovementLogMapper, radarSummaryService, objectMapper);
        handlers.add(rejRadar);

        //// Initialize the Passenger Service Charge Returns components ////
        pscrLoader = new PassengerServiceChargeReturnLoader(rejectedItemService, validator, uploadedFileService, pscrMapper, pscrService, threadLocalVariableUtility, userService);
        pscrMapper.setDateTimeConfigurationHandler(dateTimeConfigurationHandler);
        rejPSCR = new RejectedItemHandlerForPSCR(validator, pscrImportService,
            pscrMapper, pscrService, objectMapper);
        handlers.add(rejPSCR);

        //// Initialize the Flight movement object components ////

        rejFPL = new RejectedItemHandlerForFPL(validator,
            fplMapper, fplService, objectMapper);
        handlers.add(rejFPL);

        //// Initialize the Rejected item components ////

        rejectedItemHandlerChain = new RejectedItemHandlerChain(handlers,
            rejectedItemRepository);
    }

    @Test
    public void testLoadAtcMovementLog() throws FlightMovementBuilderException {
        final AtcMovementLogCsvViewModel one = new AtcMovementLogCsvViewModel();
        one.setId(1);
        one.setRawText("a,b,c");
        one.setParsed(true);
        one.setFlightId("OK");
        final AtcMovementLogCsvViewModel two = new AtcMovementLogCsvViewModel();
        two.setRawText("aaa");
        two.setParsed(true);
        final AtcMovementLogCsvViewModel three = new AtcMovementLogCsvViewModel();
        three.setRawText("a,a,a");
        three.setParsed(false);
        three.setErrorMessage(GENERIC_ERROR);
        three.setFlightId("REJ");
        final List<AtcMovementLogCsvViewModel> csvModels = new ArrayList<>(3);
        csvModels.add(one);
        csvModels.add(two);
        csvModels.add(three);
        MockMultipartFile file = new MockMultipartFile(
            "ATC_MOVEMENT_LOG", "ATC_MOVEMENT_LOG.csv", "text/plain", "ATC_MOVEMENT_LOG".getBytes()
        );


        final AtcMovementLog ok = atcMovementLogMapper.toModel(one);
        final AtcMovementLog rejected = atcMovementLogMapper.toModel(three);
        when(atcMovementLogService.createOrUpdate(eq(ok), any(ItemLoaderObserver.class))).thenReturn(ok);
        when(atcMovementLogService.createOrUpdate(eq(rejected), any(ItemLoaderObserver.class))).thenThrow(new FlightMovementBuilderException(
            FlightMovementBuilderIssue.REJECTED_ITEM, "Invalid item"));

        final BulkLoaderSummary summary = atcLoader.bulkLoad(csvModels, file);

        assertThat(summary).isNotNull();
        assertThat(summary.getRejected()).isEqualTo(2);
        assertThat(summary.getSuccessfullyProcessed()).isEqualTo(1);
        assertThat(summary.getTotal()).isEqualTo(3);
    }

    @Test
    public void fixAtcMovementLog() throws IOException {
        final RejectedItem existingItem = buildRejectedItem(RejectedItemType.ATS_MOVEMENTS_LOG);
        final RejectedItem savedItem = buildRejectedItem(RejectedItemType.ATS_MOVEMENTS_LOG);
        savedItem.setStatus(RejectedItemStatus.CORRECTED.toValue());

        when (rejectedItemRepository.getOne(anyInt())).thenReturn(existingItem);
        when (rejectedItemRepository.save(any(RejectedItem.class))).thenReturn(savedItem);
        when (atcImportService.positionCsvToObject(eq("a, b, c"), eq(AtcMovementLogCsvViewModel.class), eq(false)))
                .thenReturn(buildATCCsvModels());
        doNothing().when(validator).validateItem(any(AtcMovementLog.class));

        rejectedItemHandlerChain.fixRejectedItemByRawText(1, "a, b, c");

        verify(rejectedItemRepository).save(any(RejectedItem.class));
        assertThat(existingItem.getStatus()).isEqualTo(RejectedItemStatus.CORRECTED.toValue());
    }

    @Test
    public void testLoadTowerMovementLog() throws FlightMovementBuilderException {
        final TowerMovementLogCsvViewModel one = new TowerMovementLogCsvViewModel();
        one.setId(1);
        one.setRawText("a,b,c");
        one.setParsed(true);
        one.setFlightId("OK");
        final TowerMovementLogCsvViewModel two = new TowerMovementLogCsvViewModel();
        two.setRawText("aaa");
        two.setParsed(true);
        final TowerMovementLogCsvViewModel three = new TowerMovementLogCsvViewModel();
        three.setRawText("a,a,a");
        three.setParsed(false);
        three.setErrorMessage(GENERIC_ERROR);
        three.setFlightId("REJ");
        final List<TowerMovementLogCsvViewModel> csvModels = new ArrayList<>(3);
        csvModels.add(one);
        csvModels.add(two);
        csvModels.add(three);

        MockMultipartFile file = new MockMultipartFile(
            "TOWER_LOG", "TOWER_LOG.csv", "text/plain", "TOWER_LOG".getBytes()
        );

        final TowerMovementLog ok = towerMovementLogMapper.toModel(one);
        final TowerMovementLog rejected = towerMovementLogMapper.toModel(three);
        when(towerMovementLogService.createOrUpdateByFlightId(eq(ok), any(ItemLoaderObserver.class))).thenReturn(ok);
        when(towerMovementLogService.createOrUpdateByFlightId(eq(rejected), any(ItemLoaderObserver.class))).thenThrow(new FlightMovementBuilderException(
            FlightMovementBuilderIssue.REJECTED_ITEM, "Invalid item"));

        final BulkLoaderSummary summary = towerLoader.bulkLoad(csvModels, file);

        assertThat(summary).isNotNull();
        assertThat(summary.getRejected()).isEqualTo(2);
        assertThat(summary.getSuccessfullyProcessed()).isEqualTo(1);
        assertThat(summary.getTotal()).isEqualTo(3);
    }

    @Test
    public void fixTowerMovementLog() throws IOException {
        final RejectedItem existingItem = buildRejectedItem(RejectedItemType.TOWER_AIRCRAFT_PASSENGER_MOVEMENTS_LOG);
        final RejectedItem savedItem = buildRejectedItem(RejectedItemType.TOWER_AIRCRAFT_PASSENGER_MOVEMENTS_LOG);
        savedItem.setStatus(RejectedItemStatus.CORRECTED.toValue());

        when (rejectedItemRepository.getOne(anyInt())).thenReturn(existingItem);
        when (rejectedItemRepository.save(any(RejectedItem.class))).thenReturn(savedItem);
        when (towerImportService.positionCsvToObject(eq("a, b, c"), eq(TowerMovementLogCsvViewModel.class), eq(false)))
            .thenReturn(buildTowerCsvModels());
        when (movementLogItemsResolver.resolveIcaoCodeByOperatorName(anyString())).thenReturn("ABC");

        doNothing().when(validator).validateItem(any(TowerMovementLog.class));

        rejectedItemHandlerChain.fixRejectedItemByRawText(1, "a, b, c");

        verify(rejectedItemRepository).save(any(RejectedItem.class));
        assertThat(existingItem.getStatus()).isEqualTo(RejectedItemStatus.CORRECTED.toValue());
    }

    @Test
    public void testLoadRadarSummary() throws FlightMovementBuilderException {
        final RadarSummaryCsvViewModel one = new RadarSummaryCsvViewModel();
        one.setId(1);
        one.setRawText("a,b,c");
        one.setParsed(true);
        one.setDepartureAeroDrome("YOW");
        final RadarSummaryCsvViewModel two = new RadarSummaryCsvViewModel();
        two.setRawText("aaa");
        two.setParsed(true);
        final RadarSummaryCsvViewModel three = new RadarSummaryCsvViewModel();
        three.setRawText("a,a,a");
        three.setParsed(false);
        three.setErrorMessage(GENERIC_ERROR);
        three.setDepartureAeroDrome("UNK");
        final List<RadarSummaryCsvViewModel> csvModels = new ArrayList<>(3);
        csvModels.add(one);
        csvModels.add(two);
        csvModels.add(three);
        MockMultipartFile file = new MockMultipartFile(
            "RADAR_SUMMARY", "RADAR_SUMMARY.csv", "text/plain", "RADAR_SUMMARY".getBytes()
        );

        final BulkLoaderSummary bulkLoaderSummary = new BulkLoaderSummary();

        final RadarSummary ok = radarMovementLogMapper.toModel(one);
        final RadarSummary rejected = radarMovementLogMapper.toModel(three);
        when(radarSummaryService.createOrUpdate(eq(ok), any(ItemLoaderObserver.class))).thenReturn(ok);
        when(radarSummaryService.createOrUpdate(eq(rejected), any(ItemLoaderObserver.class))).thenThrow(new FlightMovementBuilderException(
            FlightMovementBuilderIssue.REJECTED_ITEM, "Invalid item"));

        final BulkLoaderSummary summary = radarLoader.bulkLoad(csvModels, file);

        assertThat(summary).isNotNull();
        assertThat(summary.getRejected()).isEqualTo(2);
        assertThat(summary.getSuccessfullyProcessed()).isEqualTo(1);
        assertThat(summary.getTotal()).isEqualTo(3);
    }

    @Test
    public void fixRadarSummaryLog() throws IOException {
        final RejectedItem existingItem = buildRejectedItem(RejectedItemType.RADAR_SUMMARIES);
        final RejectedItem savedItem = buildRejectedItem(RejectedItemType.RADAR_SUMMARIES);
        savedItem.setStatus(RejectedItemStatus.CORRECTED.toValue());

        when (rejectedItemRepository.getOne(anyInt())).thenReturn(existingItem);
        when (rejectedItemRepository.save(any(RejectedItem.class))).thenReturn(savedItem);
        when (flightTravelCategoryMapper.toFlightTravelCategory(anyString())).thenReturn(FlightTravelCategory.DOMESTIC);

        doNothing().when(validator).validateItem(any(RadarSummary.class));

        rejectedItemHandlerChain.fixRejectedItemByJsonText(1, "{\"flightIdentifier\" : \"TEST\", \"flightTravelCategory\" : \"DOM\"}".getBytes("UTF-8"));

        verify(rejectedItemRepository).save(any(RejectedItem.class));
        assertThat(existingItem.getStatus()).isEqualTo(RejectedItemStatus.CORRECTED.toValue());
    }

    @Test
    public void testLoadPSCR() throws FlightMovementBuilderException {
        User user = new User();
        user.setId(1);
        user.setIsSelfcareUser(false);

        final PassengerServiceChargeReturnCsvViewModel one = new PassengerServiceChargeReturnCsvViewModel();
        one.setId(1);
        one.setRawText("a,b,c");
        one.setParsed(true);
        one.setFlightId("OK");
        final PassengerServiceChargeReturnCsvViewModel two = new PassengerServiceChargeReturnCsvViewModel();
        two.setRawText("aaa");
        two.setParsed(true);
        final PassengerServiceChargeReturnCsvViewModel three = new PassengerServiceChargeReturnCsvViewModel();
        three.setRawText("a,a,a");
        three.setParsed(false);
        three.setErrorMessage(GENERIC_ERROR);
        three.setFlightId("REJ");
        final List<PassengerServiceChargeReturnCsvViewModel> csvModels = new ArrayList<>(3);
        csvModels.add(one);
        csvModels.add(two);
        csvModels.add(three);
        MockMultipartFile file = new MockMultipartFile(
            "PASSENGER_SERVICE_LOG", "PASSENGER_SERVICE_LOG.csv", "text/plain", "PASSENGER_SERVICE_LOG".getBytes()
        );

        final PassengerServiceChargeReturn ok = pscrMapper.toModel(one);
        final PassengerServiceChargeReturn rejected = pscrMapper.toModel(three);

        when(userService.getUserByLogin(any())).thenReturn(user);
        when(pscrService.createOrUpdate(eq(ok), any(ItemLoaderObserver.class))).thenReturn(ok);
        when(pscrService.createOrUpdate(eq(rejected), any(ItemLoaderObserver.class))).thenThrow(new FlightMovementBuilderException(
            FlightMovementBuilderIssue.REJECTED_ITEM, "Invalid item"));

        final BulkLoaderSummary summary = pscrLoader.bulkLoad(csvModels, file);

        assertThat(summary).isNotNull();
        assertThat(summary.getRejected()).isEqualTo(2);
        assertThat(summary.getSuccessfullyProcessed()).isEqualTo(1);
        assertThat(summary.getTotal()).isEqualTo(3);
    }

    @Test
    public void fixPSCRLog() throws IOException {
        final RejectedItem existingItem = buildRejectedItem(RejectedItemType.PASSENGER_SERVICE_CHARGE_RETURNS);
        final RejectedItem savedItem = buildRejectedItem(RejectedItemType.PASSENGER_SERVICE_CHARGE_RETURNS);
        savedItem.setStatus(RejectedItemStatus.CORRECTED.toValue());

        when (rejectedItemRepository.getOne(anyInt())).thenReturn(existingItem);
        when (rejectedItemRepository.save(any(RejectedItem.class))).thenReturn(savedItem);
        when (pscrImportService.positionCsvToObject(eq("a, b, c"), eq(PassengerServiceChargeReturnCsvViewModel.class), eq(false)))
            .thenReturn(buildPscrCsvModels());
        doNothing().when(validator).validateItem(any(PassengerServiceChargeReturn.class));

        rejectedItemHandlerChain.fixRejectedItemByRawText(1, "a, b, c");

        verify(rejectedItemRepository).save(any(RejectedItem.class));
        assertThat(existingItem.getStatus()).isEqualTo(RejectedItemStatus.CORRECTED.toValue());
    }

    @Test
    public void fixFPL() throws IOException {
        final RejectedItem existingItem = buildRejectedItem(RejectedItemType.FLIGHT_MOVEMENTS);
        final RejectedItem savedItem = buildRejectedItem(RejectedItemType.FLIGHT_MOVEMENTS);
        savedItem.setStatus(RejectedItemStatus.CORRECTED.toValue());

        when (rejectedItemRepository.getOne(anyInt())).thenReturn(existingItem);
        when (rejectedItemRepository.save(any(RejectedItem.class))).thenReturn(savedItem);

        doNothing().when(validator).validateItem(any(FplObject.class));
        RejectedItem result = rejectedItemHandlerChain.fixRejectedItemByJsonText(1,
            "{\"flightId\" : \"TEST\"}".getBytes("UTF-8"));

        verify(rejectedItemRepository).save(any(RejectedItem.class));
        assertThat(result.getStatus()).isEqualTo(RejectedItemStatus.CORRECTED.toValue());
    }

    private RejectedItem buildRejectedItem (RejectedItemType type) {
        final RejectedItem rejectedItem = new RejectedItem();
        rejectedItem.setId(1);
        rejectedItem.setRawText("a,b,c,");
        rejectedItem.setStatus(RejectedItemStatus.UNCORRECTED.toValue());
        rejectedItem.setRecordType(type.toValue());
        rejectedItem.setRejectedReason(RejectedReasons.VALIDATION_ERROR.toValue());
        return rejectedItem;
    }

    private List<AtcMovementLogCsvViewModel> buildATCCsvModels() {
        final List<AtcMovementLogCsvViewModel> csvModels = new ArrayList<>();
        AtcMovementLogCsvViewModel csvModel = new AtcMovementLogCsvViewModel();
        csvModels.add(csvModel);
        return csvModels;
    }

    private List<TowerMovementLogCsvViewModel> buildTowerCsvModels() {
        final List<TowerMovementLogCsvViewModel> csvModels = new ArrayList<>();
        TowerMovementLogCsvViewModel csvModel = new TowerMovementLogCsvViewModel();
        csvModels.add(csvModel);
        return csvModels;
    }

    private List<RadarSummaryCsvViewModel> buildRadarCsvModels() {
        final List<RadarSummaryCsvViewModel> csvModels = new ArrayList<>();
        RadarSummaryCsvViewModel csvModel = new RadarSummaryCsvViewModel();
        csvModels.add(csvModel);
        return csvModels;
    }

    private List<PassengerServiceChargeReturnCsvViewModel> buildPscrCsvModels() {
        final List<PassengerServiceChargeReturnCsvViewModel> csvModels = new ArrayList<>();
        PassengerServiceChargeReturnCsvViewModel csvModel = new PassengerServiceChargeReturnCsvViewModel();
        csvModels.add(csvModel);
        return csvModels;
    }

    @Test
    public void getRejectedItemById() {
        RejectedItem rejectedItem = new RejectedItem();
        rejectedItem.setId(1);

        when(rejectedItemRepository.getOne(any()))
            .thenReturn(rejectedItem);

        RejectedItem result = rejectedItemService.getOne(1);
        assertThat(result).isEqualTo(rejectedItem);
    }

    @Test
    public void createRejectedItem() {
        RejectedItem rejectedItem = new RejectedItem();
        rejectedItem.setFileName("input.csv");
        RejectedException re = new RejectedException(RejectedReasons.VALIDATION_ERROR, "error", "error");

        when(rejectedItemRepository.save(any(RejectedItem.class)))
            .thenReturn(rejectedItem);

        RejectedItem result = rejectedItemService.create(RejectedItemType.ATS_MOVEMENTS_LOG, re,
            null, "input.csv", "a,b,c,","A,B,C", null);
        assertThat(result.getFileName()).isEqualTo(rejectedItem.getFileName());

    }

    @Test
    public void updateRejectedItem() {
        RejectedItem existingRejectedItem = new RejectedItem();
        existingRejectedItem.setFileName("input.csv");

        RejectedItem rejectedItem = new RejectedItem();
        rejectedItem.setFileName("new_input.csv");

        when(rejectedItemRepository.getOne(any()))
            .thenReturn(existingRejectedItem);

        when(rejectedItemRepository.save(any(RejectedItem.class)))
            .thenReturn(existingRejectedItem);

        RejectedItem result = rejectedItemService.update(1, rejectedItem);

        assertThat(result.getFileName()).isEqualTo("new_input.csv");
    }

    @Test
    public void deleteUser() {
        rejectedItemService.delete(1);
        verify(rejectedItemRepository).delete(any(Integer.class));
    }
}
