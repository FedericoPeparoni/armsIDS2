package ca.ids.abms.modules.aerodromes;

import ca.ids.abms.modules.aerodromeservicetypes.AerodromeServiceTypeViewModel;

import java.util.Set;

public class AerodromeComboViewModel {

    private Integer id;

    private String aerodromeName;

    private Set<AerodromeServiceTypeViewModel> aerodromeServices;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAerodromeName() {
        return aerodromeName;
    }

    public void setAerodromeName(String aerodromeName) {
        this.aerodromeName = aerodromeName;
    }

    public Set<AerodromeServiceTypeViewModel> getAerodromeServices() {
        return aerodromeServices;
    }

    public void setAerodromeServices(Set<AerodromeServiceTypeViewModel> aerodromeServices) {
        this.aerodromeServices = aerodromeServices;
    }
}
