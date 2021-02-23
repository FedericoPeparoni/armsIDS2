package ca.ids.abms.plugins.amhs;

public class AmhsPluginConfig {

    public AmhsPluginConfig (final boolean enabled, final String messageDir) {
        this.enabled = enabled;
        this.messageDir = messageDir;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getMessageDir() {
        return messageDir;
    }

    @Override
    public String toString() {
        return "AmhsPluginConfig [" + (messageDir != null ? "messageDir=" + messageDir : "") + "]";
    }

    private final boolean enabled;
    private final String messageDir;

}
