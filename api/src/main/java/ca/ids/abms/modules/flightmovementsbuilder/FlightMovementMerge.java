package ca.ids.abms.modules.flightmovementsbuilder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import ca.ids.abms.modules.flightmovementsbuilder.utility.DeltaFlightUtility;
import ca.ids.abms.modules.flightmovementsbuilder.utility.ThruFlightPlanUtility;
import ca.ids.abms.modules.util.models.ApplicationConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementSource;
import ca.ids.abms.modules.flightmovementsbuilder.utility.FlightMovementBuilderMergeUtility;
import ca.ids.abms.modules.util.models.ModelUtils;
import ca.ids.abms.util.StringUtils;
import org.springframework.stereotype.Component;

/**
 * Created by c.talpa on 04/03/2017.
 */
@Component
public class FlightMovementMerge {

    private static final Logger LOG = LoggerFactory.getLogger(FlightMovementMerge.class);

    private static final String[] FIELDS_CREATED_FROM_DELTA_OR_THRU = {
        "arrival_ad", "arrival_time", "item18_dep", "item18_dest", "delta_flight", "thru_flight" };
    private static final String[] FIELDS_CREATED_FROM_NETWORK = {
        "actual_departure_time", "dep_ad", "dest_ad", "flightLevel", "cruisingSpeedOrMachNumber" };
    private static final String[] FIELDS_CREATED_FROM_PASSENGER = {
        "passengers_chargeable_domestic", "passengers_chargeable_intern", "passengers_child",
        "passengers_joining_adult", "passengers_transit_adult" };

    private final DeltaFlightUtility deltaFlightUtility;
    private final ThruFlightPlanUtility thruFlightPlanUtility;

    public FlightMovementMerge(final DeltaFlightUtility deltaFlightUtility, final ThruFlightPlanUtility thruFlightPlanUtility) {
        this.deltaFlightUtility = deltaFlightUtility;
        this.thruFlightPlanUtility = thruFlightPlanUtility;
    }

    /**
     * If Flight Movement was updated manually, NO updates can overwrite those fields.
     * When a Flight Movement created by a FPL  receives a further FPL Update, this last update OVERWRITE all the fields;
     */
    FlightMovement overwriteAllFieldsExceptUserUpdated(
        final FlightMovement existingFlightMovement, final FlightMovement updateFlightMovement
    ) {
        return overwriteAllFieldsExceptUserUpdated(existingFlightMovement, updateFlightMovement, false);
    }

    /**
     * If Flight Movement was updated manually, NO updates can overwrite those fields.
     * When a Flight Movement created by a FPL  receives a further FPL Update, this last update OVERWRITE all the fields;
     */
    FlightMovement overwriteAllFieldsExceptUserUpdated(
        final FlightMovement existingFlightMovement, final FlightMovement updateFlightMovement, final Boolean isFplObject
    ) {
        LOG.debug("Overwrite all fields except user updated");

        if (!validateInputForOverwriteAllFieldsExceptUserUpdated(existingFlightMovement, updateFlightMovement))
            return null;

        // loop through each manually changed field and add to list of excluded fields
        String manuallyChangedField = existingFlightMovement.getManuallyChangedFields();
        String[] manuallyChangedFields = FlightMovementBuilderMergeUtility.getNameManuallyChangedFields(manuallyChangedField);

        // excluded fields must be in camelCase and not snake_case
        // should always exclude id and source fields
        List<String> excludedFields = new ArrayList<>();
        excludedFields.add("id");
        excludedFields.add("source");
        if (manuallyChangedFields != null) {
            for (String snakeName : manuallyChangedFields) {
                String camelName = StringUtils.snakeCaseToCamelCase(snakeName);
                excludedFields.add(camelName);
            }
        }

        // if existing flight movement is a delta or thru flight and update is not from a flight
        // plan object then prevent fields from  being overwritten as only flight plans objects
        // support delta and thru field logic
        if (!isFplObject && isDeltaOrThruFlight(existingFlightMovement)) {
            for (String deltaThruField : FIELDS_CREATED_FROM_DELTA_OR_THRU) {
                excludedFields.add(StringUtils.snakeCaseToCamelCase(deltaThruField));
            }
        }

        // merge updated flight movement into existing excluding id, source, and manually changed fields
        // keep same flight movement object during the update to avoid creation of extra/orphan route segments
        // see commit 3f887e17 and api!629 merge request for original discussion
        mergeFlightMovements(updateFlightMovement, existingFlightMovement, excludedFields.toArray(new String[]{}));

        return existingFlightMovement;
    }

