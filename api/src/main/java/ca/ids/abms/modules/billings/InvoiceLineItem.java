package ca.ids.abms.modules.billings;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.aerodromes.Aerodrome;
import ca.ids.abms.modules.charges.RecurringCharge;
import ca.ids.abms.modules.charges.ServiceChargeCatalogue;
import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.util.models.AuditedEntity;
import ca.ids.abms.modules.utilities.towns.UtilitiesTownsAndVillage;

@Entity
public class InvoiceLineItem extends AuditedEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "invoice_id")
    private BillingLedger billingLedger;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "service_charge_catalogue_id")
    private ServiceChargeCatalogue serviceChargeCatalogue;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "aerodrome_id")
    private Aerodrome aerodrome;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "currency_id")
    private Currency currency;

    @ManyToOne
    @JoinColumn(name = "recurring_charge_id")
    private RecurringCharge recurringCharge;

    private String accountExternalSystemIdentifier;

    private String userDescription;

    @NotNull
    private Double amount;

    // These should be entered by user when creating a line item; each is used by
    // different service charge catalogue basis values
    private Double userUnitAmount;                // for basis == "unit"; unspecified units
    private Double userMarkupAmount;               // for basis == "percentage"; ANSP currency
    private Double userPrice;                     // for basis == "user"; ANSP currency
    private Double userElectricityMeterReading;   // for basis == "electricity"; unspecified units

    private Double userWaterMeterReading;         // for basis == "water"; unspecified units
    private Double userDiscountPercentage;        // for basis == "discount"; percent

    @ManyToOne
    @JoinColumn(name = "user_town_id")
    private UtilitiesTownsAndVillage userTown;    // for basis == "electricity" || basis == "water"

    @NotNull
    private Double exchangeRateToUsd;

    @NotNull
    private Double exchangeRateToAnsp;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BillingLedger getBillingLedger() {
        return billingLedger;
    }

    public void setBillingLedger(BillingLedger billingLedger) {
        this.billingLedger = billingLedger;
    }

    public ServiceChargeCatalogue getServiceChargeCatalogue() {
        return serviceChargeCatalogue;
    }

    public void setServiceChargeCatalogue(ServiceChargeCatalogue serviceChargeCatalogue) {
        this.serviceChargeCatalogue = serviceChargeCatalogue;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Aerodrome getAerodrome() {
        return aerodrome;
    }

    public void setAerodrome(Aerodrome aerodrome) {
        this.aerodrome = aerodrome;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public RecurringCharge getRecurringCharge() {
        return recurringCharge;
    }

    public void setRecurringCharge(RecurringCharge recurringCharge) {
        this.recurringCharge = recurringCharge;
    }

    public String getAccountExternalSystemIdentifier() {
        return accountExternalSystemIdentifier;
    }

    public void setAccountExternalSystemIdentifier(String accountExternalSystemIdentifier) {
        this.accountExternalSystemIdentifier = accountExternalSystemIdentifier;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getExchangeRateToUsd() {
        return exchangeRateToUsd;
    }

    public void setExchangeRateToUsd(Double exchangeRateToUsd) {
        this.exchangeRateToUsd = exchangeRateToUsd;
    }

    public Double getExchangeRateToAnsp() {
        return exchangeRateToAnsp;
    }

    public void setExchangeRateToAnsp(Double exchangeRateToAnsp) {
        this.exchangeRateToAnsp = exchangeRateToAnsp;
    }

    public Double getUserUnitAmount() {
        return userUnitAmount;
    }

    public void setUserUnitAmount(Double userUnitAmount) {
        this.userUnitAmount = userUnitAmount;
    }

    public Double getUserMarkupAmount() {
        return userMarkupAmount;
    }

    public void setUserMarkupAmount(Double userMarkupBasis) {
        this.userMarkupAmount = userMarkupBasis;
    }

    public Double getUserPrice() {
        return userPrice;
    }

    public void setUserPrice(Double userPrice) {
        this.userPrice = userPrice;
    }

    public Double getUserElectricityMeterReading() {
        return userElectricityMeterReading;
    }

    public void setUserElectricityMeterReading(Double userElectricityMeterReading) {
        this.userElectricityMeterReading = userElectricityMeterReading;
    }

    public Double getUserWaterMeterReading() {
        return userWaterMeterReading;
    }

    public void setUserWaterMeterReading(Double userWaterMeterReading) {
        this.userWaterMeterReading = userWaterMeterReading;
    }

    public Double getUserDiscountPercentage() {
        return userDiscountPercentage;
    }

    public void setUserDiscountPercentage(Double userDiscountPercentage) {
        this.userDiscountPercentage = userDiscountPercentage;
    }

    public UtilitiesTownsAndVillage getUserTown() {
        return userTown;
    }

    public void setUserTown(UtilitiesTownsAndVillage userTown) {
        this.userTown = userTown;
    }

    public String getUserDescription() {
        return userDescription;
    }

    public void setUserDescription(String userDescription) {
        this.userDescription = userDescription;
    }

    @Override
    public String toString() {
        return "InvoiceLineItem [id=" + id + ", serviceChargeCatalogue="
                + serviceChargeCatalogue + ", account=" + account + ", aerodrome=" + aerodrome + ", currency="
                + currency + ", recurringCharge=" + recurringCharge + ", amount=" + amount + ", userUnitAmount="
                + userUnitAmount + ", userMarkupAmount=" + userMarkupAmount + ", userPrice=" + userPrice
                + ", userElectricityMeterReading=" + userElectricityMeterReading + ", userWaterMeterReading="
                + userWaterMeterReading + ", userDiscountPercentage=" + userDiscountPercentage + ", userTown="
                + userTown + ", exchangeRateToUsd=" + exchangeRateToUsd + ", exchangeRateToAnsp=" + exchangeRateToAnsp
                + "userDescription=" + userDescription
                + "]";
    }
}
