package ca.ids.abms.config.db;

import ca.ids.abms.config.db.CustomImplicitNamingStrategy.NamingConverter;
import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class NamingConverterTest {

    @Test
    public void determinePrimaryTableName() throws Exception {
        String input = "User";
        String expected = "users";

        String result = NamingConverter.determinePrimaryTableName(input);

        assertThat(result).isEqualTo(expected);

        input = "FlightMovement";
        expected = "flight_movements";

        result = NamingConverter.determinePrimaryTableName(input);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void determineJoinTableName() throws Exception {
        String owner = "User";
        String nonOwner = "Role";
        String expected = "user_role";

        String result = NamingConverter.determineJoinTableName(owner, nonOwner);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void determineJoinColumnName() throws Exception {
        String entityName = "User";
        String columnName = "id";
        String expected = "user_id";

        String result = NamingConverter.determineJoinColumnName(entityName, columnName);

        assertThat(result).isEqualTo(expected);
    }
}
