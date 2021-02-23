package ca.ids.abms.modules.jobs.impl;

import ca.ids.abms.modules.jobs.JobMessage;

import java.util.Observable;
import java.util.Observer;

/**
 * Counters to use into a {Step} object. Not thread safe.
 */
public class StepSummary extends Observable {

    private final Integer totalItems;

    private Integer itemsToProcess;

    private Integer processed;

    private Integer discarded;

    private JobMessage message;

    public StepSummary(int itemsToProcess) {
        this.totalItems = itemsToProcess;
        this.itemsToProcess = itemsToProcess;
        this.processed = 0;
        this.discarded = 0;
    }

    public StepSummary(int itemsToProcess, final Observer observer) {
        this(itemsToProcess);
        addObserver(observer);
        this.setChanged();
        this.notifyObservers();
    }

    public Integer getTotalItems() {
        return totalItems;
    }

    public Integer getItemsToProcess() {
        return itemsToProcess;
    }

    public Integer getProcessed() {
        return processed;
    }

    public Integer getDiscarded() {
        return discarded;
    }

    public void sendNoItemToProcess() {
        this.itemsToProcess = 0;
        update();
    }

    public void increaseProcessed() {
        if (this.itemsToProcess > 0) {
            this.itemsToProcess--;
        }
        this.processed++;
        this.setChanged();
        this.notifyObservers();
    }

    public void increaseDiscarded() {
        if (this.itemsToProcess > 0) {
            this.itemsToProcess--;
        }
        this.discarded++;
        this.setChanged();
        this.notifyObservers();
    }

    public void increaseProcessed(int counts) {
        this.itemsToProcess -= counts;
        if (this.itemsToProcess < 0) {
            this.itemsToProcess = 0;
        }
        this.processed += counts;
        this.setChanged();
        this.notifyObservers();
    }

    public void increaseDiscarded(int counts) {
        this.itemsToProcess -= counts;
        if (this.itemsToProcess < 0) {
            this.itemsToProcess = 0;
        }
        this.discarded += counts;
        this.setChanged();
        this.notifyObservers();
    }

    public void update() {
        this.setChanged();
        this.notifyObservers();
    }

    public void setMessage(final JobMessage message) {
        this.message = message;
        this.setChanged();
        this.notifyObservers();
    }

    public JobMessage getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "StepSummary{" +
            "totalItems=" + totalItems +
            ", itemsToProcess=" + itemsToProcess +
            ", processed=" + processed +
            ", discarded=" + discarded +
            ", message=" + message +
            '}';
    }
}
