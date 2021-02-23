package ca.ids.abms.plugins.kcaa.erp.modules.salesheader;

import ca.ids.abms.plugins.kcaa.erp.utilities.DefaultValue;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "KCAA erp$Sales Header")
@SuppressWarnings({"unused", "WeakerAccess"})
public class SalesHeader implements Serializable {

    @Column(name = "Document Type", precision = 10)
    @NotNull
    private Integer documentType = DefaultValue.INTEGER;

    @Id
    @Column(name = "No_", length = 20)
    @NotNull
    private String no = DefaultValue.STRING;

    @Column(name = "Sell-to Customer No_", length = 20)
    @NotNull
    private String sellToCustomerNo = DefaultValue.STRING;

    @Column(name = "Bill-to Customer No_", length = 20)
    @NotNull
    private String billToCustomerNo = DefaultValue.STRING;

    @Column(name = "Bill-to Name", length = 50)
    @NotNull
    private String billToName = DefaultValue.STRING;

    @Column(name = "Bill-to Name 2", length = 50)
    @NotNull
    private String billToName2 = DefaultValue.STRING;

    @Column(name = "Bill-to Address", length = 50)
    @NotNull
    private String billToAddress = DefaultValue.STRING;

    @Column(name = "Bill-to Address 2", length = 50)
    @NotNull
    private String billToAddress2 = DefaultValue.STRING;

    @Column(name = "Bill-to City", length = 30)
    @NotNull
    private String billToCity = DefaultValue.STRING;

    @Column(name = "Bill-to Contact", length = 50)
    @NotNull
    private String billToContact = DefaultValue.STRING;

    @Column(name = "Your Reference", length = 30)
    @NotNull
    private String yourReference = DefaultValue.STRING;

    @Column(name = "Ship-to Code", length = 10)
    @NotNull
    private String shipToCode = DefaultValue.STRING;

    @Column(name = "Ship-to Name", length = 50)
    @NotNull
    private String shipToName = DefaultValue.STRING;

    @Column(name = "Ship-to Name 2", length = 50)
    @NotNull
    private String shipToName2 = DefaultValue.STRING;

    @Column(name = "Ship-to Address", length = 50)
    @NotNull
    private String shipToAddress = DefaultValue.STRING;

    @Column(name = "Ship-to Address 2", length = 50)
    @NotNull
    private String shipToAddress2 = DefaultValue.STRING;

    @Column(name = "Ship-to City", length = 30)
    @NotNull
    private String shipToCity = DefaultValue.STRING;

    @Column(name = "Ship-to Contact", length = 50)
    @NotNull
    private String shipToContact = DefaultValue.STRING;

    @Column(name = "Order Date")
    @NotNull
    private LocalDateTime orderDate = DefaultValue.LOCAL_DATE_TIME;

    @Column(name = "Posting Date")
    @NotNull
    private LocalDateTime postingDate = DefaultValue.LOCAL_DATE_TIME;

    @Column(name = "Shipment Date")
    @NotNull
    private LocalDateTime shipmentDate = DefaultValue.LOCAL_DATE_TIME;

    @Column(name = "Posting Description", length = 250)
    @NotNull
    private String postingDescription = DefaultValue.STRING;

    @Column(name = "Payment Terms Code", length = 10)
    @NotNull
    private String paymentTermsCode = DefaultValue.STRING;

    @Column(name = "Due Date")
    @NotNull
    private LocalDateTime dueDate = DefaultValue.LOCAL_DATE_TIME;

    @Column(name = "Payment Discount %", precision = 38, scale = 20)
    @NotNull
    private Double paymentDiscountPercentage = DefaultValue.DOUBLE;

    @Column(name = "Pmt_ Discount Date")
    @NotNull
    private LocalDateTime pmtDiscountDate = DefaultValue.LOCAL_DATE_TIME;

    @Column(name = "Shipment Method Code", length = 10)
    @NotNull
    private String shipmentMethodCode = DefaultValue.STRING;

    @Column(name = "Location Code", length = 10)
    @NotNull
    private String locationCode = DefaultValue.STRING;

    @Column(name = "Shortcut Dimension 1 Code", length = 20)
    @NotNull
    private String shortcutDimension1Code = DefaultValue.STRING;

    @Column(name = "Shortcut Dimension 2 Code", length = 20)
    @NotNull
    private String shortcutDimension2Code = DefaultValue.STRING;

    @Column(name = "Customer Posting Group", length = 10)
    @NotNull
    private String customerPostingGroup = DefaultValue.STRING;

    @Column(name = "Currency Code", length = 10)
    @NotNull
    private String currencyCode = DefaultValue.STRING;

    @Column(name = "Currency Factor", precision = 38, scale = 20)
    @NotNull
    private Double currencyFactor = DefaultValue.DOUBLE;

    @Column(name = "Customer Price Group", length = 10)
    @NotNull
    private String customerPriceGroup = DefaultValue.STRING;

    @Column(name = "Prices Including VAT", precision = 3)
    @NotNull
    private Boolean pricesIncludingVat = DefaultValue.BOOLEAN;

    @Column(name = "Invoice Disc_ Code", length = 20)
    @NotNull
    private String invoiceDiscCode = DefaultValue.STRING;

    @Column(name = "Customer Disc_ Group", length = 10)
    @NotNull
    private String customerDiscGroup = DefaultValue.STRING;

    @Column(name = "Language Code", length = 10)
    @NotNull
    private String languageCode = DefaultValue.STRING;

    @Column(name = "Salesperson Code", length = 10)
    @NotNull
    private String salespersonCode = DefaultValue.STRING;

    @Column(name = "Order Class", length = 10)
    @NotNull
    private String orderClass = DefaultValue.STRING;

    @Column(name = "No_ Printed", precision = 10)
    @NotNull
    private Integer noPrinted = DefaultValue.INTEGER;

    @Column(name = "On Hold", length = 3)
    @NotNull
    private String onHold = DefaultValue.STRING;

    @Column(name = "Applies-to Doc_ Type", precision = 10)
    @NotNull
    private Integer appliesToDocType = DefaultValue.INTEGER;

    @Column(name = "Applies-to Doc_ No_", length = 20)
    @NotNull
    private String appliesToDocNo = DefaultValue.STRING;

    @Column(name = "Bal_ Account No_", length = 20)
    @NotNull
    private String balAccountNo = DefaultValue.STRING;

    @Column(name = "Ship", precision = 3)
    @NotNull
    private Boolean ship = DefaultValue.BOOLEAN;

    @Column(name = "Invoice", precision = 3)
    @NotNull
    private Boolean invoice = DefaultValue.BOOLEAN;

    @Column(name = "Shipping No_", length = 20)
    @NotNull
    private String shippingNo = DefaultValue.STRING;

    @Column(name = "Posting No_", length = 20)
    @NotNull
    private String pPostingNo = DefaultValue.STRING;

    @Column(name = "Last Shipping No_", length = 20)
    @NotNull
    private String lastShippingNo = DefaultValue.STRING;

    @Column(name = "Last Posting No_", length = 20)
    @NotNull
    private String lastPostingNo = DefaultValue.STRING;

    @Column(name = "Prepayment No_", length = 20)
    @NotNull
    private String prepaymentNo = DefaultValue.STRING;

    @Column(name = "Last Prepayment No_", length = 20)
    @NotNull
    private String lastPrepaymentNo = DefaultValue.STRING;

