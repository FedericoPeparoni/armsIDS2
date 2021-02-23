package ca.ids.abms.amhs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ca.ids.abms.util.json.JsonHelper;

@Component
class AmhsPropertiesProvider {
    
    public AmhsPropertiesProvider (final JsonHelper jsonHelper, final AmhsProperties amhsProperties) {
        this.jsonHelper = jsonHelper;
        this.amhsProperties = amhsProperties;
        logAmhsProperties();
    }

    public AmhsProperties getAmhsProperties() {
        return amhsProperties;
    }
    
    private void logAmhsProperties() {
        if (LOG.isTraceEnabled()) {
            LOG.trace ("\n{}", jsonHelper.toJsonString (amhsProperties));
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger (AmhsPropertiesProvider.class);
    final JsonHelper jsonHelper;
    private final AmhsProperties amhsProperties;

}
