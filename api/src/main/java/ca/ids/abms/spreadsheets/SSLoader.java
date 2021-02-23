package ca.ids.abms.spreadsheets;

import java.io.InputStream;

/**
 * Read and expose Excel data as a particular view interface.
 * <p>
 * SSLoaders can load Excel files from various data sources
 * and return a specific interface that allows one to access
 * the data in a type-safe manner.
 * 
 */
public interface SSLoader <View extends SSView> {

    /** Default maximum file size used when reading files or streams */
    public static final int DFLT_MAX_BYTE_COUNT = 1024 * 1024 * 10;

    /**
     * Construct a view given an Excel file from a byte array.
     */
    public View load (byte[] rawData);
    
    /**
     * Construct a view given an Excel file from a byte array.
     * 
     * @param rawData  - Excel file data
     * @param sourceName - original file name from which the provided data was read, or null.
     *                   This is used in error and log messages only.
     */
    public View load (byte[] rawData, String sourceName);
    
    /**
     * Construct a view given an Excel file from the provided IO stream.
     * 
     */
    public View load (InputStream stream);
    /**
     * Construct a view given an Excel file from the provided IO stream.
     * 
     * @param stream   - an InputStream object that contains Excel data
     * @param sourceName - original file name from which the provided data was read, or null.
     *                   This is used in error and log messages only.
     */
    public View load (InputStream stream, String sourceName);
    
    /**
     * Construct a view given an Excel file from the provided IO stream.
     * 
     * @param stream   - an InputStream object that contains Excel data
     * @param maxByteCount - the maximum number of bytes to read.
     */
    public View load (InputStream stream, int maxByteCount);
    
    /**
     * Construct a view given an Excel file from the provided IO stream.
     * 
     * @param stream   - an InputStream object that contains Excel data
     * @param maxByteCount - the maximum number of bytes to read.
     * @param sourceName - original file name from which the provided data was read, or null.
     *                   This is used in error and log messages only.
     */
    public View load (InputStream stream, String sourceName, int maxByteCount);
    
    /**
     * Construct a view by reading an Excel file.
     */
    public View loadFile (String fileName);
    
    /**
     * Construct a view by reading an Excel file.
     * 
     * @param fileName     -
     * @param maxByteCount - the maximum number of bytes to read.
     */
    public View loadFile (String fileName, int maxByteCount);
    
    /**
     * Construct a view by reading an Excel file in the provided CLASSPATH resource
     */
    public View loadResource (String resourceName);
    
    /**
     * Construct a view by reading an Excel file in the provided CLASSPATH resource
     * 
     * @param resourceName - name/path of the resource
     * @param maxByteCount - the maximum number of bytes to read.
     * 
     */
    public View loadResource (String resourceName, int maxByteCount);

    /**
     * Construct a view by reading an Excel file in the provided CLASSPATH resource
     * 
     * @param clazz - class to use for finding the resource
     * @param resourceName - name/path of the resource
     */
    public View loadResource (final Class<?> clazz, String resourceName);
    
    /**
     * Construct a view by reading an Excel file in the provided CLASSPATH resource
     * 
     * @param clazz - class to use for finding the resource
     * @param resourceName - name/path of the resource
     * @param maxByteCount - the maximum number of bytes to read.
     * 
     */
    public View loadResource (final Class<?> clazz, String resourceName, int maxByteCount);

    /**
     * Construct an example view. This will load an internal Excel spreadsheet that
     * may be used as a starting point.
     */
    public View loadExample();
    
}
