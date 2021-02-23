package ca.ids.abms.config.event;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

@SuppressWarnings("unused")
public class ApplicationStartup {

    private static final Logger LOG = LoggerFactory.getLogger (ApplicationStartup.class);

    @Value("${abms.startupTriggerFile}")
    private String startupTriggerFile;

    /**
     * If set, create empty file after context refresh -- used by systemd (Linux).
     */
    public void createStartupTriggerFile() {
        if (startupTriggerFile != null && !startupTriggerFile.isEmpty()) {
            LOG.trace ("Creating startup trigger file {}", startupTriggerFile);
            try {
                FileUtils.touch(new File (startupTriggerFile));
            }
            catch (final IOException x) {
                LOG.error ("Failed to create startup trigger file: {}", x.getMessage(), x);
            }
        }
    }
}
