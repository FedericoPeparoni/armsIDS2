package ca.ids.abms.modules.estimators.departure;

import java.time.LocalDateTime;
import java.util.Objects;

public class DepartureEstimatorModel {

    private String displayName;
    private String flightId;
    private LocalDateTime dateOfContact;
    private String timeOfContact;
    private String depAd;
    private String regNum;
    private String aircraftType;
    private String cruisingSpeed;
    private String firEntryPoint;
    private String firEntryTime;

    @SuppressWarnings("squid:S00107")
    private DepartureEstimatorModel(
        final String displayName,
        final String flightId,
        final LocalDateTime dateOfContact,
        final String timeOfContact,
        final String depAd,
        final String regNum,
        final String aircraftType,
        final String cruisingSpeed,
        final String firEntryPoint,
        final String firEntryTime
    ) {
        this.displayName = displayName;
        this.flightId = flightId;
        this.dateOfContact = dateOfContact;
        this.timeOfContact = timeOfContact;
        this.depAd = depAd;
        this.regNum = regNum;
        this.aircraftType = aircraftType;
        this.cruisingSpeed = cruisingSpeed;
        this.firEntryPoint = firEntryPoint;
        this.firEntryTime = firEntryTime;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getFlightId() {
        return flightId;
    }

    public LocalDateTime getDateOfContact() {
        return dateOfContact;
    }

    public String getTimeOfContact() {
        return timeOfContact;
    }

    public String getDepAd() {
        return depAd;
    }

    public String getRegNum() {
        return regNum;
    }

    public String getAircraftType() {
        return aircraftType;
    }

    public String getCruisingSpeed() {
        return cruisingSpeed;
    }

    public String getFirEntryPoint() {
        return firEntryPoint;
    }

    public String getFirEntryTime() {
        return firEntryTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DepartureEstimatorModel model = (DepartureEstimatorModel) o;
        return Objects.equals(flightId, model.flightId) &&
            Objects.equals(dateOfContact, model.dateOfContact) &&
            Objects.equals(timeOfContact, model.timeOfContact) &&
            Objects.equals(depAd, model.depAd) &&
            Objects.equals(regNum, model.regNum) &&
            Objects.equals(aircraftType, model.aircraftType) &&
            Objects.equals(cruisingSpeed, model.cruisingSpeed) &&
            Objects.equals(firEntryPoint, model.firEntryPoint) &&
            Objects.equals(firEntryTime, model.firEntryTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(flightId, dateOfContact, timeOfContact, depAd, regNum, aircraftType, cruisingSpeed, firEntryPoint, firEntryTime);
    }

    @Override
    public String toString() {
        return "DepartureEstimatorModel{" +
            "flightId='" + flightId + '\'' +
            ", dateOfContact=" + dateOfContact +
            ", timeOfContact=" + timeOfContact +
            ", depAd='" + depAd + '\'' +
            ", regNum='" + regNum + '\'' +
            ", aircraftType='" + aircraftType + '\'' +
            ", cruisingSpeed='" + cruisingSpeed + '\'' +
            ", firEntryPoint='" + firEntryPoint + '\'' +
            ", firEntryTime='" + firEntryTime + '\'' +
            '}';
    }

    public static class Builder {

        private String displayName;
        private String flightId;
        private LocalDateTime dateOfContact;
        private String timeOfContact;
        private String depAd;
        private String regNum;
        private String aircraftType;
        private String cruisingSpeed;
        private String firEntryPoint;
        private String firEntryTime;

        public Builder(final String displayName) {
            this.displayName = displayName;
        }

        public Builder flightId(final String flightId) {
            this.flightId = flightId;
            return this;
        }

        public Builder dateOfContact(final LocalDateTime dateOfContact) {
            this.dateOfContact = dateOfContact;
            return this;
        }

        public Builder timeOfContact(final String timeOfContact) {
            this.timeOfContact = timeOfContact;
            return this;
        }

        public Builder depAd(final String depAd) {
            this.depAd = depAd;
            return this;
        }

        public Builder regNum(final String regNum) {
            this.regNum = regNum;
            return this;
        }

        public Builder aircraftType(final String aircraftType) {
            this.aircraftType = aircraftType;
            return this;
        }

        public Builder cruisingSpeed(final String cruisingSpeed) {
            this.cruisingSpeed = cruisingSpeed;
            return this;
        }

        public Builder firEntryPoint(final String firEntryPoint) {
            this.firEntryPoint = firEntryPoint;
            return this;
        }

        public Builder firEntryTime(final String firEntryTime) {
            this.firEntryTime = firEntryTime;
            return this;
        }

        public DepartureEstimatorModel build() {
            return new DepartureEstimatorModel(displayName, flightId, dateOfContact, timeOfContact, depAd, regNum,
                aircraftType, cruisingSpeed, firEntryPoint, firEntryTime);
        }
    }
}
