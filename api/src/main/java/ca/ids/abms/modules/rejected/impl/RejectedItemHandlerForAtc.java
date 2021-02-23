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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.config.error.RejectedException;
import ca.ids.abms.modules.atcmovements.AtcMovementLog;
import ca.ids.abms.modules.atcmovements.AtcMovementLogCsvViewModel;
import ca.ids.abms.modules.atcmovements.AtcMovementLogMapper;
import ca.ids.abms.modules.atcmovements.AtcMovementLogService;
import ca.ids.abms.modules.atcmovements.AtcMovementLogViewModel;
import ca.ids.abms.modules.common.services.EntityValidator;
import ca.ids.abms.modules.dataimport.RejectableRowCsvImportServiceImpl;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementBuilderException;
import ca.ids.abms.modules.rejected.AbstractRejectedItemHandler;
import ca.ids.abms.modules.rejected.RejectedItem;
import ca.ids.abms.modules.rejected.RejectedItemStatus;
import ca.ids.abms.modules.rejected.RejectedItemType;

@Service
public class RejectedItemHandlerForAtc extends AbstractRejectedItemHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RejectedItemHandlerForAtc.class);
    private static final String RAW_TEXT = "rawText";

    public RejectedItemHandlerForAtc(EntityValidator validator,
                                     RejectableRowCsvImportServiceImpl<AtcMovementLogCsvViewModel> atcImportService,
                                     AtcMovementLogMapper atcMovementLogMapper,
                                     AtcMovementLogService atcMovementLogService,
                                     ObjectMapper jacksonObjectMapper) {
        super(validator, jacksonObjectMapper);
        this.atcMovementLogMapper = atcMovementLogMapper;
        this.atcMovementLogService = atcMovementLogService;
        this.atcImportService = atcImportService;
    }

    /* Mapping services */
    private AtcMovementLogMapper atcMovementLogMapper;

    private AtcMovementLogService atcMovementLogService;

    /* Import services */
    @Qualifier("recordableRowCsvImporter")
    private RejectableRowCsvImportServiceImpl<AtcMovementLogCsvViewModel> atcImportService;

    @Override
    protected void fixItemWithRawText (final RejectedItem rejectedItem) {
        assert rejectedItem != null;
        assert rejectedItem.getId() != null;

        final String rawText = rejectedItem.getRawText();

        assert rawText != null;
        LOG.debug("Starting to fix the rejected item {} using the rawText field.", rejectedItem.getId());
        try {
            /* Step 1: The raw text is converted into a CSV View Model */
            final List<AtcMovementLogCsvViewModel> csvModels = atcImportService.positionCsvToObject(rawText,
                AtcMovementLogCsvViewModel.class, false);

            if (CollectionUtils.isEmpty(csvModels)) {
                throw ExceptionFactory.getInvalidDataException("The raw text is not readable or empty",
                    RejectedItem.class, RAW_TEXT);
            }

            /* Step 2: The CSV View Model is converted into the entity and validated */
            final AtcMovementLog entity = atcMovementLogMapper.toModel(csvModels.get(0));
            validator.validateItem(entity);

            /* Step 3: The entity will be persisted if everything is fine */
            atcMovementLogService.createOrUpdate(entity);

        } catch (RejectedException re) {
            LOG.debug("Rejected item {}: {}; the item is not valid",
                rejectedItem.getId(), re.getReason());
            throw ExceptionFactory.getInvalidDataException(re);
        } catch (FlightMovementBuilderException fe) {
            LOG.debug("Rejected item {}: {}; flight movement could not be built",
                rejectedItem.getId(), fe.getLocalizedMessage());
            throw ExceptionFactory.getInvalidDataException(fe);
        } catch (CustomParametrizedException cpe) {
            LOG.debug("Rejected item {}: {}; the parameter(s) are not valid",
                rejectedItem.getId(), cpe.getLocalizedMessage());
            throw cpe;
        } catch (ConstraintViolationException cve) {
            LOG.debug("Rejected item {}: {}; constraint violation",
                rejectedItem.getId(), cve.getLocalizedMessage());
            throw ExceptionFactory.getInvalidDataException(cve);
        } catch (Exception generic) {
            LOG.debug("Rejected item {}: cannot fix the item because {}; raw text was: {}", rejectedItem.getId(),
                generic.getMessage(), rawText);
            throw ExceptionFactory.getInvalidDataException(RejectedItem.class, generic, RAW_TEXT);
        }
        /* The item has been fixed successfully */
        rejectedItem.setStatus(RejectedItemStatus.CORRECTED.toValue());
        LOG.debug("The rejected item {} has been fixed successfully", rejectedItem.getId());
    }

    @Override
    protected void fixItemWithJsonText (final RejectedItem rejectedItem) {
        assert rejectedItem != null;
        assert rejectedItem.getId() != null;

        final byte[] jsonText = rejectedItem.getJsonText();

        assert jsonText != null;
        LOG.debug("Starting to fix the rejected item {} using the jsonText field.", rejectedItem.getId());
        try {
            /* Step 1: The json text is converted into the entity */
            final AtcMovementLog entity = getJsonMapper().readValue(jsonText, AtcMovementLog.class);

            /* Step 2: The entity is validated */
            validator.validateItem(entity);

            /* Step 3: The entity will be persisted if everything is fine */
            atcMovementLogService.createOrUpdate(entity);

        } catch (JsonMappingException jme) {
            final Collection<String> fields = jme.getPath().stream().map(JsonMappingException.Reference::getFieldName)
                .collect(Collectors.toList());
            throw ExceptionFactory.getInvalidDataException(AtcMovementLog.class, jme,
                fields.toArray(new String[fields.size()]));
        } catch (RejectedException re) {
            LOG.debug("Rejected item {}: {}; the item is not valid yet",
                rejectedItem.getId(), re.getReason());
            throw ExceptionFactory.getInvalidDataException(re);
        } catch (FlightMovementBuilderException fe) {
            LOG.debug("Rejected item {}: {}; flight movement could not be built",
                rejectedItem.getId(), fe.getLocalizedMessage());
            throw ExceptionFactory.getInvalidDataException(fe);
        } catch (CustomParametrizedException cpe) {
            LOG.debug("Rejected item {}: {}; the parameter(s) are not valid",
                rejectedItem.getId(), cpe.getLocalizedMessage());
            throw cpe;
        } catch (ConstraintViolationException cve) {
            LOG.debug("Rejected item {}: {}; constraint violation",
                rejectedItem.getId(), cve.getLocalizedMessage());
            throw ExceptionFactory.getInvalidDataException(cve);
        } catch (Exception generic) {
            LOG.debug("Rejected item {}: cannot fix the item because {}; maybe the json object or the values are wrongs",
                rejectedItem.getId(), generic.getMessage());
            throw ExceptionFactory.getInvalidDataException(RejectedItem.class, generic, "jsonText");
        }
        /* The item has been fixed successfully */
        rejectedItem.setStatus(RejectedItemStatus.CORRECTED.toValue());
        LOG.debug("The rejected item {} has been fixed successfully", rejectedItem.getId());
    }

    @Override
    protected byte[] convertRawTextIntoJsonText(final String rawText) {
        byte[] jsonText = null;
        try {
            final List<AtcMovementLogCsvViewModel> csvModels = atcImportService.positionCsvToObject(rawText,
                AtcMovementLogCsvViewModel.class);
            if (CollectionUtils.isNotEmpty(csvModels)) {
                /* The row has been parsed into a AtcMovementLogCsvViewModel */
                final AtcMovementLogCsvViewModel csvModel = csvModels.get(0);

                /* The AtcMovementLogCsvViewModel is converted into the entity to check if everything has been parsed well */
                final AtcMovementLog entity = atcMovementLogMapper.toModel(csvModel);

                /* The entity is converted into a ViewModel so that the frontend can manage it through the jsonText field*/
                final AtcMovementLogViewModel dto = atcMovementLogMapper.toViewModel(entity);

                /* The ViewModel is converted into a String to store it into the jsonText field*/
                jsonText = getJsonMapper().writeValueAsBytes(dto);
            }
        } catch (Exception e) {
            throw ExceptionFactory.getInvalidDataException(RejectedItem.class, e, RAW_TEXT);
        }
        return jsonText;
    }

    @Override
    protected RejectedItemType resposibleForRecordType () {
        return RejectedItemType.ATS_MOVEMENTS_LOG;
    }
}
