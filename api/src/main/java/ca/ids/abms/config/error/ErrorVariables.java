package ca.ids.abms.config.error;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ErrorVariables implements Serializable {

    private Map<String, String> entries;

    public ErrorVariables() {
        this.entries = new HashMap<>();
    }

    public void addEntry(String key, String  value) {
        entries.put(key, value);
    }

    public Map<String, String> getEntries() {
        return entries;
    }

}
