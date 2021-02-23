package ca.ids.abms.modules.flightmovementsbuilder.utility.cache;

public class RouteCacheSummaryViewModel {

    private Long count;
    
    private Long numberOfRetention;

    public Long getCount() {
        return count;
    }

	public Long getNumberOfRetention() {
		return numberOfRetention;
	}

	public void setCount(Long aCount) {
        count = aCount;
    }

    public void setNumberOfRetention(Long numberOfRetention) {
		this.numberOfRetention = numberOfRetention;
	}

    @Override
	public String toString() {
		return "RouteCacheSummaryViewModel [count=" + count + ", numberOfRetention=" + numberOfRetention + "]";
	}
}
