package ca.ids.abms.modules.aerodromeservicetypes;

import ca.ids.abms.modules.util.models.VersionedViewModel;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class AerodromeServiceTypeViewModel extends VersionedViewModel {

    private Integer id;

    @NotNull
    @Size(max = 50)
    private String serviceName;

    @NotNull
    @Size(max = 30)
    private DiscountType serviceOutageApproachDiscountType;

    @NotNull
    private Double serviceOutageApproachAmount;

    @NotNull
    @Size(max = 30)
    private DiscountType serviceOutageAerodromeDiscountType;

    @NotNull
    private Double serviceOutageAerodromeAmount;

    @NotNull
    @Size(max = 255)
    private String defaultFlightNotes;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public DiscountType getServiceOutageApproachDiscountType() {
        return serviceOutageApproachDiscountType;
    }

    public void setServiceOutageApproachDiscountType(DiscountType serviceOutageApproachDiscountType) {
        this.serviceOutageApproachDiscountType = serviceOutageApproachDiscountType;
    }

    public Double getServiceOutageApproachAmount() {
        return serviceOutageApproachAmount;
    }

    public void setServiceOutageApproachAmount(Double serviceOutageApproachAmount) {
        this.serviceOutageApproachAmount = serviceOutageApproachAmount;
    }

    public DiscountType getServiceOutageAerodromeDiscountType() {
        return serviceOutageAerodromeDiscountType;
    }

    public void setServiceOutageAerodromeDiscountType(DiscountType serviceOutageAerodromeDiscountType) {
        this.serviceOutageAerodromeDiscountType = serviceOutageAerodromeDiscountType;
    }

    public Double getServiceOutageAerodromeAmount() {
        return serviceOutageAerodromeAmount;
    }

    public void setServiceOutageAerodromeAmount(Double serviceOutageAerodromeAmount) {
        this.serviceOutageAerodromeAmount = serviceOutageAerodromeAmount;
    }

    public String getDefaultFlightNotes() {
        return defaultFlightNotes;
    }

    public void setDefaultFlightNotes(String defaultFlightNotes) {
        this.defaultFlightNotes = defaultFlightNotes;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        AerodromeServiceTypeViewModel aerodromeServiceTypeViewModel = (AerodromeServiceTypeViewModel) o;

        return id != null ? id.equals(aerodromeServiceTypeViewModel.id) : aerodromeServiceTypeViewModel.id == null;
    }

    @Override
    public String toString() {
        return "AerodromeServiceTypeViewModel{" +
            "id=" + id +
            ", serviceName='" + serviceName + '\'' +
            ", serviceOutageApproachDiscountType='" + serviceOutageApproachDiscountType + '\'' +
            ", serviceOutageApproachAmount=" + serviceOutageApproachAmount +
            ", serviceOutageAerodromeDiscountType='" + serviceOutageAerodromeDiscountType + '\'' +
            ", serviceOutageAerodromeAmount=" + serviceOutageAerodromeAmount +
            ", defaultFlightNotes='" + defaultFlightNotes + '\'' +
            '}';
    }
}
