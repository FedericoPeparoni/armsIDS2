package ca.ids.abms.modules.reports2.async;

import ca.ids.abms.modules.jobs.impl.InvoiceProgressCounter;
import ca.ids.abms.modules.reports2.common.ReportDocument;
import ca.ids.abms.modules.reports2.common.ReportFormat;
import ca.ids.abms.modules.reports2.invoices.aviation.BillingInterval;
import ca.ids.abms.modules.reports2.invoices.iata.IataInvoiceItemOrder;
import ca.ids.abms.modules.users.User;

import java.time.LocalDateTime;
import java.util.List;

public class AsyncInvoiceGeneratorScope {

    private Boolean userBillingCenterOnly;
    private ReportFormat format;
    private Boolean preview;
    private List<Integer> accountIdList;
    private Integer flightCategory;
    private BillingInterval billingInterval;
    private LocalDateTime startDate;
    private LocalDateTime endDateInclusive;
    private User currentUser;
    private String error;
    private String ipAddress;
    private IataInvoiceItemOrder iataInvoiceItemOrder;

    private InvoiceProgressCounter invoiceProgressCounter;
    private ReportDocument result;

    public AsyncInvoiceGeneratorScope(Boolean userBillingCenterOnly, ReportFormat format, Boolean preview,
                                      List<Integer> accountIdList, Integer flightCategory, BillingInterval billingInterval,
                                      LocalDateTime startDate, LocalDateTime endDateInclusive, User currentUser, String ipAddress,
                                      IataInvoiceItemOrder iataInvoiceItemOrder) {
        this.userBillingCenterOnly = userBillingCenterOnly;
        this.format = format;
        this.preview = preview != null ? preview : Boolean.FALSE;
        this.accountIdList = accountIdList;
        this.flightCategory = flightCategory;
        this.billingInterval = billingInterval;
        this.startDate = startDate;
        this.endDateInclusive = endDateInclusive;
        this.currentUser = currentUser;
        this.ipAddress = ipAddress;
        this.iataInvoiceItemOrder = iataInvoiceItemOrder;
    }

    public Boolean getUserBillingCenterOnly() {
        return userBillingCenterOnly;
    }

    public void setUserBillingCenterOnly(Boolean userBillingCenterOnly) {
        this.userBillingCenterOnly = userBillingCenterOnly;
    }

    public ReportFormat getFormat() {
        return format;
    }

    public void setFormat(ReportFormat format) {
        this.format = format;
    }

    public Boolean getPreview() {
        return preview;
    }

    public void setPreview(Boolean preview) {
        this.preview = preview;
    }

    public List<Integer> getAccountIdList() {
        return accountIdList;
    }

    public void setAccountIdList(List<Integer> accountIdList) {
        this.accountIdList = accountIdList;
    }

    public Integer getFlightCategory() {
        return flightCategory;
    }

    public void setFlightCategory(Integer flightCategory) {
        this.flightCategory = flightCategory;
    }

    public BillingInterval getBillingInterval() {
        return billingInterval;
    }

    public void setBillingInterval(BillingInterval billingInterval) {
        this.billingInterval = billingInterval;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDateInclusive() {
        return endDateInclusive;
    }

    public void setEndDateInclusive(LocalDateTime endDateInclusive) {
        this.endDateInclusive = endDateInclusive;
    }

    public ReportDocument getResult() {
        return result;
    }

    public void setResult(ReportDocument result) {
        this.result = result;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public InvoiceProgressCounter getInvoiceProgressCounter() {
        return invoiceProgressCounter;
    }

    public void setInvoiceProgressCounter(InvoiceProgressCounter invoiceProgressCounter) {
        this.invoiceProgressCounter = invoiceProgressCounter;
    }

    public IataInvoiceItemOrder getIataInvoiceItemOrder() {
        return iataInvoiceItemOrder;
    }

    public void setIataInvoiceItemOrder(IataInvoiceItemOrder iataInvoiceItemOrder) {
        this.iataInvoiceItemOrder = iataInvoiceItemOrder;
    }

    @Override
    public String toString() {
        return "AsyncInvoiceGeneratorScope{" +
            "preview=" + preview +
            "; startDate=" + startDate +
            "; endDate=" + endDateInclusive +
            "; currentUser=" + (currentUser != null ? currentUser.getName() : "system") +
            "; accountsNumber=" + (accountIdList != null ? accountIdList.size() : '0') +
            '}';
    }
}
