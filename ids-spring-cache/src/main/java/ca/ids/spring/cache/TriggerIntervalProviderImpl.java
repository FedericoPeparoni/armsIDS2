package ca.ids.spring.cache;

import java.util.concurrent.TimeUnit;

@SuppressWarnings("WeakerAccess")
public class TriggerIntervalProviderImpl implements TriggerIntervalProvider {

    public static final long DEFAULT_INTERVAL = 60;
    public static final TimeUnit DEFAULT_INTERVAL_UNIT = TimeUnit.MINUTES;

    private long interval;
    private TimeUnit intervalUnit;
    private boolean triggerLock;

    public TriggerIntervalProviderImpl() {
        this(DEFAULT_INTERVAL, DEFAULT_INTERVAL_UNIT);
    }

    public TriggerIntervalProviderImpl(long interval) {
        this(interval, DEFAULT_INTERVAL_UNIT);
    }

    public TriggerIntervalProviderImpl(long interval, TimeUnit intervalUnit) {
        this.setInterval(interval, intervalUnit);
        this.triggerLock = false;
    }

    /**
     * Set interval for which the trigger is run in minutes. If -1 is supplied,
     * default value of 60 is used.
     *
     * @param interval interval between each trigger
     */
    public void setInterval(long interval) {
        this.setInterval(interval, DEFAULT_INTERVAL_UNIT);
    }

    /**
     * Set interval for which the trigger is run. If -1 is supplied, default
     * value of 60 is used.
     *
     * @param interval interval between each trigger
     * @param intervalUnit interval time unit
     */
    public void setInterval(long interval, TimeUnit intervalUnit) {
        this.interval = interval >= 0 ? interval : DEFAULT_INTERVAL;
        this.intervalUnit = intervalUnit;
    }

    @Override
    public long getInterval() {
        return interval;
    }

    @Override
    public TimeUnit getIntervalUnit() {
        return intervalUnit;
    }

    @Override
    public boolean isLocked() {
        return triggerLock;
    }

    @Override
    public void setLock(boolean isLocked) {
        triggerLock = isLocked;
    }
}
