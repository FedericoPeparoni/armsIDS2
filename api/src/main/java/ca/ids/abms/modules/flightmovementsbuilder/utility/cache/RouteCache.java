package ca.ids.abms.modules.flightmovementsbuilder.utility.cache;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import ca.ids.abms.config.db.validators.UniqueKey;
import ca.ids.abms.modules.util.models.AuditedEntity;

@Entity
@Table(name="route_cache")
@UniqueKey(columnNames={"departureAerodrome", "destinationAerodrome", "routeText", "speed", "estimatedElapsed"})
public class RouteCache extends AuditedEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    private String departureAerodrome;
    
    @NotNull
    private String destinationAerodrome;
    
    @NotNull
    private String routeText;
    
    private Integer speed;
    
    private Integer estimatedElapsed;

    @NotNull
    private byte[] parsedRoute;
    
    private Double flightLevel;

    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof RouteCache)) {
            return false;
        }

        RouteCache that = (RouteCache) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    public String getDepartureAerodrome() {
        return departureAerodrome;
    }

    public String getDestinationAerodrome() {
        return destinationAerodrome;
    }

    public Integer getEstimatedElapsed() {
        return estimatedElapsed;
    }

    public Integer getId() {
        return id;
    }

    public byte[] getParsedRoute() {
        return parsedRoute;
    }

    public String getRouteText() {
        return routeText;
    }

    public Integer getSpeed() {
        return speed;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public void setDepartureAerodrome(String aDepartureAerodrome) {
        departureAerodrome = aDepartureAerodrome;
    }

    public void setDestinationAerodrome(String aDestinationAerodrome) {
        destinationAerodrome = aDestinationAerodrome;
    }

    public void setEstimatedElapsed(Integer aEstimatedElapsed) {
        estimatedElapsed = aEstimatedElapsed;
    }

    public void setId(Integer aId) {
        id = aId;
    }

    public void setParsedRoute(byte[] aParsedRoute) {
        parsedRoute = aParsedRoute;
    }

    public void setRouteText(String aRouteText) {
        routeText = aRouteText;
    }

    public void setSpeed(Integer aSpeed) {
        speed = aSpeed;
    }

    public Double getFlightLevel() {
        return flightLevel;
    }

    public void setFlightLevel(Double flightLevel) {
        this.flightLevel = flightLevel;
    }

    @Override
    public String toString() {
        return "RouteCache [id=" + id + ", departureAerodrome=" + departureAerodrome + ", destinationAerodrome="
                + destinationAerodrome + ", routeText=" + routeText + ", speed=" + speed + ", estimatedElapsed="
                + estimatedElapsed + "]";
    }

}
