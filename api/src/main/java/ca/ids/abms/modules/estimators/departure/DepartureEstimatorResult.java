package ca.ids.abms.modules.estimators.departure;

import java.time.LocalDateTime;
import java.util.Objects;

public class DepartureEstimatorResult {

    private LocalDateTime dayOfFlight;
    private String depTime;

    private DepartureEstimatorResult(
        final LocalDateTime dayOfFlight,
        final String depTime
    ) {
        this.dayOfFlight = dayOfFlight;
        this.depTime = depTime;
    }

    public LocalDateTime getDayOfFlight() {
        return dayOfFlight;
    }

    public String getDepTime() {
        return depTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DepartureEstimatorResult that = (DepartureEstimatorResult) o;
        return Objects.equals(dayOfFlight, that.dayOfFlight) &&
            Objects.equals(depTime, that.depTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dayOfFlight, depTime);
    }

    @Override
    public String toString() {
        return "DepartureEstimatorResult{" +
            "dayOfFlight=" + dayOfFlight +
            ", depTime='" + depTime + '\'' +
            '}';
    }

    public static class Builder {

        private LocalDateTime dayOfFlight;
        private String depTime;

        public Builder setDayOfFlight(final LocalDateTime dayOfFlight) {
            this.dayOfFlight = dayOfFlight;
            return this;
        }

        public Builder setDepTime(final String depTime) {
            this.depTime = depTime;
            return this;
        }

        public DepartureEstimatorResult build() {
            return new DepartureEstimatorResult(dayOfFlight, depTime);
        }
    }
}
