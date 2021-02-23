/**
 * 
 */
package ca.ids.abms.modules.flightmovements.category;

import ca.ids.abms.modules.util.models.VersionedViewModel;

public class FlightmovementCategoryViewModel extends VersionedViewModel {
	
	private Integer id;

    private String name;
	
	private Integer sortOrder;
	
	private String shortName;
	
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
