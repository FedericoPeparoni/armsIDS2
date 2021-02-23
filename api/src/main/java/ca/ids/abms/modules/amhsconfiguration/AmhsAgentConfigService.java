package ca.ids.abms.modules.amhsconfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ca.ids.abms.amhs.AmhsAgent;
import ca.ids.abms.amhs.AmhsAgentConfig;
import ca.ids.abms.amhs.AmhsAgentHostConfig;
import ca.ids.abms.amhs.AmhsAgentStatus;
import ca.ids.abms.util.json.JsonHelper;

@Component
public class AmhsAgentConfigService {
    
    public AmhsAgentConfigService (
            final AmhsAccountRepository amhsAccountRepository,
            final AmhsConfigurationRepository amhsConfigurationRepository,
            final AmhsAgent amhsAgent,
            final EntityManager entityManager,
            final JsonHelper jsonHelper) {
        this.amhsAccountRepository = amhsAccountRepository;
        this.amhsConfigurationRepository = amhsConfigurationRepository;
        this.amhsAgent = amhsAgent;
        this.entityManager = entityManager;
        this.jsonHelper = jsonHelper;
    }
    
    @Transactional (readOnly = true)
    public void validateAgentHostConfig (final AmhsConfiguration config) {
        if (config.getActive() != null && config.getActive() && amhsAgent.isInstalledLocally()) {
            this.amhsAgent.validateHostConfig (agentHostConfig (config));
        }
    }
    
    @Transactional
    public AmhsAgentConfig update() {
        AmhsAgentConfig conf = createAgentConfig();
        if (conf != null) {
            removeAgentConfig();
            saveAgentConfig (conf);
            amhsAgent.clearCache();
            return conf;
        }
        return null;
    }
    
    /**
     * Start the X.400/AMHS agent
     */
    @Transactional
    public AmhsAgentStatus startAgent() {
        ensureConfigValid();
        return amhsAgent.start();
    }

    /**
     * Stop the X.400/AMHS agent
     */
    @Transactional (readOnly = true)
    public AmhsAgentStatus stopAgent() {
        return amhsAgent.stop();
    }
    
    /**
     * Restart the X.400/AMHS agent
     */
    @Transactional
    public AmhsAgentStatus restartAgent() {
        ensureConfigValid();
        amhsAgent.stop();
        return amhsAgent.start();
    }
    
    public AmhsAgentStatus agentStatus() {
        return amhsAgent.status();
    }
    
    // -------------------------------- private -----------------------------------
    
    public AmhsAgentConfig createAgentConfig() {
        final AmhsConfiguration conf = amhsConfigurationRepository.findByActive (true);
        if (conf != null) {
            final List <AmhsAccount> accounts = amhsAccountRepository.findByActive (true);
            if (!accounts.isEmpty()) {
                final List <AmhsAgentConfig.File> files = new ArrayList<>();
                files.add (create_mts_users (conf, accounts));
                files.add (create_AmhsMtaLineMap (conf));
                files.add (create_clnp_channel (conf));
                files.add (create_dlpi_channel (conf));
                files.add (create_mta_channel (conf, accounts));
                files.add (create_tp_channel (conf));
                files.add (create_AmhsProperties (conf));
                files.add (create_mta_global_config (conf));
                return new AmhsAgentConfig (agentHostConfig (conf), files);
            }
        }
        return null;
    }
    
    @SuppressWarnings ("squid:S00112")
    private AmhsAgentConfig ensureConfigValid() {
        final AmhsAgentConfig config = createAgentConfig();
        if (config == null) {
            throw new RuntimeException ("no active AMHS configurations or accounts found");
        }
        this.amhsAgent.validateHostConfig (config.getHost());
        return config;
    }
    
    private static AmhsAgentHostConfig agentHostConfig (final AmhsConfiguration conf) {
        final AmhsAgentHostConfig hostConfig = new AmhsAgentHostConfig();
        hostConfig.setNetworkDevice (conf.getNetworkDevice());
        hostConfig.setLocalHostname (conf.getLocalHostname());
        hostConfig.setRemoteHostname(conf.getRemoteHostname());

        // this will prevent the back-end from updating /etc/hosts -- requested by Werner
        //hostConfig.setLocalIpaddr (conf.getLocalIpaddr());
        //hostConfig.setRemoteIpaddr (conf.getRemoteIpaddr());
        return hostConfig;
    }
    
