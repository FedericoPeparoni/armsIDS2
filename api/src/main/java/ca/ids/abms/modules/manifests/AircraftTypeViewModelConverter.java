package ca.ids.abms.modules.manifests;

import ca.ids.abms.modules.aircraft.AircraftTypeViewModel;
import com.opencsv.bean.AbstractBeanField;

import static java.lang.Integer.parseInt;

public class AircraftTypeViewModelConverter extends AbstractBeanField {

    @Override
    public AircraftTypeViewModel convert(String aircraftTypeId) {
        if (aircraftTypeId != null) {
            final AircraftTypeViewModel dto = new AircraftTypeViewModel();
            dto.setId(parseInt(aircraftTypeId));
            return dto;
        }
        return null;
    }
}
