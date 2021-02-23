package ca.ids.abms.modules.flightmovements.enumerate;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class FlightmovementCategoryScopeConverter implements AttributeConverter<FlightmovementCategoryScope, String> {

    @Override
    public String convertToDatabaseColumn(FlightmovementCategoryScope dbData) {
        if ( dbData == null ) {
            return null;
        }
        return dbData.toValue();
    }

    @Override
    public FlightmovementCategoryScope convertToEntityAttribute(String attribute) {
        if ( attribute == null ) {
            return null;
        }
        return FlightmovementCategoryScope.forValue(attribute);
    }
}