    @SuppressWarnings ("squid:S00112")
    private static String getAftnAddr (final AmhsAccount a) {
        if (a.getAddr() != null) {
            final String accountId = a.getId() == null ? "" : " #" + a.getId().toString();
            final int lastEq = a.getAddr().lastIndexOf('=');
            if (lastEq != -1) {
                final String aftnAddr = StringUtils.strip (a.getAddr().substring (lastEq + 1));
                if (aftnAddr.length() == 8) {
                    return aftnAddr;
                }
            }
            throw new RuntimeException (String.format ("Invalid AMHS addrss '%s' in account%s",
                    a.getAddr(), accountId));
        }
        throw new RuntimeException ("Invalid AMHS address in account");
    }
    
    /** Generate conf/mts_users.conf */
    @SuppressWarnings("squid:S00100")
    private AmhsAgentConfig.File create_mts_users (final AmhsConfiguration conf, final List <AmhsAccount> accounts) {
        final StringBuilder buf = new StringBuilder();
        accounts.forEach(a -> {
            buf.append(a.getAddr()).append(":");
            buf.append(a.getPasswd()).append(":");
            buf.append(Objects.equals(a.getAllowMtaConn(), true) ? "Y" : "N").append(":");
            buf.append(Objects.equals(a.getSvcHoldForDelivery(), true) ? "Y" : "N").append(":");
            buf.append("Y"); // FIXME: don't know what the last field is
            buf.append("\n");
        });
        return new AmhsAgentConfig.File ("conf/mts_users.conf", buf.toString());
    }
    
    /** Generate conf/AmhsLineMap.conf */
    @SuppressWarnings("squid:S00100")
    private AmhsAgentConfig.File create_AmhsMtaLineMap (final AmhsConfiguration conf) {
        final String content =
                "<linemap>\n" + 
                "<line>\n" + 
                "<linenbr>1</linenbr>\n" + 
                "<linetype>" + (conf.getProtocol().equals ("P1") ? "AMHS" : "AMHSP3") + "</linetype>\n" + 
                "<linemode>3</linemode>\n" + 
                "<parameters>REMOTE_OUT</parameters>\n" + 
                "<option></option>\n" + 
                "<limit></limit>\n" + 
                "<spvaddr>XXXXXXXX</spvaddr>\n" + 
                "</line>\n" + 
                "</linemap>\n"
        ;
        return new AmhsAgentConfig.File ("conf/AmhsMtaLineMap.conf", content);
    }
    
    /** Generate conf/clnp_channel.conf */
    @SuppressWarnings("squid:S00100")
    private AmhsAgentConfig.File create_clnp_channel (final AmhsConfiguration conf) {
        final StringBuilder content = new StringBuilder();
        
        // remote (??)
        content.append ("n/a").append (":");
        content.append ("1001").append (":");
        content.append ("1500").append (":");
        content.append ("1").append (":");
        content.append ("100").append (":");
        content.append ("\n");
        
        // local (??)
        content.append ("@@NETWORK_DEVICE@@").append (":");
        content.append ("6001").append (":");
        content.append ("1500").append (":");
        content.append ("1").append (":");
        content.append ("100").append (":");
        content.append ("\n");
        
        return new AmhsAgentConfig.File ("conf/clnp_channel.conf", content.toString());
    }
    
    /** Generate conf/dlpi_channel.conf */
    @SuppressWarnings({ "squid:S00100", "squid:S1172" })
    private AmhsAgentConfig.File create_dlpi_channel (final AmhsConfiguration conf) {
        final StringBuilder content = new StringBuilder();
        
        // local
        content.append ("@@NETWORK_DEVICE@@").append (":");
        content.append ("20").append (":");
        content.append ("20").append (":");
        content.append ("\n");
        
        // remote
        content.append ("n/a").append (":");
        content.append ("20").append (":");
        content.append ("20").append (":");
        content.append ("\n");
        
        return new AmhsAgentConfig.File ("conf/dlpi_channel.conf", content.toString());
    }
    
