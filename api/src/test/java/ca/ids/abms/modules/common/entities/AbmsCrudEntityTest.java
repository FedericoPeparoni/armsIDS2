package ca.ids.abms.modules.common.entities;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class AbmsCrudEntityTest {

    private static final Integer ID = 1;

    private AbmsCrudEntity<Integer> entity;

    @Before
    public void setup() {
        entity = mockEntity();
    }

    @Test
    public void getterTest() {
        assertThat(entity.getId()).isEqualTo(ID);
    }

    @Test
    public void setterTest() {
        entity.setId(ID);

        // validate all values still match by getters
        getterTest();
    }

    public static AbmsCrudEntity<Integer> mockEntity() {
        return new AbmsCrudEntity<Integer>() {

            private Integer id = ID;

            @Override
            public Integer getId() {
                return id;
            }

            @Override
            public void setId(Integer id) {
                this.id = id;
            }
        };
    }
}
