package ca.ids.abms.modules.plugins.utility;

import ca.ids.abms.plugins.PluginKey;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class PluginKeyConverter implements AttributeConverter<PluginKey, String> {

    @Override
    public String convertToDatabaseColumn(PluginKey key) {
        return key.toValue();
    }

    @Override
    public PluginKey convertToEntityAttribute(String value) {
        return PluginKey.forValue(value);
    }
}
