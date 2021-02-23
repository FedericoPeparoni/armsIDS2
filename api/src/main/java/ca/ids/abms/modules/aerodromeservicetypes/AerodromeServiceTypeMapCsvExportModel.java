package ca.ids.abms.modules.aerodromeservicetypes;

import ca.ids.abms.modules.aerodromeserviceoutages.AerodromeServiceOutageCsvExportModel;

import java.util.ArrayList;
import java.util.List;

public class AerodromeServiceTypeMapCsvExportModel {

    private String aerodrome;

    private String aerodromeServiceType;

    private List<AerodromeServiceOutageCsvExportModel> outages = new ArrayList<>();

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

    public List<AerodromeServiceOutageCsvExportModel> getOutages() {
        return outages;
    }

    public void setOutages(List<AerodromeServiceOutageCsvExportModel> outages) {
        if (outages.isEmpty()) {
            outages.add(new AerodromeServiceOutageCsvExportModel());
        }
        this.outages = outages;

    }
}