    /**
     * Overwrite passenger fields only except where user manually charged. Returns the existingFlightMovement
     * object to follow existing logic method structure.
     */
    FlightMovement overwritePassengerFieldsExceptUserUpdated(
        final FlightMovement existingFlightMovement, final FlightMovement updateFlightMovement
    ) {
        LOG.debug("Overwrite passenger fields except user updated");

        // loop through each manually changed field and add to list of excluded fields
        String manuallyChangedField = existingFlightMovement.getManuallyChangedFields();
        String[] manuallyChangedFields = FlightMovementBuilderMergeUtility.getNameManuallyChangedFields(manuallyChangedField);

        // define passenger fields and remove manually changed fields
        List<String> passengerFields = new ArrayList<>(Arrays.asList(FIELDS_CREATED_FROM_PASSENGER));
        if (manuallyChangedField != null) {
            passengerFields.removeAll(Arrays.asList(manuallyChangedFields));
        }

        // excluded fields must be in camelCase and not snake_case
        List<String> includedFields = new ArrayList<>();
        for (String snakeName : passengerFields) {
            String camelName = StringUtils.snakeCaseToCamelCase(snakeName);
            includedFields.add(camelName);
        }

        // merge updated flight movement passenger fields into existing where not user modified
        // keep same flight movement object during the update to avoid creation of extra/orphan route segments
        // see commit 3f887e17 and api!629 merge request for original discussion
        ModelUtils.mergeOnly(updateFlightMovement, existingFlightMovement, includedFields.toArray(new String[]{}));

        return existingFlightMovement;
    }

    /**
     * If Flight Movement was updated manually, NO updates can overwrite those fields.
     * When a Flight Movement created by ATC/Tower/Radar summary, a FPL can fill in <br/>
     * only empty fields in the flight movement (Document: ABMSTecSpec02Feb2017.docx).
     *
     * US 104328: delta and thru flight plan should always overwrite arrival ad and time
     */
    FlightMovement updateFlightMovementCreatedByRadarTowerAtc(
        final FlightMovement existingFlightMovement, final FlightMovement updateFlightMovement){
        LOG.debug("Update flight movement created by ATS (Radar, ATC, Tower) !!!");
        FlightMovement flightMovementResult = null;

        if(validateInputForUpdateFlightMovementCreatedByATS(existingFlightMovement,updateFlightMovement)){
            flightMovementResult = new FlightMovement();
            mergeFlightMovements(existingFlightMovement, flightMovementResult);
            Map<String,Field> flightMovementFields= FlightMovementBuilderMergeUtility.getAllFlightMovementFields();
            // check conditions field
            for(Map.Entry<String,Field> item : flightMovementFields.entrySet()){
                Field field=item.getValue();
                Object objectExist = FlightMovementBuilderMergeUtility.get(existingFlightMovement,field);
                Object objectUpdate = FlightMovementBuilderMergeUtility.get(updateFlightMovement,field);
                if(objectExist==null && objectUpdate!=null) {
                    FlightMovementBuilderMergeUtility.set(flightMovementResult, field, objectUpdate);
                }
            }
            checkACtypeInFlightMovementCreatedByRadarTowerAtc(flightMovementResult, updateFlightMovement);

            // check conditions fields for delta and thru flights for updating flight movement
            // see US 104328: delta and thru plan flight schedule should take precedence for arrival fields
            if (isDeltaOrThruFlight(updateFlightMovement)) {
                overwriteFields(FIELDS_CREATED_FROM_DELTA_OR_THRU, updateFlightMovement, flightMovementResult);
            }
        }
        return flightMovementResult;
    }

    /**
     * If Aircraft type is 'ZZZZ' in a Flight Movement created by ATC/Tower/Radar summary,
     * it should be updated from FPL if exist
     * @param flightMovement existing Flight Movement created by ATC/Tower/Radar summary
     * @param updateFlightMovement Flight Movement from FlightObject (Spatia)
     */
    private static void checkACtypeInFlightMovementCreatedByRadarTowerAtc(final FlightMovement flightMovement,
                                                                          final FlightMovement updateFlightMovement) {
        if(flightMovement.getAircraftType() != null && flightMovement.getAircraftType().equals(ApplicationConstants.PLACEHOLDER_ZZZZ)
            && updateFlightMovement.getAircraftType() != null) {
            flightMovement.setAircraftType(updateFlightMovement.getAircraftType());
        }
    }

