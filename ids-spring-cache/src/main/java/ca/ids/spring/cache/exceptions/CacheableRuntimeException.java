package ca.ids.spring.cache.exceptions;

import java.io.Serializable;

public class CacheableRuntimeException extends RuntimeException {

    private final Serializable[] metadata;

    public CacheableRuntimeException(Serializable[] metadata, Throwable throwable) {
        super(throwable);
        this.metadata = metadata;
    }

    public Object[] getMetadata() {
        return metadata;
    }
}
