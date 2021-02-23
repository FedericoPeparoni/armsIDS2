package ca.ids.spring.cache.exceptions;

public class CacheRetryException extends RuntimeException {

    public static final String MESSAGE = "Cannot complete request, will attempt again during next retry cycle.";

    public CacheRetryException() {
        super(MESSAGE);
    }

    public CacheRetryException(Throwable throwable) {
        super(MESSAGE, throwable);
    }
}