    @Column(name = "Prepmt_ Cr_ Memo No_", length = 20)
    @NotNull
    private String prepmtCrMemoNo = DefaultValue.STRING;

    @Column(name = "Last Prepmt_ Cr_ Memo No_", length = 20)
    @NotNull
    private String lastPrepmtCrMemoNo = DefaultValue.STRING;

    @Column(name = "VAT Registration No_", length = 20)
    @NotNull
    private String vatRegistrationNo = DefaultValue.STRING;

    @Column(name = "Combine Shipments", precision = 3)
    @NotNull
    private Boolean combineShipments = DefaultValue.BOOLEAN;

    @Column(name = "Reason Code", length = 10)
    @NotNull
    private String reasonCode = DefaultValue.STRING;

    @Column(name = "Gen_ Bus_ Posting Group", length = 10)
    @NotNull
    private String genBusPostingGroup = DefaultValue.STRING;

    @Column(name = "EU 3-Party Trade", precision = 3)
    @NotNull
    private Boolean eu3PartyTrade = DefaultValue.BOOLEAN;

    @Column(name = "Transaction Type", length = 10)
    @NotNull
    private String transactionType = DefaultValue.STRING;

    @Column(name = "Transport Method", length = 10)
    @NotNull
    private String transportMethod = DefaultValue.STRING;

    @Column(name = "VAT Country_Region Code", length = 10)
    @NotNull
    private String vatCountryRegionCode = DefaultValue.STRING;

    @Column(name = "Sell-to Customer Name", length = 50)
    @NotNull
    private String sellToCustomerName = DefaultValue.STRING;

    @Column(name = "Sell-to Customer Name 2", length = 50)
    @NotNull
    private String sellToCustomerName2 = DefaultValue.STRING;

    @Column(name = "Sell-to Address", length = 50)
    @NotNull
    private String sellToAddress = DefaultValue.STRING;

    @Column(name = "Sell-to Address 2", length = 50)
    @NotNull
    private String sellToAddress2 = DefaultValue.STRING;

    @Column(name = "Sell-to City", length = 30)
    @NotNull
    private String sellToCity = DefaultValue.STRING;

    @Column(name = "Sell-to Contact", length = 50)
    @NotNull
    private String sellToContact = DefaultValue.STRING;

    @Column(name = "Bill-to Post Code", length = 20)
    @NotNull
    private String billToPostCode = DefaultValue.STRING;

    @Column(name = "Bill-to County", length = 30)
    @NotNull
    private String billToCounty = DefaultValue.STRING;

    @Column(name = "Bill-to Country_Region Code", length = 10)
    @NotNull
    private String billToCountryRegionCode = DefaultValue.STRING;

    @Column(name = "Sell-to Post Code", length = 20)
    @NotNull
    private String sellToPostCode = DefaultValue.STRING;

    @Column(name = "Sell-to County", length = 30)
    @NotNull
    private String sellToCounty = DefaultValue.STRING;

    @Column(name = "Sell-to Country_Region Code", length = 10)
    @NotNull
    private String sellToCountryRegionCode = DefaultValue.STRING;

    @Column(name = "Ship-to Post Code", length = 20)
    @NotNull
    private String shipToPostCode = DefaultValue.STRING;

    @Column(name = "Ship-to County", length = 30)
    @NotNull
    private String shipToCounty = DefaultValue.STRING;

    @Column(name = "Ship-to Country_Region Code", length = 10)
    @NotNull
    private String shipToCountryRegionCode = DefaultValue.STRING;

    @Column(name = "Bal_ Account Type", precision = 10)
    @NotNull
    private Integer balAccountType = DefaultValue.INTEGER;

    @Column(name = "Exit Point", length = 10)
    @NotNull
    private String exitPoint = DefaultValue.STRING;

    @Column(name = "Correction", precision = 3)
    @NotNull
    private Boolean correction = DefaultValue.BOOLEAN;

    @Column(name = "Document Date")
    @NotNull
    private LocalDateTime documentDate = DefaultValue.LOCAL_DATE_TIME;

    @Column(name = "External Document No_", length = 20)
    @NotNull
    private String externalDocumentNo = DefaultValue.STRING;

    @Column(name = "Area", length = 10)
    @NotNull
    private String area = DefaultValue.STRING;

    @Column(name = "Transaction Specification", length = 10)
    @NotNull
    private String transactionSpecification = DefaultValue.STRING;

    @Column(name = "Payment Method Code", length = 10)
    @NotNull
    private String paymentMethodCode = DefaultValue.STRING;

    @Column(name = "Shipping Agent Code", length = 10)
    @NotNull
    private String shippingAgentCode = DefaultValue.STRING;

    @Column(name = "Package Tracking No_", length = 30)
    @NotNull
    private String packageTrackingNo = DefaultValue.STRING;

    @Column(name = "No_ Series", length = 10)
    @NotNull
    private String noSeries = DefaultValue.STRING;

    @Column(name = "Posting No_ Series", length = 10)
    @NotNull
    private String postingNoSeries = DefaultValue.STRING;

    @Column(name = "Shipping No_ Series", length = 10)
    @NotNull
    private String shippingNoSeries = DefaultValue.STRING;

    @Column(name = "Tax Area Code", length = 20)
    @NotNull
    private String taxAreaCode = DefaultValue.STRING;

    @Column(name = "Tax Liable", precision = 3)
    @NotNull
    private Boolean taxLiable = DefaultValue.BOOLEAN;

    @Column(name = "VAT Bus_ Posting Group", length = 10)
    @NotNull
    private String vatBusPostingGroup = DefaultValue.STRING;

    @Column(name = "Reserve", precision = 10)
    @NotNull
    private Integer reserve = DefaultValue.INTEGER;

    @Column(name = "Applies-to ID", length = 20)
    @NotNull
    private String appliesToId = DefaultValue.STRING;

    @Column(name = "VAT Base Discount %", precision = 38, scale = 20)
    @NotNull
    private Double vatBaseDiscountPercentage = DefaultValue.DOUBLE;

    @Column(name = "Status", precision = 10)
    @NotNull
    private Integer status = DefaultValue.INTEGER;

    @Column(name = "Invoice Discount Calculation", precision = 10)
    @NotNull
    private Integer invoiceDiscountCalculation = DefaultValue.INTEGER;

    @Column(name = "Invoice Discount Value", precision = 38, scale = 20)
    @NotNull
    private Double invoiceDiscountValue = DefaultValue.DOUBLE;

    @Column(name = "Send IC Document", precision = 3)
    @NotNull
    private Boolean sendIcDocument = DefaultValue.BOOLEAN;

    @Column(name = "IC Status", precision = 10)
    @NotNull
    private Integer icStatus = DefaultValue.INTEGER;

    @Column(name = "Sell-to IC Partner Code", length = 20)
    @NotNull
    private String sellToIcPartnerCode = DefaultValue.STRING;

    @Column(name = "Bill-to IC Partner Code", length = 20)
    @NotNull
    private String billToIcPartnerCode = DefaultValue.STRING;

    @Column(name = "IC Direction", precision = 10)
    @NotNull
    private Integer icDirection = DefaultValue.INTEGER;

    @Column(name = "Prepayment %", precision = 38, scale = 20)
    @NotNull
    private Double prepaymentPercentage = DefaultValue.DOUBLE;

