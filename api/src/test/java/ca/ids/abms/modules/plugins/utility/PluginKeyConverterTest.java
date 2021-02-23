package ca.ids.abms.modules.plugins.utility;

import ca.ids.abms.plugins.PluginKey;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class PluginKeyConverterTest {

    private PluginKeyConverter pluginKeyConverter;

    @Before
    public void setup() {
        pluginKeyConverter = new PluginKeyConverter();
    }

    @Test
    public void convertToDatabaseColumnTest() {
        String result = pluginKeyConverter.convertToDatabaseColumn(PluginKey.PROTOTYPE);
        assertThat(result).isEqualTo(PluginKey.PROTOTYPE.toValue());
    }

    @Test
    public void convertToEntityAttributeTest() {
        PluginKey result = pluginKeyConverter.convertToEntityAttribute(PluginKey.PROTOTYPE.toValue());
        assertThat(result).isEqualTo(PluginKey.PROTOTYPE);
    }
}
