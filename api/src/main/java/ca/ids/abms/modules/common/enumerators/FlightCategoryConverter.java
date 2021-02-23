package ca.ids.abms.modules.common.enumerators;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class FlightCategoryConverter implements AttributeConverter<FlightCategory, String> {

    @Override
    public String convertToDatabaseColumn(FlightCategory dbData) {
        if ( dbData == null ) {
            return null;
        }
        return dbData.toValue();
    }

    @Override
    public FlightCategory convertToEntityAttribute(String attribute) {
        if ( attribute == null ) {
            return null;
        }
        return FlightCategory.forValue(attribute);
    }
}
