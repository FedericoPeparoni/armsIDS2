package ca.ids.abms.modules.aircraft;

import ca.ids.abms.config.error.*;
import ca.ids.abms.config.security.SecurityUtils;
import ca.ids.abms.modules.common.controllers.BulkLoaderComponent;
import ca.ids.abms.modules.common.dto.ItemLoaderObserver;
import ca.ids.abms.modules.common.dto.UploadReportViewModel;
import ca.ids.abms.modules.common.services.AbstractBulkLoader;
import ca.ids.abms.modules.common.services.EntityValidator;
import ca.ids.abms.modules.dataimport.RejectableCsvModel;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.rejected.RejectedItemService;
import ca.ids.abms.modules.rejected.RejectedItemType;
import ca.ids.abms.modules.reports2.common.ReportHelper;
import ca.ids.abms.modules.selfcareportal.approvalrequests.SelfCarePortalApprovalRequestService;
import ca.ids.abms.modules.selfcareportal.approvalrequests.enumerate.RequestDataset;
import ca.ids.abms.modules.selfcareportal.approvalrequests.enumerate.RequestType;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.modules.uploadedfiles.UploadedFileService;
import ca.ids.abms.modules.users.User;
import ca.ids.abms.modules.users.UserService;
import ca.ids.abms.util.billingcontext.BillingContextUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component(BulkLoaderComponent.AIRCRAFT_REGISTRATION_LOADER)
public class AircraftRegistrationLoader extends AbstractBulkLoader {

    private static final Logger LOG = LoggerFactory.getLogger(AircraftRegistrationLoader.class);

    private final AircraftRegistrationMapper mapper;

    private final AircraftRegistrationService service;

    private final UserService userService;

    private final SystemConfigurationService systemConfigurationService;

    private final SelfCarePortalApprovalRequestService selfCarePortalApprovalRequestService;

    private final ReportHelper reportHelper;

    private Boolean isSelfCare = false;

    public AircraftRegistrationLoader(
        final RejectedItemService rejectedItemService,
        final EntityValidator validator,
        final UploadedFileService uploadedFileService,
        final AircraftRegistrationMapper mapper,
        final AircraftRegistrationService service,
        final UserService userService,
        SystemConfigurationService systemConfigurationService,
        SelfCarePortalApprovalRequestService selfCarePortalApprovalRequestService,
        final ReportHelper reportHelper,
        final BillingContextUtility billingContextUtility
    ) {
        super(rejectedItemService, validator, uploadedFileService, billingContextUtility);
        this.mapper = mapper;
        this.service = service;
        this.userService = userService;
        this.systemConfigurationService = systemConfigurationService;
        this.selfCarePortalApprovalRequestService = selfCarePortalApprovalRequestService;
        this.reportHelper = reportHelper;
    }

