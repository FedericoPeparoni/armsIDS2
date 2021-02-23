package ca.ids.abms.amhs;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
class AmhsAgentDetector {
    
    public AmhsAgentDetector (final AmhsProperties amhsConfig) {
        this.amhsConfig = amhsConfig;
        // print a log message as a side effect
        isInstalledLocally();
    }

    public boolean isInstalledLocally() {
        final boolean installed = new File (amhsConfig.getLocalInstallCheckFile()).isFile();
        synchronized (this) {
            if ((isInstalled == null || installed != isInstalled) && LOG.isInfoEnabled()) {
                if (installed) {
                    LOG.info ("AMHS agent [{}] detected", amhsConfig.getLocalInstallCheckFile());
                }
                else {
                    LOG.info ("AMHS agent [{}] not detected", amhsConfig.getLocalInstallCheckFile());
                }
            }
            isInstalled = installed;
        }
        return installed;
    }

    private static final Logger LOG = LoggerFactory.getLogger (AmhsAgentDetector.class);
    private volatile Boolean isInstalled;
    private final AmhsProperties amhsConfig;
}
