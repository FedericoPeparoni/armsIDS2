package ca.ids.abms.config.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.context.event.EventListener;

import java.util.Date;

@SuppressWarnings("unused")
public class ApplicationShutdown {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationShutdown.class);

    @EventListener
    public void onFailed(ApplicationFailedEvent event) {
        logShutdown("failed", event);
        Throwable throwable = event != null ? event.getException() : null;
        LOG.warn("Application will shutdown due to unrecoverable exception", throwable);
    }

    @EventListener
    public void onClosed(ContextClosedEvent event) {
        logShutdown("closed", event);
    }

    @EventListener
    public void onStopped(ContextStoppedEvent event) {
        logShutdown("stopped", event);
    }

    /**
     * Log application event and time it was triggered.
     */
    private void logShutdown(String eventName, ApplicationEvent event) {
        try {
            Date triggeredAt = event != null ? new Date(event.getTimestamp()) : null;
            LOG.info("Application {} event triggered at {}", eventName, triggeredAt);
        } catch (Exception ex) {
            LOG.error("Could not log application event trigger due to exception", ex);
        }
    }
}
