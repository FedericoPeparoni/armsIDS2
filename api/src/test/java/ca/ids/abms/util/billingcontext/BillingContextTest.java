package ca.ids.abms.util.billingcontext;

import org.junit.After;
import org.junit.Test;

import java.util.Map;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class BillingContextTest {

    @After
    public void destroy() {
        BillingContext.clear();
    }

    @Test
    public void putGetTest() {

        // assert that value is not defined
        assertThat((Boolean) BillingContext.get(BillingContextKey.MERGE_WAYPOINTS)).isNull();

        // assert that value can be set for key
        BillingContext.put(BillingContextKey.MERGE_WAYPOINTS, Boolean.TRUE);
        assertThat((Boolean) BillingContext.get(BillingContextKey.MERGE_WAYPOINTS))
            .isEqualTo(Boolean.TRUE);

        // validate that value is overwritten
        BillingContext.put(BillingContextKey.MERGE_WAYPOINTS, Boolean.FALSE);
        assertThat((Boolean) BillingContext.get(BillingContextKey.MERGE_WAYPOINTS))
            .isEqualTo(Boolean.FALSE);
    }

    @Test
    public void clearTest() {
        BillingContext.put(BillingContextKey.MERGE_WAYPOINTS, Boolean.TRUE);
        assertThat(BillingContext.getCopyOfContextMap())
            .isNotNull();
        assertThat(BillingContext.getCopyOfContextMap())
            .isNotEmpty();

        BillingContext.clear();
        assertThat(BillingContext.getCopyOfContextMap())
            .isNull();
    }

    @Test
    public void removeTest() {
        BillingContext.put(BillingContextKey.MERGE_WAYPOINTS, Boolean.TRUE);
        assertThat((Boolean) BillingContext.get(BillingContextKey.MERGE_WAYPOINTS))
            .isNotNull();
        BillingContext.remove(BillingContextKey.MERGE_WAYPOINTS);
        assertThat((Boolean) BillingContext.get(BillingContextKey.MERGE_WAYPOINTS))
            .isNull();
    }

    @Test
    public void classCastTest() {
        // assert that IllegalStateException thrown when incompatible types
        BillingContext.put(BillingContextKey.MERGE_WAYPOINTS, Boolean.TRUE);
        try {
            Integer integer = BillingContext.get(BillingContextKey.MERGE_WAYPOINTS);
            fail("Failed class cast exception handling: " + integer);
        } catch (ClassCastException ex) {
            assertThat(ex.getMessage())
                .isEqualTo("java.lang.Boolean cannot be cast to java.lang.Integer");
        }
    }

    @Test
    public void getCopyOfContextMapTest() {
        BillingContext.put(BillingContextKey.MERGE_WAYPOINTS, Boolean.TRUE);
        Map<BillingContextKey, Object> copy = BillingContext.getCopyOfContextMap();

        BillingContext.put(BillingContextKey.ANSP_CURRENCY, 1);

        assertThat(copy).isNotNull();
        assertThat(BillingContext.getCopyOfContextMap()).isNotNull();
        assertThat(copy.size()).isNotEqualTo(BillingContext.getCopyOfContextMap().size());
    }

    @Test
    public void inheritThreadTest() {
        BillingContext.put(BillingContextKey.MERGE_WAYPOINTS, Boolean.TRUE);
        runAndWait(() -> {
            // assert child thread inherits parent's thread values
            assertThat((Boolean) BillingContext.get(BillingContextKey.MERGE_WAYPOINTS))
                .isEqualTo(Boolean.TRUE);

            // assert child can overwrite parent thread value
            BillingContext.put(BillingContextKey.MERGE_WAYPOINTS, Boolean.FALSE);
            assertThat((Boolean) BillingContext.get(BillingContextKey.MERGE_WAYPOINTS))
                .isEqualTo(Boolean.FALSE);
        });

        // assert parent thread does not inherit child thread values
        assertThat((Boolean) BillingContext.get(BillingContextKey.MERGE_WAYPOINTS))
            .isEqualTo(Boolean.TRUE);
    }

    private void runAndWait(Runnable runnable) {
        RecordingExceptionHandler handler = new RecordingExceptionHandler();

        Thread thread = new Thread(runnable);
        thread.setUncaughtExceptionHandler(handler);
        thread.start();

        try {
            thread.join();
        } catch (Throwable t) {
            fail("Unexpected failure in child thread:" + t.getMessage());
        }

        assertThat(handler.getMessage()).isEmpty();
        assertThat(handler.hadException()).isFalse();
    }

    /** A {@link Thread.UncaughtExceptionHandler} that records whether the thread threw an exception. */
    private static class RecordingExceptionHandler implements Thread.UncaughtExceptionHandler {
        private Throwable exception;

        public void uncaughtException(Thread t, Throwable e) {
            exception = e;
        }

        boolean hadException() {
            return exception != null;
        }

        String getMessage() {
            return exception != null ? exception.getMessage() : "";
        }
    }
}
