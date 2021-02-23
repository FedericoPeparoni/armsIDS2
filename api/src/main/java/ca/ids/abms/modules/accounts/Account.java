package ca.ids.abms.modules.accounts;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ca.ids.abms.modules.accounts.enumerate.WhitelistState;
import com.fasterxml.jackson.annotation.JsonIgnore;
import ca.ids.abms.modules.selfcareportal.approvalrequests.SelfCarePortalApprovalRequest;
import ca.ids.abms.config.db.SearchableEntity;
import ca.ids.abms.config.db.SearchableText;
import ca.ids.abms.config.db.validators.UniqueKey;
import ca.ids.abms.modules.aircraft.AircraftRegistration;
import ca.ids.abms.modules.billings.BillingLedger;
import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.flight.FlightSchedule;
import ca.ids.abms.modules.flightmovements.enumerate.FlightmovementCategoryNationality;
import ca.ids.abms.modules.flightmovements.enumerate.FlightmovementCategoryNationalityConverter;
import ca.ids.abms.modules.transactions.Transaction;
import ca.ids.abms.modules.users.User;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;


@Entity
@UniqueKey(columnNames = { "name", "iataCode", "icaoCode", "alias", "oprIdentifier" }, checkSeparately = true)
public class Account extends VersionedAuditedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Size(max = 100)
    @SearchableText
    private String name;

    @Size(max = 100)
    @SearchableText
    private String alias;

    @Size(max = 100)
    @SearchableText
    private String aviationBillingContactPersonName;

    @Size(max = 100)
    @SearchableText
    private String aviationBillingPhoneNumber;

    @Size(max = 255)
    @SearchableText
    private String aviationBillingMailingAddress;

    @Size(max = 255)
    @SearchableText
    private String aviationBillingEmailAddress;

    @Size(max = 100)
    @SearchableText
    private String aviationBillingSmsNumber;

    @Size(max = 100)
    @SearchableText
    private String nonAviationBillingContactPersonName;

    @Size(max = 100)
    @SearchableText
    private String nonAviationBillingPhoneNumber;

    @Size(max = 255)
    @SearchableText
    private String nonAviationBillingMailingAddress;

    @Size(max = 255)
    @SearchableText
    private String nonAviationBillingEmailAddress;

    @Size(max = 100)
    @SearchableText
    private String nonAviationBillingSmsNumber;

    @Size(max = 2)
    @SearchableText
    private String iataCode;

    @Size(max = 3)
    @SearchableText
    private String icaoCode;

    @Size(max = 100)
    @SearchableText
    private String oprIdentifier;

    @NotNull
    private Integer paymentTerms;

    private Double discountStructure;

    @Size(max = 60)
    private String taxProfile;

    private Double percentageOfPassengerFeePayable;

    @NotNull
    @Size(max = 100)
    private String invoiceDeliveryFormat;

    @NotNull
    @Size(max = 60)
    private String invoiceDeliveryMethod;

    @ManyToOne
    @JoinColumn(name = "invoice_currency")
    @NotNull
    private Currency invoiceCurrency;

    @NotNull
    private Double monthlyOverduePenaltyRate;

    @Size(max = 100)
    @SearchableText
    private String notes;

    @ManyToOne
    @JoinColumn(name = "account_type")
    @SearchableEntity
    @NotNull
    private AccountType accountType;

    @JsonIgnore
    @OneToMany(mappedBy = "account")
    private Set<AircraftRegistration> aircraftRegistrations = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "account")
    private Set<BillingLedger> billingLedgers = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "account")
    private Set<Transaction> transactions = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "account")
    private Set<FlightSchedule> flightSchedules = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "account")
    private Set<SelfCarePortalApprovalRequest> selfCarePortalApprovalRequest = new HashSet<>();

    @NotNull
    private Boolean blackListedIndicator;

    @NotNull
    private Boolean blackListedOverride;

    @NotNull
    private Double creditLimit;

    private Integer aircraftParkingExemption;

    @NotNull
    private Boolean iataMember;

    @NotNull
    private Boolean separatePaxInvoice;

    @NotNull
    private Boolean active;

    @NotNull
    private Boolean cashAccount;

    @NotNull
    private Boolean approvedFlightSchoolIndicator;

    @JsonIgnore
    @OneToMany(mappedBy = "account")
    private List<AccountEventMap> listOfEventsAccountNotified = new ArrayList<>();

    @NotNull
    private Boolean isSelfCare;

    @NotNull
    private LocalDateTime whitelistLastActivityDateTime;

    @NotNull
    private Boolean whitelistInactivityNoticeSentFlag;

    @NotNull
    private Boolean whitelistExpiryNoticeSentFlag;

    @NotNull
    @Enumerated(EnumType.STRING)
    private WhitelistState whitelistState;

    @ManyToMany()  
    @JoinTable(name="account_users_map",   
                joinColumns={@JoinColumn(name="account_id")},   
                inverseJoinColumns={@JoinColumn(name="user_id")})  
    private List<User> accountUsers;
    
    @Convert(converter = FlightmovementCategoryNationalityConverter.class)
    private FlightmovementCategoryNationality nationality;

    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o != null) {
            try {
                final Account other = (Account) o;
                if (getId() == null || other.getId() == null)
                    return false;
                return getId().equals(other.getId());
            } catch (final ClassCastException x) {
            	// ignore this exception
            }
        }
        return false;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public Boolean getActive() {
        return active;
    }

    public Integer getAircraftParkingExemption() {
        return aircraftParkingExemption;
    }

    public Set<AircraftRegistration> getAircraftRegistrations() {
        return aircraftRegistrations;
    }

    public String getAlias() {
        return alias;
    }

    public String getAviationBillingContactPersonName() {
        return aviationBillingContactPersonName;
    }

    public String getAviationBillingEmailAddress() {
        return aviationBillingEmailAddress;
    }

    public String getAviationBillingMailingAddress() {
        return aviationBillingMailingAddress;
    }

    public String getAviationBillingPhoneNumber() {
        return aviationBillingPhoneNumber;
    }

    public String getAviationBillingSmsNumber() {
        return aviationBillingSmsNumber;
    }

    public Set<BillingLedger> getBillingLedgers() {
        return billingLedgers;
    }

    public Boolean getBlackListedIndicator() {
        return blackListedIndicator;
    }

    public Boolean getBlackListedOverride() {
        return blackListedOverride;
    }

    public Boolean getCashAccount() {
        return cashAccount;
    }

    public Double getCreditLimit() {
        return creditLimit;
    }

    public Double getDiscountStructure() {
        return discountStructure;
    }

    public Set<FlightSchedule> getFlightSchedules() {
        return flightSchedules;
    }

    public Set<SelfCarePortalApprovalRequest> getSelfCarePortalApprovalRequest() {
        return selfCarePortalApprovalRequest;
    }

    public String getIataCode() {
        return iataCode;
    }

    public Boolean getIataMember() {
        return iataMember;
    }

    public String getIcaoCode() {
        return icaoCode;
    }

    public Integer getId() {
        return id;
    }

    public Currency getInvoiceCurrency() {
        return invoiceCurrency;
    }

    public String getInvoiceDeliveryFormat() {
        return invoiceDeliveryFormat;
    }

    public String getInvoiceDeliveryMethod() {
        return invoiceDeliveryMethod;
    }

    public Double getMonthlyOverduePenaltyRate() {
        return monthlyOverduePenaltyRate;
    }

    public String getName() {
        return name;
    }

    public FlightmovementCategoryNationality getNationality() {
        return nationality;
    }

    public String getNonAviationBillingContactPersonName() {
        return nonAviationBillingContactPersonName;
    }

    public String getNonAviationBillingEmailAddress() {
        return nonAviationBillingEmailAddress;
    }

    public String getNonAviationBillingMailingAddress() {
        return nonAviationBillingMailingAddress;
    }

    public String getNonAviationBillingPhoneNumber() {
        return nonAviationBillingPhoneNumber;
    }

    public String getNonAviationBillingSmsNumber() {
        return nonAviationBillingSmsNumber;
    }

    public String getNotes() {
        return notes;
    }

    public String getOprIdentifier() {
        return oprIdentifier;
    }

    public Integer getPaymentTerms() {
        return paymentTerms;
    }

    public Double getPercentageOfPassengerFeePayable() {
        return percentageOfPassengerFeePayable;
    }

    public Boolean getSeparatePaxInvoice() {
        return separatePaxInvoice;
    }

    public String getTaxProfile() {
        return taxProfile;
    }

    public Set<Transaction> getTransactions() {
        return transactions;
    }

    public List<AccountEventMap> getListOfEventsAccountNotified() {
        return listOfEventsAccountNotified;
    }
    
    public List<User> getAccountUsers() {
        return accountUsers;
    }
    public boolean containsAccountUser (final User user) {
        if (accountUsers != null && user != null) {
            return accountUsers.contains (user);
        }
        return false;
    }
    public boolean hasAccountUsers() {
        return accountUsers != null && !accountUsers.isEmpty();
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public void setAccountType(AccountType anAccountType) {
        accountType = anAccountType;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setAircraftParkingExemption(Integer aircraftParkingExemption) {
        this.aircraftParkingExemption = aircraftParkingExemption;
    }

    public void setAircraftRegistrations(Set<AircraftRegistration> aAircraftRegistrations) {
        aircraftRegistrations = aAircraftRegistrations;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void setAviationBillingContactPersonName(String aviationBillingContactPersonName) {
        this.aviationBillingContactPersonName = aviationBillingContactPersonName;
    }

    public void setAviationBillingEmailAddress(String aviationBillingEmailAddress) {
        this.aviationBillingEmailAddress = aviationBillingEmailAddress;
    }

    public void setAviationBillingMailingAddress(String aviationBillingMailingAddress) {
        this.aviationBillingMailingAddress = aviationBillingMailingAddress;
    }

    public void setAviationBillingPhoneNumber(String aviationBillingPhoneNumber) {
        this.aviationBillingPhoneNumber = aviationBillingPhoneNumber;
    }

    public void setAviationBillingSmsNumber(String aviationBillingSmsNumber) {
        this.aviationBillingSmsNumber = aviationBillingSmsNumber;
    }

    public void setBillingLedgers(Set<BillingLedger> aBillingLedgers) {
        billingLedgers = aBillingLedgers;
    }

    public void setBlackListedIndicator(Boolean blackListedIndicator) {
        this.blackListedIndicator = blackListedIndicator;
    }

    public void setBlackListedOverride(Boolean blackListedOverride) {
        this.blackListedOverride = blackListedOverride;
    }

    public void setCashAccount(Boolean cashAccount) {
        this.cashAccount = cashAccount;
    }

    public void setCreditLimit(Double creditLimit) {
        this.creditLimit = creditLimit;
    }

    public void setDiscountStructure(Double discountStructure) {
        this.discountStructure = discountStructure;
    }

    public void setFlightSchedules(Set<FlightSchedule> aFlightSchedules) {
        flightSchedules = aFlightSchedules;
    }

    public void setSelfCarePortalApprovalRequest(Set<SelfCarePortalApprovalRequest> selfCarePortalApprovalRequest) {
        this.selfCarePortalApprovalRequest = selfCarePortalApprovalRequest;
    }

    public void setIataCode(String iataCode) {
        this.iataCode = iataCode;
    }

    public void setIataMember(Boolean iataMember) {
        this.iataMember = iataMember;
    }

    public void setIcaoCode(String icaoCode) {
        this.icaoCode = icaoCode;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setInvoiceCurrency(Currency invoiceCurrency) {
        this.invoiceCurrency = invoiceCurrency;
    }

    public void setInvoiceDeliveryFormat(String invoiceDeliveryFormat) {
        this.invoiceDeliveryFormat = invoiceDeliveryFormat;
    }

    public void setInvoiceDeliveryMethod(String invoiceDeliveryMethod) {
        this.invoiceDeliveryMethod = invoiceDeliveryMethod;
    }

    public void setMonthlyOverduePenaltyRate(Double monthlyOverduePenaltyRate) {
        this.monthlyOverduePenaltyRate = monthlyOverduePenaltyRate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNationality(FlightmovementCategoryNationality aNationality) {
        nationality = aNationality;
    }

    public void setNonAviationBillingContactPersonName(String nonAviationBillingContactPersonName) {
        this.nonAviationBillingContactPersonName = nonAviationBillingContactPersonName;
    }

    public void setNonAviationBillingEmailAddress(String nonAviationBillingEmailAddress) {
        this.nonAviationBillingEmailAddress = nonAviationBillingEmailAddress;
    }

    public void setNonAviationBillingMailingAddress(String nonAviationBillingMailingAddress) {
        this.nonAviationBillingMailingAddress = nonAviationBillingMailingAddress;
    }

    public void setNonAviationBillingPhoneNumber(String nonAviationBillingPhoneNumber) {
        this.nonAviationBillingPhoneNumber = nonAviationBillingPhoneNumber;
    }

    public void setNonAviationBillingSmsNumber(String nonAviationBillingSmsNumber) {
        this.nonAviationBillingSmsNumber = nonAviationBillingSmsNumber;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setOprIdentifier(String oprIdentifier) {
        this.oprIdentifier = oprIdentifier;
    }

    public void setPaymentTerms(Integer paymentTerms) {
        this.paymentTerms = paymentTerms;
    }

    public void setPercentageOfPassengerFeePayable(Double percentageOfPassengerFeePayable) {
        this.percentageOfPassengerFeePayable = percentageOfPassengerFeePayable;
    }

    public void setSeparatePaxInvoice(Boolean separatePaxInvoice) {
        this.separatePaxInvoice = separatePaxInvoice;
    }

    public void setTaxProfile(String taxProfile) {
        this.taxProfile = taxProfile;
    }

    public void setTransactions(Set<Transaction> aTransactions) {
        transactions = aTransactions;
    }

    public void setListOfEventsAccountNotified(List<AccountEventMap> listOfEventsAccountNotified) {
        this.listOfEventsAccountNotified = listOfEventsAccountNotified;
    }

    public Boolean getApprovedFlightSchoolIndicator() {
        return approvedFlightSchoolIndicator;
    }

    public void setApprovedFlightSchoolIndicator(Boolean approvedFlightSchoolIndicator) {
        this.approvedFlightSchoolIndicator = approvedFlightSchoolIndicator;
    }

    public Boolean getIsSelfCare() {
        return isSelfCare;
    }

    public void setIsSelfCare(Boolean isSelfCare) {
        this.isSelfCare = isSelfCare;
    }

    public void setAccountUsers(List<User> accountUsers) {
        this.accountUsers = accountUsers;
    }

    public LocalDateTime getWhitelistLastActivityDateTime() {
        return whitelistLastActivityDateTime;
    }

    public void setWhitelistLastActivityDateTime(LocalDateTime whitelistLastActivityDateTime) {
        this.whitelistLastActivityDateTime = whitelistLastActivityDateTime;
    }

    public Boolean getWhitelistInactivityNoticeSentFlag() {
        return whitelistInactivityNoticeSentFlag;
    }

    public void setWhitelistInactivityNoticeSentFlag(Boolean whitelistInactivityNoticeSentFlag) {
        this.whitelistInactivityNoticeSentFlag = whitelistInactivityNoticeSentFlag;
    }

    public Boolean getWhitelistExpiryNoticeSentFlag() {
        return whitelistExpiryNoticeSentFlag;
    }

    public void setWhitelistExpiryNoticeSentFlag(Boolean whitelistExpiryNoticeSentFlag) {
        this.whitelistExpiryNoticeSentFlag = whitelistExpiryNoticeSentFlag;
    }

    public WhitelistState getWhitelistState() {
        return whitelistState;
    }

    public void setWhitelistState(WhitelistState whitelistState) {
        this.whitelistState = whitelistState;
    }

    @Override
    public String toString() {
        return "Account [id=" + id + ", name=" + name + ", alias=" + alias + ", iataCode=" + iataCode + ", icaoCode="
                + icaoCode + ", accountUsers=" + accountUsers + ", nationality=" + nationality + "]";
    }

}
