package ca.ids.abms.modules.common.enumerators;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class FlightItemTypeConverter implements AttributeConverter<FlightItemType, String> {

    @Override
    public String convertToDatabaseColumn(FlightItemType dbData) {
        if ( dbData == null ) {
            return null;
        }
        return dbData.toValue();
    }

    @Override
    public FlightItemType convertToEntityAttribute(String attribute) {
        if ( attribute == null ) {
            return null;
        }
        return FlightItemType.forValue(attribute);
    }
}
