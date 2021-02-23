package ca.ids.abms.spreadsheets.impl;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import ca.ids.abms.modules.translation.Translation;

class ByteArrayLoader {

    private static final Logger logger = LoggerFactory.getLogger (ByteArrayLoader.class);
    public final byte data[];
    public String sourceName;
    public String baseName;
    public final String namePrefix;

    private static final int BUF_SIZE = 4096;

    public static ByteArrayLoader loadByteArray (final byte[] data, final String sourceName) {
        Preconditions.checkNotNull (data);
        return new ByteArrayLoader (data, sourceName);
    }

    public static ByteArrayLoader loadFile (final String fileName, final int maxByteCount) {
        Preconditions.checkNotNull (fileName);
        Preconditions.checkNotNull (maxByteCount > 0);
        logger.debug ("reading file {}, maxByteCount={}", fileName, maxByteCount);
        try (final InputStream stream = new FileInputStream (fileName)) {
            return do_loadStream (stream, fileName, maxByteCount);
        }
        catch (final IOException x) {
            throw SSUtils.wrapException (x);
        }
    }

    public static ByteArrayLoader loadResource (final Class<?> clazz, final String resourceName, final int maxByteCount) {
        Preconditions.checkNotNull (clazz);
        Preconditions.checkNotNull (resourceName);
        Preconditions.checkNotNull (maxByteCount > 0);
        logger.debug ("reading classpath resource {}, maxByteCount={}", resourceName, maxByteCount);
        try (final InputStream stream = clazz.getResourceAsStream (resourceName)) {
            SSUtils.check (stream != null, resourceName, Translation.getLangByToken("resource not found"));
            return do_loadStream (stream, resourceName, maxByteCount);
        }
        catch (final IOException x) {
            throw new RuntimeException (x);
        }
    }

    public static ByteArrayLoader loadStream (final InputStream stream, final String sourceName, final int maxByteCount) {
        Preconditions.checkNotNull (stream);
        Preconditions.checkNotNull (maxByteCount > 0);
        logger.debug ("reading data from stream, maxByteCount={}", maxByteCount);
        return do_loadStream (stream, sourceName, maxByteCount);
    }

    // ----------------------- private --------------------

    private static ByteArrayLoader do_loadStream (final InputStream stream, final String sourceName, final int maxByteCount) {
        try {
            boolean eof = false;
            final byte[] buf = new byte[BUF_SIZE];
            final ByteArrayOutputStream out = new ByteArrayOutputStream (BUF_SIZE);
            while (out.size() < maxByteCount) {
                final int bufSize = stream.read (buf, 0, Math.min (BUF_SIZE, maxByteCount - out.size()));
                if (bufSize <= 0) {
                    eof = true;
                    break;
                }
                out.write(buf, 0, bufSize);
            }
            SSUtils.check (eof || stream.read() == -1, sourceName, Translation.getLangByToken("file too large, expecting") + " <= {} byte(s)", maxByteCount);
            return new ByteArrayLoader (out.toByteArray(), sourceName);
        }
        catch (final IOException x) {
            throw new RuntimeException (x);
        }
    }

    private ByteArrayLoader (final byte[] data, final String sourceName) {
        this.data = data;
        this.sourceName = sourceName;
        if (sourceName != null) {
            this.baseName = FileSystems.getDefault().getPath (sourceName).getFileName().toString();
            this.namePrefix = sourceName + ": ";
        }
        else {
            this.baseName = null;
            this.namePrefix = "";
        }
    }

}
