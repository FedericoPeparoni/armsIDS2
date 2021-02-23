package ca.ids.oxr.client.dto;

import javax.validation.constraints.NotNull;

public class Context {

    @NotNull
    private String appId;

    private String base;

    private String symbols;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getSymbols() {
        return symbols;
    }

    public void setSymbols(String symbols) {
        this.symbols = symbols;
    }
}
