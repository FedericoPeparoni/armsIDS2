package ca.ids.abms.amhs;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.ErrorDTO;
import ca.ids.abms.util.json.JsonHelper;

@Component
class AmhsAgentManager {
    @Inject
    public AmhsAgentManager (
            final AmhsPropertiesProvider amhsPropertiesProvider,
            final AmhsAgentDetector amhsAgentDetector,
            final JsonHelper jsonHelper,
            final Shell shell) {
        this.amhsConfig = amhsPropertiesProvider.getAmhsProperties();
        this.amhsAgentDetector = amhsAgentDetector;
        this.jsonHelper = jsonHelper;
        this.shell = shell;
    }
    
    public AmhsAgentStatus status() {
        final boolean installed = isInstalledLocally();
        final AmhsAgentStatus x = new AmhsAgentStatus();
        x.setInstalled (installed);
        x.setStarted (installed && isStarted());
        return x;
    }

    public boolean isStartedLocally() {
        return shell.run("localStatusCmd", amhsConfig.getLocalStatusCmd(), true) == 0;
    }

    public AmhsAgentStatus start() {
        if (shell.run("localStartCmd", amhsConfig.getStartCmd()) != 0) {
            throw new AmhsAgentException ("Failed to start AMHS agent");
        }
        final boolean installed = isInstalledLocally();
        final AmhsAgentStatus x = new AmhsAgentStatus();
        x.setInstalled (installed);
        x.setStarted (installed);
        return x;
    }

    public AmhsAgentStatus stop() {
        if (shell.run("localStopCmd", amhsConfig.getStopCmd()) != 0) {
            throw new AmhsAgentException ("Failed to stop AMHS agent");
        }
        final boolean installed = isInstalledLocally();
        final AmhsAgentStatus x = new AmhsAgentStatus();
        x.setInstalled (installed);
        x.setStarted (false);
        return x;
    }

    public boolean isStarted() {
        return shell.run("statusCmd", amhsConfig.getStatusCmd(), true) == 0;
    }

    public boolean isInstalledLocally() {
        return amhsAgentDetector.isInstalledLocally();
    }

    public void validateHostConfig (final AmhsAgentHostConfig hostConfig) {
        final String hostConfigJson = jsonHelper.toJsonString (hostConfig);
        final StringBuilder commandOutput = new StringBuilder();
        final int res = shell.run ("validateHostConfigCmd", amhsConfig.getValidateHostConfigCmd(), false, hostConfigJson, commandOutput);
        if (res != 0) {
            final List <String> errors =
                    Arrays.stream (RE_NEWLINE.split (commandOutput.toString()))
                        .filter (Objects::nonNull).map (s->{
                            final Matcher m = RE_VALIDATE_ERROR_LINE.matcher (s);
                            if (m.matches()) {
                                final String host = StringUtils.trimToNull (m.group(2));
                                final String descr = m.group(3);
                                if (host != null) {
                                    return String.format ("%s [host=%s]", descr, host);
                                }
                                return descr;
                            }
                            return null;
                        })
                        .filter (Objects::nonNull)
                        .collect (Collectors.toList());
            if (!errors.isEmpty()) {
                throw error (errors.get(0));
            }
            throw error ("invalid configuration");
        }
    }

    // --------------------------- private --------------------------------
    
    private RuntimeException error (final String what) {
        final ErrorDTO dto = new ErrorDTO.Builder()
                .setErrorMessage (what)
                .build();
        return new CustomParametrizedException (dto);
    }
    
    private static final Pattern RE_NEWLINE = Pattern.compile ("\n");
    private static final Pattern RE_VALIDATE_ERROR_LINE = Pattern.compile ("^\\s*(host=(.+?)\\s+)?error:\\s*(.+)$", Pattern.CASE_INSENSITIVE);
    
    private final AmhsProperties amhsConfig;
    private final AmhsAgentDetector amhsAgentDetector;
    private final JsonHelper jsonHelper;
    private final Shell shell;

}
