package ca.ids.spring.cache.exceptions;

import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class RetryCycleLockExceptionTest {

    @Test
    public void retryCycleLockExceptionTest() {
        RetryCycleLockException result = new RetryCycleLockException();
        assertThat(result.getMessage()).isEqualTo(RetryCycleLockException.MESSAGE);
    }
}