    @Column(name = "Prepayment No_ Series", length = 10)
    @NotNull
    private String prepaymentNoSeries = DefaultValue.STRING;

    @Column(name = "Compress Prepayment", precision = 3)
    @NotNull
    private Boolean compressPrepayment = DefaultValue.BOOLEAN;

    @Column(name = "Prepayment Due Date")
    @NotNull
    private LocalDateTime prepaymentDueDate = DefaultValue.LOCAL_DATE_TIME;

    @Column(name = "Prepmt_ Cr_ Memo No_ Series", length = 10)
    @NotNull
    private String prepmtCrMemoNoSeries = DefaultValue.STRING;

    @Column(name = "Prepmt_ Posting Description", length = 50)
    @NotNull
    private String prepmtPostingDescription = DefaultValue.STRING;

    @Column(name = "Prepmt_ Pmt_ Discount Date")
    @NotNull
    private LocalDateTime prepmtPmtDiscountDate = DefaultValue.LOCAL_DATE_TIME;

    @Column(name = "Prepmt_ Payment Terms Code", length = 10)
    @NotNull
    private String prepmtPaymentTermsCode = DefaultValue.STRING;

    @Column(name = "Prepmt_ Payment Discount %", precision = 38, scale = 20)
    @NotNull
    private Double prepmtPaymentDiscountPercentage = DefaultValue.DOUBLE;

    @Column(name = "Doc_ No_ Occurrence", precision = 10)
    @NotNull
    private Integer docNoOccurrence = DefaultValue.INTEGER;

    @Column(name = "Campaign No_", length = 20)
    @NotNull
    private String campaignNo = DefaultValue.STRING;

    @Column(name = "Sell-to Customer Template Code", length = 10)
    @NotNull
    private String sellToCustomerTemplateCode = DefaultValue.STRING;

    @Column(name = "Sell-to Contact No_", length = 20)
    @NotNull
    private String sellToContactNo = DefaultValue.STRING;

    @Column(name = "Bill-to Contact No_", length = 20)
    @NotNull
    private String billToContactNo = DefaultValue.STRING;

    @Column(name = "Bill-to Customer Template Code", length = 10)
    @NotNull
    private String billToCustomerTemplateCode = DefaultValue.STRING;

    @Column(name = "Opportunity No_", length = 20)
    @NotNull
    private String opportunityNo = DefaultValue.STRING;

    @Column(name = "Responsibility Center", length = 10)
    @NotNull
    private String responsibilityCenter = DefaultValue.STRING;

    @Column(name = "Shipping Advice", precision = 10)
    @NotNull
    private Integer shippingAdvice = DefaultValue.INTEGER;

    @Column(name = "Posting from Whse_ Ref_", precision = 10)
    @NotNull
    private Integer postingFromWhseRef = DefaultValue.INTEGER;

    @Column(name = "Requested Delivery Date")
    @NotNull
    private LocalDateTime requestedDeliveryDate = DefaultValue.LOCAL_DATE_TIME;

    @Column(name = "Promised Delivery Date")
    @NotNull
    private LocalDateTime promisedDeliveryDate = DefaultValue.LOCAL_DATE_TIME;

    @Column(name = "Shipping Time", length = 32)
    @NotNull
    private String shippingTime = DefaultValue.STRING;

    @Column(name = "Outbound Whse_ Handling Time", length = 32)
    @NotNull
    private String outboundWhseHandlingTime = DefaultValue.STRING;

    @Column(name = "Shipping Agent Service Code", length = 10)
    @NotNull
    private String shippingAgentServiceCode = DefaultValue.STRING;

    @Column(name = "Receive", precision = 3)
    @NotNull
    private Boolean receive = DefaultValue.BOOLEAN;

    @Column(name = "Return Receipt No_", length = 20)
    @NotNull
    private String returnReceiptNo = DefaultValue.STRING;

    @Column(name = "Return Receipt No_ Series", length = 10)
    @NotNull
    private String returnReceiptNoSeries = DefaultValue.STRING;

    @Column(name = "Last Return Receipt No_", length = 20)
    @NotNull
    private String lastReturnReceiptNo = DefaultValue.STRING;

    @Column(name = "Allow Line Disc_", precision = 3)
    @NotNull
    private Boolean allowLineDisc = DefaultValue.BOOLEAN;

    @Column(name = "Get Shipment Used", precision = 3)
    @NotNull
    private Boolean getShipmentUsed = DefaultValue.BOOLEAN;

    @Column(name = "Assigned User ID", length = 20)
    @NotNull
    private String assignedUserId = DefaultValue.STRING;

    @Column(name = "Date Received")
    @NotNull
    private LocalDateTime dateReceived = DefaultValue.LOCAL_DATE_TIME;

    @Column(name = "Time Received")
    @NotNull
    private LocalDateTime timeReceived = DefaultValue.LOCAL_DATE_TIME;

    @Column(name = "BizTalk Request for Sales Qte_", precision = 3)
    @NotNull
    private Boolean bizTalkRequestForSalesQte = DefaultValue.BOOLEAN;

    @Column(name = "BizTalk Sales Order", precision = 3)
    @NotNull
    private Boolean bizTalkSalesOrder = DefaultValue.BOOLEAN;

    @Column(name = "Date Sent")
    @NotNull
    private LocalDateTime dateSent = DefaultValue.LOCAL_DATE_TIME;

    @Column(name = "Time Sent")
    @NotNull
    private LocalDateTime timeSent = DefaultValue.LOCAL_DATE_TIME;

    @Column(name = "BizTalk Sales Quote", precision = 3)
    @NotNull
    private Boolean bizTalkSalesQuote = DefaultValue.BOOLEAN;

    @Column(name = "BizTalk Sales Order Cnfmn_", precision = 3)
    @NotNull
    private Boolean bizTalkSalesOrderCnfmn = DefaultValue.BOOLEAN;

    @Column(name = "Customer Quote No_", length = 20)
    @NotNull
    private String customerQuoteNo = DefaultValue.STRING;

    @Column(name = "Customer Order No_", length = 20)
    @NotNull
    private String customerOrderNo = DefaultValue.STRING;

    @Column(name = "BizTalk Document Sent", precision = 3)
    @NotNull
    private Boolean bizTalkDocumentSent = DefaultValue.BOOLEAN;

    @Column(name = "Invoice Status", precision = 10)
    @NotNull
    private Integer invoiceStatus = DefaultValue.INTEGER;

    @Column(name = "Credit Memo Status", precision = 10)
    @NotNull
    private Integer creditMemoStatus = DefaultValue.INTEGER;

    @Column(name = "Prepared by", length = 20)
    @NotNull
    private String preparedBy = DefaultValue.STRING;

    @Column(name = "Preparation Date")
    @NotNull
    private LocalDateTime preparationDate = DefaultValue.LOCAL_DATE_TIME;

    @Column(name = "Preparation Time")
    @NotNull
    private LocalDateTime preparationTime = DefaultValue.LOCAL_DATE_TIME;

    @Column(name = "Verified by  ID", length = 20)
    @NotNull
    private String verifiedById = DefaultValue.STRING;

    @Column(name = "Verification Date")
    @NotNull
    private LocalDateTime verificationDate = DefaultValue.LOCAL_DATE_TIME;

