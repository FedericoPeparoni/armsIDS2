package ca.ids.abms.modules.cachedevents.enumerators;

import ca.ids.abms.modules.plugins.enumerators.PluginSqlAction;

public enum CachedEventAction {
    GET,
    DELETE,
    INSERT,
    POST,
    PUT,
    SELECT,
    UPDATE,
    OTHER;

    public static CachedEventAction from(PluginSqlAction pluginSqlAction) {
        switch (pluginSqlAction) {
            case SELECT:
                return SELECT;
            case INSERT:
                return INSERT;
            case UPDATE:
                return UPDATE;
            case DELETE:
                return DELETE;
            default:
                return OTHER;
        }
    }
}
