package ca.ids.abms.util.mapper.column;

import org.junit.Before;
import org.junit.Test;

import javax.persistence.Column;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class SimpleColumnMapperTest {

    private SimpleColumnMapper<MockObject> simpleColumnMapper;

    @Before
    public void setup() {
        simpleColumnMapper = new SimpleColumnMapper<MockObject>() {};
    }

    @Test
    public void doubleTest() {

        MockObject mockObject = new MockObject();

        // verify double under column.precision and column.scale is not rounded
        mockObject.setMockDoubleField(1234.56);
        simpleColumnMapper.columnMapping(mockObject);
        assertThat(mockObject.getMockDoubleField())
            .isEqualTo(1234.56);

        // verify double over column.precision and column.scale is rounded
        mockObject.setMockDoubleField(12345.678);
        simpleColumnMapper.columnMapping(mockObject);
        assertThat(mockObject.getMockDoubleField())
            .isEqualTo(12345.7);

        // verify null double is not touched
        mockObject.setMockDoubleField(null);
        simpleColumnMapper.columnMapping(mockObject);
        assertThat(mockObject.getMockDoubleField())
            .isEqualTo(null);
    }

    @Test
    public void integerTest() {

        MockObject mockObject = new MockObject();

        // verify double under column.precision and column.scale is not rounded
        mockObject.setMockIntegerField(123456);
        simpleColumnMapper.columnMapping(mockObject);
        assertThat(mockObject.getMockIntegerField())
            .isEqualTo(123456);

        // verify double over column.precision and column.scale is rounded
        mockObject.setMockIntegerField(1234567);
        simpleColumnMapper.columnMapping(mockObject);
        assertThat(mockObject.getMockIntegerField())
            .isEqualTo(1234570);

        // verify null double is not touched
        mockObject.setMockIntegerField(null);
        simpleColumnMapper.columnMapping(mockObject);
        assertThat(mockObject.getMockIntegerField())
            .isEqualTo(null);
    }

    @Test
    public void stringTest() {

        MockObject mockObject = new MockObject();

        // verify string under column.length is not truncated
        mockObject.setMockStringField("ABC");
        simpleColumnMapper.columnMapping(mockObject);
        assertThat(mockObject.getMockStringField())
            .isEqualTo("ABC");

        // verify string over column.length is truncated
        mockObject.setMockStringField("ABC123");
        simpleColumnMapper.columnMapping(mockObject);
        assertThat(mockObject.getMockStringField())
            .isEqualTo("ABC12");

        // verify null string is not touched
        mockObject.setMockStringField(null);
        simpleColumnMapper.columnMapping(mockObject);
        assertThat(mockObject.getMockStringField())
            .isEqualTo(null);
    }

    @Test
    public void nullTest() {

        // verify null target does not throw exception
        simpleColumnMapper.columnMapping(null);
    }

    private class MockObject {

        @Column(precision = 6, scale = 2)
        private Double mockDoubleField;

        @Column(precision = 6)
        private Integer mockIntegerField;

        @Column(length = 5)
        private String mockStringField;

        Double getMockDoubleField() {
            return mockDoubleField;
        }

        void setMockDoubleField(Double mockDoubleField) {
            this.mockDoubleField = mockDoubleField;
        }

        Integer getMockIntegerField() {
            return mockIntegerField;
        }

        void setMockIntegerField(Integer mockIntegerField) {
            this.mockIntegerField = mockIntegerField;
        }

        String getMockStringField() {
            return mockStringField;
        }

        void setMockStringField(String mockStringField) {
            this.mockStringField = mockStringField;
        }
    }
}
