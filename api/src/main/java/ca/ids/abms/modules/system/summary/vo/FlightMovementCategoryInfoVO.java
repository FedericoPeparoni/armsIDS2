package ca.ids.abms.modules.system.summary.vo;

public class FlightMovementCategoryInfoVO {

    private String categoryName;
    
    private FlightMovementVO flightMovementVO;

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FlightMovementCategoryInfoVO other = (FlightMovementCategoryInfoVO) obj;
        if (categoryName == null) {
            if (other.categoryName != null)
                return false;
        } else if (!categoryName.equals(other.categoryName))
            return false;
        return true;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public FlightMovementVO getFlightMovementVO() {
        return flightMovementVO;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((categoryName == null) ? 0 : categoryName.hashCode());
        return result;
    }

    public void setCategoryName(String aCategoryName) {
        categoryName = aCategoryName;
    }

    public void setFlightMovementVO(FlightMovementVO aFlightMovementVO) {
        flightMovementVO = aFlightMovementVO;
    }

    @Override
    public String toString() {
        return "FlightMovementCategoryInfoVO [categoryName=" + categoryName + ", flightMovementVO=" + flightMovementVO
                + "]";
    }
}
