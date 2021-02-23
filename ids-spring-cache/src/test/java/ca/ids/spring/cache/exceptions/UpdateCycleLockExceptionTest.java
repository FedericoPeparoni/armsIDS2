package ca.ids.spring.cache.exceptions;

import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class UpdateCycleLockExceptionTest {

    @Test
    public void updateCycleLockExceptionTest() {
        UpdateCycleLockException result = new UpdateCycleLockException();
        assertThat(result.getMessage()).isEqualTo(UpdateCycleLockException.MESSAGE);
    }
}
