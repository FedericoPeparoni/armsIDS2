package ca.ids.abms.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "abms.uploads.allowed-attachments-content-type")
public class AttachmentsConfig {

    private Map<String, String> certificateTemplates = new HashMap<>();

    public Map<String, String> getCertificateTemplates() {
        return certificateTemplates;
    }

    private Map<String, String> ldpBillingFormulas = new HashMap<>();

    public Map<String, String> getLdpBillingFormulas() {
        return ldpBillingFormulas;
    }

    private Map<String, String> billingLedgerDocuments = new HashMap<>();

    public Map<String, String> getBillingLedgerDocuments() {
        return billingLedgerDocuments;
    }
    
    private Map<String, String> reportTemplates = new HashMap<>();

    public Map<String, String> getReportTemplates() {
        return reportTemplates;
    }
}
