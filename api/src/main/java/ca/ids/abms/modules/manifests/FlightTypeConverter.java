package ca.ids.abms.modules.manifests;

import com.opencsv.bean.AbstractBeanField;

public class FlightTypeConverter extends AbstractBeanField {

    @Override
    public FlightType convert(String flightType) {
        return FlightType.forValue(flightType);
    }

}
