package ca.ids.spring.cache.managers;

import ca.ids.spring.cache.*;
import ca.ids.spring.cache.exceptions.RetryCycleLockException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.PeriodicTrigger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Date;

@SuppressWarnings("WeakerAccess")
public class CacheExceptionManager implements SchedulingConfigurer, ApplicationContextAware {

    private CacheableExceptionProvider cacheableExceptionProvider;

    private TriggerIntervalProvider triggerIntervalProvider;

    private Date nextExecutionTime;

    private ApplicationContext applicationContext;

    public CacheExceptionManager() {
        this(new CacheableExceptionProviderImpl(), new TriggerIntervalProviderImpl());
    }

    public CacheExceptionManager(TriggerIntervalProvider triggerIntervalProvider) {
        this(new CacheableExceptionProviderImpl(), triggerIntervalProvider);
    }

    public CacheExceptionManager(CacheableExceptionProvider cacheableExceptionProvider) {
        this(cacheableExceptionProvider, new TriggerIntervalProviderImpl());
    }

    public CacheExceptionManager(CacheableExceptionProvider cacheableExceptionProvider,
                                 TriggerIntervalProvider triggerIntervalProvider) {
        this.setCacheableExceptionProvider(cacheableExceptionProvider);
        this.setTriggerIntervalProvider(triggerIntervalProvider);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Get cache exception provider used to store and retrieve cacheable exceptions. Defaults
     * to {@link CacheableExceptionProviderImpl} if never set.
     *
     * @return cacheable exception provider
     */
    public CacheableExceptionProvider getCacheableExceptionProvider() {
        return cacheableExceptionProvider;
    }

    /**
     * Set cache exception provider used to store and retrieve cacheable exceptions. Defaults
     * to {@link CacheableExceptionProviderImpl}.
     *
     * @param cacheableExceptionProvider cacheable exception provider to use
     */
    public void setCacheableExceptionProvider(CacheableExceptionProvider cacheableExceptionProvider) {
        this.cacheableExceptionProvider = cacheableExceptionProvider;
    }

    /**
     * Get interval provider used to configure cacheable exception retry cycle.
     *
     * @return trigger interval provider
     */
    public TriggerIntervalProvider getTriggerIntervalProvider() {
        return triggerIntervalProvider;
    }

    /**
     * Set interval provider used to configure cacheable exception retry cycle.
     *
     * @param triggerIntervalProvider interval provider to use
     */
    public void setTriggerIntervalProvider(TriggerIntervalProvider triggerIntervalProvider) {
        this.triggerIntervalProvider = triggerIntervalProvider;
    }

    /**
     * Get the next retry cycle execution time. This is determined from the interval set after the last
     * completed retry cycle execution.
     *
     * @return next execution time
     */
    public Date getNextExecutionTime() {
        return nextExecutionTime;
    }

    /**
     * This method retrieves all cached exceptions and attempts to rerun each.
     *
     * @throws RetryCycleLockException when retry cycle in progress
     */
    public void runRetryCycle() throws RetryCycleLockException {
        // run retry cycle with lock validation
        this.runWithLock(this::doRetryCycle);
    }

    /**
     * Method used to rerun cached exception supplied as a parameter. Results are updated and passed
     * to the cacheable exception manager.
     *
     * @param cacheableException cached exception to rerun
     * @throws RetryCycleLockException when retry cycle in progress
     */
    public void retry(CacheableException cacheableException) throws RetryCycleLockException {
        // retry cacheable exception with lock validation
        this.runWithLock(() -> doRetry(cacheableException));
    }

    /**
     * This method retrieves all cached exceptions and attempts to rerun them. This should
     * only be called from `runRetryCycle` to handle `retryCycleLock`.
     */
    private void doRetryCycle() {
        for (CacheableException cacheableException : cacheableExceptionProvider.findAll()) {
            this.doRetry(cacheableException);
        }
    }

    /**
     * Method used to rerun cached exception supplied as a parameter. Results are updated and passed
     * to the cacheable exception manager. This should only be called from `retry` to handle
     * `retryCycleLock`.
     *
     * @param cacheableException cached exception to rerun
     */
    private void doRetry(CacheableException cacheableException) {
        try {

            // Increment retry count
            cacheableException.setRetryCount(cacheableException.getRetryCount() + 1);

            // get target from singleton bean from cacheable exception target
            Object target = applicationContext.getBean((Class<?>) cacheableException.getTarget());

            // get method from target
            Method method = target.getClass().getDeclaredMethod(cacheableException.getMethodName(),
                cacheableException.getParamTypes());

            // make method accessible if possible so it can be invoked
            method.setAccessible(true);

            // invoke method and response to update the cacheable exception result
            Object result = method.invoke(target, (Object[]) cacheableException.getArgs());

            // update cacheable result and remove from next retry cycle
            this.updateCacheableResult(cacheableException, result);

        } catch (InvocationTargetException ex) {

            // handle get cause of exception if exists
            Throwable result;
            if (ex.getCause() != null)
                result = ex.getCause();
            else
                result = ex;

            // handle undeclared checked exceptions from proxy
            if(result instanceof UndeclaredThrowableException
                && ((UndeclaredThrowableException) result).getUndeclaredThrowable() != null)
                result = ((UndeclaredThrowableException) result).getUndeclaredThrowable();
            else if (result instanceof UndeclaredThrowableException
                && result.getCause() != null)
                result = result.getCause();

            // keep if exception invoked inside target is to be handled
            // else remove from next retry cycle
            boolean exclude = cacheableException.getExclude() != null ? cacheableException.getExclude() : false;
            if (cacheableExceptionProvider.handleException(cacheableException.getExceptions(), result, exclude))
                this.updateCacheableResult(cacheableException, result, false);
            else
                this.updateCacheableResult(cacheableException, result, true);

        } catch (Exception ex) {

            // update cacheable exception result and remove from next retry cycle
            // this is an error and should not be retried, something like a method
            // no longer existing would trigger this catch
            this.updateCacheableResult(cacheableException, ex, true);
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
     * Update cacheable exception result from object using injected cacheable
     * exception provider. Will be removed from next retry cycle.
     *
     * @param cacheableException cacheable exception to update
     * @param object result returned
     */
    private void updateCacheableResult(CacheableException cacheableException, Object object) {

        // add new result to cacheable exception
        cacheableException.addReturnedResult(object);

        // update cacheable exception using injected provider
        cacheableExceptionProvider.update(cacheableException);

        // remove cacheable exception from next cycle
        cacheableExceptionProvider.remove(cacheableException);
    }

    /**
     * Update cacheable exception result from throwable object using injected cacheable
     * exception provider.
     *
     * @param cacheableException cacheable exception to update
     * @param throwable result thrown
     * @param remove remove from next retry cycle
     */
    private void updateCacheableResult(CacheableException cacheableException, Throwable throwable, Boolean remove) {

        // add new result to cacheable exception
        cacheableException.addThrownResult(throwable);

        // update cacheable exception using injected provider
        cacheableExceptionProvider.update(cacheableException);

        // remove cacheable exception from next cycle
        if (remove)
            cacheableExceptionProvider.remove(cacheableException);
    }

    /**
     * Wrapper to run methods with retry cycle lock validation.
     *
     * @param task either doRetryCycle() or doRetry(CacheableException)
     * @throws RetryCycleLockException when retry cycle lock set
     */
    private void runWithLock(Runnable task) throws RetryCycleLockException {

        // validate and set retry cycle lock
        if (triggerIntervalProvider.isLocked())
            throw new RetryCycleLockException();
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
        // trigger runRetryCycle and ignore RetryCycleLockException
        // this means cycles are skipped if lock set
        scheduledTaskRegistrar.addTriggerTask(() -> {
            try { this.runRetryCycle(); }
            catch (RetryCycleLockException ignored) { /* skip retry cycle as locked */ }
        }, this::trigger);
    }
}
