package ca.ids.abms.spreadsheets.impl;

import java.io.InputStream;

import com.google.common.base.Preconditions;

import ca.ids.abms.spreadsheets.SSLoader;
import ca.ids.abms.spreadsheets.SSView;

abstract class SSLoaderBase <View extends SSView> implements SSLoader <View> {
    
    @Override
    public View load(byte[] rawData) {
        return load (rawData, null);
    }

    @Override
    public View load(byte[] rawData, String sourceName) {
        return createView (ByteArrayLoader.loadByteArray (rawData, sourceName));
    }

    @Override
    public View load (InputStream stream) {
        return load (stream, null, DFLT_MAX_BYTE_COUNT);
    }

    @Override
    public View load (InputStream stream, int maxByteCount) {
        return load (stream, null, maxByteCount);
    }

    @Override
    public View load (InputStream stream, String sourceName) {
        return load (stream, sourceName, DFLT_MAX_BYTE_COUNT);
    }

    @Override
    public View load (InputStream stream, String sourceName, int maxByteCount) {
        return createView (ByteArrayLoader.loadStream (stream, sourceName, maxByteCount));
    }

    @Override
    public View loadFile (String fileName) {
        return loadFile (fileName, DFLT_MAX_BYTE_COUNT);
    }
    
    @Override
    public View loadFile (String fileName, int maxByteCount) {
        return createView (ByteArrayLoader.loadFile (fileName, maxByteCount));
    }
    
    @Override
    public View loadResource (String resourceName) {
        return loadResource (resourceName, DFLT_MAX_BYTE_COUNT);
    }
    
    @Override
    public View loadResource (String resourceName, int maxByteCount) {
        return loadResource (SSLoaderBase.class, resourceName, maxByteCount);
    }
    
    @Override
    public View loadResource(Class<?> clazz, String resourceName) {
        return loadResource (clazz, resourceName, DFLT_MAX_BYTE_COUNT);
    }
    
    @Override
    public View loadResource(Class<?> clazz, String resourceName, int maxByteCount) {
        return createView (ByteArrayLoader.loadResource(clazz, resourceName, maxByteCount));
    }
    
    @Override
    public View loadExample() {
        return createExampleView();
    }
    
    // --------------------- protected --------------
    
    protected final String sheetName;
    protected SSLoaderBase (final String exampleResourceName) {
        this (exampleResourceName, null);
    }
    protected SSLoaderBase (final String exampleResourceName, final String sheetName) {
        Preconditions.checkNotNull (exampleResourceName);
        Preconditions.checkArgument (!exampleResourceName.isEmpty());
        this.exampleResourceName = exampleResourceName;
        this.sheetName = sheetName;
    }
    protected abstract View createView (final WorkbookHelper helper, final String sheetName);
    
    // ----------------------- private -------------------
    private final String exampleResourceName;
    private View createExampleView() {
        final ByteArrayLoader byteArrayLoader = ByteArrayLoader.loadResource (SSLoaderBase.class, exampleResourceName, DFLT_MAX_BYTE_COUNT);
        return createView (new WorkbookHelper (byteArrayLoader), null);
    }
    private View createView (final ByteArrayLoader byteArrayLoader) {
        return createView (new WorkbookHelper (byteArrayLoader), sheetName);
    }
    

}
