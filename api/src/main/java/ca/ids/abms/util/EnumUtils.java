package ca.ids.abms.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

public class EnumUtils {

	private static Logger LOG = Logger.getLogger(EnumUtils.class);

	/**
	 * Returns Enum values based on String value that is passed.
	 * If it can't find the value, it returns null
	 * This method helps to avoid potential exceptions that are thrown when using Enum.valueOf for getting enum value based on string provided.
	 *
	 * @param elementToCheck
	 * @param enumToCheckForMatch
	 * @return Enum value
	 */
	public static <E extends Enum<E>> E convertStringToEnumValue(String inputString, E[] inputEnumValues)
    {
		if (inputString !=null && inputEnumValues !=null) {
			for (int i = 0; i < inputEnumValues.length; i++) {
			    String strFromEnum = inputEnumValues[i].toString();
			    if (strFromEnum.equalsIgnoreCase(inputString)) {
			    	return inputEnumValues[i];
			    }
			}
		}
         return null;
    }

	/**
	 * This method converts Enum values to a list of string.
	 * @param inputEnumValues
	 * @return
	 */
	public static <E extends Enum<E>> List<String> convertEnumToListOfStrings(E[] inputEnumValues) {

		List<String> result = null;

		if (inputEnumValues != null && inputEnumValues.length>0) {
			result = new LinkedList<String>();
			for (E enumElement : inputEnumValues) {
				result.add(enumElement.toString());
			}
		}

		return result;
	}

	/**
	 *  Converts a list of coma separated values to EnumSet
	 *
	 * @param eClass
	 * @param comaSeparatedStr
	 * @return
	 */
	public static <E extends Enum<E>> EnumSet<E> convertStringToEnumSet(Class<E> eClass, String comaSeparatedStr) {

		if (eClass==null || comaSeparatedStr==null || comaSeparatedStr.isEmpty()) {
			return null;
		}

		EnumSet<E> result = EnumSet.noneOf(eClass);

	    String[] arr = comaSeparatedStr.split(",");

	    for (String e : arr) {
	    	try {
	    		result.add(E.valueOf(eClass, e.trim()));
	    	} catch (IllegalArgumentException  ex) {
	    		LOG.warn(ex.getMessage());
	    	}
	    }

	    return result;
	}

	/**
	 *  Convert EnumSet to a String that contains coma ceparated values
	 * @param enumSet
	 * @return
	 */
	public static <E extends Enum<E>> String convertEnumSetToComaSeparatedString(
			EnumSet<E> enumSet) {

		if (enumSet == null || enumSet.isEmpty()) {
			return null;
		}

		return enumSet.stream().map(E::name).collect(Collectors.joining(","));
	}
	
	/**
	 * Return all values of the given enum class
	 */
    public static <E extends Enum<E>> List <E> getEnumValues (final Class<E> enumClass) {
        try {
            @SuppressWarnings("unchecked")
            final E[] values = (E[])enumClass.getMethod("values").invoke(null);
            if (values != null) {
                return Arrays.asList (values);
            }
        } catch (IllegalAccessException|IllegalArgumentException|InvocationTargetException|NoSuchMethodException|SecurityException e) {
            throw new RuntimeException (e);
        }
        return Collections.emptyList();
    }

    /**
     * Return the labels associated with the given enum, if it implements {@link EnumWithLabels} interface.
     * Otherwise return an empty list.
     */
    public static <E extends Enum<E>> List <String> getEnumLabels (final E enumValue) {
        if (enumValue instanceof EnumWithLabels) {
            return ((EnumWithLabels) enumValue).labels();
        }
        return Collections.emptyList();
    }
        
}
