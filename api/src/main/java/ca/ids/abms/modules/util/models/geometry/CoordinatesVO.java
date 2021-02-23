package ca.ids.abms.modules.util.models.geometry;


public class CoordinatesVO {

    private Double latitude;

    private Double longitude;

    public CoordinatesVO(Double lat, Double lon){
        this.latitude =lat;
        this.longitude =lon;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "CoordinatesVO{" +
            "latitude=" + latitude +
            ", longitude=" + longitude +
            '}';
    }
}
