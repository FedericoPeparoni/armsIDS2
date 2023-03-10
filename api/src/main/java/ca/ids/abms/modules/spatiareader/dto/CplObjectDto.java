package ca.ids.abms.modules.spatiareader.dto;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * FplObject generated by hbm2java
 */
@Entity
@Table(name = "processed_cpl", schema = "${spatia-db.schemaName}")
public class CplObjectDto implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

	@NotNull
	private long catalogueCplId;

    @NotNull
    private Date catalogueDate;

	@NotNull
	private String estimateData;

	@NotNull
	private String cruisingAltitude;

	@NotNull
	private String cruisingSpeed;

	@NotNull
	private String route;

	@NotNull
	private String destinationAerodrome;

	@NotNull
	private long flightObjectId;

	public CplObjectDto() {
	}
	@Column(name = "estimate_data", nullable = false, length = 1)
	public String getEstimateData() {
		return estimateData;
	}

	public void setEstimateData(String estimateData) {
		this.estimateData = estimateData;
	}

	@Column(name = "flight_level", nullable = false, length = 1)
	public String getCruisingAltitude() {
		return cruisingAltitude;
	}

	public void setCruisingAltitude(String cruisingAltitude) {
		this.cruisingAltitude = cruisingAltitude;
	}

	@Column(name = "speed", nullable = false, length = 1)
	public String getCruisingSpeed() {
		return cruisingSpeed;
	}

	public void setCruisingSpeed(String cruisingSpeed) {
		this.cruisingSpeed = cruisingSpeed;
	}

	@Column(name = "route", nullable = false, length = 1)
	public String getRoute() {
		return route;
	}

	public void setRoute(String route) {
		this.route = route;
	}

	@Column(name = "destination_ad", nullable = false, length = 1)
	public String getDestinationAerodrome() {
		return destinationAerodrome;
	}

	public void setDestinationAerodrome(String destinationAerodrome) {
		this.destinationAerodrome = destinationAerodrome;
	}

	@Column(name = "catalogue_cpl_id", nullable = false, length = 1)
	public void setCatalogueCplId(long catalogueCplId) {
		this.catalogueCplId = catalogueCplId;
	}

	@Id
	@Column(name = "catalogue_cpl_id", unique = true, nullable = false, precision = 10, scale = 0)
	public long getCatalogueCplId() {
		return this.catalogueCplId;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "catalogue_date", nullable = false, length = 22)
	public Date getCatalogueDate() {
		return this.catalogueDate;
	}

	public void setCatalogueDate(Date catalogueDate) {
		this.catalogueDate = catalogueDate;
	}

	@Column(name = "fpl_object_id", nullable = false, length = 22)
	public long getFlightObjectId() {
		return flightObjectId;
	}

	public void setFlightObjectId(long flightObjectId) {
		this.flightObjectId = flightObjectId;
	}

}
