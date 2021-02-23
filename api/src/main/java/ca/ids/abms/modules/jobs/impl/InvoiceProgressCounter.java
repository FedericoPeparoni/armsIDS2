package ca.ids.abms.modules.jobs.impl;

import ca.ids.abms.modules.jobs.JobMessage;

import java.util.Observer;

public class InvoiceProgressCounter extends StepSummary {

    private String message;

    private String accountName;

    private int accountNumber;

    private int accountsTotal;

    private int flightNumber;

    private int flightsTotal;

    public InvoiceProgressCounter(int itemsToProcess) {
        super(itemsToProcess);

    }

    public InvoiceProgressCounter(int itemsToProcess, final Observer observer) {
        super(itemsToProcess, observer);
        addObserver(observer);
        this.setChanged();
        this.notifyObservers();
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public void resetAccountsTotal(int accountsTotal) {
        this.accountsTotal = accountsTotal;
        this.accountNumber = 0;
        this.accountName = null;
        setFlightsTotal(0);
    }

    public void increaseAccountNumber () {
        if (accountNumber < accountsTotal) {
            accountNumber++;
            setFlightsTotal(0);
        }
    }

    public void setFlightsTotal(int flightsTotal) {
        this.flightsTotal = flightsTotal;
        this.flightNumber = 0;
    }

    public void increaseFlightNumber() {
        if (flightNumber < flightsTotal) {
            flightNumber++;
        }
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public int getAccountsTotal() {
        return accountsTotal;
    }

    public int getFlightNumber() {
        return flightNumber;
    }

    public int getFlightsTotal() {
        return flightsTotal;
    }

    @Override
    public void update() {
        final  JobMessage.Builder jmb = new JobMessage.Builder().setMessage(message);
        if (accountName != null) {
            jmb.addVariable("accountName", accountName);
        }
        if (accountsTotal > 0) {
            jmb.addVariable("accountsTotal", accountsTotal).addVariable("accountNumber", accountNumber);
        }
        if (flightsTotal > 0) {
            jmb.addVariable("flightsTotal", flightsTotal).addVariable("flightNumber", flightNumber);
        }
        super.setMessage(jmb.build());
        super.update();
    }
}
