package ca.ids.abms.modules.aerodromes;

import javax.validation.constraints.Size;

import ca.ids.abms.modules.aerodromeservicetypes.AerodromeServiceTypeViewModel;
import ca.ids.abms.modules.util.models.VersionedViewModel;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vividsolutions.jts.geom.Geometry;

import ca.ids.abms.modules.billingcenters.BillingCenter;
import ca.ids.abms.modules.util.models.geometry.GeometryDeserializer;
import ca.ids.abms.modules.util.models.geometry.GeometrySerializer;

import java.util.List;

public class AerodromeViewModel extends VersionedViewModel {

    private Integer id;

    @Size(max = 100)
    private String aerodromeName;

    @Size(max = 100)
    private String extendedAerodromeName;

    private Boolean aixmFlag;

    private Boolean isDefaultBillingCenter;

    @JsonSerialize(using = GeometrySerializer.class)
    @JsonDeserialize(using = GeometryDeserializer.class)
    private Geometry geometry;

    private AerodromeCategory aerodromeCategory;

    private BillingCenter billingCenter;

    private String externalAccountingSystemIdentifier;

    private List<AerodromeServiceTypeViewModel> aerodromeServices;

    public AerodromeCategory getAerodromeCategory() {
        return aerodromeCategory;
    }

    public String getAerodromeName() {
        return aerodromeName;
    }

    public String getExtendedAerodromeName() {
        return extendedAerodromeName;
    }

    public Boolean getAixmFlag() {
        return aixmFlag;
    }

    public BillingCenter getBillingCenter() {
        return billingCenter;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public Integer getId() {
        return id;
    }

    public Boolean getIsDefaultBillingCenter() {
        return isDefaultBillingCenter;
    }

    public String getExternalAccountingSystemIdentifier() {
        return externalAccountingSystemIdentifier;
    }

    public List<AerodromeServiceTypeViewModel> getAerodromeServices() {
        return aerodromeServices;
    }

    public void setAerodromeCategory(AerodromeCategory aAerodromeCategory) {
        aerodromeCategory = aAerodromeCategory;
    }

    public void setAerodromeName(String aAerodromeName) {
        aerodromeName = aAerodromeName;
    }

    public void setExtendedAerodromeName(String extendedAerodromeName) {
        this.extendedAerodromeName = extendedAerodromeName;
    }

    public void setAixmFlag(Boolean aAixmFlag) {
        aixmFlag = aAixmFlag;
    }

    public void setBillingCenter(BillingCenter aBillingCenter) {
        billingCenter = aBillingCenter;
    }

    public void setGeometry(Geometry aGeometry) {
        geometry = aGeometry;
    }

    public void setId(Integer aId) {
        id = aId;
    }

    public void setIsDefaultBillingCenter(Boolean aIsDefaultBillingCenter) {
        isDefaultBillingCenter = aIsDefaultBillingCenter;
    }

    public void setExternalAccountingSystemIdentifier(String externalAccountingSystemIdentifier) {
        this.externalAccountingSystemIdentifier = externalAccountingSystemIdentifier;
    }

    public void setAerodromeServices(List<AerodromeServiceTypeViewModel> aerodromeServices) {
        this.aerodromeServices = aerodromeServices;
    }

    @Override
    public String toString() {
        return "AerodromeViewModel{" +
            "id=" + id +
            ", aerodromeName='" + aerodromeName + '\'' +
            ", aerodromeServices=" + aerodromeServices +
            '}';
    }
}
