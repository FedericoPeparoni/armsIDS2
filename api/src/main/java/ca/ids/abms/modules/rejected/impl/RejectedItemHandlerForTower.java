package ca.ids.abms.modules.rejected.impl;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.config.error.RejectedException;
import ca.ids.abms.modules.common.services.EntityValidator;
import ca.ids.abms.modules.dataimport.RejectableRowCsvImportServiceImpl;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementBuilderException;
import ca.ids.abms.modules.rejected.AbstractRejectedItemHandler;
import ca.ids.abms.modules.rejected.RejectedItem;
import ca.ids.abms.modules.rejected.RejectedItemStatus;
import ca.ids.abms.modules.rejected.RejectedItemType;
import ca.ids.abms.modules.towermovements.TowerMovementLog;
import ca.ids.abms.modules.towermovements.TowerMovementLogCsvViewModel;
import ca.ids.abms.modules.towermovements.TowerMovementLogMapper;
import ca.ids.abms.modules.towermovements.TowerMovementLogService;
import ca.ids.abms.modules.towermovements.TowerMovementLogViewModel;

@Service
public class RejectedItemHandlerForTower extends AbstractRejectedItemHandler {

    private final Logger log = LoggerFactory.getLogger(RejectedItemHandlerForTower.class);

    public RejectedItemHandlerForTower(EntityValidator validator,
                                       RejectableRowCsvImportServiceImpl<TowerMovementLogCsvViewModel> importService,
                                       TowerMovementLogMapper mapper,
                                       TowerMovementLogService service,
                                       ObjectMapper jacksonObjectMapper) {
        super( validator, jacksonObjectMapper);
        this.importService = importService;
        this.mapper = mapper;
        this.service = service;
    }

    /* Import services */
    @Autowired
    @Qualifier("recordableRowCsvImporter")
    private RejectableRowCsvImportServiceImpl<TowerMovementLogCsvViewModel> importService;

    /* Mapping services */
    @Autowired
    private TowerMovementLogMapper mapper;

    @Autowired
    private TowerMovementLogService service;

    @Override
    protected void fixItemWithRawText (final RejectedItem rejectedItem) throws CustomParametrizedException {
        assert rejectedItem != null;
        assert rejectedItem.getId() != null;

        final String rawText = rejectedItem.getRawText();

        assert rawText != null;
        log.debug("Starting to fix the rejected item {} using the rawText field.", rejectedItem.getId());
        try {
            /* Step 1: The raw text is converted into a CSV View Model */
            final List<TowerMovementLogCsvViewModel> csvModels = importService.positionCsvToObject(rawText,
                TowerMovementLogCsvViewModel.class, false);

            if (CollectionUtils.isEmpty(csvModels)) {
                throw ExceptionFactory.getInvalidDataException("The raw text is not readable or empty",
                    RejectedItem.class, "rawText");
            }

            /* Step 2: The CSV View Model is converted into the entity and validated */
            final TowerMovementLog entity = mapper.toModel(csvModels.get(0));
            validator.validateItem(entity);

            /* Step 3: The entity will be persisted if everything is fine */
            service.createOrUpdateByFlightId(entity);
        } catch (RejectedException re) {
            log.debug("Rejected item {}: {}; the item is not valid yet",
                rejectedItem.getId(), re.getReason());
            throw ExceptionFactory.getInvalidDataException(re);
        } catch (FlightMovementBuilderException fe) {
            log.debug("Rejected item {}: {}; flight movement could not be built",
                rejectedItem.getId(), fe.getLocalizedMessage());
            throw ExceptionFactory.getInvalidDataException(fe);
        } catch (CustomParametrizedException cpe) {
            log.debug("Rejected item {}: {}; the parameter(s) are not valid",
                rejectedItem.getId(), cpe.getLocalizedMessage());
            throw cpe;
        } catch (ConstraintViolationException cve) {
            log.debug("Rejected item {}: {}; constraint violation",
                rejectedItem.getId(), cve.getLocalizedMessage());
            throw ExceptionFactory.getInvalidDataException(cve);
        } catch (Exception generic) {
            log.debug("Rejected item {}: cannot fix the item because {}; raw text was: {}", rejectedItem.getId(),
                generic.getMessage(), rawText);
            throw ExceptionFactory.getInvalidDataException(RejectedItem.class, generic, "rawText");
        }
        /* The item has been fixed successfully */
        rejectedItem.setStatus(RejectedItemStatus.CORRECTED.toValue());
        log.debug("The rejected item {} has been fixed successfully", rejectedItem.getId());
    }

