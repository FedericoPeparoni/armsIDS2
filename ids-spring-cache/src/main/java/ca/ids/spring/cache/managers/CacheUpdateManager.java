package ca.ids.spring.cache.managers;

import ca.ids.spring.cache.TriggerIntervalProvider;
import ca.ids.spring.cache.TriggerIntervalProviderImpl;
import ca.ids.spring.cache.exceptions.UpdateCycleLockException;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.PeriodicTrigger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class CacheUpdateManager implements SchedulingConfigurer {

    private TriggerIntervalProvider triggerIntervalProvider;

    private List<Runnable> registeredMethods = new ArrayList<>();

    private Date nextExecutionTime;

    public CacheUpdateManager() {
        this(new TriggerIntervalProviderImpl());
    }

    public CacheUpdateManager(TriggerIntervalProvider triggerIntervalProvider) {
        this.setTriggerIntervalProvider(triggerIntervalProvider);
    }

    /**
     * Set interval provider used to configure update cycle.
     *
     * @param triggerIntervalProvider interval provider to use
     */
    public void setTriggerIntervalProvider(TriggerIntervalProvider triggerIntervalProvider) {
        this.triggerIntervalProvider = triggerIntervalProvider;
    }

    /**
     * Get interval provider used to configure update cycle.
     *
     * @return trigger interval provider
     */
    public TriggerIntervalProvider getTriggerIntervalProvider() {
        return triggerIntervalProvider;
    }

    /**
     * Get the next update cycle execution time. This is determined from the interval set after the last
     * completed update cycle execution.
     *
     * @return next execution time
     */
    public Date getNextExecutionTime() {
        return nextExecutionTime;
    }

    /**
     * Register runnable method to include in the update cache schedule.
     * @param method runnable method
     */
    public void registerMethod(Runnable method) {
        registeredMethods.add(method);
    }

    /**
     * Retrieves all registered methods and attempts to rerun each.
     *
     * @throws UpdateCycleLockException when update cycle in progress
     */
    public void runUpdateCycle() throws UpdateCycleLockException {
        // run update cycle with lock validation
        this.runWithLock(this::doUpdateCache);
    }

    /**
     * Method used to rerun cacheable method supplied as a parameter. Results update
     * `@CacheableOnException` store.
     *
     * @param method rerunnable method to update cache
     * @throws UpdateCycleLockException when update cycle in progress
     */
    public void update(Runnable method) throws UpdateCycleLockException {
        // update cacheable with lock validation
        this.runWithLock(() -> doUpdate(method));
    }

    /**
     * This method runs all runnable registered methods and ignores any exceptions.
     * This should only be called from `runUpdateCycle` to handle `updateCycleLock`.
     */
    private void doUpdateCache() {
        for (Runnable method : registeredMethods) {
            this.doUpdate(method);
        }
    }

    /**
     * Method used to rerun registered method supplied as a parameter. Results are
     * automatically cached by AspectJ `@CacheableOnException` interceptor and
     * ignores any exceptions.
     *
     * This should only be called from `doUpdateCache` to handle `updateCycleLock`.
     *
     * @param method registered method to rerun
     */
    private void doUpdate(Runnable method) {
        try {
            method.run();
        } catch (Exception ignored) {
            // squash any exceptions to prevent affecting system
        }
    }

    /**
     * This will get the trigger for automated upload schedule. Defaults
     * to 60 minutes if no value supplied.
     *
     * @param triggerContext current trigger context
     * @return date for next trigger interval
     */
    private Date trigger(TriggerContext triggerContext) {

        // calculate next trigger date from trigger context
        nextExecutionTime = new PeriodicTrigger(triggerIntervalProvider.getInterval(), triggerIntervalProvider.getIntervalUnit())
            .nextExecutionTime(triggerContext);

        // return next trigger execution date
        return nextExecutionTime;
    }

    /**
     * Wrapper to run registered methods with update cycle lock validation.
     *
     * @param task either `doUpdateCache()` or `doUpdate(Runnable method)`
     * @throws UpdateCycleLockException when update cycle lock set
     */
    private void runWithLock(Runnable task) throws UpdateCycleLockException {

        // validate and set update cycle lock
        if (triggerIntervalProvider.isLocked())
            throw new UpdateCycleLockException();
        else
            triggerIntervalProvider.setLock(true);

        // run task and ALWAYS release retry cycle lock when finished even if exception thrown
        try {
            task.run();
        } finally {
            triggerIntervalProvider.setLock(false);
        }
    }

    /**
     * Used to manage the scheduling of automated uploads.
     *
     * @param scheduledTaskRegistrar registrar for scheduling tasks
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        // trigger `runUpdateCycle()` and ignore UpdateCycleLockException
        // this means cycles are skipped if lock set
        scheduledTaskRegistrar.addTriggerTask(() -> {
            try { this.runUpdateCycle(); }
            catch (UpdateCycleLockException ignored) { /* skip update cycle as locked */ }
        }, this::trigger);
    }
}
