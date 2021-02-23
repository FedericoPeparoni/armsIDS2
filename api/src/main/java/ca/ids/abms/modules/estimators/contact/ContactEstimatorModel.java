package ca.ids.abms.modules.estimators.contact;

import java.time.LocalDateTime;
import java.util.Objects;

public class ContactEstimatorModel {

    private String displayName;
    private String depAd;
    private String cruisingSpeed;
    private LocalDateTime dateOfFlight;
    private String deptime;
    private String actualArrivalTime;
    private String eet;
    private String contactPoint;

    @SuppressWarnings("squid:S00107")
    private ContactEstimatorModel(String displayName, String depAd, String cruisingSpeed,
            LocalDateTime dateOfFlight, String deptime, String actualArrivalTime, String eet, String contactPoint) {

        this.displayName = displayName;
        this.depAd = depAd;
        this.cruisingSpeed=cruisingSpeed;
        this.dateOfFlight = dateOfFlight;
        this.deptime =deptime;
        this.actualArrivalTime =actualArrivalTime;
        this.eet=eet;
        this.contactPoint = contactPoint;

    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDepAd() {
        return depAd;
    }

    public String getCruisingSpeed() {
        return cruisingSpeed;
    }

    public LocalDateTime getDateOfFlight() {
        return dateOfFlight;
    }

    public String getDeptime() {
        return deptime;
    }

    public String getActualArrivalTime() {
        return actualArrivalTime;
    }

    public String getEet() {
        return eet;
    }

    public String getContactPoint() {
        return contactPoint;
    }

    @Override
    public int hashCode() {
        return Objects.hash(depAd, cruisingSpeed, dateOfFlight, deptime, actualArrivalTime, eet, contactPoint);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContactEstimatorModel model = (ContactEstimatorModel) o;
        return Objects.equals(depAd, model.depAd) &&
            Objects.equals(cruisingSpeed, model.cruisingSpeed) &&
            Objects.equals(dateOfFlight, model.dateOfFlight) &&
            Objects.equals(deptime, model.deptime) &&
            Objects.equals(actualArrivalTime, model.actualArrivalTime) &&
            Objects.equals(eet, model.eet) &&
            Objects.equals(contactPoint, model.contactPoint) ;
    }

    @Override
    public String toString() {
        return "ContactEstimatorModel{" +
            "depAd='" + depAd + '\'' +
            ", cruisingSpeed=" + cruisingSpeed +
            ", dateOfFlight=" + dateOfFlight +
            ", deptime='" + deptime + '\'' +
            ", actualArrivalTime='" + actualArrivalTime + '\'' +
            ", eet='" + eet + '\'' +
            ", contactPoint='" + contactPoint + '\'' +
            '}';
    }

    public static class Builder {
        private String displayName;
        private String depAd;
        private String cruisingSpeed;
        private LocalDateTime dateOfFlight;
        private String deptime;
        private String actualArrivalTime;
        private String eet;
        private String contactPoint;

        public Builder(final String displayName) {
            this.displayName = displayName;
        }

        public Builder cruisingSpeed(final String cruisingSpeed) {
            this.cruisingSpeed = cruisingSpeed;
            return this;
        }

        public Builder depAd(final String depAd) {
            this.depAd = depAd;
            return this;
        }

        public Builder dateOfFlight(final LocalDateTime dateOfFlight) {
            this.dateOfFlight = dateOfFlight;
            return this;
        }

        public Builder deptime(final String deptime) {
            this.deptime = deptime;
            return this;
        }

        public Builder actualArrivalTime(final String actualArrivalTime) {
            this.actualArrivalTime = actualArrivalTime;
            return this;
        }

        public Builder contactPoint(final String contactPoint) {
            this.contactPoint = contactPoint;
            return this;
        }

        public Builder eet(final String eet) {
            this.eet = eet;
            return this;
        }

        public ContactEstimatorModel build() {
            return new ContactEstimatorModel(displayName,  depAd, cruisingSpeed, dateOfFlight, deptime, actualArrivalTime, eet, contactPoint);
        }
    }
}
