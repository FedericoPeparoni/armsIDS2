package ca.ids.abms.util.jdbc;

import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PluginKeyHolder extends GeneratedKeyHolder {

    /**
     * Retrieve the first item from the first map, assuming that there is just
     * one item and just one map, and that the item is a number.
     *
     * This is the typical case: a single, numeric generated key.
     *
     * Keys are held in a List of Maps, where each item in the list represents
     * the keys for each row. If there are multiple columns, then the Map will have
     * multiple entries as well. If this method encounters multiple entries in
     * either the map or the list meaning that multiple keys were returned,
     * then an InvalidDataAccessApiUsageException is thrown.
     *
     * @return the generated key
     *
     * @throws InvalidDataAccessApiUsageException if multiple keys are encountered.
     */
    public Number getKeyAsNumber() {
        // alias for consistency in method naming
        return super.getKey();
    }

    /**
     * Retrieve the first item from the first map, assuming that there is just
     * one item and just one map.
     *
     * This is the typical case: a single, object generated key.
     *
     * Keys are held in a List of Maps, where each item in the list represents
     * the keys for each row. If there are multiple columns, then the Map will have
     * multiple entries as well. If this method encounters multiple entries in
     * either the map or the list meaning that multiple keys were returned,
     * then an InvalidDataAccessApiUsageException is thrown.
     *
     * @return the generated key as an object
     *
     * @throws InvalidDataAccessApiUsageException if multiple keys are encountered.
     */
    public Object getKeyAsObject() {
        List<Map<String, Object>> keyList = super.getKeyList();

        if (keyList.isEmpty()) {
            return null;
        }
        if (keyList.size() > 1 || keyList.get(0).size() > 1) {
            throw new InvalidDataAccessApiUsageException(
                "The getKey method should only be used when a single key is returned.  " +
                    "The current key entry contains multiple keys: " + keyList);
        }
        Iterator<Object> keyIter = keyList.get(0).values().iterator();
        if (keyIter.hasNext()) {
            return keyIter.next();
        }
        else {
            throw new DataRetrievalFailureException("Unable to retrieve the generated key. " +
                "Check that the table has an identity column enabled.");
        }
    }

    /**
     * Retrieve the first item from the first map, assuming that there is just
     * one item and just one map, and that the item is a string.
     *
     * This is the typical case: a single, string generated key.
     *
     * Keys are held in a List of Maps, where each item in the list represents
     * the keys for each row. If there are multiple columns, then the Map will have
     * multiple entries as well. If this method encounters multiple entries in
     * either the map or the list meaning that multiple keys were returned,
     * then an InvalidDataAccessApiUsageException is thrown.
     *
     * @return the generated key as a string
     *
     * @throws InvalidDataAccessApiUsageException if multiple keys are encountered.
     */
    public String getKeyAsString() {

        List<Map<String, Object>> keyList = super.getKeyList();

        if (keyList.isEmpty()) {
            return null;
        }
        if (keyList.size() > 1 || keyList.get(0).size() > 1) {
            throw new InvalidDataAccessApiUsageException(
                "The getKey method should only be used when a single key is returned.  " +
                    "The current key entry contains multiple keys: " + keyList);
        }
        Iterator<Object> keyIter = keyList.get(0).values().iterator();
        if (keyIter.hasNext()) {
            Object key = keyIter.next();
            if (!(key instanceof String)) {
                throw new DataRetrievalFailureException(
                    "The generated key is not of a supported string type. " +
                        "Unable to cast [" + (key != null ? key.getClass().getName() : null) +
                        "] to [" + String.class.getName() + "]");
            }
            return (String) key;
        }
        else {
            throw new DataRetrievalFailureException("Unable to retrieve the generated key. " +
                "Check that the table has an identity column enabled.");
        }
    }
}
