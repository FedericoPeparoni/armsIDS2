package ca.ids.abms.modules.selfcareportal.creditpayment;

import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.util.models.VersionedViewModel;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class CreditPaymentTransactionViewModel extends VersionedViewModel {

    private Integer id;

    @NotNull
    private LocalDateTime transactionTime;

    @NotNull
    private Account account;

    @NotNull
    private String requestorIp;

    @NotNull
    private String request;

    @NotNull
    private String response;

    @NotNull
    private String responseStatus;

    private String responseDescription;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(LocalDateTime transactionTime) {
        this.transactionTime = transactionTime;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
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
