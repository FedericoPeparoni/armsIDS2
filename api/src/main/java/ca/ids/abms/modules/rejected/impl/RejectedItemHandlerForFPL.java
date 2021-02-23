package ca.ids.abms.modules.rejected.impl;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.config.error.RejectedException;
import ca.ids.abms.modules.common.services.EntityValidator;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.FlightMovementService;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementBuilderException;
import ca.ids.abms.modules.rejected.AbstractRejectedItemHandler;
import ca.ids.abms.modules.rejected.RejectedItem;
import ca.ids.abms.modules.rejected.RejectedItemStatus;
import ca.ids.abms.modules.rejected.RejectedItemType;
import ca.ids.abms.modules.spatiareader.dto.FplObjectDto;
import ca.ids.abms.modules.spatiareader.mapper.CplFplMapper;

@Service
public class RejectedItemHandlerForFPL extends AbstractRejectedItemHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RejectedItemHandlerForFPL.class);

    public RejectedItemHandlerForFPL(EntityValidator validator,
                                     CplFplMapper mapper,
                                     FlightMovementService flightMovementService,
                                     ObjectMapper jacksonObjectMapper
                                     ) {
        super(validator, jacksonObjectMapper);
        this.mapper = mapper;
        this.flightMovementService = flightMovementService;
    }

    /* Mapping services */
    private CplFplMapper mapper;

    private FlightMovementService flightMovementService;

    @Override
    protected void fixItemWithRawText (final RejectedItem rejectedItem) {
        throw new CustomParametrizedException("Not allowed");
    }

    @Override
    protected void fixItemWithJsonText (final RejectedItem rejectedItem) {
        assert rejectedItem != null;
        assert rejectedItem.getId() != null;

        final byte[] jsonText = rejectedItem.getJsonText();

        assert jsonText != null;

        FlightMovement savedFlightMovement;

        LOG.debug("Starting to fix the rejected item {} using the jsonText field.", rejectedItem.getId());
        try {
            /* Step 1: The json text is converted into the dto and than into an entity */
            final FplObjectDto dto = getJsonMapper().readValue(jsonText, FplObjectDto.class);
            if (dto.getDayOfFlight() != null && dto.getDayOfFlight().toEpochDay() == 0) {
                dto.setDayOfFlight(null);
            }

            /* Step 2: The entity is validated */
            validator.validateItem(dto);

            /* Step 3: The entity will be persisted if everything is fine */
            // Validate
            savedFlightMovement = flightMovementService.createUpdateFlightMovementFromSpatia(dto, true);
            
            // Calculate all charges, route segments, etc.
            flightMovementService.calculateFlightMovementFromFplObject(savedFlightMovement);
            flightMovementService.checkWhitelisting(savedFlightMovement);

        } catch (JsonMappingException jme) {
            final Collection<String> fields = jme.getPath().stream().map(JsonMappingException.Reference::getFieldName)
                .collect(Collectors.toList());
            throw ExceptionFactory.getInvalidDataException(FplObjectDto.class, jme,
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
                rejectedItem.getId(), generic.getMessage(), generic);
            throw ExceptionFactory.getInvalidDataExceptionFromCause(RejectedItem.class, generic, "jsonText");
        }

        if (savedFlightMovement != null) {
            /* The item has been fixed successfully */
            rejectedItem.setStatus(RejectedItemStatus.CORRECTED.toValue());
            LOG.debug("The rejected item {} has been fixed successfully", rejectedItem.getId());
        } else {
            /* The item has not been fixed */
            LOG.debug("The rejected item {} has NOT been fixed, maybe the related FLM is not editable", rejectedItem.getId());
        }
    }

    @Override
    protected byte[] convertRawTextIntoJsonText(final String rawText) {
        throw new CustomParametrizedException("Not allowed");
    }

    @Override
    protected RejectedItemType resposibleForRecordType () {
        return RejectedItemType.FLIGHT_MOVEMENTS;
    }
}
