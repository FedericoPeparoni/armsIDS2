package ca.ids.abms.modules.charges;

public class PassengerChargeReconciliationJob {

    private Integer countFM;

    private Integer countPSCR;

    private Double totalDomCollected;

    private Double totalItlCollected;

    private Double totalDomFees;

    private Double totalItlFees;

    private Boolean itlWarning = false;

    private Boolean domWarning = false;

    public Integer getCountFM() {
        return countFM;
    }

    public void setCountFM(Integer countFM) {
        this.countFM = countFM;
    }

    public Integer getCountPSCR() {
        return countPSCR;
    }

    public void setCountPSCR(Integer countPSCR) {
        this.countPSCR = countPSCR;
    }

    public Double getTotalDomCollected() {
        return totalDomCollected;
    }

    public void setTotalDomCollected(Double totalDomCollected) {
        Double rounded ;

        if (totalDomCollected != null) {
            rounded = Math.round(totalDomCollected*100.0)/100.0;
        } else {
            rounded = 0.0;
        }

        if (rounded < 98.0) {
            setDomWarning(true);
        }

        this.totalDomCollected = rounded;
    }

    public Double getTotalItlCollected() {
        return totalItlCollected;
    }

    public void setTotalItlCollected(Double totalItlCollected) {
        Double rounded;

        if (totalItlCollected != null) {
            rounded = Math.round(totalItlCollected*100.0)/100.0;
        } else {
            rounded = 0.0;
        }

        if (rounded < 98.0) {
            setItlWarning(true);
        }

        this.totalItlCollected = rounded;
    }

    public Double getTotalDomFees() {
        return totalDomFees;
    }

    public void setTotalDomFees(Double totalDomFees) {
        this.totalDomFees = totalDomFees;
    }

    public Double getTotalItlFees() {
        return totalItlFees;
    }

    public void setTotalItlFees(Double totalItlFees) {
        this.totalItlFees = totalItlFees;
    }

    public Boolean getItlWarning()  {
        return itlWarning;
    }

    public void setItlWarning(Boolean itlWarning)  {
        this.itlWarning = itlWarning;
    }

    public Boolean getDomWarning()  {
        return domWarning;
    }

    public void setDomWarning(Boolean domWarning)  {
        this.domWarning = domWarning;
    }

    @Override
    public String toString() {
        return "PassengerChargeReconciliationJob{" +
            "count_fm=" + countFM +
            ", count_pscr=" + countPSCR +
            ", total_dom_collected=" + totalDomCollected +
            ", total_itl_collected=" + totalItlCollected +
            ", total_dom_fees=" + totalDomFees +
            ", total_itl_fees=" + totalItlFees +
            ", dom_warning=" + domWarning +
            ", itl_warning=" + itlWarning +
            '}';
    }
}
