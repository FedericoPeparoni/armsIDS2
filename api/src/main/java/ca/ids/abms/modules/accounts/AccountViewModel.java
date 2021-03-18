package ca.ids.abms.modules.accounts;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ca.ids.abms.modules.accounts.enumerate.WhitelistState;
import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.users.UserViewModel;
import ca.ids.abms.modules.util.models.VersionedViewModel;

public class AccountViewModel extends VersionedViewModel {

    private Integer id;

    @NotNull
    @Size(max = 100)
    private String name;

    @Size(max = 100)
    private String alias;

    @Size(max = 100)
    private String aviationBillingContactPersonName;

    @Size(max = 100)
    private String aviationBillingPhoneNumber;

    @Size(max = 255)
    private String aviationBillingMailingAddress;

    @Size(max = 255)
    private String aviationBillingEmailAddress;

    @Size(max = 100)
    private String aviationBillingSmsNumber;

    @Size(max = 100)
    private String nonAviationBillingContactPersonName;

    @Size(max = 100)
    private String nonAviationBillingPhoneNumber;

    @Size(max = 255)
    private String nonAviationBillingMailingAddress;

    @Size(max = 255)
    private String nonAviationBillingEmailAddress;

    @Size(max = 100)
    private String nonAviationBillingSmsNumber;

    @Size(max = 2)
    private String iataCode;

    @Size(max = 3)
    private String icaoCode;

    @Size(max = 100)
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

    @NotNull
    private Currency invoiceCurrency;

    @NotNull
    private Double monthlyOverduePenaltyRate;

    @Size(max = 100)
    private String notes;

    @NotNull
    private AccountType accountType;

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

    private List<AccountEventMapViewModel> listOfEventsAccountNotified;

    private Boolean isSelfCare;

    @Size(max = 50)
    private String nationality;

    private LocalDateTime whitelistLastActivityDateTime;

    private Boolean whitelistInactivityNoticeSentFlag;

    private Boolean whitelistExpiryNoticeSentFlag;

    private WhitelistState whitelistState;

    private List<AccountExternalChargeCategory> accountExternalChargeCategories;

    private List<UserViewModel> accountUsers;
    
    private Integer accountTypeDiscount;

    // these fields don't exist in the table because we use them
    // only on the front-end to show the status of the record,
    // if an approval request exists for this record
    private Integer scRequestId;

    private String scRequestType;

    private LocalDateTime updatedAt;

	public Boolean getIsSelfCare() {
		return isSelfCare;
	}

	public void setIsSelfCare(Boolean isSelfCare) {
		this.isSelfCare = isSelfCare;
	}

	public AccountType getAccountType() {
        return accountType;
    }

    public Boolean getActive() {
        return active;
    }

    public Boolean getCashAccount() {
        return cashAccount;
    }

