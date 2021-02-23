package ca.ids.abms.modules.selfcareportal.creditpayment;

import ca.ids.abms.util.csv.annotations.CsvProperty;

import java.time.LocalDateTime;

public class CreditPaymentTransactionCsvExportModel {

    @CsvProperty(value = "Transaction Time")
    private LocalDateTime transactionTime;

    private String account;

    @CsvProperty(value = "Requestor IP")
    private String requestorIp;

    private String request;

    private String response;

    @CsvProperty(value = "Response Status")
    private String responseStatus;

    @CsvProperty(value = "Response Description")
    private String responseDescription;

    public LocalDateTime getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(LocalDateTime transactionTime) {
        this.transactionTime = transactionTime;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getRequestorIp() {
        return requestorIp;
    }

    public void setRequestorIp(String requestorIp) {
        this.requestorIp = requestorIp;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(String responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String getResponseDescription() {
        return responseDescription;
    }

    public void setResponseDescription(String responseDescription) {
        this.responseDescription = responseDescription;
    }
}


