package ca.ids.abms.modules.selfcareportal.approvalrequests;

import ca.ids.abms.config.db.Filter;
import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.config.error.*;
import ca.ids.abms.config.security.SecurityUtils;
import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.accounts.AccountMapper;
import ca.ids.abms.modules.accounts.AccountService;
import ca.ids.abms.modules.accounts.AccountViewModel;
import ca.ids.abms.modules.aircraft.AircraftRegistration;
import ca.ids.abms.modules.aircraft.AircraftRegistrationMapper;
import ca.ids.abms.modules.aircraft.AircraftRegistrationService;
import ca.ids.abms.modules.aircraft.AircraftRegistrationViewModel;
import ca.ids.abms.modules.common.services.EntityValidator;
import ca.ids.abms.modules.flight.FlightSchedule;
import ca.ids.abms.modules.flight.FlightScheduleMapper;
import ca.ids.abms.modules.flight.FlightScheduleService;
import ca.ids.abms.modules.flight.FlightScheduleViewModel;
import ca.ids.abms.modules.selfcareportal.approvalrequests.enumerate.RequestDataset;
import ca.ids.abms.modules.selfcareportal.approvalrequests.enumerate.RequestStatus;
import ca.ids.abms.modules.selfcareportal.approvalrequests.enumerate.RequestType;
import ca.ids.abms.modules.users.UserService;
import ca.ids.abms.modules.util.models.ModelUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class SelfCarePortalApprovalRequestService {

    private static final Logger LOG = LoggerFactory.getLogger(SelfCarePortalApprovalRequest.class);

    private static final String REQUEST_DATASET = "requestDataset";
    private static final String STATUS = "status";

    private final SelfCarePortalApprovalRequestRepository selfCarePortalApprovalRequestRepository;
    private final UserService userService;
    private final AccountMapper accountMapper;
    private final AccountService accountService;
    private final AircraftRegistrationService aircraftRegistrationService;
    private final AircraftRegistrationMapper aircraftRegistrationMapper;
    private final FlightScheduleService flightScheduleService;
    private final FlightScheduleMapper flightScheduleMapper;
    private final ObjectMapper mapper;
    private final EntityValidator entityValidator;

    private static final String EXCEPTION = "Exception: {}";

    @SuppressWarnings("squid:S00107")
    public SelfCarePortalApprovalRequestService(final SelfCarePortalApprovalRequestRepository selfCarePortalApprovalRequestRepository,
                                                final UserService userService,
                                                final AccountMapper accountMapper,
                                                final AccountService accountService,
                                                final AircraftRegistrationService aircraftRegistrationService,
                                                final AircraftRegistrationMapper aircraftRegistrationMapper,
                                                final FlightScheduleService flightScheduleService,
                                                final FlightScheduleMapper flightScheduleMapper,
                                                final ObjectMapper mapper,
                                                final EntityValidator entityValidator) {
        this.selfCarePortalApprovalRequestRepository = selfCarePortalApprovalRequestRepository;
        this.userService = userService;
        this.accountMapper = accountMapper;
        this.accountService = accountService;
        this.aircraftRegistrationService = aircraftRegistrationService;
        this.aircraftRegistrationMapper = aircraftRegistrationMapper;
        this.flightScheduleService = flightScheduleService;
        this.flightScheduleMapper = flightScheduleMapper;
        this.mapper = mapper;
        this.entityValidator = entityValidator;
    }

    @SuppressWarnings("squid:S00119")
    public <Entity> Entity createNewApprovalRequest(Entity obj, Integer objectId, Integer accountId, Class<Entity> cl, RequestDataset dataset, RequestType type) throws IOException {
        LOG.debug("Request to create a new {} from self-care portal : {}", dataset, obj);
        SelfCarePortalApprovalRequest newRequest = new SelfCarePortalApprovalRequest();
        newRequest.setUser(userService.getUserByLogin (SecurityUtils.getCurrentUserLogin()));
        newRequest.setRequestType(type.toValue());
        newRequest.setRequestDataset(dataset.toValue());

        String json = getValueAsString(obj);

        newRequest.setRequestText(json);
        newRequest.setStatus(RequestStatus.OPEN.toValue());

        if (type != RequestType.CREATE) {
            if (objectId == null) {
                LOG.debug("Bad request: Object id is null");
                throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                    new Exception("Object id is missing"));
            } else
                newRequest.setObjectId(objectId);
        }

        if (dataset != RequestDataset.ACCOUNT) {
            if (accountId == null) {
                LOG.debug("Bad request: Account id is null");
                throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                    new Exception("Account is missing"));
            } else
                newRequest.setAccount(accountService.getOne(accountId));
        }

        save(newRequest);
        return mapper.readValue(newRequest.getRequestText(), cl);
    }

    @SuppressWarnings("squid:S00119")
    public <Entity> Entity updateUnprovenApprovalRequest(Integer id, Entity obj, Class<Entity> cl) throws IOException {
        LOG.debug("Request to update new self-care portal approval request : {}", id);
        SelfCarePortalApprovalRequest existingRequest = selfCarePortalApprovalRequestRepository.getOne(id);
        String json = getValueAsString(obj);
        existingRequest.setRequestText(json);
        selfCarePortalApprovalRequestRepository.save(existingRequest);
        return mapper.readValue(existingRequest.getRequestText(), cl);
    }

    public SelfCarePortalApprovalRequest save(SelfCarePortalApprovalRequest selfCarePortalApprovalRequest) {
        LOG.debug("Request to save self-care portal approval request : {}", selfCarePortalApprovalRequest);
        return selfCarePortalApprovalRequestRepository.save(selfCarePortalApprovalRequest);
    }

    @Transactional(readOnly = true)
    public SelfCarePortalApprovalRequest getOne(Integer id) {
        LOG.debug("Request to get self-care portal approval request : {}", id);
        return selfCarePortalApprovalRequestRepository.getOne(id);
    }

    @Transactional(readOnly = true)
    public Page<SelfCarePortalApprovalRequest> findAll(String textSearch,
                                                       String status,
                                                       String objectType,
                                                       Integer accountId,
                                                       LocalDateTime startDate,
                                                       LocalDateTime endDate,
                                                       Pageable pageable) {

        LOG.debug("Attempting to find self-care portal approval request by filters. " +
                "Search: {}, Status: {}, Dateset: {}, For Account: {}, Start date: {}, End date: {}",
            textSearch, status, objectType, accountId, startDate, endDate);

        final FiltersSpecification.Builder filterBuilder = new FiltersSpecification.Builder().lookFor(textSearch);

        if (status != null && !status.equals("all")) {
            filterBuilder.restrictOn(Filter.equals(STATUS, status));
        }

        if (objectType != null && !objectType.equals("")) {
            filterBuilder.restrictOn(Filter.equals(REQUEST_DATASET, objectType));
        }

        if (accountId != null)
            filterBuilder.restrictOn(Filter.equals("account", accountId));

        if (startDate != null && endDate != null) {
            filterBuilder.restrictOn(Filter.included("createdAt", startDate, endDate));
        }

        return selfCarePortalApprovalRequestRepository.findAll(filterBuilder.build(), pageable);
    }

    @Transactional(readOnly = true)
    public List<SelfCarePortalApprovalRequest> findAllinList(String textSearch,
                                                             String status,
                                                             String objectType,
                                                             Integer accountId,
                                                             LocalDateTime startDate,
                                                             LocalDateTime endDate,
                                                             Integer userId) {

        LOG.debug("Attempting to find self-care portal approval request by filters. " +
                "Search: {}, Status: {}, Dateset: {}, For Account: {}, Start date: {}, End date: {}",
            textSearch, status, objectType, accountId, startDate, endDate);

        final FiltersSpecification.Builder filterBuilder = new FiltersSpecification.Builder().lookFor(textSearch);

        if (status != null && !status.equals("all"))
            filterBuilder.restrictOn(Filter.equals(STATUS, status));

        if (objectType != null && !objectType.equals(""))
            filterBuilder.restrictOn(Filter.equals(REQUEST_DATASET, objectType));

        if (accountId != null)
            filterBuilder.restrictOn(Filter.equals("account", accountId));

        if (startDate != null && endDate != null)
            filterBuilder.restrictOn(Filter.included("createdAt", startDate, endDate));

        if (userId != null)
            filterBuilder.restrictOn(Filter.equals("user", userId));


        return selfCarePortalApprovalRequestRepository.findAll(filterBuilder.build());
    }

    public long countAllinList(String status, String objectType, Integer userId) {
        final FiltersSpecification.Builder filterBuilder = new FiltersSpecification.Builder();

        if (status != null && !status.equals("all"))
            filterBuilder.restrictOn(Filter.equals(STATUS, status));

        if (objectType != null && !objectType.equals(""))
            filterBuilder.restrictOn(Filter.equals(REQUEST_DATASET, objectType));

        if (userId != null)
            filterBuilder.restrictOn(Filter.equals("user", userId));

        return selfCarePortalApprovalRequestRepository.count(filterBuilder.build());
    }

    public List<AccountViewModel> getAccountFromApprovalRequestList(List<SelfCarePortalApprovalRequest> requests) throws IOException {
        List<AccountViewModel> accountList = new ArrayList<>();
        for (SelfCarePortalApprovalRequest request: requests) {
            AccountViewModel account = accountMapper.toViewModel(mapper.readValue(request.getRequestText(), Account.class));
            account.setScRequestId(request.getId());
            account.setScRequestType(request.getRequestType());
            accountList.add(account);
        }
        return accountList;
    }

    public List<AircraftRegistrationViewModel> getAircraftRegistrationFromApprovalRequestList(List<SelfCarePortalApprovalRequest> requests) throws IOException {
        List<AircraftRegistrationViewModel> arList = new ArrayList<>();
        for (SelfCarePortalApprovalRequest request: requests) {
            AircraftRegistrationViewModel aircraftRegistration = aircraftRegistrationMapper.toViewModel(mapper.readValue(request.getRequestText(), AircraftRegistration.class));
            aircraftRegistration.setScRequestId(request.getId());
            aircraftRegistration.setScRequestType(request.getRequestType());
            arList.add(aircraftRegistration);
        }
        return arList;
    }

    public List<FlightScheduleViewModel> getFlightScheduleFromApprovalRequestList(List<SelfCarePortalApprovalRequest> requests) throws IOException {
        List<FlightScheduleViewModel> flightScheduleList = new ArrayList<>();
        for (SelfCarePortalApprovalRequest request: requests) {
            FlightScheduleViewModel flightSchedule = flightScheduleMapper.toViewModel(mapper.readValue(request.getRequestText(), FlightSchedule.class));
            flightSchedule.setScRequestId(request.getId());
            flightSchedule.setScRequestType(request.getRequestType());
            flightScheduleList.add(flightSchedule);
        }
        return flightScheduleList;
    }

    SelfCarePortalApprovalRequest approveAccount(Integer id, SelfCarePortalApprovalRequest request) {
        LOG.debug("Request to approve Account creation form self-care portal : {}", request);
        SelfCarePortalApprovalRequest existingRequest = selfCarePortalApprovalRequestRepository.getOne(id);
        ModelUtils.merge(request, existingRequest);
        Account account = new Account();

        if (existingRequest.getRequestType().equals(RequestType.CREATE.toValue())) {
            try {
                account = mapper.readValue(existingRequest.getRequestText(), Account.class);
                entityValidator.validateItem(account);
                accountService.save(account);
            } catch (Exception e) {
                LOG.debug(EXCEPTION, e.getMessage(), e);
                String error = String.format("Cannot create Account %s: %s", account.getName(), e.getMessage().toLowerCase());
                throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION, new Exception(error));
            }
        }

        setStatus(existingRequest, RequestStatus.APPROVED);
        return selfCarePortalApprovalRequestRepository.save(existingRequest);
    }

    SelfCarePortalApprovalRequest approveAircraftRegistration(Integer id, SelfCarePortalApprovalRequest request) {
        SelfCarePortalApprovalRequest existingRequest = selfCarePortalApprovalRequestRepository.getOne(id);
        ModelUtils.merge(request, existingRequest);
        AircraftRegistration aircraftRegistration = new AircraftRegistration();

        try {
            if (existingRequest.getRequestType().equals(RequestType.CREATE.toValue())) {
                LOG.debug("Request to approve Aircraft Registration creation form self-care portal : {}", request);
                aircraftRegistration = mapper.readValue(existingRequest.getRequestText(), AircraftRegistration.class);
                entityValidator.validateItem(aircraftRegistration);
                aircraftRegistrationService.save(aircraftRegistration);
            }
            if (existingRequest.getRequestType().equals(RequestType.DELETE.toValue())) {
                LOG.debug("Request to approve Aircraft Registration deletion form self-care portal : {}", request.getObjectId());
                aircraftRegistration = aircraftRegistrationService.getOne(existingRequest.getObjectId());
                aircraftRegistrationService.delete(existingRequest.getObjectId());
            }
            if (existingRequest.getRequestType().equals(RequestType.UPDATE.toValue())) {
                LOG.debug("Request to approve Aircraft Registration modification form self-care portal : {}", request.getObjectId());
                AircraftRegistration existingAircraftRegistration = aircraftRegistrationService.getOne(existingRequest.getObjectId());
                aircraftRegistration = mapper.readValue(existingRequest.getRequestText(), AircraftRegistration.class);
                entityValidator.validateItem(aircraftRegistration);

                // we ignore the version of the record when updating
                if (!Objects.equals(existingAircraftRegistration.getVersion(), aircraftRegistration.getVersion())) {
                    aircraftRegistration.setVersion(existingAircraftRegistration.getVersion());
                }
                aircraftRegistrationService.update(existingRequest.getObjectId(), aircraftRegistration);
            }
        } catch (Exception e) {
            LOG.debug(EXCEPTION, e.getMessage(), e);
            String error = String.format("Cannot create/modify Aircraft Registration %s: %s", aircraftRegistration.getAircraftType(), e.getMessage().toLowerCase());
            throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION, new Exception(error));
        }

        setStatus(existingRequest, RequestStatus.APPROVED);
        return selfCarePortalApprovalRequestRepository.save(existingRequest);
    }

    SelfCarePortalApprovalRequest approveFlightSchedule(Integer id, SelfCarePortalApprovalRequest request) {
        SelfCarePortalApprovalRequest existingRequest = selfCarePortalApprovalRequestRepository.getOne(id);
        ModelUtils.merge(request, existingRequest);
        FlightSchedule flightSchedule = new FlightSchedule();

        try {
            if (existingRequest.getRequestType().equals(RequestType.CREATE.toValue())) {
                LOG.debug("Request to approve Flight Schedule creation form self-care portal : {}", request);
                flightSchedule = mapper.readValue(existingRequest.getRequestText(), FlightSchedule.class);
                entityValidator.validateItem(flightSchedule);
                flightScheduleService.create(flightSchedule);
            }
            if (existingRequest.getRequestType().equals(RequestType.DELETE.toValue())) {
                LOG.debug("Request to approve Flight Schedule deletion form self-care portal : {}", request.getObjectId());
                flightSchedule = flightScheduleService.findOne(existingRequest.getObjectId());
                flightScheduleService.delete(existingRequest.getObjectId());
            }
            if (existingRequest.getRequestType().equals(RequestType.UPDATE.toValue())) {
                LOG.debug("Request to approve Flight Schedule modification form self-care portal : {}", request.getObjectId());
                FlightSchedule existingFlightSchedule = flightScheduleService.findOne(existingRequest.getObjectId());
                flightSchedule = mapper.readValue(existingRequest.getRequestText(), FlightSchedule.class);
                entityValidator.validateItem(flightSchedule);

                // we ignore the version of the record when updating
                if (!Objects.equals(existingFlightSchedule.getVersion(), flightSchedule.getVersion())) {
                    flightSchedule.setVersion(existingFlightSchedule.getVersion());
                }
                flightScheduleService.update(existingRequest.getObjectId(), flightSchedule);
            }
        } catch (Exception e) {
            LOG.debug(EXCEPTION, e.getMessage(), e);
            String error = String.format("Cannot create/modify Flight Schedule %s: %s", flightSchedule.getFlightServiceNumber(), e.getMessage().toLowerCase());
            throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION, new Exception(error));
        }

        setStatus(existingRequest, RequestStatus.APPROVED);
        return selfCarePortalApprovalRequestRepository.save(existingRequest);
    }

    SelfCarePortalApprovalRequest rejectRequest(Integer id, SelfCarePortalApprovalRequest request) {
        LOG.debug("Request to reject Approval Request form self-care portal : {}", id);
        SelfCarePortalApprovalRequest existingRequest = selfCarePortalApprovalRequestRepository.getOne(id);
        ModelUtils.merge(request, existingRequest);
        setStatus(existingRequest, RequestStatus.REJECTED);
        return selfCarePortalApprovalRequestRepository.save(existingRequest);
    }

    private String getValueAsString(Object object) {
        String json = null;
        try {
            json = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            LOG.debug(EXCEPTION, e.getMessage(), e);
        }
        return json;
    }

    private SelfCarePortalApprovalRequest setStatus(SelfCarePortalApprovalRequest request, RequestStatus status){
        request.setStatus(status.toValue());
        request.setResponseDate(LocalDateTime.now());
        request.setRespondersName(userService.getUserByLogin (SecurityUtils.getCurrentUserLogin()).getName());
        return request;
    }

    String getRequestIdentifier(Integer id) throws IOException {
        SelfCarePortalApprovalRequest existingRequest = selfCarePortalApprovalRequestRepository.getOne(id);
        String requestIdentifier = null;
        if (existingRequest == null)
            return null;

        if (Objects.equals(existingRequest.getRequestDataset(), RequestDataset.ACCOUNT.toValue()))
            requestIdentifier = (accountMapper.toViewModel(mapper.readValue(existingRequest.getRequestText(), Account.class))).getName();

        if (Objects.equals(existingRequest.getRequestDataset(), RequestDataset.AIRCRAFT_REGISTRATION.toValue()))
            requestIdentifier = (aircraftRegistrationMapper.toViewModel(mapper.readValue(existingRequest.getRequestText(), AircraftRegistration.class))).getRegistrationNumber();

        if (Objects.equals(existingRequest.getRequestDataset(), RequestDataset.FLIGHT_SCHEDULE.toValue())) {
            requestIdentifier = (flightScheduleMapper.toViewModel(mapper.readValue(existingRequest.getRequestText(), FlightSchedule.class))).getFlightServiceNumber();
        }

        return requestIdentifier;
    }

    public Integer findOpenApprovalRequestByObjectId(Integer objectId) {
        return selfCarePortalApprovalRequestRepository.findOpenApprovalRequestByObjectId(objectId);
    }

    public long countAll() {
        return selfCarePortalApprovalRequestRepository.count();
    }

    public List<Account> getAccountFromApprovalRequestListForFlightSchedule(Integer id) {
        return selfCarePortalApprovalRequestRepository.getAccountFromApprovalRequestListForFlightSchedule(id);
    }
}
