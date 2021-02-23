package ca.ids.abms.amhs;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties (prefix = "amhs")
class AmhsProperties {
    
    public String getServerShell() {
        return serverShell;
    }
    public void setServerShell(String serverShell) {
        this.serverShell = serverShell;
    }
    public String getOutputDir() {
        return outputDir;
    }
    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }
    public String getInputDir() {
        return inputDir;
    }
    public void setInputDir(String inputDir) {
        this.inputDir = inputDir;
    }
    public String getFailedDir() {
        return failedDir;
    }
    public void setFailedDir(String failedDir) {
        this.failedDir = failedDir;
    }
    public Integer getInputScanDelay() {
        return inputScanDelay;
    }
    public void setInputScanDelay(Integer inputScanDelay) {
        this.inputScanDelay = inputScanDelay;
    }
    public Integer getMaxFailedFileCount() {
        return maxFailedFileCount;
    }
    public void setMaxFailedFileCount(Integer maxFailedFileCount) {
        this.maxFailedFileCount = maxFailedFileCount;
    }
    public Boolean getForcePing() {
        return forcePing;
    }
    public void setForcePing(Boolean forcePing) {
        this.forcePing = forcePing;
    }
    public String getStartCmd() {
        return startCmd;
    }
    public void setStartCmd(String startCmd) {
        this.startCmd = startCmd;
    }
    public String getStopCmd() {
        return stopCmd;
    }
    public void setStopCmd(String stopCmd) {
        this.stopCmd = stopCmd;
    }
    public String getStatusCmd() {
        return statusCmd;
    }
    public void setStatusCmd(String statusCmd) {
        this.statusCmd = statusCmd;
    }
    public String getLocalStatusCmd() {
        return localStatusCmd;
    }
    public void setLocalStatusCmd(String localStatusCmd) {
        this.localStatusCmd = localStatusCmd;
    }
    public String getValidateHostConfigCmd() {
        return validateHostConfigCmd;
    }
    public void setValidateHostConfigCmd(String validateHostConfigCmd) {
        this.validateHostConfigCmd = validateHostConfigCmd;
    }
    public String getLocalInstallCheckFile() {
        return localInstallCheckFile;
    }
    public void setLocalInstallCheckFile(String localInstallCheckFile) {
        this.localInstallCheckFile = localInstallCheckFile;
    }
    
    private String serverShell;
    private String outputDir;
    private String inputDir;
    private String failedDir;
    private Integer inputScanDelay;
    private Integer maxFailedFileCount;
    private Boolean forcePing;
    private String startCmd;
    private String stopCmd;
    private String statusCmd;
    private String localStatusCmd;
    private String validateHostConfigCmd;
    private String localInstallCheckFile;
    
}
