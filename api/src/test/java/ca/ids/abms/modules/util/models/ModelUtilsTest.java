package ca.ids.abms.modules.util.models;

import ca.ids.abms.config.error.StaleVersionException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.assertj.core.api.Assertions.assertThat;

public class ModelUtilsTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void merge() throws Exception {
        TestModel source = new TestModel();
        source.setFirstName("Baz");
        source.setAge(25);

        TestModel target = new TestModel();
        target.setFirstName("Foo");
        target.setLastName("Bar");
        target.setAge(20);

        ModelUtils.merge(source, target, "age");

        // firstName should be updated
        assertThat(target.getFirstName()).isEqualTo("Baz");

        // lastName should remain the same as null properties are ignored
        assertThat(target.getLastName()).isEqualTo("Bar");

        // age should remain the same as we excluded it from update
        assertThat(target.getAge()).isEqualTo(20);
    }

    @Test
    public void mergeOnly() throws Exception {
        TestModel source = new TestModel();
        source.setFirstName("Baz");
        source.setLastName("Test");
        source.setAge(25);

        TestModel target = new TestModel();
        target.setFirstName("Foo");
        target.setLastName("Bar");
        target.setAge(20);

        ModelUtils.mergeOnly(source, target, "age");

        // firstName should remain the same as it was not included
        assertThat(target.getFirstName()).isEqualTo("Foo");

        // lastName should remain the same as it was not included
        assertThat(target.getLastName()).isEqualTo("Bar");

        // age should be updated
        assertThat(target.getAge()).isEqualTo(25);
    }

    @Test
    public void testVersionChecker() throws Exception {
        TestModel source = new TestModel();
        source.setFirstName("Baz");
        source.setLastName("Test");
        source.setAge(25);

        TestModel target = new TestModel();
        target.setFirstName("Foo");
        target.setLastName("Bar");
        target.setAge(20);

        ModelUtils.checkVersionIfComparables(source, target);

        source.setVersion(null);
        target.setVersion(2L);

        ModelUtils.checkVersionIfComparables(source, target);

        source.setVersion(2L);
        target.setVersion(2L);

        ModelUtils.checkVersionIfComparables(source, target);

        source.setVersion(3L);
        target.setVersion(2L);

        ModelUtils.checkVersionIfComparables(source, target);

        exception.expect(StaleVersionException.class);

        source.setVersion(1L);
        target.setVersion(2L);

        ModelUtils.checkVersionIfComparables(source, target);
    }
}
