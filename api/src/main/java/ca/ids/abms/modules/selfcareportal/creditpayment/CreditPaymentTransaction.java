package ca.ids.abms.modules.selfcareportal.creditpayment;

import ca.ids.abms.config.db.SearchableEntity;
import ca.ids.abms.config.db.SearchableText;
import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.common.entities.AbmsCrudEntity;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "credit_payments")
public class CreditPaymentTransaction extends VersionedAuditedEntity implements AbmsCrudEntity<Integer> {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @SearchableText
    private LocalDateTime transactionTime;

    @ManyToOne
    @SearchableEntity
    private Account account;

    @NotNull
    @SearchableText
    private String requestorIp;

    @NotNull
    @SearchableText
    private String request;

    @NotNull
    @SearchableText
    private String response;

    @NotNull
    @SearchableText
    private String responseStatus;

    @SearchableText
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreditPaymentTransaction that = (CreditPaymentTransaction) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(transactionTime, that.transactionTime) &&
            Objects.equals(account, that.account) &&
            Objects.equals(requestorIp, that.requestorIp) &&
            Objects.equals(request, that.request) &&
            Objects.equals(response, that.response) &&
            Objects.equals(responseStatus, that.responseStatus) &&
            Objects.equals(responseDescription, that.responseDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, transactionTime, account, requestorIp, request, response, responseStatus, responseDescription);
    }

    @Override
    public String toString() {
        return "CreditPaymentTransaction{" +
            "id=" + id +
            ", transactionTime=" + transactionTime +
            ", account=" + account +
            ", requestorIp='" + requestorIp + '\'' +
            ", request='" + request + '\'' +
            ", response='" + response + '\'' +
            ", responseStatus='" + responseStatus + '\'' +
            ", responseDescription='" + responseDescription + '\'' +
            '}';
    }
}

