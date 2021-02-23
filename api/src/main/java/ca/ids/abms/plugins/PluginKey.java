package ca.ids.abms.plugins;

import java.util.HashMap;
import java.util.Map;

public enum PluginKey {
    CAAB_SAGE("caab.sage"),
    CRONOS_1("cronos1"),
    CRONOS_2("cronos2"),
    KCAA_AATIS("kcaa.aatis"),
    KCAA_ERP("kcaa.erp"),
    KCAA_EAIP("kcaa.eaip"),
    AMHS("amhs"),
    PROTOTYPE("prototype");

    private String key;

    private static Map<String, PluginKey> pluginKeys = new HashMap<>();

    static {
        for (PluginKey pluginKey : PluginKey.values()) {
            pluginKeys.put(pluginKey.key, pluginKey);
        }
    }

    PluginKey(final String key) {
        this.key = key;
    }

    public static PluginKey forValue(final String value) {
        return pluginKeys.get(value);
    }

    public String toValue() {
        for (Map.Entry<String, PluginKey> entry : pluginKeys.entrySet())
            if (entry.getValue() == this)
                return entry.getKey();
        return null;
    }
}
