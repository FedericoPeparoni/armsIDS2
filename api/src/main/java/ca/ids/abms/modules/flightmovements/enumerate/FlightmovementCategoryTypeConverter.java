package ca.ids.abms.modules.flightmovements.enumerate;

import javax.persistence.AttributeConverter;

public class FlightmovementCategoryTypeConverter implements AttributeConverter<FlightmovementCategoryType, String> {

    @Override
    public String convertToDatabaseColumn(FlightmovementCategoryType dbData) {
        if ( dbData == null ) {
            return null;
        }
        return dbData.toValue();
    }

    @Override
    public FlightmovementCategoryType convertToEntityAttribute(String attribute) {
        if ( attribute == null ) {
            return null;
        }
        return FlightmovementCategoryType.forValue(attribute);
    }
}