    /**
     * If Flight Movement was updated manually, check if has calculated route for ATC/Tower/Radar summary
     * in a previous updating. This is also required as route segment geometry properties aren't passed
     * between front and back end in flight movement management.
     */
    public void updateFlightMovementCreatedByUI(
        final FlightMovement existingFlightMovement, final FlightMovement updateFlightMovement) {
        LOG.debug("Update flight movement created by UI (set the old values for RADAR, ATC and TOWER)");
        
        ModelUtils.mergeOnly(existingFlightMovement, updateFlightMovement,  "radarRouteText",
                    "atcLogRouteText", "towerLogTrack", "routeSegments", "radarRoute");   
    }

    /**
     * If Flight Movement was updated manually, NO updates can overwrite those fields.
     *
     * When a Flight Movement created by a FPL, ATC/Tower/Radar summaries can: <br/>
     * <ul>
     *     <li>Overwrite the Registration Number;</li>
     *     <li>Overwrite the Aircraft type (and the related Wake Category);</li>
     *     <li>Overwrite the Operator;</li>
     *     <li>Write ONLY IF EMPTY the Actual Dep/Arr time;</li>
     *     <li>Write ONLY IF EMPTY the Actual Arrival Airport;</li>
     * </ul>
     *
     * US 104328: delta and thru flight plan should never have arrival ad and time overwritten
     */
    FlightMovement updateFlightMovementCreatedByFPL(
        final FlightMovement existingFlightMovement, final FlightMovement updateFlightMovement) {
        LOG.debug("Update flight movement created by a network FPL message");
        FlightMovement flightMovementResult = null;

        if(validateInputForUpdateFlightMovementCreatedByFPL(existingFlightMovement,updateFlightMovement)){
            flightMovementResult = new FlightMovement();
            String manuallyChangedField = existingFlightMovement.getManuallyChangedFields();
            String[] manuallyChangedFields = FlightMovementBuilderMergeUtility.getNameManuallyChangedFields(manuallyChangedField);
            mergeFlightMovements(existingFlightMovement, flightMovementResult);
            mergeFlightMovements(updateFlightMovement, flightMovementResult,"id","source");

            // restore these protected fields fields from the existing record
            overwriteFields(FIELDS_CREATED_FROM_NETWORK, existingFlightMovement, flightMovementResult);

            // check conditions fields for delta and thru flights for existing flight movement
            // see US 104328: delta and thru plan flight schedule should take precedence for arrival fields
            if (isDeltaOrThruFlight(existingFlightMovement)) {
                overwriteFields(FIELDS_CREATED_FROM_DELTA_OR_THRU, existingFlightMovement, flightMovementResult);
            }

            // check on manually changed field - by user
            if (manuallyChangedFields != null) {
                overwriteFields(manuallyChangedFields, existingFlightMovement, flightMovementResult);
            }
        }
        return flightMovementResult;
    }

    public void mergeFlightMovementsFromUI(FlightMovement source, FlightMovement target, String... excludedFields) {
        doMergeFlightMovements(() -> ModelUtils.mergeExcept(source, target, excludedFields),
            source, target, excludedFields);
    }

    private void mergeFlightMovements(FlightMovement source, FlightMovement target, String... excludedFields) {
        List<String> old = Arrays.asList(excludedFields);
       ArrayList<String> excludedFieldsArray = new ArrayList();
       excludedFieldsArray.addAll(old);
        excludedFieldsArray.add("routeSegments");
        excludedFieldsArray.add("resolutionErrors");
        excludedFieldsArray.add("resolutionErrorsSet");
        String[] newExcludedFields = excludedFieldsArray.toArray(new String[0]);
        
        doMergeFlightMovements(() -> ModelUtils.merge(source, target, newExcludedFields), source, target, newExcludedFields);
    }

    private void doMergeFlightMovements (Runnable callback, FlightMovement source, FlightMovement target, String... excludedFields) {
        if (target != null && source != null && target != source) {       
            callback.run();

            // merge route segments
            if (target.getRouteSegments() != source.getRouteSegments()) {

                // clear target route segments if any and not null to prevent null pointer exception
                // target route segments should never be null as set on flight movement initialization
                if (target.getRouteSegments() == null) {
                    target.setRouteSegments(new ArrayList<>());
                } else {
                    target.getRouteSegments().clear();
                }

                // if source route segments, add to target
                if (source.getRouteSegments() != null) {
                    target.getRouteSegments().addAll(source.getRouteSegments());
                }
            }

            // merge resolution errors
            target.setResolutionErrorsSet(source.getResolutionErrorsSet());
        }

    }

    /* Utility Methods */

