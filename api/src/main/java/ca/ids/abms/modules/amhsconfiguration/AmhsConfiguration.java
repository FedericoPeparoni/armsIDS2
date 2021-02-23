package ca.ids.abms.modules.amhsconfiguration;

import ca.ids.abms.config.db.SearchableText;
import ca.ids.abms.modules.common.entities.AbmsCrudEntity;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;
import ca.ids.abms.util.csv.annotations.CsvProperty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@SuppressWarnings("serial")
@Entity
@Table(name="amhs_configurations")
public class AmhsConfiguration extends VersionedAuditedEntity implements AbmsCrudEntity<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @CsvProperty
    private Integer id;
    
    @NotNull
    @CsvProperty
    private Boolean active;
    
    @SearchableText
    @CsvProperty
    private String descr;

    @NotNull
    @SearchableText
    @CsvProperty
    private String protocol;

    @NotNull
    @CsvProperty
    private Integer rtseCheckpointSize;
    
    @NotNull
    @CsvProperty
    private Integer rtseWindowSize;
    
    @NotNull
    @CsvProperty
    private Integer maxConn;
    
    @NotNull
    @CsvProperty
    private Boolean pingEnabled;
    
    @CsvProperty
    private Integer pingDelay;
    
    @CsvProperty
    private String networkDevice;

    // --------- local*
    
    @NotNull
    @CsvProperty
    private Boolean localBindAuthenticated;
    
    @NotNull
    @CsvProperty
    private String localPasswd;
    
    @CsvProperty
    private String localHostname;
    
    @CsvProperty
    private String localIpaddr;
    
    @NotNull
    @CsvProperty
    private Integer localPort;
    
    @NotNull
    @SearchableText
    @CsvProperty
    private String localTsapAddr;
    
    @NotNull
    @CsvProperty
    private Boolean localTsapAddrIsHex;
    
    // --------- remote*
    
    @NotNull
    @SearchableText
    @CsvProperty
    private String remoteHostname;
    
    @CsvProperty
    private String remoteIpaddr;
    
    @NotNull
    @CsvProperty
    private Integer remotePort;
    
    @NotNull
    @CsvProperty
    private String remotePasswd;
    
    @NotNull
    @CsvProperty
    private Boolean remoteBindAuthenticated;

    @NotNull
    @CsvProperty
    private Boolean remoteClassExtended;
    
    @NotNull
    @CsvProperty
    private Boolean remoteContentCorr;
    
    @NotNull
    @CsvProperty
    private Boolean remoteDlExpProhibit;
    
    @NotNull
    @CsvProperty
    private Boolean remoteRcptReassProhibit;
    
    @NotNull
    @CsvProperty
    private Integer remoteIdleTime;
    
    @NotNull
    @CsvProperty
    private Boolean remoteInternalTrace;
    
    @NotNull
    @CsvProperty
    private Boolean remoteLatestDeliveryTime;
    
    @NotNull
    @SearchableText
    @CsvProperty
    private String remoteTsapAddr;
    
    @NotNull
    @CsvProperty
    private Boolean remoteTsapAddrIsHex;
    
    @Override
    public boolean equals (final Object o) {
        if (o instanceof AmhsConfiguration) {
            return Objects.equals(getId(), ((AmhsConfiguration)o).getId());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash (getId());
    }
    
    // --------------------- getters & setters ---------------------------

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AmhsConfiguration [");
        if (id != null)
            builder.append("id=").append(id).append(", ");
        if (active != null)
            builder.append("active=").append(active).append(", ");
        if (descr != null)
            builder.append("descr=").append(descr).append(", ");
        if (protocol != null)
            builder.append("protocol=").append(protocol).append(", ");
        if (rtseCheckpointSize != null)
            builder.append("rtseCheckpointSize=").append(rtseCheckpointSize).append(", ");
        if (rtseWindowSize != null)
            builder.append("rtseWindowSize=").append(rtseWindowSize).append(", ");
        if (maxConn != null)
            builder.append("maxConn=").append(maxConn).append(", ");
        if (pingEnabled != null)
            builder.append("pingEnabled=").append(pingEnabled).append(", ");
        if (pingDelay != null)
            builder.append("pingDelay=").append(pingDelay).append(", ");
        if (networkDevice != null)
            builder.append("networkDevice=").append(networkDevice).append(", ");
        if (localBindAuthenticated != null)
            builder.append("localBindAuthenticated=").append(localBindAuthenticated).append(", ");
        if (localPasswd != null)
            builder.append("localPasswd=").append(localPasswd).append(", ");
        if (localHostname != null)
            builder.append("localHostname=").append(localHostname).append(", ");
        if (localIpaddr != null)
            builder.append("localIpaddr=").append(localIpaddr).append(", ");
        if (localPort != null)
            builder.append("localPort=").append(localPort).append(", ");
        if (localTsapAddr != null)
            builder.append("localTsapAddr=").append(localTsapAddr).append(", ");
        if (localTsapAddrIsHex != null)
            builder.append("localTsapAddrIsHex=").append(localTsapAddrIsHex).append(", ");
        if (remoteHostname != null)
            builder.append("remoteHostname=").append(remoteHostname).append(", ");
        if (remoteIpaddr != null)
            builder.append("remoteIpaddr=").append(remoteIpaddr).append(", ");
        if (remotePort != null)
            builder.append("remotePort=").append(remotePort).append(", ");
        if (remotePasswd != null)
            builder.append("remotePasswd=").append(remotePasswd).append(", ");
        if (remoteBindAuthenticated != null)
            builder.append("remoteBindAuthenticated=").append(remoteBindAuthenticated).append(", ");
        if (remoteClassExtended != null)
            builder.append("remoteClassExtended=").append(remoteClassExtended).append(", ");
        if (remoteContentCorr != null)
            builder.append("remoteContentCorr=").append(remoteContentCorr).append(", ");
        if (remoteDlExpProhibit != null)
            builder.append("remoteDlExpProhibit=").append(remoteDlExpProhibit).append(", ");
        if (remoteRcptReassProhibit != null)
            builder.append("remoteRcptReassProhibit=").append(remoteRcptReassProhibit).append(", ");
        if (remoteIdleTime != null)
            builder.append("remoteIdleTime=").append(remoteIdleTime).append(", ");
        if (remoteInternalTrace != null)
            builder.append("remoteInternalTrace=").append(remoteInternalTrace).append(", ");
        if (remoteLatestDeliveryTime != null)
            builder.append("remoteLatestDeliveryTime=").append(remoteLatestDeliveryTime).append(", ");
        if (remoteTsapAddr != null)
            builder.append("remoteTsapAddr=").append(remoteTsapAddr).append(", ");
        if (remoteTsapAddrIsHex != null)
            builder.append("remoteTsapAddrIsHex=").append(remoteTsapAddrIsHex);
        builder.append("]");
        return builder.toString();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Integer getRtseCheckpointSize() {
        return rtseCheckpointSize;
    }

    public void setRtseCheckpointSize(Integer rtseCheckpointSize) {
        this.rtseCheckpointSize = rtseCheckpointSize;
    }

    public Integer getRtseWindowSize() {
        return rtseWindowSize;
    }

    public void setRtseWindowSize(Integer rtseWindowSize) {
        this.rtseWindowSize = rtseWindowSize;
    }

    public Integer getMaxConn() {
        return maxConn;
    }

    public void setMaxConn(Integer maxConn) {
        this.maxConn = maxConn;
    }

    public Boolean getLocalBindAuthenticated() {
        return localBindAuthenticated;
    }

    public Boolean getPingEnabled() {
        return pingEnabled;
    }

    public void setPingEnabled(Boolean pingEnabled) {
        this.pingEnabled = pingEnabled;
    }

    public Integer getPingDelay() {
        return pingDelay;
    }

    public void setPingDelay(Integer pingDelay) {
        this.pingDelay = pingDelay;
    }

    public String getNetworkDevice() {
        return networkDevice;
    }

    public void setNetworkDevice(String networkDevice) {
        this.networkDevice = networkDevice;
    }

    public void setLocalBindAuthenticated(Boolean localBindAuthenticated) {
        this.localBindAuthenticated = localBindAuthenticated;
    }

    public String getLocalPasswd() {
        return localPasswd;
    }

    public void setLocalPasswd(String localPasswd) {
        this.localPasswd = localPasswd;
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

    public Integer getLocalPort() {
        return localPort;
    }

    public void setLocalPort(Integer localPort) {
        this.localPort = localPort;
    }

    public String getLocalTsapAddr() {
        return localTsapAddr;
    }

    public void setLocalTsapAddr(String localTsapAddr) {
        this.localTsapAddr = localTsapAddr;
    }

    public Boolean getLocalTsapAddrIsHex() {
        return localTsapAddrIsHex;
    }

    public void setLocalTsapAddrIsHex(Boolean localTsapAddrIsHex) {
        this.localTsapAddrIsHex = localTsapAddrIsHex;
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

    public Integer getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(Integer remotePort) {
        this.remotePort = remotePort;
    }

    public String getRemotePasswd() {
        return remotePasswd;
    }

    public void setRemotePasswd(String remotePasswd) {
        this.remotePasswd = remotePasswd;
    }

    public Boolean getRemoteBindAuthenticated() {
        return remoteBindAuthenticated;
    }

    public void setRemoteBindAuthenticated(Boolean remoteBindAuthenticated) {
        this.remoteBindAuthenticated = remoteBindAuthenticated;
    }

    public Boolean getRemoteClassExtended() {
        return remoteClassExtended;
    }

    public void setRemoteClassExtended(Boolean remoteClassExtended) {
        this.remoteClassExtended = remoteClassExtended;
    }

    public Boolean getRemoteContentCorr() {
        return remoteContentCorr;
    }

    public void setRemoteContentCorr(Boolean remoteContentCorr) {
        this.remoteContentCorr = remoteContentCorr;
    }

    public Boolean getRemoteDlExpProhibit() {
        return remoteDlExpProhibit;
    }

    public void setRemoteDlExpProhibit(Boolean remoteDlExpProhibit) {
        this.remoteDlExpProhibit = remoteDlExpProhibit;
    }

    public Boolean getRemoteRcptReassProhibit() {
        return remoteRcptReassProhibit;
    }

    public void setRemoteRcptReassProhibit(Boolean remoteRcptReassProhibit) {
        this.remoteRcptReassProhibit = remoteRcptReassProhibit;
    }

    public Integer getRemoteIdleTime() {
        return remoteIdleTime;
    }

    public void setRemoteIdleTime(Integer remoteIdleTime) {
        this.remoteIdleTime = remoteIdleTime;
    }

    public Boolean getRemoteInternalTrace() {
        return remoteInternalTrace;
    }

    public void setRemoteInternalTrace(Boolean remoteInternalTrace) {
        this.remoteInternalTrace = remoteInternalTrace;
    }

    public Boolean getRemoteLatestDeliveryTime() {
        return remoteLatestDeliveryTime;
    }

    public void setRemoteLatestDeliveryTime(Boolean remoteLatestDeliveryTime) {
        this.remoteLatestDeliveryTime = remoteLatestDeliveryTime;
    }

    public String getRemoteTsapAddr() {
        return remoteTsapAddr;
    }

    public void setRemoteTsapAddr(String remoteTsapAddr) {
        this.remoteTsapAddr = remoteTsapAddr;
    }

    public Boolean getRemoteTsapAddrIsHex() {
        return remoteTsapAddrIsHex;
    }

    public void setRemoteTsapAddrIsHex(Boolean remoteTsapAddrIsHex) {
        this.remoteTsapAddrIsHex = remoteTsapAddrIsHex;
    }
}
