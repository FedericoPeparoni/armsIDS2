package ca.ids.spring.cache;

import java.util.concurrent.TimeUnit;

public interface TriggerIntervalProvider {

    /**
     * Used to get trigger interval duration between the completion of
     * one trigger to the start of the next.
     *
     * Use `getIntervalUnit` for interval unit measure.
     *
     * @return trigger interval
     */
    long getInterval();

    /**
     * Used to get the trigger interval's units of measure in `TimeUnit`.
     *
     * Use `getInterval()` for duration units are applied to.
     *
     * @return interval units of measure
     */
    TimeUnit getIntervalUnit();

    /**
     * Used to determine if trigger is locked.
     *
     * @return true if trigger is locked
     */
    boolean isLocked();

    /**
     * Used to update the trigger lock value.
     *
     * @param isLocked true if trigger is locked
     */
    void setLock(boolean isLocked);
}