    private static Boolean validateInputForOverwriteAllFieldsExceptUserUpdated(
        final FlightMovement existingFlightMovement, final FlightMovement updateFlightMovement){

        Boolean returnValue=Boolean.FALSE;

        if(existingFlightMovement!=null && updateFlightMovement!=null && existingFlightMovement.getSource()!=null && updateFlightMovement.getSource()!=null  ) {
            if(existingFlightMovement.getSource().equals(updateFlightMovement.getSource()) || existingFlightMovement.getSource().equals(FlightMovementSource.MANUAL)){
                returnValue=Boolean.TRUE;
            }
            else{
                LOG.debug("The flight movements are not valid for method 'FlightMovementMerge.overwriteAllFieldsExceptUserUpdated': " +
                        "existingFlightMovement.source {}, updateFlightMovement {}",
                    existingFlightMovement.getSource(), updateFlightMovement.getSource());
            }
        }else{
            LOG.debug("The flight movements are not valid for method 'FlightMovementMerge.overwriteAllFieldsExceptUserUpdated'");
        }

        return  returnValue;
    }

    private static Boolean validateInputForUpdateFlightMovementCreatedByATS(
        final FlightMovement existingFlightMovement, final FlightMovement updateFlightMovement){
        Boolean returnValue=Boolean.FALSE;

        if(existingFlightMovement!=null && updateFlightMovement!=null && existingFlightMovement.getSource()!=null && updateFlightMovement.getSource()!=null  ) {
            if(updateFlightMovement.getSource().equals(FlightMovementSource.NETWORK) && !existingFlightMovement.getSource().equals(FlightMovementSource.MANUAL) && !existingFlightMovement.getSource().equals(FlightMovementSource.NETWORK)){
                returnValue=Boolean.TRUE;
            }
            else{
                LOG.debug("The flight movements are not valid for method 'FlightMovementMerge.updateFlightMovementCreatedByRadarTowerAtc': " +
                        "existingFlightMovement.source {}, updateFlightMovement.source {}",
                    existingFlightMovement.getSource(), updateFlightMovement.getSource());
            }
        }else{
            LOG.debug("The flight movements are not valid for method 'FlightMovementMerge.updateFlightMovementCreatedByRadarTowerAtc'.");
        }
        return returnValue;
    }

    private static Boolean validateInputForUpdateFlightMovementCreatedByFPL(
        final FlightMovement existingFlightMovement, final FlightMovement updateFlightMovement){
        Boolean returnValue=Boolean.FALSE;

        if(existingFlightMovement!=null && updateFlightMovement!=null && existingFlightMovement.getSource()!=null && updateFlightMovement.getSource()!=null  ) {
            if(existingFlightMovement.getSource().equals(FlightMovementSource.NETWORK) && !updateFlightMovement.getSource().equals(FlightMovementSource.MANUAL) && !updateFlightMovement.getSource().equals(FlightMovementSource.NETWORK)){
                returnValue=Boolean.TRUE;
            }
            else{
                LOG.debug("The flight movements are not valid for method 'FlightMovementMerge.updateFlightMovementCreatedByFPL': " +
                        "existingFlightMovement.source {}, updateFlightMovement {}",
                    existingFlightMovement.getSource(), updateFlightMovement.getSource());
            }
        }else{
            LOG.debug("The flight movements are not valid for method 'FlightMovementMerge.updateFlightMovementCreatedByFPL'");
        }
        return returnValue;
    }

    /**
     * Overwrite target flight movement fields from source flight movement fields for provided array of fields,
     * only applies to non-null fields.
     */
    private static void overwriteFields(final String[] fields, final FlightMovement source, final FlightMovement target) {

        for (String snakeName: fields) {
            String camelName = StringUtils.snakeCaseToCamelCase(snakeName);
            Field field= FlightMovementBuilderMergeUtility.getField(camelName);
            if (field!=null) {
                Object object = FlightMovementBuilderMergeUtility.get(source, field);
                if (object != null) {
                    FlightMovementBuilderMergeUtility.set(target, field, object);
                }
            }
        }
    }

    /**
     * Determine if delta flight or thru flight from flags if TRUE otherwise delta and thru flight utility classes.
     * This calculation must be done when flag is false due to corrupted data, US 104328.
     */
    private boolean isDeltaOrThruFlight(final FlightMovement flightMovement) {

        // don't trust existing flags as it is to unreliable for merge process
        // calculate flags each time to be sure after a merge has been applied
        boolean isDelta = Boolean.TRUE.equals(flightMovement.getDeltaFlight())
            || deltaFlightUtility.isDeltaFlight(flightMovement);
        boolean isThru = Boolean.TRUE.equals(flightMovement.getThruFlight())
            || thruFlightPlanUtility.isThruFlight(flightMovement);

        return isDelta || isThru;
    }
}
