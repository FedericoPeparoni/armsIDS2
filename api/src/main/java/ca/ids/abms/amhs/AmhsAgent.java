package ca.ids.abms.amhs;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import ca.ids.abms.plugins.amhs.AmhsPluginConfigLoader;

@Component
public class AmhsAgent {

    @Inject
    public AmhsAgent (final AmhsAgentManager amhsAgentManager, final PingSender pingSender, final AmhsPluginConfigLoader amhsPluginConfigLoader) {
        this.amhsAgentManager = amhsAgentManager;
        this.pingSender = pingSender;
        this.amhsPluginConfigLoader = amhsPluginConfigLoader;
    }
    
    public void clearCache() {
        pingSender.clearCache();
        amhsPluginConfigLoader.clearCache();
    }
    
    public AmhsAgentStatus status() {
        return amhsAgentManager.status();
    }

    public boolean isStartedLocally() {
        return amhsAgentManager.isStartedLocally();
    }

    public AmhsAgentStatus start() {
        return amhsAgentManager.start();
    }

    public AmhsAgentStatus stop() {
        return amhsAgentManager.stop();
    }

    public boolean isStarted() {
        return amhsAgentManager.isStarted();
    }

    public boolean isInstalledLocally() {
        return amhsAgentManager.isInstalledLocally();
    }

    public void validateHostConfig (final AmhsAgentHostConfig hostConfig) {
        amhsAgentManager.validateHostConfig (hostConfig);
    }

    // --------------------------- private --------------------------------
    private final AmhsAgentManager amhsAgentManager;
    private final PingSender pingSender;
    private final AmhsPluginConfigLoader amhsPluginConfigLoader;
}
