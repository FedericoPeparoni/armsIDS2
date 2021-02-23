/**
 * 
 */
package ca.ids.abms.modules.flightmovements.category;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import ca.ids.abms.modules.util.models.VersionedAuditedEntity;

@Entity
public class FlightmovementCategoryAttribute extends VersionedAuditedEntity{
	
	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

	@ManyToOne
    @JoinColumn(name = "flightmovement_category")
	private FlightmovementCategory flightmovementCategory;

	@Column(name="flight_type",length = 2)
	private String flightType;
	
	@Column(name="flight_scope",length = 2)
	private String flightScope;
	
	@Column(name="flight_nationality",length = 2)
	private String flightNationality;
	
	@Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
	 
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof FlightmovementCategory)) {
            return false;
        }
        final FlightmovementCategory that = (FlightmovementCategory) o;

        return id != null ? id.equals(that.getId()) : that.getId() == null;
    }

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public FlightmovementCategory getFlightmovementCategory() {
		return flightmovementCategory;
	}

	public void setFlightmovementCategory(FlightmovementCategory flightmovementCategory) {
		this.flightmovementCategory = flightmovementCategory;
	}

	public String getFlightType() {
		return flightType;
	}

	public void setFlightType(String flightType) {
		this.flightType = flightType;
	}

	public String getFlightScope() {
		return flightScope;
	}

	public void setFlightScope(String flightScope) {
		this.flightScope = flightScope;
	}

	public String getFlightNationality() {
		return flightNationality;
	}

	public void setFlightNationality(String flightNationality) {
		this.flightNationality = flightNationality;
	}
	
}
