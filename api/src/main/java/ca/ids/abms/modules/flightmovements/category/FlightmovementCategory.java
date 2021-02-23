package ca.ids.abms.modules.flightmovements.category;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ca.ids.abms.config.db.validators.UniqueKey;
import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;

@Entity
@UniqueKey(columnNames = {"name","shortName"})
public class FlightmovementCategory extends VersionedAuditedEntity{
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	@Column(unique = true)
	private String name;

    @Column(name="sort_order")
	private Integer sortOrder;
	
	@Column(unique = true, name= "short_name")
	private String shortName;
	
	@ManyToOne
    @JoinColumn(name = "enroute_result_currency_id")
	private Currency enrouteResultCurrency;
	
	@ManyToOne
    @JoinColumn(name="enroute_invoice_currency_id")
	private Currency enrouteInvoiceCurrency;
	
	@JsonIgnore
    @OneToMany(mappedBy = "flightmovementCategory")
    private Set<FlightmovementCategoryAttribute> flightmovementCategoryAttributes = new HashSet<>();
	
	private static final long serialVersionUID = 1L;
	
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof FlightmovementCategory)) {
            return false;
        }
        final FlightmovementCategory that = (FlightmovementCategory) o;

        return id != null ? id.equals(that.getId()) : that.getId() == null;
    }
	 
	public Currency getEnrouteInvoiceCurrency() {
		return enrouteInvoiceCurrency;
	}

	public Currency getEnrouteResultCurrency() {
		return enrouteResultCurrency;
	}

	public Set<FlightmovementCategoryAttribute> getFlightmovementCategoryAttributes() {
		return flightmovementCategoryAttributes;
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getShortName() {
		return shortName;
	}

	public Integer getSortOrder() {
		return sortOrder;
	}

	@Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

	public void setEnrouteInvoiceCurrency(Currency enrouteInvoiceCurrency) {
		this.enrouteInvoiceCurrency = enrouteInvoiceCurrency;
	}

	public void setEnrouteResultCurrency(Currency enrouteResultCurrency) {
		this.enrouteResultCurrency = enrouteResultCurrency;
	}

	public void setFlightmovementCategoryAttributes(Set<FlightmovementCategoryAttribute> flightmovementCategoryAttributes) {
		this.flightmovementCategoryAttributes = flightmovementCategoryAttributes;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}
	
}
