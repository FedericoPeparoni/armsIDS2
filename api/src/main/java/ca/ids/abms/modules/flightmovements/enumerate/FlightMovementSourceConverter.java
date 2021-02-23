package ca.ids.abms.modules.flightmovements.enumerate;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class FlightMovementSourceConverter implements AttributeConverter<FlightMovementSource, String> {

    @Override
    public String convertToDatabaseColumn(FlightMovementSource dbData) {
        String column=null;

        if (dbData!=null ) {
            column= dbData.toValue();
        }
        return column;
    }

    @Override
    public FlightMovementSource convertToEntityAttribute(String attribute) {
        FlightMovementSource flightMovementSource=null;
        if ( attribute != null && !attribute.isEmpty()) {
            if(attribute.equalsIgnoreCase(FlightMovementSource.NETWORK_TYPE)){
                flightMovementSource=FlightMovementSource.NETWORK;
            }else{
                flightMovementSource=FlightMovementSource.forValue(attribute);
            }
        }
        return flightMovementSource;
    }
}