    /** Generate conf/mta_channel.conf */
    @SuppressWarnings({ "squid:S00100", "squid:S1192", "squid:S3776" })
    private AmhsAgentConfig.File create_mta_channel (final AmhsConfiguration conf, final List <AmhsAccount> accounts) {
        final StringBuilder content = new StringBuilder();
        
        boolean keepConnection = conf.getRemoteIdleTime() > 0;

        // local
        content.append ("@@LOCAL_HOSTNAME@@").append (":");
        content.append ("1").append (":");
        content.append ("1").append (":");
        content.append ("10").append (":");
        content.append ("LOCAL_IN").append (":");
        content.append (conf.getLocalPasswd()).append (":");
        content.append ("1").append (":");
        content.append (conf.getRtseCheckpointSize()).append (":");
        content.append (conf.getRtseWindowSize()).append (":");
        content.append (conf.getLocalBindAuthenticated() ? "authenticated" : "unauthenticated").append (":");
        content.append (conf.getLocalPort()).append (":");
        content.append (conf.getRemoteClassExtended() ? "extended" : "basic").append (":");
        content.append (conf.getRemoteLatestDeliveryTime() ? "Y" : "N").append (":");
        content.append (conf.getRemoteDlExpProhibit() ? "Y" : "N").append (":");
        content.append (conf.getRemoteRcptReassProhibit() ? "Y" : "N").append (":");
        content.append (conf.getRemoteContentCorr() ? "Y" : "N").append (":");
        content.append (conf.getRemoteInternalTrace() ? "Y" : "N").append (":");
        content.append (conf.getMaxConn()).append (":");
        content.append (conf.getRemoteIdleTime()).append (":");
        content.append (keepConnection ? "Y" : "N").append (":");
        content.append (conf.getProtocol().equals ("P1") ? 1 : 3).append (":");
        content.append ("\n");
        
        // remote
        content.append ("@@LOCAL_HOSTNAME@@").append (":");
        content.append ("2").append (":");
        content.append ("1").append (":");
        content.append ("10").append (":");
        content.append ("LOCAL_OUT").append (":");
        content.append (conf.getLocalPasswd()).append (":");
        content.append ("1").append (":");
        content.append (conf.getRtseCheckpointSize()).append (":");
        content.append (conf.getRtseWindowSize()).append (":");
        content.append (conf.getLocalBindAuthenticated() ? "authenticated" : "unauthenticated").append (":");
        content.append (conf.getLocalPort()).append (":");
        content.append (conf.getRemoteClassExtended() ? "extended" : "basic").append (":");
        content.append (conf.getRemoteLatestDeliveryTime() ? "Y" : "N").append (":");
        content.append (conf.getRemoteDlExpProhibit() ? "Y" : "N").append (":");
        content.append (conf.getRemoteRcptReassProhibit() ? "Y" : "N").append (":");
        content.append (conf.getRemoteContentCorr() ? "Y" : "N").append (":");
        content.append (conf.getRemoteInternalTrace() ? "Y" : "N").append (":");
        content.append (conf.getMaxConn()).append (":");
        content.append (conf.getRemoteIdleTime()).append (":");
        content.append (keepConnection ? "Y" : "N").append (":");
        content.append (conf.getProtocol().equals ("P1") ? 1 : 3).append (":");
        content.append ("\n");

        final String remoteHostname = ObjectUtils.firstNonNull (StringUtils.trimToNull (conf.getRemoteHostname()), "@@REMOTE_HOSTNAME@@");
        for (final AmhsAccount a: accounts) {
            content.append (remoteHostname).append (":");
            content.append ("2").append (":");
            content.append ("1").append (":");
            content.append ("10").append (":");
            content.append (getAftnAddr (a)).append (":");
            content.append (conf.getRemotePasswd()).append (":");
            content.append ("3").append (":");
            content.append (conf.getRtseCheckpointSize()).append (":");
            content.append (conf.getRtseWindowSize()).append (":");
            content.append (conf.getRemoteBindAuthenticated() ? "authenticated" : "unauthenticated").append (":");
            content.append (conf.getRemotePort()).append (":");
            content.append (conf.getRemoteClassExtended() ? "extended" : "basic").append (":");
            content.append (conf.getRemoteLatestDeliveryTime() ? "Y" : "N").append (":");
            content.append (conf.getRemoteDlExpProhibit() ? "Y" : "N").append (":");
            content.append (conf.getRemoteRcptReassProhibit() ? "Y" : "N").append (":");
            content.append (conf.getRemoteContentCorr() ? "Y" : "N").append (":");
            content.append (conf.getRemoteInternalTrace() ? "Y" : "N").append (":");
            content.append (conf.getMaxConn()).append (":");
            content.append (conf.getRemoteIdleTime()).append (":");
            content.append (keepConnection ? "Y" : "N").append (":");
            content.append (conf.getProtocol().equals ("P1") ? 1 : 3).append (":");
            content.append ("\n");
        }

        return new AmhsAgentConfig.File ("conf/mta_channel.conf", content.toString());
    }
    
