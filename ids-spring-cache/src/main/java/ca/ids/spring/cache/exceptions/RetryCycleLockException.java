package ca.ids.spring.cache.exceptions;

public class RetryCycleLockException extends Exception {

    public static final String MESSAGE = "Retry cycle is currently in progress, please try again later.";

    public RetryCycleLockException() {
        super(MESSAGE);
    }
}
