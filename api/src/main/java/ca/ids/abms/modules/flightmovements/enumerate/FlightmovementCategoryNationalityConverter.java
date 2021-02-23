/**
 * 
 */
package ca.ids.abms.modules.flightmovements.enumerate;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import ca.ids.abms.modules.common.enumerators.FlightItemType;

/**
 * @author heskina
 *
 */
@Converter
public class FlightmovementCategoryNationalityConverter implements AttributeConverter<FlightmovementCategoryNationality, String> {

    @Override
    public String convertToDatabaseColumn(FlightmovementCategoryNationality dbData) {
        if ( dbData == null ) {
            return null;
        }
        return dbData.toValue();
    }

    @Override
    public FlightmovementCategoryNationality convertToEntityAttribute(String attribute) {
        if ( attribute == null ) {
            return null;
        }
        return FlightmovementCategoryNationality.forValue(attribute);
    }
}
