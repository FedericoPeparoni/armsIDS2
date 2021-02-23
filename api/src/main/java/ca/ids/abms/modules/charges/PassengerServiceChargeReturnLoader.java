package ca.ids.abms.modules.charges;

import ca.ids.abms.config.error.*;
import ca.ids.abms.config.security.SecurityUtils;
import ca.ids.abms.modules.common.dto.ItemLoaderObserver;
import ca.ids.abms.modules.common.dto.UploadReportViewModel;
import ca.ids.abms.modules.uploadedfiles.UploadedFileService;
import ca.ids.abms.modules.users.User;
import ca.ids.abms.modules.users.UserService;
import ca.ids.abms.util.billingcontext.BillingContextUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ca.ids.abms.modules.common.controllers.BulkLoaderComponent;
import ca.ids.abms.modules.common.services.AbstractBulkLoader;
import ca.ids.abms.modules.common.services.EntityValidator;
import ca.ids.abms.modules.dataimport.RejectableCsvModel;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.rejected.RejectedItemService;
import ca.ids.abms.modules.rejected.RejectedItemType;


@Component(BulkLoaderComponent.PASSENGER_CHARGE_RETURN_LOADER)
public class PassengerServiceChargeReturnLoader extends AbstractBulkLoader {

    private static final Logger LOG = LoggerFactory.getLogger(PassengerServiceChargeReturnLoader.class);

    private PassengerServiceChargeReturnMapper pscrMovementLogMapper;
    private PassengerServiceChargeReturnService pscrMovementLogService;
    private UserService userService;

    public PassengerServiceChargeReturnLoader(RejectedItemService rejectedItemService,
                                              EntityValidator validator,
                                              UploadedFileService uploadedFileService,
                                              PassengerServiceChargeReturnMapper pscrMovementLogMapper,
                                              PassengerServiceChargeReturnService pscrMovementLogService,
                                              BillingContextUtility billingContextUtility,
                                              UserService userService) {
        super(rejectedItemService, validator, uploadedFileService, billingContextUtility);
        this.pscrMovementLogMapper = pscrMovementLogMapper;
        this.pscrMovementLogService = pscrMovementLogService;
        this.userService = userService;
    }

    @Override
    protected RejectedItemType resposibleForRecordType() {
        return RejectedItemType.PASSENGER_SERVICE_CHARGE_RETURNS;
    }

    @Override
    protected void importItem(final RejectableCsvModel csvModel, ItemLoaderObserver o) {
        assert csvModel != null;
        assert csvModel instanceof PassengerServiceChargeReturnCsvViewModel;
        final PassengerServiceChargeReturnCsvViewModel dto = (PassengerServiceChargeReturnCsvViewModel)csvModel;
        PassengerServiceChargeReturn itemEntity;

        User user = userService.getUserByLogin (SecurityUtils.getCurrentUserLogin());

        ErrorVariables detailVariables = new ErrorVariables();
        detailVariables.addEntry("userId", user.getId().toString());
        detailVariables.addEntry("accountName", dto.getAccountName());

        try {
            itemEntity = pscrMovementLogMapper.toModel(dto);
            itemEntity.setCreatedBySelfCare(user.getIsSelfcareUser());
        } catch (CustomParametrizedException cpe) {
            LOG.debug("Discarded record with flightId={}, because: {}", dto.getFlightId(),
                cpe.getLocalizedMessage());
                throw new RejectedException(RejectedReasons.PARSE_ERROR, cpe.getErrorDTO());
        } catch (Exception generic) {
            LOG.debug("Discarded record with flightId={}, because: {}",
                dto.getFlightId(), generic.getMessage());
            throw new RejectedException(RejectedReasons.PARSE_ERROR, generic);
        }

        if (user.getIsSelfcareUser() && itemEntity.getAccount() == null) {
            LOG.debug("Discarded record with null account because it is required from the self-care portal");

            ErrorDTO errorDto = new ErrorDTO.Builder()
                .setErrorMessage("Discarded record with null account")
                .appendDetails("Discarded record with null account. An account is required from self-care portal.")
                .setDetailMessageVariables(detailVariables)
                .build();

            throw new RejectedException(RejectedReasons.PARSE_ERROR, errorDto);
        }

        if (user.getIsSelfcareUser() && !itemEntity.getAccount().containsAccountUser(user)) {
            LOG.debug("Discarded record with account: {}, because the user {} doesn't have permissions for account {}", dto.getAccountName(),
                user.getId(), dto.getAccountName());

            ErrorDTO errorDto = new ErrorDTO.Builder()
                .setErrorMessage("User doesn't have permission for account")
                .appendDetails("Discarded record with account number {{accountName}}, because the user doesn't have permissions for this account")
                .setDetailMessageVariables(detailVariables)
                .build();

            throw new RejectedException(RejectedReasons.PARSE_ERROR, errorDto);
        }

        validator.validateItem(itemEntity);

        try {
            final PassengerServiceChargeReturn persistedItem = pscrMovementLogService.createOrUpdate(itemEntity, o);
            LOG.debug("Updated record #{}: flightId={};", persistedItem.getId(), persistedItem.getFlightId());
        } catch (CustomParametrizedException cpe) {
            LOG.debug("Discarded record with flightId={} dayOfFlight={}, because: {}",
                itemEntity.getFlightId(), itemEntity.getDayOfFlight(), itemEntity.getDepartureTime());

            // if object is FlightMovement, set rejected reason to flight movement builder error
            // this will help the user know the message is referring to FlightMovement
            if (cpe.getObjectName() != null && cpe.getObjectName().equals(FlightMovement.class.getSimpleName())) {
                throw new RejectedException(RejectedReasons.FLIGHT_MOVEMENT_BUILDER_ERROR, cpe.getErrorDTO());
            } else {
                throw new RejectedException(cpe.getErrorDTO());
            }
        } catch (RejectedException rejected) {
            LOG.debug("Discarded record with flightId={} dayOfFlight={}, because: {}",
                itemEntity.getFlightId(), itemEntity.getDayOfFlight(), rejected.getMessage());
            throw rejected;
        } catch (Exception generic) {
            LOG.debug("Discarded record with flightId={} dayOfFlight={}, because: {}:{}",
                itemEntity.getFlightId(), itemEntity.getDayOfFlight(),
                generic.getClass().getSimpleName(), generic.getMessage());
            throw new RejectedException(ExceptionFactory.resolveManagedErrors(generic));
        }
    }

    @Override
    protected UploadReportViewModel checkItem(RejectableCsvModel aCsvModel) {
        return null;
    }
}
