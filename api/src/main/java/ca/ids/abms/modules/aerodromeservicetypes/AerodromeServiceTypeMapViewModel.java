package ca.ids.abms.modules.aerodromeservicetypes;

import ca.ids.abms.modules.aerodromeserviceoutages.AerodromeServiceOutageComboViewModel;

import java.util.List;

public class AerodromeServiceTypeMapViewModel {

    private String aerodrome;

    private String aerodromeServiceType;

    private List<AerodromeServiceOutageComboViewModel> aerodromeServiceOutages;

    public String getAerodrome() {
        return aerodrome;
    }

    public void setAerodrome(String aerodrome) {
        this.aerodrome = aerodrome;
    }

    public String getAerodromeServiceType() {
        return aerodromeServiceType;
    }

    public void setAerodromeServiceType(String aerodromeServiceType) {
        this.aerodromeServiceType = aerodromeServiceType;
    }

    public List<AerodromeServiceOutageComboViewModel> getAerodromeServiceOutages() {
        return aerodromeServiceOutages;
    }

    public void setAerodromeServiceOutages(List<AerodromeServiceOutageComboViewModel> aerodromeServiceOutages) {
        this.aerodromeServiceOutages = aerodromeServiceOutages;
    }
}