    public Integer getAircraftParkingExemption() {
        return aircraftParkingExemption;
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

    public Boolean getBlackListedIndicator() {
        return blackListedIndicator;
    }

    public Boolean getBlackListedOverride() {
        return blackListedOverride;
    }

    public Double getCreditLimit() {
        return creditLimit;
    }

    public Double getDiscountStructure() {
        return discountStructure;
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

    public String getNationality() {
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

    public Collection<AccountEventMapViewModel> getListOfEventsAccountNotified() {
        return listOfEventsAccountNotified;
    }

    public List<AccountExternalChargeCategory> getAccountExternalChargeCategories() {
	    return accountExternalChargeCategories;
    }

    public List<UserViewModel> getAccountUsers() {
        return accountUsers;
    }

    public void setAccountType(AccountType anAccountType) {
        accountType = anAccountType;
    }

    public void setActive(Boolean aActive) {
        active = aActive;
    }

    public void setAircraftParkingExemption(Integer aAircraftParkingExemption) {
        aircraftParkingExemption = aAircraftParkingExemption;
    }

    public void setAlias(String aAlias) {
        alias = aAlias;
    }

    public void setAviationBillingContactPersonName(String aAviationBillingContactPersonName) {
        aviationBillingContactPersonName = aAviationBillingContactPersonName;
    }

    public void setAviationBillingEmailAddress(String aAviationBillingEmailAddress) {
        aviationBillingEmailAddress = aAviationBillingEmailAddress;
    }

    public void setAviationBillingMailingAddress(String aAviationBillingMailingAddress) {
        aviationBillingMailingAddress = aAviationBillingMailingAddress;
    }

    public void setAviationBillingPhoneNumber(String aAviationBillingPhoneNumber) {
        aviationBillingPhoneNumber = aAviationBillingPhoneNumber;
    }

    public void setAviationBillingSmsNumber(String aAviationBillingSmsNumber) {
        aviationBillingSmsNumber = aAviationBillingSmsNumber;
    }

    public void setBlackListedIndicator(Boolean aBlackListedIndicator) {
        blackListedIndicator = aBlackListedIndicator;
    }

    public void setCashAccount(Boolean aCashAccount) {
        cashAccount = aCashAccount;
    }

    public void setBlackListedOverride(Boolean aBlackListedOverride) {
        blackListedOverride = aBlackListedOverride;
    }

    public void setCreditLimit(Double aCreditLimit) {
        creditLimit = aCreditLimit;
    }

    public void setDiscountStructure(Double aDiscountStructure) {
        discountStructure = aDiscountStructure;
    }

    public void setIataCode(String aIataCode) {
        iataCode = aIataCode;
    }

    public void setIataMember(Boolean aIataMember) {
        iataMember = aIataMember;
    }

    public void setIcaoCode(String aIcaoCode) {
        icaoCode = aIcaoCode;
    }

    public void setId(Integer aId) {
        id = aId;
    }

    public void setInvoiceCurrency(Currency aInvoiceCurrency) {
        invoiceCurrency = aInvoiceCurrency;
    }

    public void setInvoiceDeliveryFormat(String aInvoiceDeliveryFormat) {
        invoiceDeliveryFormat = aInvoiceDeliveryFormat;
    }

    public void setInvoiceDeliveryMethod(String aInvoiceDeliveryMethod) {
        invoiceDeliveryMethod = aInvoiceDeliveryMethod;
    }

    public void setMonthlyOverduePenaltyRate(Double aMonthlyOverduePenaltyRate) {
        monthlyOverduePenaltyRate = aMonthlyOverduePenaltyRate;
    }

    public void setName(String aName) {
        name = aName;
    }

    public void setNationality(String aNationality) {
        nationality = aNationality;
    }

    public void setNonAviationBillingContactPersonName(String aNonAviationBillingContactPersonName) {
        nonAviationBillingContactPersonName = aNonAviationBillingContactPersonName;
    }

    public void setNonAviationBillingEmailAddress(String aNonAviationBillingEmailAddress) {
        nonAviationBillingEmailAddress = aNonAviationBillingEmailAddress;
    }

    public void setNonAviationBillingMailingAddress(String aNonAviationBillingMailingAddress) {
        nonAviationBillingMailingAddress = aNonAviationBillingMailingAddress;
    }

    public void setNonAviationBillingPhoneNumber(String aNonAviationBillingPhoneNumber) {
        nonAviationBillingPhoneNumber = aNonAviationBillingPhoneNumber;
    }

    public void setNonAviationBillingSmsNumber(String aNonAviationBillingSmsNumber) {
        nonAviationBillingSmsNumber = aNonAviationBillingSmsNumber;
    }

    public void setNotes(String aNotes) {
        notes = aNotes;
    }

    public void setOprIdentifier(String aOprIdentifier) {
        oprIdentifier = aOprIdentifier;
    }

    public void setPaymentTerms(Integer aPaymentTerms) {
        paymentTerms = aPaymentTerms;
    }

    public void setPercentageOfPassengerFeePayable(Double aPercentageOfPassengerFeePayable) {
        percentageOfPassengerFeePayable = aPercentageOfPassengerFeePayable;
    }

    public Boolean getApprovedFlightSchoolIndicator() {
        return approvedFlightSchoolIndicator;
    }

    public void setApprovedFlightSchoolIndicator(Boolean approvedFlightSchoolIndicator) {
        this.approvedFlightSchoolIndicator = approvedFlightSchoolIndicator;
    }

    public void setSeparatePaxInvoice(Boolean aSeparatePaxInvoice) {
        separatePaxInvoice = aSeparatePaxInvoice;
    }

    public void setTaxProfile(String aTaxProfile) {
        taxProfile = aTaxProfile;
    }

    public void setListOfEventsAccountNotified(List<AccountEventMapViewModel> listOfEventsAccountNotified) {
        this.listOfEventsAccountNotified = listOfEventsAccountNotified;
    }

    public void setAccountExternalChargeCategories(List<AccountExternalChargeCategory> accountExternalChargeCategories) {
	    this.accountExternalChargeCategories = accountExternalChargeCategories;
    }

    public void setAccountUsers(List<UserViewModel> accountUsers) {
        this.accountUsers = accountUsers;
    }

    @Override
    public String toString() {
        return "Account [id=" + id + ", name=" + name + ", alias=" + alias + ", iataCode=" + iataCode + ", icaoCode="
            + icaoCode + ", accountUsers=" + accountUsers + "]";
    }

    public Integer getScRequestId() {
        return scRequestId;
    }

    public void setScRequestId(Integer scRequestId) {
        this.scRequestId = scRequestId;
    }

    public String getScRequestType() {
        return scRequestType;
    }

    public void setScRequestType(String scRequestType) {
        this.scRequestType = scRequestType;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
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

	public Integer getAccountTypeDiscount() {
		return accountTypeDiscount;
	}

	public void setAccountTypeDiscount(Integer accountTypeDiscount) {
		this.accountTypeDiscount = accountTypeDiscount;
	}
}
