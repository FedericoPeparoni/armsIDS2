package ca.ids.abms.modules.reports2.async;

import ca.ids.abms.modules.jobs.ItemWriter;
import ca.ids.abms.modules.reports2.common.ReportFormat;
import com.google.common.base.Preconditions;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class AsyncInvoiceGeneratorPreviewWriter implements ItemWriter<AsyncInvoiceGeneratorScope> {

    private static final Logger LOG = LoggerFactory.getLogger(AsyncInvoiceGeneratorPreviewWriter.class);

    private static final String PREFIX = "AviationInvoice_";

    private static final String TEMP_FOLDER = "java.io.tmpdir";

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
}