    /** Generate conf/tp_channel.conf */
    @SuppressWarnings("squid:S00100")
    private AmhsAgentConfig.File create_tp_channel (final AmhsConfiguration conf) {
        final StringBuilder content = new StringBuilder();
        
        // remote
        content.append ("1001").append (":");
        content.append (conf.getRemoteTsapAddrIsHex() ? "H" : "I").append (":");
        content.append (conf.getRemoteTsapAddr()).append (":");
        content.append ("10").append (":");
        content.append ("100").append (":");
        content.append ("10").append (":");
        content.append ("1").append (":");
        content.append ("3").append (":");
        content.append ("\n");

        // local
        content.append ("6001").append (":");
        content.append (conf.getLocalTsapAddrIsHex() ? "H" : "I").append (":");
        content.append (conf.getLocalTsapAddr()).append (":");
        content.append ("10").append (":");
        content.append ("100").append (":");
        content.append ("10").append (":");
        content.append ("1").append (":");
        content.append ("1").append (":");
        content.append ("\n");
        
        return new AmhsAgentConfig.File ("conf/tp_channel.conf", content.toString());
    }
    
    /** Generate bin/AmhsProperties.prop */
    @SuppressWarnings("squid:S00100")
    private AmhsAgentConfig.File create_AmhsProperties (final AmhsConfiguration conf) {
        // We only generate *some* properties here -- agent startup script will merge them into
        // the existing file, not overwrite it entirely
        final StringBuilder content = new StringBuilder();
        // Don't update AmhsAppProperties -- requeted by Werner
        //content.append ("AMHS_ATN_MTA_NAME=@@LOCAL_HOSTNAME@@\n");
        //content.append ("AMHS_IPAT_IP_ADDR_ALIAS=@@LOCAL_IPADDR@@\n");
        return new AmhsAgentConfig.File ("bin/AmhsProperties.prop", content.toString(), true);
    }
    
    /** Generate bin/mta_global_config */
    @SuppressWarnings("squid:S00100")
    private AmhsAgentConfig.File create_mta_global_config (final AmhsConfiguration conf) {
        // We only generate *some* properties here -- agent startup script will merge them into
        // the existing file, not overwrite it entirely
        final StringBuilder content = new StringBuilder();
        return new AmhsAgentConfig.File ("conf/mta_global_config", content.toString(), true);
    }
    
    private void removeAgentConfig() {
        entityManager
            .createNativeQuery("truncate amhs_agent_config")
            .executeUpdate();
    }
    private void saveAgentConfig (final AmhsAgentConfig obj) {
        entityManager
            .createNativeQuery("insert into amhs_agent_config values (:obj)")
            .setParameter("obj", jsonHelper.toJsonString (obj))
            .executeUpdate();
    }
    
    private final AmhsAccountRepository amhsAccountRepository;
    private final AmhsConfigurationRepository amhsConfigurationRepository;
    private final AmhsAgent amhsAgent;
    private final JsonHelper jsonHelper;
    private final EntityManager entityManager;

}
