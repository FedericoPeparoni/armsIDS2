package ca.ids.abms.modules.flightmovementsbuilder.utility;

import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by c.talpa on 02/03/2017.
 */
public class FlightMovementBuilderMergeUtility {

    private static final Logger LOG = LoggerFactory.getLogger(FlightMovementBuilderMergeUtility.class);

    private static final String REGEX_FOR_SPLIT = ",";

    private static final Map<String, Field> FLIGHT_MOVEMENT_FIELDS = new HashMap<>();
    
    private FlightMovementBuilderMergeUtility() {}

    static {

        Field[] allFields = FlightMovement.class.getDeclaredFields();
        for (Field field : allFields) {
            FLIGHT_MOVEMENT_FIELDS.put(field.getName(), field);

        }
    }

    public static Map<String, Field> getAllFlightMovementFields() {
        return FLIGHT_MOVEMENT_FIELDS;
    }

    public static Field getFlightMovementFieldsByName(String name) {
        Field field = null;
        if (StringUtils.isStringIfNotNull(name)) {
            field = FLIGHT_MOVEMENT_FIELDS.get(name);
        }
        return field;
    }

    public static Field getFlightMovementFieldsBySnakeCaseName(String name) {
        LOG.debug("Get flight movement by SnackeCaseName: {}", name);
        Field field = null;
        if (StringUtils.isStringIfNotNull(name)) {
            String camelCaseName = StringUtils.snakeCaseToCamelCase(name);
            field = FLIGHT_MOVEMENT_FIELDS.get(camelCaseName);
        }
        return field;
    }

    public static List<Field> getFlightMovementFieldsByManuallyChanged(String manuallyChangedFields) {
        List<Field> fildsManuallyChanged = null;
        if (StringUtils.isStringIfNotNull(manuallyChangedFields)) {
            fildsManuallyChanged = new ArrayList<>();
            String[] fieldNames = manuallyChangedFields.split(REGEX_FOR_SPLIT);
            for (String fieldName : fieldNames) {
                fildsManuallyChanged.add(getFlightMovementFieldsBySnakeCaseName(fieldName));
            }
        }

        if (fildsManuallyChanged != null && fildsManuallyChanged.size() == 0) {
            fildsManuallyChanged = null;
        }

        return fildsManuallyChanged;
    }

    public static Field getManuallyFieldChanged(String fieldToCheck, String manuallyChangedFields) {
        Field retunField = null;
        if (StringUtils.isStringIfNotNull(fieldToCheck) && StringUtils.isStringIfNotNull(manuallyChangedFields)) {
            String[] fieldNames = getNameManuallyChangedFields(manuallyChangedFields);
            for (String fieldName : fieldNames) {
                Field field = getFlightMovementFieldsBySnakeCaseName(fieldName);
                if (field != null && field.getName().equals(fieldToCheck)) {
                    retunField = field;
                    break;
                }
            }
        }
        return retunField;
    }


    public static Boolean checkManuallyFieldChanged(String fieldToCheck, String manuallyChangedFields) {
        Boolean retunValue = Boolean.FALSE;
        Field field = getManuallyFieldChanged(fieldToCheck, manuallyChangedFields);
        if (field != null) {
            retunValue = Boolean.TRUE;
        }
        return retunValue;
    }


    public static String[] getNameManuallyChangedFields(String manuallyChangedFields) {
        String[] fieldNames = null;
        if (StringUtils.isStringIfNotNull(manuallyChangedFields)) {
            fieldNames = manuallyChangedFields.split(REGEX_FOR_SPLIT);
        }
        return fieldNames;
    }


    public static Field getField(String name) {
        Field retunField = null;
        if (StringUtils.isStringIfNotNull(name)) {
            retunField = FLIGHT_MOVEMENT_FIELDS.get(name);
        }
        return retunField;
    }


    public static Boolean checkField(String name) {
        Boolean retunValue = Boolean.FALSE;
        Field field = getField(name);
        if (field != null) {
            retunValue = Boolean.TRUE;
        }
        return retunValue;
    }



    public static <V> V get(Object object, Field field) {
        Object objectReturn = null;
        try {
            field.setAccessible(true);
            objectReturn = field.get(object);
        } catch (IllegalAccessException e) {
            LOG.error ("Unexpected exception: {}", e.getMessage(), e);
        }

        return (V) objectReturn;
    }



    public static Boolean set(Object object, Field field, Object fieldValue) {
        Boolean returnValue = Boolean.FALSE;
        try {
            field.setAccessible(true);
            field.set(object, fieldValue);
            returnValue = Boolean.TRUE;
        } catch (IllegalAccessException e) {
            LOG.error ("Unexpected exception: {}", e.getMessage(), e);
        }
        return returnValue;
    }

}
