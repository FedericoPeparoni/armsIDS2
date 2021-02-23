package ca.ids.abms.amhs;

public class AmhsAgentStatus {
    
    public boolean isInstalled() {
        return installed;
    }
    public void setInstalled(boolean installed) {
        this.installed = installed;
    }
    public boolean isStarted() {
        return started;
    }
    public void setStarted(boolean started) {
        this.started = started;
    }
    
    private boolean installed;
    private boolean started;

}
