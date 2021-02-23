package ca.ids.abms.modules.common.enumerators;

import javax.persistence.AttributeConverter;

public class FlightTypeConverter implements AttributeConverter<FlightType, String> {

    @Override
    public String convertToDatabaseColumn(FlightType dbData) {
        if ( dbData == null ) {
            return null;
        }
        return dbData.toValue();
    }

    @Override
    public FlightType convertToEntityAttribute(String attribute) {
        if ( attribute == null ) {
            return null;
        }
        return FlightType.forValue(attribute);
    }
}
