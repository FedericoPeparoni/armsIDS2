package ca.ids.abms.amhs;

public class AmhsAgentHostConfig {

    public String getNetworkDevice() {
        return networkDevice;
    }
    public void setNetworkDevice(String networkDevice) {
        this.networkDevice = networkDevice;
    }
    public String getLocalHostname() {
        return localHostname;
    }
    public void setLocalHostname(String localHostname) {
        this.localHostname = localHostname;
    }
    public String getLocalIpaddr() {
        return localIpaddr;
    }
    public void setLocalIpaddr(String localIpaddr) {
        this.localIpaddr = localIpaddr;
    }
    public String getRemoteHostname() {
        return remoteHostname;
    }
    public void setRemoteHostname(String remoteHostname) {
        this.remoteHostname = remoteHostname;
    }
    public String getRemoteIpaddr() {
        return remoteIpaddr;
    }
    public void setRemoteIpaddr(String remoteIpaddr) {
        this.remoteIpaddr = remoteIpaddr;
    }
    
    private String networkDevice;
    private String localHostname;
    private String localIpaddr;
    private String remoteHostname;
    private String remoteIpaddr;
    
}