    @Column(name = "Verification Time")
    @NotNull
    private LocalDateTime verificationTime = DefaultValue.LOCAL_DATE_TIME;

    @Column(name = "Approval User ID", length = 20)
    @NotNull
    private String approvalUserId = DefaultValue.STRING;

    @Column(name = "Approval  Date")
    @NotNull
    private LocalDateTime approvalDate = DefaultValue.LOCAL_DATE_TIME;

    @Column(name = "Approval Time")
    @NotNull
    private LocalDateTime approvalTime = DefaultValue.LOCAL_DATE_TIME;

    @Column(name = "Station of Preparation", length = 20)
    @NotNull
    private String stationOfPreparation = DefaultValue.STRING;

    @Column(name = "Revenue Bank", length = 20)
    @NotNull
    private String revenueBank = DefaultValue.STRING;

    @Column(name = "Receipt Date")
    @NotNull
    private LocalDateTime receiptDate = DefaultValue.LOCAL_DATE_TIME;

    @Column(name = "KRA Cashier Name", length = 50)
    @NotNull
    private String kraCashierName = DefaultValue.STRING;

    @Column(name = "Reconciled", precision = 3)
    @NotNull
    private Boolean reconciled = DefaultValue.BOOLEAN;

    @Column(name = "Customer Category", precision = 10)
    @NotNull
    private Integer customerCategory = DefaultValue.INTEGER;

    @Column(name = "To be paid by", length = 100)
    @NotNull
    private String toBePaidBy = DefaultValue.STRING;

    @Column(name = "Invoice Type", precision = 10)
    @NotNull
    private Integer invoiceType = DefaultValue.INTEGER;

    @Column(name = "Remarks", length = 250)
    @NotNull
    private String remarks = DefaultValue.STRING;

    @Column(name = "Paid By", length = 100)
    @NotNull
    private String paidBy = DefaultValue.STRING;

    @Column(name = "Confirmed  by", length = 20)
    @NotNull
    private String confirmedBy = DefaultValue.STRING;

    @Column(name = "Confirmation Date")
    @NotNull
    private LocalDateTime confirmationDate = DefaultValue.LOCAL_DATE_TIME;

    @Column(name = "Confirmation Time")
    @NotNull
    private LocalDateTime confirmationTime = DefaultValue.LOCAL_DATE_TIME;

    @Column(name = "Reconciled  by", length = 20)
    @NotNull
    private String reconciledBy = DefaultValue.STRING;

    @Column(name = "Reconciliation Date")
    @NotNull
    private LocalDateTime reconciliationDate = DefaultValue.LOCAL_DATE_TIME;

    @Column(name = "Reconciliation Time")
    @NotNull
    private LocalDateTime reconciliationTime = DefaultValue.LOCAL_DATE_TIME;

    @Column(name = "Posted  by", length = 20)
    @NotNull
    private String postedBy = DefaultValue.STRING;

    @Column(name = "Posting Time")
    @NotNull
    private LocalDateTime postingTime = DefaultValue.LOCAL_DATE_TIME;

    @Column(name = "Attracts Examiners Fee", length = 20)
    @NotNull
    private String attractsExaminersFee = DefaultValue.STRING;

    @Column(name = "Exchange  Rate", precision = 38, scale = 20)
    @NotNull
    private Double exchangeRate = DefaultValue.DOUBLE;

    @Column(name = "Cust_AATIS_Account_No", length = 250)
    @NotNull
    private String custAatisAccountNo = DefaultValue.STRING;

    @Column(name = "Bank branch", length = 10)
    @NotNull
    private String bankBranch = DefaultValue.STRING;

    @Column(name = "Bank", length = 10)
    @NotNull
    private String bank = DefaultValue.STRING;

    @Column(name = "Debit advice Details", length = 10)
    @NotNull
    private String debitAdviceDetails = DefaultValue.STRING;

    public Integer getDocumentType() {
        return documentType;
    }

