package ca.ids.abms.modules.reports2.async;

import ca.ids.abms.modules.jobs.ItemWriter;
import ca.ids.abms.modules.reports2.common.ReportFormat;
import com.google.common.base.Preconditions;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class AsyncInvoiceGeneratorPreviewWriter implements ItemWriter<AsyncInvoiceGeneratorScope> {

    private static final Logger LOG = LoggerFactory.getLogger(AsyncInvoiceGeneratorPreviewWriter.class);

    private static final String PREFIX = "AviationInvoice_";

    private static final String TEMP_FOLDER = "java.io.tmpdir";
    
    private static final int BUFFER_SIZE = 4096;
    
    @Override
    public void close() {
        // Not required
    }

    @Override
    public void open() {
        // Not required
    }

    @Override
    public void write(AsyncInvoiceGeneratorScope item) throws IOException {
        Preconditions.checkArgument(item != null && item.getCurrentUser() != null && item.getFormat() != null);

        final Path tempFile = getTempFile(item.getCurrentUser().getLogin(), item.getFormat());
        if (tempFile != null) {
            Files.deleteIfExists(tempFile);
            if (item.getResult() != null && ArrayUtils.isNotEmpty(item.getResult().data())) {
                Files.write(tempFile, item.getResult().data(), StandardOpenOption.CREATE);
                tempFile.toFile().deleteOnExit();
                LOG.debug("Created the temporary file {}", tempFile);
            } else {
                LOG.info("No data to write into the folder {}", getTempDirectory());
            }
        } else {
            LOG.error("Cannot write the preview file under the folder {}", getTempDirectory());
        }
    }

    static Path getTempFile(final String userName, final ReportFormat format) {
        assert (userName != null && format != null);
        
        Path path = null;

        String tempDir = getTempDirectory();
        if (tempDir != null)
            path = Paths.get(tempDir + PREFIX + userName + format.fileNameSuffix());

        return path;
    }

    /**
     * Get temporary directory and insure system file separator is append.
     */
    private static String getTempDirectory() {
        String tempDir = System.getProperty(TEMP_FOLDER);
        if (tempDir != null && tempDir.charAt(tempDir.length() - 1) != File.separatorChar)
            tempDir += File.separatorChar;
         return tempDir;
    }
    
    public static String unzip(Path zipPath) throws IOException {
    	String filePath = "";
    	
        File destDir = new File(getTempDirectory() + File.separator + "singleFile");
        if (!destDir.exists()) {
        	LOG.info("Directory does not Exists.");
            destDir.mkdir();
            LOG.info("Directory " + destDir + " created.");
        }
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipPath.toFile()));
        ZipEntry entry = zipIn.getNextEntry();
        // iterates over entries in the zip file
        while (entry != null) {
            filePath = destDir + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                // if the entry is a file, extracts it
                extractFile(zipIn, filePath);
            } else {
                // if the entry is a directory, make the directory
                File dir = new File(filePath);
                dir.mkdirs();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
        return filePath;
    }
    
    private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }
}