    @Override
    protected void fixItemWithJsonText (final RejectedItem rejectedItem) throws CustomParametrizedException {
        assert rejectedItem != null;
        assert rejectedItem.getId() != null;

        final byte[] jsonText = rejectedItem.getJsonText();

        assert jsonText != null;
        log.debug("Starting to fix the rejected item {} using the jsonText field.", rejectedItem.getId());
        try {
            /* Step 1: The json text is converted into an entity */
            final TowerMovementLog entity = getJsonMapper().readValue(jsonText, TowerMovementLog.class);

            /* Step 2: The entity is validated */
            validator.validateItem(entity);

            /* Step 3: The entity will be persisted if everything is fine */
            service.createOrUpdateByFlightId(entity);
        } catch (JsonMappingException jme) {
            final Collection<String> fields = jme.getPath().stream().map(JsonMappingException.Reference::getFieldName)
                .collect(Collectors.toList());
            throw ExceptionFactory.getInvalidDataException(TowerMovementLog.class, jme,
                fields.toArray(new String[fields.size()]));
        } catch (RejectedException re) {
            log.debug("Rejected item {}: {}; the item is not valid yet",
                rejectedItem.getId(), re.getReason());
            throw ExceptionFactory.getInvalidDataException(re);
        } catch (FlightMovementBuilderException fe) {
            log.debug("Rejected item {}: {}; flight movement could not be built",
                rejectedItem.getId(), fe.getLocalizedMessage());
            throw ExceptionFactory.getInvalidDataException(fe);
        } catch (CustomParametrizedException cpe) {
            log.debug("Rejected item {}: {}; the parameter(s) are not valid",
                rejectedItem.getId(), cpe.getLocalizedMessage());
            throw cpe;
        } catch (ConstraintViolationException cve) {
            log.debug("Rejected item {}: {}; constraint violation",
                rejectedItem.getId(), cve.getLocalizedMessage());
            throw ExceptionFactory.getInvalidDataException(cve);
        } catch (Exception generic) {
            log.debug("Rejected item {}: cannot fix the item because {}; maybe the json object or the values are wrongs",
                rejectedItem.getId(), generic.getMessage());
            throw ExceptionFactory.getInvalidDataException(RejectedItem.class, generic, "jsonText");
        }
        /* The item has been fixed successfully */
        rejectedItem.setStatus(RejectedItemStatus.CORRECTED.toValue());
        log.debug("The rejected item {} has been fixed successfully", rejectedItem.getId());
    }


    @Override
    protected byte[] convertRawTextIntoJsonText(final String rawText) throws CustomParametrizedException {
        byte[] jsonText = null;
        try {
            final List<TowerMovementLogCsvViewModel> csvModels = importService.positionCsvToObject(rawText,
                TowerMovementLogCsvViewModel.class);
            if (CollectionUtils.isNotEmpty(csvModels)) {
            /* The row has been parsed into a CSV View Model */
                final TowerMovementLogCsvViewModel csvModel = csvModels.get(0);

            /* The CSV View Model is converted into the entity to check if everything has been parsed well */
                final TowerMovementLog entity = mapper.toModel(csvModel);

            /* The entity is converted into a View Model so that the frontend can manage it through the jsonText field*/
                final TowerMovementLogViewModel dto = mapper.toViewModel(entity);

            /* The View Model is converted into a String to store it into the jsonText field*/
                jsonText = getJsonMapper().writeValueAsBytes(dto);
            }
        } catch (Exception e) {
            throw ExceptionFactory.getInvalidDataException(RejectedItem.class, e, "rawText");
        }
        return jsonText;
    }

    @Override
    protected RejectedItemType resposibleForRecordType () {
        return RejectedItemType.TOWER_AIRCRAFT_PASSENGER_MOVEMENTS_LOG;
    }
}