    public void setDocumentType(Integer documentType) {
        this.documentType = documentType;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getSellToCustomerNo() {
        return sellToCustomerNo;
    }

    public void setSellToCustomerNo(String sellToCustomerNo) {
        this.sellToCustomerNo = sellToCustomerNo;
    }

    public String getBillToCustomerNo() {
        return billToCustomerNo;
    }

    public void setBillToCustomerNo(String billToCustomerNo) {
        this.billToCustomerNo = billToCustomerNo;
    }

    public String getBillToName() {
        return billToName;
    }

    public void setBillToName(String billToName) {
        this.billToName = billToName;
    }

    public String getBillToName2() {
        return billToName2;
    }

    public void setBillToName2(String billToName2) {
        this.billToName2 = billToName2;
    }

    public String getBillToAddress() {
        return billToAddress;
    }

    public void setBillToAddress(String billToAddress) {
        this.billToAddress = billToAddress;
    }

    public String getBillToAddress2() {
        return billToAddress2;
    }

    public void setBillToAddress2(String billToAddress2) {
        this.billToAddress2 = billToAddress2;
    }

    public String getBillToCity() {
        return billToCity;
    }

    public void setBillToCity(String billToCity) {
        this.billToCity = billToCity;
    }

    public String getBillToContact() {
        return billToContact;
    }

    public void setBillToContact(String billToContact) {
        this.billToContact = billToContact;
    }

    public String getYourReference() {
        return yourReference;
    }

    public void setYourReference(String yourReference) {
        this.yourReference = yourReference;
    }

    public String getShipToCode() {
        return shipToCode;
    }

    public void setShipToCode(String shipToCode) {
        this.shipToCode = shipToCode;
    }

    public String getShipToName() {
        return shipToName;
    }

    public void setShipToName(String shipToName) {
        this.shipToName = shipToName;
    }

    public String getShipToName2() {
        return shipToName2;
    }

    public void setShipToName2(String shipToName2) {
        this.shipToName2 = shipToName2;
    }

    public String getShipToAddress() {
        return shipToAddress;
    }

    public void setShipToAddress(String shipToAddress) {
        this.shipToAddress = shipToAddress;
    }

    public String getShipToAddress2() {
        return shipToAddress2;
    }

    public void setShipToAddress2(String shipToAddress2) {
        this.shipToAddress2 = shipToAddress2;
    }

    public String getShipToCity() {
        return shipToCity;
    }

    public void setShipToCity(String shipToCity) {
        this.shipToCity = shipToCity;
    }

    public String getShipToContact() {
        return shipToContact;
    }

    public void setShipToContact(String shipToContact) {
        this.shipToContact = shipToContact;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public LocalDateTime getPostingDate() {
        return postingDate;
    }

    public void setPostingDate(LocalDateTime postingDate) {
        this.postingDate = postingDate;
    }

    public LocalDateTime getShipmentDate() {
        return shipmentDate;
    }

    public void setShipmentDate(LocalDateTime shipmentDate) {
        this.shipmentDate = shipmentDate;
    }

    public String getPostingDescription() {
        return postingDescription;
    }

    public void setPostingDescription(String postingDescription) {
        this.postingDescription = postingDescription;
    }

    public String getPaymentTermsCode() {
        return paymentTermsCode;
    }

    public void setPaymentTermsCode(String paymentTermsCode) {
        this.paymentTermsCode = paymentTermsCode;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public Double getPaymentDiscountPercentage() {
        return paymentDiscountPercentage;
    }

    public void setPaymentDiscountPercentage(Double paymentDiscountPercentage) {
        this.paymentDiscountPercentage = paymentDiscountPercentage;
    }

    public LocalDateTime getPmtDiscountDate() {
        return pmtDiscountDate;
    }

    public void setPmtDiscountDate(LocalDateTime pmtDiscountDate) {
        this.pmtDiscountDate = pmtDiscountDate;
    }

    public String getShipmentMethodCode() {
        return shipmentMethodCode;
    }

    public void setShipmentMethodCode(String shipmentMethodCode) {
        this.shipmentMethodCode = shipmentMethodCode;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public String getShortcutDimension1Code() {
        return shortcutDimension1Code;
    }

    public void setShortcutDimension1Code(String shortcutDimension1Code) {
        this.shortcutDimension1Code = shortcutDimension1Code;
    }

    public String getShortcutDimension2Code() {
        return shortcutDimension2Code;
    }

    public void setShortcutDimension2Code(String shortcutDimension2Code) {
        this.shortcutDimension2Code = shortcutDimension2Code;
    }

    public String getCustomerPostingGroup() {
        return customerPostingGroup;
    }

    public void setCustomerPostingGroup(String customerPostingGroup) {
        this.customerPostingGroup = customerPostingGroup;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public Double getCurrencyFactor() {
        return currencyFactor;
    }

    public void setCurrencyFactor(Double currencyFactor) {
        this.currencyFactor = currencyFactor;
    }

    public String getCustomerPriceGroup() {
        return customerPriceGroup;
    }

    public void setCustomerPriceGroup(String customerPriceGroup) {
        this.customerPriceGroup = customerPriceGroup;
    }

    public Boolean getPricesIncludingVat() {
        return pricesIncludingVat;
    }

    public void setPricesIncludingVat(Boolean pricesIncludingVat) {
        this.pricesIncludingVat = pricesIncludingVat;
    }

    public String getInvoiceDiscCode() {
        return invoiceDiscCode;
    }

    public void setInvoiceDiscCode(String invoiceDiscCode) {
        this.invoiceDiscCode = invoiceDiscCode;
    }

    public String getCustomerDiscGroup() {
        return customerDiscGroup;
    }

    public void setCustomerDiscGroup(String customerDiscGroup) {
        this.customerDiscGroup = customerDiscGroup;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getSalespersonCode() {
        return salespersonCode;
    }

    public void setSalespersonCode(String salespersonCode) {
        this.salespersonCode = salespersonCode;
    }

    public String getOrderClass() {
        return orderClass;
    }

    public void setOrderClass(String orderClass) {
        this.orderClass = orderClass;
    }

    public Integer getNoPrinted() {
        return noPrinted;
    }

    public void setNoPrinted(Integer noPrinted) {
        this.noPrinted = noPrinted;
    }

    public String getOnHold() {
        return onHold;
    }

    public void setOnHold(String onHold) {
        this.onHold = onHold;
    }

    public Integer getAppliesToDocType() {
        return appliesToDocType;
    }

    public void setAppliesToDocType(Integer appliesToDocType) {
        this.appliesToDocType = appliesToDocType;
    }

    public String getAppliesToDocNo() {
        return appliesToDocNo;
    }

    public void setAppliesToDocNo(String appliesToDocNo) {
        this.appliesToDocNo = appliesToDocNo;
    }

    public String getBalAccountNo() {
        return balAccountNo;
    }

    public void setBalAccountNo(String balAccountNo) {
        this.balAccountNo = balAccountNo;
    }

    public Boolean getShip() {
        return ship;
    }

    public void setShip(Boolean ship) {
        this.ship = ship;
    }

    public Boolean getInvoice() {
        return invoice;
    }

    public void setInvoice(Boolean invoice) {
        this.invoice = invoice;
    }

    public String getShippingNo() {
        return shippingNo;
    }

    public void setShippingNo(String shippingNo) {
        this.shippingNo = shippingNo;
    }

    public String getpPostingNo() {
        return pPostingNo;
    }

    public void setpPostingNo(String pPostingNo) {
        this.pPostingNo = pPostingNo;
    }

    public String getLastShippingNo() {
        return lastShippingNo;
    }

    public void setLastShippingNo(String lastShippingNo) {
        this.lastShippingNo = lastShippingNo;
    }

    public String getLastPostingNo() {
        return lastPostingNo;
    }

    public void setLastPostingNo(String lastPostingNo) {
        this.lastPostingNo = lastPostingNo;
    }

    public String getPrepaymentNo() {
        return prepaymentNo;
    }

    public void setPrepaymentNo(String prepaymentNo) {
        this.prepaymentNo = prepaymentNo;
    }

    public String getLastPrepaymentNo() {
        return lastPrepaymentNo;
    }

    public void setLastPrepaymentNo(String lastPrepaymentNo) {
        this.lastPrepaymentNo = lastPrepaymentNo;
    }

    public String getPrepmtCrMemoNo() {
        return prepmtCrMemoNo;
    }

    public void setPrepmtCrMemoNo(String prepmtCrMemoNo) {
        this.prepmtCrMemoNo = prepmtCrMemoNo;
    }

    public String getLastPrepmtCrMemoNo() {
        return lastPrepmtCrMemoNo;
    }

    public void setLastPrepmtCrMemoNo(String lastPrepmtCrMemoNo) {
        this.lastPrepmtCrMemoNo = lastPrepmtCrMemoNo;
    }

    public String getVatRegistrationNo() {
        return vatRegistrationNo;
    }

    public void setVatRegistrationNo(String vatRegistrationNo) {
        this.vatRegistrationNo = vatRegistrationNo;
    }

    public Boolean getCombineShipments() {
        return combineShipments;
    }

    public void setCombineShipments(Boolean combineShipments) {
        this.combineShipments = combineShipments;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }

    public String getGenBusPostingGroup() {
        return genBusPostingGroup;
    }

    public void setGenBusPostingGroup(String genBusPostingGroup) {
        this.genBusPostingGroup = genBusPostingGroup;
    }

    public Boolean getEu3PartyTrade() {
        return eu3PartyTrade;
    }

    public void setEu3PartyTrade(Boolean eu3PartyTrade) {
        this.eu3PartyTrade = eu3PartyTrade;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransportMethod() {
        return transportMethod;
    }

    public void setTransportMethod(String transportMethod) {
        this.transportMethod = transportMethod;
    }

    public String getVatCountryRegionCode() {
        return vatCountryRegionCode;
    }

    public void setVatCountryRegionCode(String vatCountryRegionCode) {
        this.vatCountryRegionCode = vatCountryRegionCode;
    }

    public String getSellToCustomerName() {
        return sellToCustomerName;
    }

    public void setSellToCustomerName(String sellToCustomerName) {
        this.sellToCustomerName = sellToCustomerName;
    }

    public String getSellToCustomerName2() {
        return sellToCustomerName2;
    }

    public void setSellToCustomerName2(String sellToCustomerName2) {
        this.sellToCustomerName2 = sellToCustomerName2;
    }

    public String getSellToAddress() {
        return sellToAddress;
    }

    public void setSellToAddress(String sellToAddress) {
        this.sellToAddress = sellToAddress;
    }

    public String getSellToAddress2() {
        return sellToAddress2;
    }

    public void setSellToAddress2(String sellToAddress2) {
        this.sellToAddress2 = sellToAddress2;
    }

    public String getSellToCity() {
        return sellToCity;
    }

    public void setSellToCity(String sellToCity) {
        this.sellToCity = sellToCity;
    }

    public String getSellToContact() {
        return sellToContact;
    }

    public void setSellToContact(String sellToContact) {
        this.sellToContact = sellToContact;
    }

    public String getBillToPostCode() {
        return billToPostCode;
    }

    public void setBillToPostCode(String billToPostCode) {
        this.billToPostCode = billToPostCode;
    }

    public String getBillToCounty() {
        return billToCounty;
    }

    public void setBillToCounty(String billToCounty) {
        this.billToCounty = billToCounty;
    }

    public String getBillToCountryRegionCode() {
        return billToCountryRegionCode;
    }

    public void setBillToCountryRegionCode(String billToCountryRegionCode) {
        this.billToCountryRegionCode = billToCountryRegionCode;
    }

    public String getSellToPostCode() {
        return sellToPostCode;
    }

    public void setSellToPostCode(String sellToPostCode) {
        this.sellToPostCode = sellToPostCode;
    }

    public String getSellToCounty() {
        return sellToCounty;
    }

    public void setSellToCounty(String sellToCounty) {
        this.sellToCounty = sellToCounty;
    }

    public String getSellToCountryRegionCode() {
        return sellToCountryRegionCode;
    }

    public void setSellToCountryRegionCode(String sellToCountryRegionCode) {
        this.sellToCountryRegionCode = sellToCountryRegionCode;
    }

    public String getShipToPostCode() {
        return shipToPostCode;
    }

    public void setShipToPostCode(String shipToPostCode) {
        this.shipToPostCode = shipToPostCode;
    }

    public String getShipToCounty() {
        return shipToCounty;
    }

    public void setShipToCounty(String shipToCounty) {
        this.shipToCounty = shipToCounty;
    }

    public String getShipToCountryRegionCode() {
        return shipToCountryRegionCode;
    }

    public void setShipToCountryRegionCode(String shipToCountryRegionCode) {
        this.shipToCountryRegionCode = shipToCountryRegionCode;
    }

    public Integer getBalAccountType() {
        return balAccountType;
    }

    public void setBalAccountType(Integer balAccountType) {
        this.balAccountType = balAccountType;
    }

    public String getExitPoint() {
        return exitPoint;
    }

    public void setExitPoint(String exitPoint) {
        this.exitPoint = exitPoint;
    }

    public Boolean getCorrection() {
        return correction;
    }

    public void setCorrection(Boolean correction) {
        this.correction = correction;
    }

    public LocalDateTime getDocumentDate() {
        return documentDate;
    }

    public void setDocumentDate(LocalDateTime documentDate) {
        this.documentDate = documentDate;
    }

    public String getExternalDocumentNo() {
        return externalDocumentNo;
    }

    public void setExternalDocumentNo(String externalDocumentNo) {
        this.externalDocumentNo = externalDocumentNo;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getTransactionSpecification() {
        return transactionSpecification;
    }

    public void setTransactionSpecification(String transactionSpecification) {
        this.transactionSpecification = transactionSpecification;
    }

    public String getPaymentMethodCode() {
        return paymentMethodCode;
    }

    public void setPaymentMethodCode(String paymentMethodCode) {
        this.paymentMethodCode = paymentMethodCode;
    }

    public String getShippingAgentCode() {
        return shippingAgentCode;
    }

    public void setShippingAgentCode(String shippingAgentCode) {
        this.shippingAgentCode = shippingAgentCode;
    }

    public String getPackageTrackingNo() {
        return packageTrackingNo;
    }

    public void setPackageTrackingNo(String packageTrackingNo) {
        this.packageTrackingNo = packageTrackingNo;
    }

    public String getNoSeries() {
        return noSeries;
    }

    public void setNoSeries(String noSeries) {
        this.noSeries = noSeries;
    }

    public String getPostingNoSeries() {
        return postingNoSeries;
    }

    public void setPostingNoSeries(String postingNoSeries) {
        this.postingNoSeries = postingNoSeries;
    }

    public String getShippingNoSeries() {
        return shippingNoSeries;
    }

    public void setShippingNoSeries(String shippingNoSeries) {
        this.shippingNoSeries = shippingNoSeries;
    }

    public String getTaxAreaCode() {
        return taxAreaCode;
    }

    public void setTaxAreaCode(String taxAreaCode) {
        this.taxAreaCode = taxAreaCode;
    }

    public Boolean getTaxLiable() {
        return taxLiable;
    }

    public void setTaxLiable(Boolean taxLiable) {
        this.taxLiable = taxLiable;
    }

    public String getVatBusPostingGroup() {
        return vatBusPostingGroup;
    }

    public void setVatBusPostingGroup(String vatBusPostingGroup) {
        this.vatBusPostingGroup = vatBusPostingGroup;
    }

    public Integer getReserve() {
        return reserve;
    }

    public void setReserve(Integer reserve) {
        this.reserve = reserve;
    }

    public String getAppliesToId() {
        return appliesToId;
    }

    public void setAppliesToId(String appliesToId) {
        this.appliesToId = appliesToId;
    }

    public Double getVatBaseDiscountPercentage() {
        return vatBaseDiscountPercentage;
    }

    public void setVatBaseDiscountPercentage(Double vatBaseDiscountPercentage) {
        this.vatBaseDiscountPercentage = vatBaseDiscountPercentage;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getInvoiceDiscountCalculation() {
        return invoiceDiscountCalculation;
    }

    public void setInvoiceDiscountCalculation(Integer invoiceDiscountCalculation) {
        this.invoiceDiscountCalculation = invoiceDiscountCalculation;
    }

    public Double getInvoiceDiscountValue() {
        return invoiceDiscountValue;
    }

    public void setInvoiceDiscountValue(Double invoiceDiscountValue) {
        this.invoiceDiscountValue = invoiceDiscountValue;
    }

    public Boolean getSendIcDocument() {
        return sendIcDocument;
    }

    public void setSendIcDocument(Boolean sendIcDocument) {
        this.sendIcDocument = sendIcDocument;
    }

    public Integer getIcStatus() {
        return icStatus;
    }

    public void setIcStatus(Integer icStatus) {
        this.icStatus = icStatus;
    }

    public String getSellToIcPartnerCode() {
        return sellToIcPartnerCode;
    }

    public void setSellToIcPartnerCode(String sellToIcPartnerCode) {
        this.sellToIcPartnerCode = sellToIcPartnerCode;
    }

    public String getBillToIcPartnerCode() {
        return billToIcPartnerCode;
    }

    public void setBillToIcPartnerCode(String billToIcPartnerCode) {
        this.billToIcPartnerCode = billToIcPartnerCode;
    }

    public Integer getIcDirection() {
        return icDirection;
    }

    public void setIcDirection(Integer icDirection) {
        this.icDirection = icDirection;
    }

    public Double getPrepaymentPercentage() {
        return prepaymentPercentage;
    }

    public void setPrepaymentPercentage(Double prepaymentPercentage) {
        this.prepaymentPercentage = prepaymentPercentage;
    }

    public String getPrepaymentNoSeries() {
        return prepaymentNoSeries;
    }

    public void setPrepaymentNoSeries(String prepaymentNoSeries) {
        this.prepaymentNoSeries = prepaymentNoSeries;
    }

    public Boolean getCompressPrepayment() {
        return compressPrepayment;
    }

    public void setCompressPrepayment(Boolean compressPrepayment) {
        this.compressPrepayment = compressPrepayment;
    }

    public LocalDateTime getPrepaymentDueDate() {
        return prepaymentDueDate;
    }

    public void setPrepaymentDueDate(LocalDateTime prepaymentDueDate) {
        this.prepaymentDueDate = prepaymentDueDate;
    }

    public String getPrepmtCrMemoNoSeries() {
        return prepmtCrMemoNoSeries;
    }

    public void setPrepmtCrMemoNoSeries(String prepmtCrMemoNoSeries) {
        this.prepmtCrMemoNoSeries = prepmtCrMemoNoSeries;
    }

    public String getPrepmtPostingDescription() {
        return prepmtPostingDescription;
    }

    public void setPrepmtPostingDescription(String prepmtPostingDescription) {
        this.prepmtPostingDescription = prepmtPostingDescription;
    }

    public LocalDateTime getPrepmtPmtDiscountDate() {
        return prepmtPmtDiscountDate;
    }

    public void setPrepmtPmtDiscountDate(LocalDateTime prepmtPmtDiscountDate) {
        this.prepmtPmtDiscountDate = prepmtPmtDiscountDate;
    }

    public String getPrepmtPaymentTermsCode() {
        return prepmtPaymentTermsCode;
    }

    public void setPrepmtPaymentTermsCode(String prepmtPaymentTermsCode) {
        this.prepmtPaymentTermsCode = prepmtPaymentTermsCode;
    }

    public Double getPrepmtPaymentDiscountPercentage() {
        return prepmtPaymentDiscountPercentage;
    }

    public void setPrepmtPaymentDiscountPercentage(Double prepmtPaymentDiscountPercentage) {
        this.prepmtPaymentDiscountPercentage = prepmtPaymentDiscountPercentage;
    }

    public Integer getDocNoOccurrence() {
        return docNoOccurrence;
    }

    public void setDocNoOccurrence(Integer docNoOccurrence) {
        this.docNoOccurrence = docNoOccurrence;
    }

    public String getCampaignNo() {
        return campaignNo;
    }

    public void setCampaignNo(String campaignNo) {
        this.campaignNo = campaignNo;
    }

    public String getSellToCustomerTemplateCode() {
        return sellToCustomerTemplateCode;
    }

    public void setSellToCustomerTemplateCode(String sellToCustomerTemplateCode) {
        this.sellToCustomerTemplateCode = sellToCustomerTemplateCode;
    }

    public String getSellToContactNo() {
        return sellToContactNo;
    }

    public void setSellToContactNo(String sellToContactNo) {
        this.sellToContactNo = sellToContactNo;
    }

    public String getBillToContactNo() {
        return billToContactNo;
    }

    public void setBillToContactNo(String billToContactNo) {
        this.billToContactNo = billToContactNo;
    }

    public String getBillToCustomerTemplateCode() {
        return billToCustomerTemplateCode;
    }

    public void setBillToCustomerTemplateCode(String billToCustomerTemplateCode) {
        this.billToCustomerTemplateCode = billToCustomerTemplateCode;
    }

    public String getOpportunityNo() {
        return opportunityNo;
    }

    public void setOpportunityNo(String opportunityNo) {
        this.opportunityNo = opportunityNo;
    }

    public String getResponsibilityCenter() {
        return responsibilityCenter;
    }

    public void setResponsibilityCenter(String responsibilityCenter) {
        this.responsibilityCenter = responsibilityCenter;
    }

    public Integer getShippingAdvice() {
        return shippingAdvice;
    }

    public void setShippingAdvice(Integer shippingAdvice) {
        this.shippingAdvice = shippingAdvice;
    }

    public Integer getPostingFromWhseRef() {
        return postingFromWhseRef;
    }

    public void setPostingFromWhseRef(Integer postingFromWhseRef) {
        this.postingFromWhseRef = postingFromWhseRef;
    }

    public LocalDateTime getRequestedDeliveryDate() {
        return requestedDeliveryDate;
    }

    public void setRequestedDeliveryDate(LocalDateTime requestedDeliveryDate) {
        this.requestedDeliveryDate = requestedDeliveryDate;
    }

    public LocalDateTime getPromisedDeliveryDate() {
        return promisedDeliveryDate;
    }

    public void setPromisedDeliveryDate(LocalDateTime promisedDeliveryDate) {
        this.promisedDeliveryDate = promisedDeliveryDate;
    }

    public String getShippingTime() {
        return shippingTime;
    }

    public void setShippingTime(String shippingTime) {
        this.shippingTime = shippingTime;
    }

    public String getOutboundWhseHandlingTime() {
        return outboundWhseHandlingTime;
    }

    public void setOutboundWhseHandlingTime(String outboundWhseHandlingTime) {
        this.outboundWhseHandlingTime = outboundWhseHandlingTime;
    }

    public String getShippingAgentServiceCode() {
        return shippingAgentServiceCode;
    }

    public void setShippingAgentServiceCode(String shippingAgentServiceCode) {
        this.shippingAgentServiceCode = shippingAgentServiceCode;
    }

    public Boolean getReceive() {
        return receive;
    }

    public void setReceive(Boolean receive) {
        this.receive = receive;
    }

    public String getReturnReceiptNo() {
        return returnReceiptNo;
    }

    public void setReturnReceiptNo(String returnReceiptNo) {
        this.returnReceiptNo = returnReceiptNo;
    }

    public String getReturnReceiptNoSeries() {
        return returnReceiptNoSeries;
    }

    public void setReturnReceiptNoSeries(String returnReceiptNoSeries) {
        this.returnReceiptNoSeries = returnReceiptNoSeries;
    }

    public String getLastReturnReceiptNo() {
        return lastReturnReceiptNo;
    }

    public void setLastReturnReceiptNo(String lastReturnReceiptNo) {
        this.lastReturnReceiptNo = lastReturnReceiptNo;
    }

    public Boolean getAllowLineDisc() {
        return allowLineDisc;
    }

    public void setAllowLineDisc(Boolean allowLineDisc) {
        this.allowLineDisc = allowLineDisc;
    }

    public Boolean getGetShipmentUsed() {
        return getShipmentUsed;
    }

    public void setGetShipmentUsed(Boolean getShipmentUsed) {
        this.getShipmentUsed = getShipmentUsed;
    }

    public String getAssignedUserId() {
        return assignedUserId;
    }

    public void setAssignedUserId(String assignedUserId) {
        this.assignedUserId = assignedUserId;
    }

    public LocalDateTime getDateReceived() {
        return dateReceived;
    }

    public void setDateReceived(LocalDateTime dateReceived) {
        this.dateReceived = dateReceived;
    }

    public LocalDateTime getTimeReceived() {
        return timeReceived;
    }

    public void setTimeReceived(LocalDateTime timeReceived) {
        this.timeReceived = timeReceived;
    }

    public Boolean getBizTalkRequestForSalesQte() {
        return bizTalkRequestForSalesQte;
    }

    public void setBizTalkRequestForSalesQte(Boolean bizTalkRequestForSalesQte) {
        this.bizTalkRequestForSalesQte = bizTalkRequestForSalesQte;
    }

    public Boolean getBizTalkSalesOrder() {
        return bizTalkSalesOrder;
    }

    public void setBizTalkSalesOrder(Boolean bizTalkSalesOrder) {
        this.bizTalkSalesOrder = bizTalkSalesOrder;
    }

    public LocalDateTime getDateSent() {
        return dateSent;
    }

    public void setDateSent(LocalDateTime dateSent) {
        this.dateSent = dateSent;
    }

    public LocalDateTime getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(LocalDateTime timeSent) {
        this.timeSent = timeSent;
    }

    public Boolean getBizTalkSalesQuote() {
        return bizTalkSalesQuote;
    }

    public void setBizTalkSalesQuote(Boolean bizTalkSalesQuote) {
        this.bizTalkSalesQuote = bizTalkSalesQuote;
    }

    public Boolean getBizTalkSalesOrderCnfmn() {
        return bizTalkSalesOrderCnfmn;
    }

    public void setBizTalkSalesOrderCnfmn(Boolean bizTalkSalesOrderCnfmn) {
        this.bizTalkSalesOrderCnfmn = bizTalkSalesOrderCnfmn;
    }

    public String getCustomerQuoteNo() {
        return customerQuoteNo;
    }

    public void setCustomerQuoteNo(String customerQuoteNo) {
        this.customerQuoteNo = customerQuoteNo;
    }

    public String getCustomerOrderNo() {
        return customerOrderNo;
    }

    public void setCustomerOrderNo(String customerOrderNo) {
        this.customerOrderNo = customerOrderNo;
    }

    public Boolean getBizTalkDocumentSent() {
        return bizTalkDocumentSent;
    }

    public void setBizTalkDocumentSent(Boolean bizTalkDocumentSent) {
        this.bizTalkDocumentSent = bizTalkDocumentSent;
    }

    public Integer getInvoiceStatus() {
        return invoiceStatus;
    }

    public void setInvoiceStatus(Integer invoiceStatus) {
        this.invoiceStatus = invoiceStatus;
    }

    public Integer getCreditMemoStatus() {
        return creditMemoStatus;
    }

    public void setCreditMemoStatus(Integer creditMemoStatus) {
        this.creditMemoStatus = creditMemoStatus;
    }

    public String getPreparedBy() {
        return preparedBy;
    }

    public void setPreparedBy(String preparedBy) {
        this.preparedBy = preparedBy;
    }

    public LocalDateTime getPreparationDate() {
        return preparationDate;
    }

    public void setPreparationDate(LocalDateTime preparationDate) {
        this.preparationDate = preparationDate;
    }

    public LocalDateTime getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(LocalDateTime preparationTime) {
        this.preparationTime = preparationTime;
    }

    public String getVerifiedById() {
        return verifiedById;
    }

    public void setVerifiedById(String verifiedById) {
        this.verifiedById = verifiedById;
    }

    public LocalDateTime getVerificationDate() {
        return verificationDate;
    }

    public void setVerificationDate(LocalDateTime verificationDate) {
        this.verificationDate = verificationDate;
    }

    public LocalDateTime getVerificationTime() {
        return verificationTime;
    }

    public void setVerificationTime(LocalDateTime verificationTime) {
        this.verificationTime = verificationTime;
    }

    public String getApprovalUserId() {
        return approvalUserId;
    }

    public void setApprovalUserId(String approvalUserId) {
        this.approvalUserId = approvalUserId;
    }

    public LocalDateTime getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(LocalDateTime approvalDate) {
        this.approvalDate = approvalDate;
    }

    public LocalDateTime getApprovalTime() {
        return approvalTime;
    }

    public void setApprovalTime(LocalDateTime approvalTime) {
        this.approvalTime = approvalTime;
    }

    public String getStationOfPreparation() {
        return stationOfPreparation;
    }

    public void setStationOfPreparation(String stationOfPreparation) {
        this.stationOfPreparation = stationOfPreparation;
    }

    public String getRevenueBank() {
        return revenueBank;
    }

    public void setRevenueBank(String revenueBank) {
        this.revenueBank = revenueBank;
    }

    public LocalDateTime getReceiptDate() {
        return receiptDate;
    }

    public void setReceiptDate(LocalDateTime receiptDate) {
        this.receiptDate = receiptDate;
    }

    public String getKraCashierName() {
        return kraCashierName;
    }

    public void setKraCashierName(String kraCashierName) {
        this.kraCashierName = kraCashierName;
    }

    public Boolean getReconciled() {
        return reconciled;
    }

    public void setReconciled(Boolean reconciled) {
        this.reconciled = reconciled;
    }

    public Integer getCustomerCategory() {
        return customerCategory;
    }

    public void setCustomerCategory(Integer customerCategory) {
        this.customerCategory = customerCategory;
    }

    public String getToBePaidBy() {
        return toBePaidBy;
    }

    public void setToBePaidBy(String toBePaidBy) {
        this.toBePaidBy = toBePaidBy;
    }

    public Integer getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(Integer invoiceType) {
        this.invoiceType = invoiceType;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getPaidBy() {
        return paidBy;
    }

    public void setPaidBy(String paidBy) {
        this.paidBy = paidBy;
    }

    public String getConfirmedBy() {
        return confirmedBy;
    }

    public void setConfirmedBy(String confirmedBy) {
        this.confirmedBy = confirmedBy;
    }

    public LocalDateTime getConfirmationDate() {
        return confirmationDate;
    }

    public void setConfirmationDate(LocalDateTime confirmationDate) {
        this.confirmationDate = confirmationDate;
    }

    public LocalDateTime getConfirmationTime() {
        return confirmationTime;
    }

    public void setConfirmationTime(LocalDateTime confirmationTime) {
        this.confirmationTime = confirmationTime;
    }

    public String getReconciledBy() {
        return reconciledBy;
    }

    public void setReconciledBy(String reconciledBy) {
        this.reconciledBy = reconciledBy;
    }

    public LocalDateTime getReconciliationDate() {
        return reconciliationDate;
    }

    public void setReconciliationDate(LocalDateTime reconciliationDate) {
        this.reconciliationDate = reconciliationDate;
    }

    public LocalDateTime getReconciliationTime() {
        return reconciliationTime;
    }

    public void setReconciliationTime(LocalDateTime reconciliationTime) {
        this.reconciliationTime = reconciliationTime;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public LocalDateTime getPostingTime() {
        return postingTime;
    }

    public void setPostingTime(LocalDateTime postingTime) {
        this.postingTime = postingTime;
    }

    public String getAttractsExaminersFee() {
        return attractsExaminersFee;
    }

    public void setAttractsExaminersFee(String attractsExaminersFee) {
        this.attractsExaminersFee = attractsExaminersFee;
    }

    public Double getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(Double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public String getCustAatisAccountNo() {
        return custAatisAccountNo;
    }

    public void setCustAatisAccountNo(String custAatisAccountNo) {
        this.custAatisAccountNo = custAatisAccountNo;
    }

    public String getBankBranch() {
        return bankBranch;
    }

    public void setBankBranch(String bankBranch) {
        this.bankBranch = bankBranch;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getDebitAdviceDetails() {
        return debitAdviceDetails;
    }

    public void setDebitAdviceDetails(String debitAdviceDetails) {
        this.debitAdviceDetails = debitAdviceDetails;
    }
}
