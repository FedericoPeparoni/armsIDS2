package ca.ids.abms.modules.flight;

import ca.ids.abms.config.security.SecurityUtils;
import ca.ids.abms.modules.selfcareportal.approvalrequests.SelfCarePortalApprovalRequestService;
import ca.ids.abms.modules.selfcareportal.approvalrequests.enumerate.RequestDataset;
import ca.ids.abms.modules.selfcareportal.approvalrequests.enumerate.RequestType;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.modules.users.User;
import ca.ids.abms.modules.users.UserService;
import ca.ids.abms.util.billingcontext.BillingContextUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.config.error.RejectedException;
import ca.ids.abms.config.error.RejectedReasons;
import ca.ids.abms.modules.common.controllers.BulkLoaderComponent;
import ca.ids.abms.modules.common.dto.ItemLoaderObserver;
import ca.ids.abms.modules.common.dto.UploadReportViewModel;
import ca.ids.abms.modules.common.services.AbstractBulkLoader;
import ca.ids.abms.modules.common.services.EntityValidator;
import ca.ids.abms.modules.dataimport.RejectableCsvModel;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.rejected.RejectedItemService;
import ca.ids.abms.modules.rejected.RejectedItemType;
import ca.ids.abms.modules.uploadedfiles.UploadedFileService;

@Component(BulkLoaderComponent.FLIGHT_SCHEDULE_LOADER)
public class FlightScheduleLoader extends AbstractBulkLoader {

    private static final Logger LOG = LoggerFactory.getLogger(FlightScheduleLoader.class);

    private FlightScheduleMapper mapper;

    private FlightScheduleService service;

    private final UserService userService;

    private final SystemConfigurationService systemConfigurationService;

    private final SelfCarePortalApprovalRequestService selfCarePortalApprovalRequestService;

    public FlightScheduleLoader(
        RejectedItemService rejectedItemService,
        EntityValidator validator,
        UploadedFileService uploadedFileService,
        FlightScheduleMapper mapper,
        FlightScheduleService service,
        UserService userService,
        SystemConfigurationService systemConfigurationService,
        SelfCarePortalApprovalRequestService selfCarePortalApprovalRequestService,
        BillingContextUtility billingContextUtility
    ) {
        super(rejectedItemService, validator, uploadedFileService, billingContextUtility);
        this.mapper = mapper;
        this.service = service;
        this.userService = userService;
        this.systemConfigurationService = systemConfigurationService;
        this.selfCarePortalApprovalRequestService = selfCarePortalApprovalRequestService;
    }

    @Override
    protected UploadReportViewModel checkItem(RejectableCsvModel csvModel) {
        assert csvModel != null;
        assert csvModel instanceof FlightScheduleCsvViewModel;
        UploadReportViewModel checkViewModel = null;
        final FlightScheduleCsvViewModel dto = (FlightScheduleCsvViewModel) csvModel;
        FlightSchedule itemEntity;
        try {
            itemEntity = mapper.toModel(dto);
            validator.validateItem(itemEntity);
            final FlightSchedule toInactiveFlightSchedule = service.checkToInactive(itemEntity);
            if (toInactiveFlightSchedule != null) {
                LOG.debug("Checked record #{}: account={};", toInactiveFlightSchedule.getId(),
                            toInactiveFlightSchedule.getAccount());
                checkViewModel = createCheckViewModel(toInactiveFlightSchedule);
            }
        } catch (Exception generic) {
            LOG.debug("Discarded record with flight service number={}, because: {}", dto.getFlightServiceNumber(),
                    generic.getMessage());
        }
        return checkViewModel;
    }

