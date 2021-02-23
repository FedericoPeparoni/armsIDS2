package ca.ids.abms.config.tomcat;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;

public class CustomTomcatEmbeddedServletContainerFactory extends TomcatEmbeddedServletContainerFactory {

    // max swallow size in bytes, default to 250MB
    @Value("${tomcat.connector.max-swallow-size}")
    @SuppressWarnings("FieldCanBeLocal")
    private int maxSwallowSize = 250 * 1024 * 1024;

    @Override
    protected void customizeConnector(Connector connector) {
        super.customizeConnector(connector);

        // handle max swallow size, limit before requests aren't accepted
        // no response message will be sent for requests exceeding this limit
        if (connector.getProtocolHandler() instanceof AbstractHttp11Protocol) {
            ((AbstractHttp11Protocol <?>) connector.getProtocolHandler())
                .setMaxSwallowSize(maxSwallowSize);
        }
    }
}
