package ca.ids.oxr.client.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import ca.ids.oxr.client.config.UnixTimestampDeserializer;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

public class LatestExchangeRates implements Serializable {

    private String disclaimer;

    private String license;

    @JsonDeserialize(using = UnixTimestampDeserializer.class)
    private LocalDateTime timestamp;

    private String base;

    private Map<String, Double> rates;

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public Map<String, Double> getRates() {
        return rates;
    }

    public void setRates(Map<String, Double> rates) {
        this.rates = rates;
    }
}