    @Override
    protected void importItem(final RejectableCsvModel csvModel, ItemLoaderObserver o) throws RejectedException {
        assert csvModel != null;
        assert csvModel instanceof FlightScheduleCsvViewModel;
        final FlightScheduleCsvViewModel dto = (FlightScheduleCsvViewModel) csvModel;
        FlightSchedule itemEntity;
        try {
            itemEntity = mapper.toModel(dto);
        } catch (Exception generic) {
            LOG.debug("Discarded record with flight registration number={}, because: {}", dto.getFlightServiceNumber(),
                    generic.getMessage());
            throw new RejectedException(RejectedReasons.PARSE_ERROR, generic);
        }
        validator.validateItem(itemEntity);

        FlightSchedule persistedItem;
        boolean needApproval = systemConfigurationService.getBoolean(SystemConfigurationItemName.REQUIRE_ADMIN_APPROVAL_FOR_SC_FLIGHT_SCHEDULES);
        User currentUser = userService.getUserByLogin (SecurityUtils.getCurrentUserLogin());
        try {
            if (needApproval && currentUser.getIsSelfcareUser()) {
                final Integer flightScheduleId = service.checkIfExistsFlightSchedule(itemEntity);
                if (flightScheduleId != null) {
                    itemEntity.setId(flightScheduleId);
                    final Integer approvalRequestId = checkSelfCarePortalApprovalRequest(flightScheduleId);
                    if (approvalRequestId != null) {
                        persistedItem = selfCarePortalApprovalRequestService.updateUnprovenApprovalRequest(approvalRequestId, itemEntity, FlightSchedule.class);
                        LOG.debug("Updated unproven flightSchedule self-care approval request #{}: account={};", approvalRequestId, persistedItem.getAccount());
                    } else {
                        persistedItem = selfCarePortalApprovalRequestService.createNewApprovalRequest(
                            itemEntity, flightScheduleId, itemEntity.getAccount().getId(), FlightSchedule.class,
                            RequestDataset.FLIGHT_SCHEDULE, RequestType.UPDATE);
                        LOG.debug("Created new self-care approval request for flightSchedule modification #{}: account={};", persistedItem.getId(), persistedItem.getAccount());
                    }
                } else {
                    persistedItem = selfCarePortalApprovalRequestService.createNewApprovalRequest(
                        itemEntity, null, itemEntity.getAccount().getId(), FlightSchedule.class,
                        RequestDataset.FLIGHT_SCHEDULE, RequestType.CREATE);
                    LOG.debug("Created new self-care approval request for flightSchedule creation #{}: account={};", persistedItem.getId(), persistedItem.getAccount());
                }
            } else {
                persistedItem = service.createOrUpdate(itemEntity, null);
                LOG.debug("Updated flightSchedule record #{}: account={};", persistedItem.getId(), persistedItem.getAccount());
            }
        } catch (CustomParametrizedException cpe) {
            LOG.debug("Discarded record with flightServiceNumber={} depAd={} depTime={} account={}, because: {}",
                    itemEntity.getFlightServiceNumber(), itemEntity.getDepAd(), itemEntity.getDepTime(),
                    itemEntity.getAccount(), cpe.getLocalizedMessage());

            if (cpe.getObjectName() != null && cpe.getObjectName().equals(FlightMovement.class.getSimpleName())) {
                throw new RejectedException(RejectedReasons.FLIGHT_MOVEMENT_BUILDER_ERROR, cpe.getErrorDTO());
            } else {
                throw new RejectedException(cpe.getErrorDTO());
            }
        } catch (RejectedException rejected) {
            LOG.debug("Discarded record with flightServiceNumber={} depAd={} depTime={} account={}, because: {}",
                    itemEntity.getFlightServiceNumber(), itemEntity.getDepAd(), itemEntity.getDepTime(),
                    itemEntity.getAccount(), rejected.getMessage());
            throw rejected;
        } catch (Exception generic) {
            LOG.debug("Discarded record with flightServiceNumber={} depAd={} depTime={} account={}, because: {}:{}",
                    itemEntity.getFlightServiceNumber(), itemEntity.getDepAd(), itemEntity.getDepTime(),
                    itemEntity.getAccount(), generic.getClass().getSimpleName(), generic.getMessage());
            throw new RejectedException(ExceptionFactory.resolveManagedErrors(generic));
        }
    }

    @Override
    protected RejectedItemType resposibleForRecordType() {
        return RejectedItemType.FLIGHT_SCHEDULE;
    }

    private UploadReportViewModel createCheckViewModel(FlightSchedule flightSchedule) {
        UploadReportViewModel checkViewModel = new UploadReportViewModel();
        checkViewModel.setId(flightSchedule.getFlightServiceNumber());
        StringBuilder sb = new StringBuilder();
        sb.append(flightSchedule.getDepAd());
        sb.append(" ");
        sb.append(flightSchedule.getDepTime());
        sb.append(" ");
        sb.append(flightSchedule.getDestAd());
        sb.append(" ");
        sb.append(flightSchedule.getDestTime());
        sb.append(" ");
        sb.append(flightSchedule.getDailySchedule());
        checkViewModel.setDetails(sb.toString());
        return checkViewModel;
    }

    private Integer checkSelfCarePortalApprovalRequest(final Integer flightScheduleId) {
        return selfCarePortalApprovalRequestService.findOpenApprovalRequestByObjectId(flightScheduleId);
    }
}
