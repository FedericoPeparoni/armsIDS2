package ca.ids.spring.cache.exceptions;

public class UpdateCycleLockException extends Exception {

    public static final String MESSAGE = "Update cycle is currently in progress, please try again later.";

    public UpdateCycleLockException() {
        super(MESSAGE);
    }
}