    @Override
    protected UploadReportViewModel checkItem(RejectableCsvModel aCsvModel) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void importItem(final RejectableCsvModel csvModel, ItemLoaderObserver o) throws RejectedException {
        assert csvModel != null;
        assert csvModel instanceof AircraftRegistrationCsvViewModel;
        final AircraftRegistrationCsvViewModel dto = (AircraftRegistrationCsvViewModel) csvModel;
        AircraftRegistration itemEntity;
        User currentUser = userService.getUserByLogin (SecurityUtils.getCurrentUserLogin());

        try {
            itemEntity = mapper.toModel(dto);

            // set is_local - true if country of registration matches ANSP country, otherwise false
            String countryCode = systemConfigurationService.getCurrentValue(SystemConfigurationItemName.ANSP_COUNTRY_CODE);
            itemEntity.setIsLocal(countryCode.equals(itemEntity.getCountryOfRegistration().getCountryCode()));

            // handle weight by system configuration setting before importing
            // database only stores weight in short tons
            itemEntity.setMtowOverride(reportHelper.convertMTOWinTons(itemEntity.getMtowOverride()));

            itemEntity.setCreatedBySelfCare(isSelfCare);
            ErrorVariables detailVariables = new ErrorVariables();

            detailVariables.addEntry("flightRegNum", dto.getRegistrationNumber());
            detailVariables.addEntry("userId", currentUser.getId().toString());
            detailVariables.addEntry("accountName", dto.getAccountName());

            if(isSelfCare) {
            	if(itemEntity.getAccount() != null && itemEntity.getAccount().hasAccountUsers()) {
            		// self-care account
            		if(currentUser.getIsSelfcareUser() && !itemEntity.getAccount().containsAccountUser (currentUser)) {
                        LOG.debug("Discarded record with flight registration number={}, because user: {} doesn't have permissions for account {}", dto.getRegistrationNumber(),
            				currentUser.getId(), dto.getAccountName());

                        ErrorDTO errorDto = new ErrorDTO.Builder()
                            .setErrorMessage("User doesn't have permission for account")
                            .appendDetails("Discarded record with flight registration number={{flightRegNum}}, because user: {{userId}}, doesn't have permissions for account {{accountName}}")
                            .setDetailMessageVariables(detailVariables)
                            .build();

            			throw new RejectedException(RejectedReasons.PARSE_ERROR, errorDto);
            		}
            	}else {
                    LOG.debug("Discarded record with flight registration number={}, because account: {} is not self-care", dto.getRegistrationNumber(),
            				dto.getAccountName());
            		ErrorDTO errorDto = new ErrorDTO.Builder()
                            .setErrorMessage("Account is not self-care")
                            .appendDetails("Account  {{accountName}} is not self-care")
                            .setDetailMessageVariables(detailVariables)
                            .build();
            		throw new RejectedException(RejectedReasons.PARSE_ERROR, errorDto);
            	}
            }
        } catch (CustomParametrizedException cpe) {
            LOG.debug("Discarded record with flight registration number={}, because: {}", dto.getRegistrationNumber(),
                    cpe.getLocalizedMessage());
            throw new RejectedException(RejectedReasons.PARSE_ERROR, cpe.getErrorDTO());
        } catch (Exception generic) {
            LOG.debug("Discarded record with flight registration number={}, because: {}", dto.getRegistrationNumber(),
                    generic.getMessage());

            throw new RejectedException(RejectedReasons.PARSE_ERROR, generic);
        }
        validator.validateItem(itemEntity);

        AircraftRegistration persistedItem;
        boolean needApproval = systemConfigurationService.getBoolean(SystemConfigurationItemName.REQUIRE_ADMIN_APPROVAL_FOR_SC_AIRCRAFT_REGISTRATION);
        try {
            if (needApproval && currentUser.getIsSelfcareUser()) {
                final Integer aircraftRegistrationId = service.checkIfExistsAircraftRegistration(itemEntity);
                if (aircraftRegistrationId != null) {
                    itemEntity.setId(aircraftRegistrationId);
                    persistedItem = selfCarePortalApprovalRequestService.createNewApprovalRequest(
                        itemEntity, aircraftRegistrationId, itemEntity.getAccount().getId(), AircraftRegistration.class,
                        RequestDataset.AIRCRAFT_REGISTRATION, RequestType.UPDATE);
                    LOG.debug("Created new self-care approval request for aircraft registration modification #{}: account={};", persistedItem.getId(), persistedItem.getAccount());
                } else {
                    service.validate(itemEntity, null);
                    persistedItem = selfCarePortalApprovalRequestService.createNewApprovalRequest(
                        itemEntity, null, itemEntity.getAccount().getId(), AircraftRegistration.class,
                        RequestDataset.AIRCRAFT_REGISTRATION, RequestType.CREATE);
                    LOG.debug("Created new self-care approval request for aircraft registration creation #{}: account={};", persistedItem.getId(), persistedItem.getAccount());
                }
            } else {
                persistedItem = service.createOrUpdate(itemEntity);
                LOG.debug("Updated record #{}: account={};", persistedItem.getId(), persistedItem.getAccount());
            }
        } catch (CustomParametrizedException cpe) {
            LOG.debug("Discarded record with registration number={} startDate={} expiryDate={} account={}, because: {}",
                    itemEntity.getRegistrationNumber(), itemEntity.getRegistrationStartDate(),
                    itemEntity.getRegistrationExpiryDate(), itemEntity.getAccount(), cpe.getLocalizedMessage());

            if (cpe.getObjectName() != null && cpe.getObjectName().equals(FlightMovement.class.getSimpleName())) {
                throw new RejectedException(RejectedReasons.FLIGHT_MOVEMENT_BUILDER_ERROR, cpe.getErrorDTO());
            } else {
                throw new RejectedException(cpe.getErrorDTO());
            }
        } catch (RejectedException rejected) {
            LOG.debug("Discarded record with registration number={} startDate={} expiryDate={} account={}, because: {}",
                    itemEntity.getRegistrationNumber(), itemEntity.getRegistrationStartDate(),
                    itemEntity.getRegistrationExpiryDate(), itemEntity.getAccount(), rejected.getMessage());
            throw rejected;
        } catch (Exception generic) {
            LOG.debug(
                    "Discarded record with registration number={} startDate={} expiryDate={} account={}, because: {}:{}",
                    itemEntity.getRegistrationNumber(), itemEntity.getRegistrationStartDate(),
                    itemEntity.getRegistrationExpiryDate(), itemEntity.getAccount(), generic.getClass().getSimpleName(),
                    generic.getMessage());
            throw new RejectedException(ExceptionFactory.resolveManagedErrors(generic));
        }
    }

    @Override
    protected RejectedItemType resposibleForRecordType() {
        return RejectedItemType.AIRCRAFT_REGISTRATION;
    }

	public void setIsSelfCare(Boolean isSelfCare) {
		this.isSelfCare = isSelfCare;
	}


}
