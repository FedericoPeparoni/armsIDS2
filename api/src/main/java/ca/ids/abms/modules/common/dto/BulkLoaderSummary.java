package ca.ids.abms.modules.common.dto;

import ca.ids.abms.modules.common.enumerators.ItemLoaderResult;
import ca.ids.abms.modules.flightmovements.FlightMovementService;

public class BulkLoaderSummary implements ItemLoaderObserver {

    private Long successfullyProcessed = 0L;

    private Long rejected = 0L;

    private Long fplAdded = 0L;

    private Long fplUpdated = 0L;

    public Long getSuccessfullyProcessed() {
        return successfullyProcessed;
    }

    public void setSuccessfullyProcessed(Long successfullyProcessed) {
        this.successfullyProcessed = successfullyProcessed;
    }

    public Long getTotal() {
        return Long.sum(successfullyProcessed, rejected);
    }

    public Long getRejected() {
        return rejected;
    }

    public void setRejected(Long rejected) {
        this.rejected = rejected;
    }

    public void incrementProcessed() {
        successfullyProcessed++;
    }

    public void incrementRejected() {
        rejected++;
    }

    public Long getFplAdded() {
        return this.fplAdded;
    }

    public Long getFplUpdated() {
        return this.fplUpdated;
    }

    /**
     * Used to record when records added, updated, deleted, or rejected.
     * @param o object, supports FlightMovementService
     * @param result results, supports ItemLoaderResult
     */
    public void update(Object o, ItemLoaderResult result) {

        if (o instanceof FlightMovementService && result == ItemLoaderResult.CREATED) {
            fplAdded++;
        } else if (o instanceof FlightMovementService && result == ItemLoaderResult.UPDATED) {
            fplUpdated++;
        }
    }

    @Override
    public String toString() {
        return "BulkLoaderSummary{" +
            "successfully_processed=" + successfullyProcessed +
            ", rejected=" + rejected +
            ", total=" + getTotal() +
            ", fpl_added=" + this.fplAdded +
            ", fpl_updated=" + this.fplUpdated +
            '}';
    }
}
