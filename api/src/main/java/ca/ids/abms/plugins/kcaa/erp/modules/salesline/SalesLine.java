package ca.ids.abms.plugins.kcaa.erp.modules.salesline;

import ca.ids.abms.plugins.kcaa.erp.utilities.DefaultValue;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "KCAA erp$Sales Line")
@SuppressWarnings({"unused", "WeakerAccess"})
public class SalesLine implements Serializable {

    static final String DOCUMENT_NO_COLUMN_NAME = "Document No_";
    static final String LINE_NO_COLUMN_NAME = "Line No_";

    @Column(name = "Document Type")
    @NotNull
    private Integer documentType = DefaultValue.INTEGER;

    @Column(name = DOCUMENT_NO_COLUMN_NAME)
    @NotNull
    private String documentNo = DefaultValue.STRING;

    @Column(name = LINE_NO_COLUMN_NAME, precision = 10)
    @NotNull
    private Integer lineNo = DefaultValue.INTEGER;

    @Column(name = "Sell-to Customer No_", length = 20)
    @NotNull
    private String sellToCustomerNo = DefaultValue.STRING;

    @Column(name = "Type", precision = 10)
    @NotNull
    private Integer type = DefaultValue.INTEGER;

    @Id
    @Column(name = "No_", length = 20)
    @NotNull
    private String no = DefaultValue.STRING;

    @Column(name = "Location Code", length = 10)
    @NotNull
    private String locationCode = DefaultValue.STRING;

    @Column(name = "Posting Group", length = 10)
    @NotNull
    private String postingGroup = DefaultValue.STRING;

    @Column(name = "Shipment Date")
    @NotNull
    private LocalDateTime shipmentDate = DefaultValue.LOCAL_DATE_TIME;

    @Column(name = "Description", length = 250)
    @NotNull
    private String description = DefaultValue.STRING;

    @Column(name = "Description 2", length = 50)
    @NotNull
    private String description2 = DefaultValue.STRING;

    @Column(name = "Unit of Measure", length = 10)
    @NotNull
    private String unitOfMeasure = DefaultValue.STRING;

    @Column(name = "Quantity", precision = 38, scale = 20)
    @NotNull
    private Double quantity = DefaultValue.DOUBLE;

    @Column(name = "Outstanding Quantity", precision = 38, scale = 20)
    @NotNull
    private Double outstandingQuantity = DefaultValue.DOUBLE;

    @Column(name = "Qty_ to Invoice", precision = 38, scale = 20)
    @NotNull
    private Double qtyToInvoice = DefaultValue.DOUBLE;

    @Column(name = "Qty_ to Ship", precision = 38, scale = 20)
    @NotNull
    private Double qtyToShip = DefaultValue.DOUBLE;

    @Column(name = "Unit Price", precision = 38, scale = 20)
    @NotNull
    private Double unitPrice = DefaultValue.DOUBLE;

    @Column(name = "Unit Cost (LCY)", precision = 38, scale = 20)
    @NotNull
    private Double unitCostLcy = DefaultValue.DOUBLE;

    @Column(name = "VAT %", precision = 38, scale = 20)
    @NotNull
    private Double vatPercentage = DefaultValue.DOUBLE;

    @Column(name = "Line Discount %", precision = 38, scale = 20)
    @NotNull
    private Double lineDiscountPercentage = DefaultValue.DOUBLE;

    @Column(name = "Line Discount Amount", precision = 38, scale = 20)
    @NotNull
    private Double lineDiscountAmount = DefaultValue.DOUBLE;

    @Column(name = "Amount", precision = 38, scale = 20)
    @NotNull
    private Double amount = DefaultValue.DOUBLE;

    @Column(name = "Amount Including VAT", precision = 38, scale = 20)
    @NotNull
    private Double amountIncludingVat = DefaultValue.DOUBLE;

    @Column(name = "Allow Invoice Disc_", precision = 3)
    @NotNull
    private Boolean allowInvoiceDisc = DefaultValue.BOOLEAN;

    @Column(name = "Gross Weight", precision = 38, scale = 20)
    @NotNull
    private Double grossWeight = DefaultValue.DOUBLE;

    @Column(name = "Net Weight", precision = 38, scale = 20)
    @NotNull
    private Double netWeight = DefaultValue.DOUBLE;

    @Column(name = "Units per Parcel", precision = 38, scale = 20)
    @NotNull
    private Double unitsPerParcel = DefaultValue.DOUBLE;

    @Column(name = "Unit Volume", precision = 38, scale = 20)
    @NotNull
    private Double unitVolume = DefaultValue.DOUBLE;

    @Column(name = "Appl_-to Item Entry", precision = 10)
    @NotNull
    private Integer applToItemEntry = DefaultValue.INTEGER;

    @Column(name = "Shortcut Dimension 1 Code", length = 20)
    @NotNull
    private String shortcutDimension1Code = DefaultValue.STRING;

    @Column(name = "Shortcut Dimension 2 Code", length = 20)
    @NotNull
    private String shortcutDimension2Code = DefaultValue.STRING;

    @Column(name = "Customer Price Group", length = 10)
    @NotNull
    private String customerPriceGroup = DefaultValue.STRING;

    @Column(name = "Job No_", length = 20)
    @NotNull
    private String jobNo = DefaultValue.STRING;

    @Column(name = "Work Type Code", length = 10)
    @NotNull
    private String workTypeCode = DefaultValue.STRING;

    @Column(name = "Outstanding Amount", precision = 38, scale = 20)
    @NotNull
    private Double outstandingAmount = DefaultValue.DOUBLE;

    @Column(name = "Qty_ Shipped Not Invoiced", precision = 38, scale = 20)
    @NotNull
    private Double qtyShippedNotInvoiced = DefaultValue.DOUBLE;

    @Column(name = "Shipped Not Invoiced", precision = 38, scale = 20)
    @NotNull
    private Double shippedNotInvoiced = DefaultValue.DOUBLE;

    @Column(name = "Quantity Shipped", precision = 38, scale = 20)
    @NotNull
    private Double quantityShipped = DefaultValue.DOUBLE;

    @Column(name = "Quantity Invoiced", precision = 38, scale = 20)
    @NotNull
    private Double quantityInvoiced = DefaultValue.DOUBLE;

    @Column(name = "Shipment No_", length = 20)
    @NotNull
    private String shipmentNo = DefaultValue.STRING;

    @Column(name = "Shipment Line No_", precision = 10)
    @NotNull
    private Double shipmentLineNo = DefaultValue.DOUBLE;

    @Column(name = "Profit %", precision = 38, scale = 20)
    @NotNull
    private Double profitPercentage = DefaultValue.DOUBLE;

    @Column(name = "Bill-to Customer No_", length = 20)
    @NotNull
    private String billToCustomerNo = DefaultValue.STRING;

    @Column(name = "Inv_ Discount Amount", precision = 38, scale = 20)
    @NotNull
    private Double invDiscountAmount = DefaultValue.DOUBLE;

    @Column(name = "Purchase Order No_", length = 20)
    @NotNull
    private String purchaseOrderNo = DefaultValue.STRING;

    @Column(name = "Purch_ Order Line No_", precision = 10)
    @NotNull
    private Integer purchOrderLineNo = DefaultValue.INTEGER;

    @Column(name = "Drop Shipment", precision = 3)
    @NotNull
    private Boolean dropShipment = DefaultValue.BOOLEAN;

    @Column(name = "Gen_ Bus_ Posting Group", length = 10)
    @NotNull
    private String genBusPostingGroup = DefaultValue.STRING;

    @Column(name = "Gen_ Prod_ Posting Group", length = 10)
    @NotNull
    private String genProdPostingGroup = DefaultValue.STRING;

    @Column(name = "VAT Calculation Type", precision = 10)
    @NotNull
    private Integer vatCalculationType = DefaultValue.INTEGER;

    @Column(name = "Transaction Type", length = 10)
    @NotNull
    private String transactionType = DefaultValue.STRING;

    @Column(name = "Transport Method", length = 10)
    @NotNull
    private String transportMethod = DefaultValue.STRING;

    @Column(name = "Attached to Line No_", precision = 10)
    @NotNull
    private Integer attachedToLineNo = DefaultValue.INTEGER;

    @Column(name = "Exit Point", length = 10)
    @NotNull
    private String exitPoint = DefaultValue.STRING;

    @Column(name = "Area", length = 10)
    @NotNull
    private String area = DefaultValue.STRING;

    @Column(name = "Transaction Specification", length = 10)
    @NotNull
    private String transactionSpecification = DefaultValue.STRING;

    @Column(name = "Tax Area Code", length = 20)
    @NotNull
    private String taxAreaCode = DefaultValue.STRING;

    @Column(name = "Tax Liable", precision = 3)
    @NotNull
    private Boolean taxLiable = DefaultValue.BOOLEAN;

    @Column(name = "Tax Group Code", length = 10)
    @NotNull
    private String taxGroupCode = DefaultValue.STRING;

    @Column(name = "VAT Bus_ Posting Group", length = 10)
    @NotNull
    private String vatBusPostingGroup = DefaultValue.STRING;

    @Column(name = "VAT Prod_ Posting Group", length = 10)
    @NotNull
    private String vatProdPostingGroup = DefaultValue.STRING;

    @Column(name = "Currency Code", length = 10)
    @NotNull
    private String currencyCode = DefaultValue.STRING;

    @Column(name = "Outstanding Amount (LCY)", precision = 38, scale = 20)
    @NotNull
    private Double outstandingAmountLcy = DefaultValue.DOUBLE;

    @Column(name = "Shipped Not Invoiced (LCY)", precision = 38, scale = 20)
    @NotNull
    private Double shippedNotInvoicedLcy = DefaultValue.DOUBLE;

    @Column(name = "Reserve", precision = 10)
    @NotNull
    private Integer reserve = DefaultValue.INTEGER;

    @Column(name = "Blanket Order No_", length = 20)
    @NotNull
    private String blanketOrderNo = DefaultValue.STRING;

    @Column(name = "Blanket Order Line No_", precision = 10)
    @NotNull
    private Integer blanketOrderLineNo = DefaultValue.INTEGER;

    @Column(name = "VAT Base Amount", precision = 38, scale = 20)
    @NotNull
    private Double vatBaseAmount = DefaultValue.DOUBLE;

    @Column(name = "Unit Cost", precision = 38, scale = 20)
    @NotNull
    private Double unitCost = DefaultValue.DOUBLE;

    @Column(name = "System-Created Entry", precision = 3)
    @NotNull
    private Boolean systemCreatedEntry = DefaultValue.BOOLEAN;

    @Column(name = "Line Amount", precision = 38, scale = 20)
    @NotNull
    private Double lineAmount = DefaultValue.DOUBLE;

    @Column(name = "VAT Difference", precision = 38, scale = 20)
    @NotNull
    private Double vatDifference = DefaultValue.DOUBLE;

    @Column(name = "Inv_ Disc_ Amount to Invoice", precision = 38, scale = 20)
    @NotNull
    private Double invDiscAmountToInvoice = DefaultValue.DOUBLE;

    @Column(name = "VAT Identifier", length = 10)
    @NotNull
    private String vatIdentifier = DefaultValue.STRING;

    @Column(name = "IC Partner Ref_ Type", precision = 10)
    @NotNull
    private Integer icPartnerRefType = DefaultValue.INTEGER;

    @Column(name = "IC Partner Reference", length = 20)
    @NotNull
    private String icPartnerReference = DefaultValue.STRING;

    @Column(name = "Prepayment %", precision = 38, scale = 20)
    @NotNull
    private Double prepaymentPercentage = DefaultValue.DOUBLE;

    @Column(name = "Prepmt_ Line Amount", precision = 38, scale = 20)
    @NotNull
    private Double prepmtLineAmount = DefaultValue.DOUBLE;

    @Column(name = "Prepmt_ Amt_ Inv_", precision = 38, scale = 20)
    @NotNull
    private Double prepmtAmtInv = DefaultValue.DOUBLE;

    @Column(name = "Prepmt_ Amt_ Incl_ VAT", precision = 38, scale = 20)
    @NotNull
    private Double prepmtAmtInclVat = DefaultValue.DOUBLE;

    @Column(name = "Prepayment Amount", precision = 38, scale = 20)
    @NotNull
    private Double prepaymentAmount = DefaultValue.DOUBLE;

    @Column(name = "Prepmt_ VAT Base Amt_", precision = 38, scale = 20)
    @NotNull
    private Double prepmtVatBaseAmt = DefaultValue.DOUBLE;

    @Column(name = "Prepayment VAT %", precision = 38, scale = 20)
    @NotNull
    private Double prepaymentVatPercentage = DefaultValue.DOUBLE;

    @Column(name = "Prepmt_ VAT Calc_ Type", precision = 10)
    @NotNull
    private Integer prepmtVatCalcType = DefaultValue.INTEGER;

    @Column(name = "Prepayment VAT Identifier", length = 10)
    @NotNull
    private String prepaymentVatIdentifier = DefaultValue.STRING;

    @Column(name = "Prepayment Tax Area Code", length = 20)
    @NotNull
    private String prepaymentTaxAreaCode = DefaultValue.STRING;

    @Column(name = "Prepayment Tax Liable", precision = 3)
    @NotNull
    private Boolean prepaymentTaxLiable = DefaultValue.BOOLEAN;

    @Column(name = "Prepayment Tax Group Code", length = 10)
    @NotNull
    private String prepaymentTaxGroupCode = DefaultValue.STRING;

    @Column(name = "Prepmt Amt to Deduct", precision = 38, scale = 20)
    @NotNull
    private Double prepmtAmtToDeduct = DefaultValue.DOUBLE;

    @Column(name = "Prepmt Amt Deducted", precision = 38, scale = 20)
    @NotNull
    private Double prepmtAmtDeducted = DefaultValue.DOUBLE;

    @Column(name = "Prepayment Line", precision = 3)
    @NotNull
    private Boolean prepaymentLine = DefaultValue.BOOLEAN;

    @Column(name = "Prepmt_ Amount Inv_ Incl_ VAT", precision = 38, scale = 20)
    @NotNull
    private Double prepmtAmountInvInclVat = DefaultValue.DOUBLE;

    @Column(name = "IC Partner Code", length = 20)
    @NotNull
    private String icPartnerCode = DefaultValue.STRING;

    @Column(name = "Job Task No_", length = 20)
    @NotNull
    private String jobTaskNo = DefaultValue.STRING;

    @Column(name = "Job Contract Entry No_", precision = 10)
    @NotNull
    private Integer jobContractEntryNo = DefaultValue.INTEGER;

    @Column(name = "Variant Code", length = 10)
    @NotNull
    private String variantCode = DefaultValue.STRING;

    @Column(name = "Bin Code", length = 20)
    @NotNull
    private String binCode = DefaultValue.STRING;

    @Column(name = "Qty_ per Unit of Measure", precision = 38, scale = 20)
    @NotNull
    private Double qtyPerUnitOfMeasure = DefaultValue.DOUBLE;

    @Column(name = "Planned", precision = 3)
    @NotNull
    private Boolean planned = DefaultValue.BOOLEAN;

    @Column(name = "Unit of Measure Code", length = 10)
    @NotNull
    private String unitOfMeasureCode = DefaultValue.STRING;

    @Column(name = "Quantity (Base)", precision = 38, scale = 20)
    @NotNull
    private Double quantityBase = DefaultValue.DOUBLE;

    @Column(name = "Outstanding Qty_ (Base)", precision = 38, scale = 20)
    @NotNull
    private Double outstandingQtyBase = DefaultValue.DOUBLE;

    @Column(name = "Qty_ to Invoice (Base)", precision = 38, scale = 20)
    @NotNull
    private Double qtyToInvoiceBase = DefaultValue.DOUBLE;

    @Column(name = "Qty_ to Ship (Base)", precision = 38, scale = 20)
    @NotNull
    private Double qtyToShipBase = DefaultValue.DOUBLE;

    @Column(name = "Qty_ Shipped Not Invd_ (Base)", precision = 38, scale = 20)
    @NotNull
    private Double qtyShippedNotInvdBase = DefaultValue.DOUBLE;

    @Column(name = "Qty_ Shipped (Base)", precision = 38, scale = 20)
    @NotNull
    private Double qtyShippedBase = DefaultValue.DOUBLE;

    @Column(name = "Qty_ Invoiced (Base)", precision = 38, scale = 20)
    @NotNull
    private Double qtyInvoicedBase = DefaultValue.DOUBLE;

    @Column(name = "FA Posting Date")
    @NotNull
    private LocalDateTime faPostingDate = DefaultValue.LOCAL_DATE_TIME;

    @Column(name = "Depreciation Book Code", length = 10)
    @NotNull
    private String depreciationBookCode = DefaultValue.STRING;

    @Column(name = "Depr_ until FA Posting Date", precision = 3)
    @NotNull
    private Boolean deprUntilFaPostingDate = DefaultValue.BOOLEAN;

    @Column(name = "Duplicate in Depreciation Book", length = 10)
    @NotNull
    private String duplicateInDepreciationBook = DefaultValue.STRING;

    @Column(name = "Use Duplication List", precision = 3)
    @NotNull
    private Boolean useDuplicationList = DefaultValue.BOOLEAN;

    @Column(name = "Responsibility Center", length = 10)
    @NotNull
    private String responsibilityCenter = DefaultValue.STRING;

    @Column(name = "Out-of-Stock Substitution", precision = 3)
    @NotNull
    private Boolean outOfStockSubstitution = DefaultValue.BOOLEAN;

    @Column(name = "Originally Ordered No_", length = 20)
    @NotNull
    private String originallyOrderedNo = DefaultValue.STRING;

    @Column(name = "Originally Ordered Var_ Code", length = 10)
    @NotNull
    private String originallyOrderedVarCode = DefaultValue.STRING;

    @Column(name = "Cross-Reference No_", length = 20)
    @NotNull
    private String crossReferenceNo = DefaultValue.STRING;

    @Column(name = "Unit of Measure (Cross Ref_)", length = 10)
    @NotNull
    private String unitOfMeasureCrossRef = DefaultValue.STRING;

    @Column(name = "Cross-Reference Type", precision = 10)
    @NotNull
    private Integer crossReferenceType = DefaultValue.INTEGER;

    @Column(name = "Cross-Reference Type No_", length = 30)
    @NotNull
    private String crossReferenceTypeNo = DefaultValue.STRING;

    @Column(name = "Item Category Code", length = 10)
    @NotNull
    private String itemCategoryCode = DefaultValue.STRING;

    @Column(name = "Nonstock", precision = 3)
    @NotNull
    private Boolean nonstock = DefaultValue.BOOLEAN;

    @Column(name = "Purchasing Code", length = 10)
    @NotNull
    private String purchasingCode = DefaultValue.STRING;

    @Column(name = "Product Group Code", length = 10)
    @NotNull
    private String productGroupCode = DefaultValue.STRING;

    @Column(name = "Special Order", precision = 3)
    @NotNull
    private Boolean specialOrder = DefaultValue.BOOLEAN;

    @Column(name = "Special Order Purchase No_", length = 20)
    @NotNull
    private String specialOrderPurchaseNo = DefaultValue.STRING;

    @Column(name = "Special Order Purch_ Line No_", precision = 10)
    @NotNull
    private Integer specialOrderPurchLineNo = DefaultValue.INTEGER;

    @Column(name = "Completely Shipped", precision = 3)
    @NotNull
    private Boolean completelyShipped = DefaultValue.BOOLEAN;

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

    @Column(name = "Planned Delivery Date")
    @NotNull
    private LocalDateTime plannedDeliveryDate = DefaultValue.LOCAL_DATE_TIME;

    @Column(name = "Planned Shipment Date")
    @NotNull
    private LocalDateTime plannedShipmentDate = DefaultValue.LOCAL_DATE_TIME;

    @Column(name = "Shipping Agent Code", length = 10)
    @NotNull
    private String shippingAgentCode = DefaultValue.STRING;

    @Column(name = "Shipping Agent Service Code", length = 10)
    @NotNull
    private String shippingAgentServiceCode = DefaultValue.STRING;

    @Column(name = "Allow Item Charge Assignment", precision = 3)
    @NotNull
    private Boolean allowItemChargeAssignment = DefaultValue.BOOLEAN;

    @Column(name = "Return Qty_ to Receive", precision = 38, scale = 20)
    @NotNull
    private Double returnQtyToReceive = DefaultValue.DOUBLE;

    @Column(name = "Return Qty_ to Receive (Base)", precision = 38, scale = 20)
    @NotNull
    private Double returnQtyToReceiveBase = DefaultValue.DOUBLE;

    @Column(name = "Return Qty_ Rcd_ Not Invd_", precision = 38, scale = 20)
    @NotNull
    private Double returnQtyRcdNotInvd = DefaultValue.DOUBLE;

    @Column(name = "Ret_ Qty_ Rcd_ Not Invd_(Base)", precision = 38, scale = 20)
    @NotNull
    private Double retQtyRcdNotInvdBase = DefaultValue.DOUBLE;

    @Column(name = "Return Rcd_ Not Invd_", precision = 38, scale = 20)
    @NotNull
    private Double returnRcdNotInvd = DefaultValue.DOUBLE;

    @Column(name = "Return Rcd_ Not Invd_ (LCY)", precision = 38, scale = 20)
    @NotNull
    private Double returnRcdNotInvdLcy = DefaultValue.DOUBLE;

    @Column(name = "Return Qty_ Received", precision = 38, scale = 20)
    @NotNull
    private Double returnQtyReceived = DefaultValue.DOUBLE;

    @Column(name = "Return Qty_ Received (Base)", precision = 38, scale = 20)
    @NotNull
    private Double returnQtyReceivedBase = DefaultValue.DOUBLE;

    @Column(name = "Appl_-from Item Entry", precision = 10)
    @NotNull
    private Integer applFromItemEntry = DefaultValue.INTEGER;

    @Column(name = "BOM Item No_", length = 20)
    @NotNull
    private String bomItemNo = DefaultValue.STRING;

    @Column(name = "Return Receipt No_", length = 20)
    @NotNull
    private String returnReceiptNo = DefaultValue.STRING;

    @Column(name = "Return Receipt Line No_", precision = 10)
    @NotNull
    private Integer returnReceiptLineNo = DefaultValue.INTEGER;

    @Column(name = "Return Reason Code", length = 10)
    @NotNull
    private String returnReasonCode = DefaultValue.STRING;

    @Column(name = "Allow Line Disc_", precision = 3)
    @NotNull
    private Boolean allowLineDisc = DefaultValue.BOOLEAN;

    @Column(name = "Customer Disc_ Group", length = 10)
    @NotNull
    private String customerDiscGroup = DefaultValue.STRING;

    @Column(name = "Reference 2", length = 20)
    @NotNull
    private String reference2 = DefaultValue.STRING;

    @Column(name = "Code", length = 50)
    @NotNull
    private String code = DefaultValue.STRING;

    @Column(name = "Category Code", length = 10)
    @NotNull
    private String categoryCode = DefaultValue.STRING;

    @Column(name = "Charge Type Code", length = 10)
    @NotNull
    private String chargeTypeCode = DefaultValue.STRING;

    @Column(name = "Charge Code", length = 10)
    @NotNull
    private String chargeCode = DefaultValue.STRING;

    @Column(name = "Reg Description", length = 100)
    @NotNull
    private String regDescription = DefaultValue.STRING;

    @Column(name = "Reg Amount", precision = 38, scale = 20)
    @NotNull
    private Double regAmount = DefaultValue.DOUBLE;

    @Column(name = "G_L Account No", length = 30)
    @NotNull
    private String glAccountNo = DefaultValue.STRING;

    @Column(name = "Account Name", length = 100)
    @NotNull
    private String accountName = DefaultValue.STRING;

    @Column(name = "Manufacturer", length = 100)
    @NotNull
    private String manufacturer = DefaultValue.STRING;

    @Column(name = "Model", length = 50)
    @NotNull
    private String model = DefaultValue.STRING;

    @Column(name = "ICAO", length = 10)
    @NotNull
    private String icao = DefaultValue.STRING;

    @Column(name = "Mtow", precision = 10)
    @NotNull
    private Integer mtow = DefaultValue.INTEGER;

    @Column(name = "Wake", length = 50)
    @NotNull
    private String wake = DefaultValue.STRING;

    @Column(name = "Bands", precision = 3)
    @NotNull
    private Boolean bands = DefaultValue.BOOLEAN;

    @Column(name = "Charge bands", length = 20)
    @NotNull
    private String chargeBands = DefaultValue.STRING;

    @Column(name = "Band No_", precision = 10)
    @NotNull
    private Integer bandNo = DefaultValue.INTEGER;

    @Column(name = "Charge option", precision = 10)
    @NotNull
    private Integer chargeOption = DefaultValue.INTEGER;

    @Column(name = "MTOM", precision = 10)
    @NotNull
    private Integer mtom = DefaultValue.INTEGER;

    @Column(name = "Based on Charge", length = 20)
    @NotNull
    private String basedOnCharge = DefaultValue.STRING;

    @Column(name = "Page document", length = 20)
    @NotNull
    private String pageDocument = DefaultValue.STRING;

    @Column(name = "Band Name", length = 10)
    @NotNull
    private String bandName = DefaultValue.STRING;

    @Column(name = "Reg_ Mark", length = 250)
    @NotNull
    private String regMark = DefaultValue.STRING;

    @Column(name = "Examiners fee", precision = 38, scale = 20)
    @NotNull
    private Double examinersFee = DefaultValue.DOUBLE;

    @Column(name = "Upper Limit", precision = 10)
    @NotNull
    private Integer upperLimit = DefaultValue.INTEGER;

    @Column(name = "Difference", precision = 10)
    @NotNull
    private Integer difference = DefaultValue.INTEGER;

    @Column(name = "Additional Rate", precision = 10)
    @NotNull
    private Integer additionalRate = DefaultValue.INTEGER;

    @Column(name = "Amount Rate", precision = 38, scale = 20)
    @NotNull
    private Double amountRate = DefaultValue.DOUBLE;

    @Column(name = "Number", precision = 10)
    @NotNull
    private Integer number = DefaultValue.INTEGER;

    @Column(name = "Transaction No_", precision = 10)
    @NotNull
    private Integer transactionNo = DefaultValue.INTEGER;

    @Column(name = "Lease Value", precision = 38, scale = 20)
    @NotNull
    private Double leaseValue = DefaultValue.DOUBLE;

    @Column(name = "Spare Parts Value", precision = 38, scale = 20)
    @NotNull
    private Double sparePartsValue = DefaultValue.DOUBLE;

    @Column(name = "TBO", precision = 38, scale = 20)
    @NotNull
    private Double tbo = DefaultValue.DOUBLE;

    @Column(name = "Extension Granted", precision = 38, scale = 20)
    @NotNull
    private Double extensionGranted = DefaultValue.DOUBLE;

    @Column(name = "Current Operation hrs", precision = 38, scale = 20)
    @NotNull
    private Double currentOperationHrs = DefaultValue.DOUBLE;

    @Column(name = "AATIS Payment", precision = 3)
    @NotNull
    private Boolean aatisPayment = DefaultValue.BOOLEAN;

    @Column(name = "CoA Start Date")
    @NotNull
    private LocalDateTime coaStartDate = DefaultValue.LOCAL_DATE_TIME;

    @Column(name = "CoA End Date")
    @NotNull
    private LocalDateTime coaEndDate = DefaultValue.LOCAL_DATE_TIME;

    @Column(name = "Annual ANS Charge", precision = 3)
    @NotNull
    private Boolean annualAnsCharge = DefaultValue.BOOLEAN;

    @Column(name = "Attracted", precision = 3)
    @NotNull
    private Boolean attracted = DefaultValue.BOOLEAN;

    @Column(name = "C of A Charge", precision = 3)
    @NotNull
    private Boolean cOfACharge = DefaultValue.BOOLEAN;

    public Integer getDocumentType() {
        return documentType;
    }

    public void setDocumentType(Integer documentType) {
        this.documentType = documentType;
    }

    public String getDocumentNo() {
        return documentNo;
    }

    public void setDocumentNo(String documentNo) {
        this.documentNo = documentNo;
    }

    public Integer getLineNo() {
        return lineNo;
    }

    public void setLineNo(Integer lineNo) {
        this.lineNo = lineNo;
    }

    public String getSellToCustomerNo() {
        return sellToCustomerNo;
    }

    public void setSellToCustomerNo(String sellToCustomerNo) {
        this.sellToCustomerNo = sellToCustomerNo;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public String getPostingGroup() {
        return postingGroup;
    }

    public void setPostingGroup(String postingGroup) {
        this.postingGroup = postingGroup;
    }

    public LocalDateTime getShipmentDate() {
        return shipmentDate;
    }

    public void setShipmentDate(LocalDateTime shipmentDate) {
        this.shipmentDate = shipmentDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription2() {
        return description2;
    }

    public void setDescription2(String description2) {
        this.description2 = description2;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getOutstandingQuantity() {
        return outstandingQuantity;
    }

    public void setOutstandingQuantity(Double outstandingQuantity) {
        this.outstandingQuantity = outstandingQuantity;
    }

    public Double getQtyToInvoice() {
        return qtyToInvoice;
    }

    public void setQtyToInvoice(Double qtyToInvoice) {
        this.qtyToInvoice = qtyToInvoice;
    }

    public Double getQtyToShip() {
        return qtyToShip;
    }

    public void setQtyToShip(Double qtyToShip) {
        this.qtyToShip = qtyToShip;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Double getUnitCostLcy() {
        return unitCostLcy;
    }

    public void setUnitCostLcy(Double unitCostLcy) {
        this.unitCostLcy = unitCostLcy;
    }

    public Double getVatPercentage() {
        return vatPercentage;
    }

    public void setVatPercentage(Double vatPercentage) {
        this.vatPercentage = vatPercentage;
    }

    public Double getLineDiscountPercentage() {
        return lineDiscountPercentage;
    }

    public void setLineDiscountPercentage(Double lineDiscountPercentage) {
        this.lineDiscountPercentage = lineDiscountPercentage;
    }

    public Double getLineDiscountAmount() {
        return lineDiscountAmount;
    }

    public void setLineDiscountAmount(Double lineDiscountAmount) {
        this.lineDiscountAmount = lineDiscountAmount;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getAmountIncludingVat() {
        return amountIncludingVat;
    }

    public void setAmountIncludingVat(Double amountIncludingVat) {
        this.amountIncludingVat = amountIncludingVat;
    }

    public Boolean getAllowInvoiceDisc() {
        return allowInvoiceDisc;
    }

    public void setAllowInvoiceDisc(Boolean allowInvoiceDisc) {
        this.allowInvoiceDisc = allowInvoiceDisc;
    }

    public Double getGrossWeight() {
        return grossWeight;
    }

    public void setGrossWeight(Double grossWeight) {
        this.grossWeight = grossWeight;
    }

    public Double getNetWeight() {
        return netWeight;
    }

    public void setNetWeight(Double netWeight) {
        this.netWeight = netWeight;
    }

    public Double getUnitsPerParcel() {
        return unitsPerParcel;
    }

    public void setUnitsPerParcel(Double unitsPerParcel) {
        this.unitsPerParcel = unitsPerParcel;
    }

    public Double getUnitVolume() {
        return unitVolume;
    }

    public void setUnitVolume(Double unitVolume) {
        this.unitVolume = unitVolume;
    }

    public Integer getApplToItemEntry() {
        return applToItemEntry;
    }

    public void setApplToItemEntry(Integer applToItemEntry) {
        this.applToItemEntry = applToItemEntry;
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

    public String getCustomerPriceGroup() {
        return customerPriceGroup;
    }

    public void setCustomerPriceGroup(String customerPriceGroup) {
        this.customerPriceGroup = customerPriceGroup;
    }

    public String getJobNo() {
        return jobNo;
    }

    public void setJobNo(String jobNo) {
        this.jobNo = jobNo;
    }

    public String getWorkTypeCode() {
        return workTypeCode;
    }

    public void setWorkTypeCode(String workTypeCode) {
        this.workTypeCode = workTypeCode;
    }

    public Double getOutstandingAmount() {
        return outstandingAmount;
    }

    public void setOutstandingAmount(Double outstandingAmount) {
        this.outstandingAmount = outstandingAmount;
    }

    public Double getQtyShippedNotInvoiced() {
        return qtyShippedNotInvoiced;
    }

    public void setQtyShippedNotInvoiced(Double qtyShippedNotInvoiced) {
        this.qtyShippedNotInvoiced = qtyShippedNotInvoiced;
    }

    public Double getShippedNotInvoiced() {
        return shippedNotInvoiced;
    }

    public void setShippedNotInvoiced(Double shippedNotInvoiced) {
        this.shippedNotInvoiced = shippedNotInvoiced;
    }

    public Double getQuantityShipped() {
        return quantityShipped;
    }

    public void setQuantityShipped(Double quantityShipped) {
        this.quantityShipped = quantityShipped;
    }

    public Double getQuantityInvoiced() {
        return quantityInvoiced;
    }

    public void setQuantityInvoiced(Double quantityInvoiced) {
        this.quantityInvoiced = quantityInvoiced;
    }

    public String getShipmentNo() {
        return shipmentNo;
    }

    public void setShipmentNo(String shipmentNo) {
        this.shipmentNo = shipmentNo;
    }

    public Double getShipmentLineNo() {
        return shipmentLineNo;
    }

    public void setShipmentLineNo(Double shipmentLineNo) {
        this.shipmentLineNo = shipmentLineNo;
    }

    public Double getProfitPercentage() {
        return profitPercentage;
    }

    public void setProfitPercentage(Double profitPercentage) {
        this.profitPercentage = profitPercentage;
    }

    public String getBillToCustomerNo() {
        return billToCustomerNo;
    }

    public void setBillToCustomerNo(String billToCustomerNo) {
        this.billToCustomerNo = billToCustomerNo;
    }

    public Double getInvDiscountAmount() {
        return invDiscountAmount;
    }

    public void setInvDiscountAmount(Double invDiscountAmount) {
        this.invDiscountAmount = invDiscountAmount;
    }

    public String getPurchaseOrderNo() {
        return purchaseOrderNo;
    }

    public void setPurchaseOrderNo(String purchaseOrderNo) {
        this.purchaseOrderNo = purchaseOrderNo;
    }

    public Integer getPurchOrderLineNo() {
        return purchOrderLineNo;
    }

    public void setPurchOrderLineNo(Integer purchOrderLineNo) {
        this.purchOrderLineNo = purchOrderLineNo;
    }

    public Boolean getDropShipment() {
        return dropShipment;
    }

    public void setDropShipment(Boolean dropShipment) {
        this.dropShipment = dropShipment;
    }

    public String getGenBusPostingGroup() {
        return genBusPostingGroup;
    }

    public void setGenBusPostingGroup(String genBusPostingGroup) {
        this.genBusPostingGroup = genBusPostingGroup;
    }

    public String getGenProdPostingGroup() {
        return genProdPostingGroup;
    }

    public void setGenProdPostingGroup(String genProdPostingGroup) {
        this.genProdPostingGroup = genProdPostingGroup;
    }

    public Integer getVatCalculationType() {
        return vatCalculationType;
    }

    public void setVatCalculationType(Integer vatCalculationType) {
        this.vatCalculationType = vatCalculationType;
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

    public Integer getAttachedToLineNo() {
        return attachedToLineNo;
    }

    public void setAttachedToLineNo(Integer attachedToLineNo) {
        this.attachedToLineNo = attachedToLineNo;
    }

    public String getExitPoint() {
        return exitPoint;
    }

    public void setExitPoint(String exitPoint) {
        this.exitPoint = exitPoint;
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

    public String getTaxGroupCode() {
        return taxGroupCode;
    }

    public void setTaxGroupCode(String taxGroupCode) {
        this.taxGroupCode = taxGroupCode;
    }

    public String getVatBusPostingGroup() {
        return vatBusPostingGroup;
    }

    public void setVatBusPostingGroup(String vatBusPostingGroup) {
        this.vatBusPostingGroup = vatBusPostingGroup;
    }

    public String getVatProdPostingGroup() {
        return vatProdPostingGroup;
    }

    public void setVatProdPostingGroup(String vatProdPostingGroup) {
        this.vatProdPostingGroup = vatProdPostingGroup;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public Double getOutstandingAmountLcy() {
        return outstandingAmountLcy;
    }

    public void setOutstandingAmountLcy(Double outstandingAmountLcy) {
        this.outstandingAmountLcy = outstandingAmountLcy;
    }

    public Double getShippedNotInvoicedLcy() {
        return shippedNotInvoicedLcy;
    }

    public void setShippedNotInvoicedLcy(Double shippedNotInvoicedLcy) {
        this.shippedNotInvoicedLcy = shippedNotInvoicedLcy;
    }

    public Integer getReserve() {
        return reserve;
    }

    public void setReserve(Integer reserve) {
        this.reserve = reserve;
    }

    public String getBlanketOrderNo() {
        return blanketOrderNo;
    }

    public void setBlanketOrderNo(String blanketOrderNo) {
        this.blanketOrderNo = blanketOrderNo;
    }

    public Integer getBlanketOrderLineNo() {
        return blanketOrderLineNo;
    }

    public void setBlanketOrderLineNo(Integer blanketOrderLineNo) {
        this.blanketOrderLineNo = blanketOrderLineNo;
    }

    public Double getVatBaseAmount() {
        return vatBaseAmount;
    }

    public void setVatBaseAmount(Double vatBaseAmount) {
        this.vatBaseAmount = vatBaseAmount;
    }

    public Double getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(Double unitCost) {
        this.unitCost = unitCost;
    }

    public Boolean getSystemCreatedEntry() {
        return systemCreatedEntry;
    }

    public void setSystemCreatedEntry(Boolean systemCreatedEntry) {
        this.systemCreatedEntry = systemCreatedEntry;
    }

    public Double getLineAmount() {
        return lineAmount;
    }

    public void setLineAmount(Double lineAmount) {
        this.lineAmount = lineAmount;
    }

    public Double getVatDifference() {
        return vatDifference;
    }

    public void setVatDifference(Double vatDifference) {
        this.vatDifference = vatDifference;
    }

    public Double getInvDiscAmountToInvoice() {
        return invDiscAmountToInvoice;
    }

    public void setInvDiscAmountToInvoice(Double invDiscAmountToInvoice) {
        this.invDiscAmountToInvoice = invDiscAmountToInvoice;
    }

    public String getVatIdentifier() {
        return vatIdentifier;
    }

    public void setVatIdentifier(String vatIdentifier) {
        this.vatIdentifier = vatIdentifier;
    }

    public Integer getIcPartnerRefType() {
        return icPartnerRefType;
    }

    public void setIcPartnerRefType(Integer icPartnerRefType) {
        this.icPartnerRefType = icPartnerRefType;
    }

    public String getIcPartnerReference() {
        return icPartnerReference;
    }

    public void setIcPartnerReference(String icPartnerReference) {
        this.icPartnerReference = icPartnerReference;
    }

    public Double getPrepaymentPercentage() {
        return prepaymentPercentage;
    }

    public void setPrepaymentPercentage(Double prepaymentPercentage) {
        this.prepaymentPercentage = prepaymentPercentage;
    }

    public Double getPrepmtLineAmount() {
        return prepmtLineAmount;
    }

    public void setPrepmtLineAmount(Double prepmtLineAmount) {
        this.prepmtLineAmount = prepmtLineAmount;
    }

    public Double getPrepmtAmtInv() {
        return prepmtAmtInv;
    }

    public void setPrepmtAmtInv(Double prepmtAmtInv) {
        this.prepmtAmtInv = prepmtAmtInv;
    }

    public Double getPrepmtAmtInclVat() {
        return prepmtAmtInclVat;
    }

    public void setPrepmtAmtInclVat(Double prepmtAmtInclVat) {
        this.prepmtAmtInclVat = prepmtAmtInclVat;
    }

    public Double getPrepaymentAmount() {
        return prepaymentAmount;
    }

    public void setPrepaymentAmount(Double prepaymentAmount) {
        this.prepaymentAmount = prepaymentAmount;
    }

    public Double getPrepmtVatBaseAmt() {
        return prepmtVatBaseAmt;
    }

    public void setPrepmtVatBaseAmt(Double prepmtVatBaseAmt) {
        this.prepmtVatBaseAmt = prepmtVatBaseAmt;
    }

    public Double getPrepaymentVatPercentage() {
        return prepaymentVatPercentage;
    }

    public void setPrepaymentVatPercentage(Double prepaymentVatPercentage) {
        this.prepaymentVatPercentage = prepaymentVatPercentage;
    }

    public Integer getPrepmtVatCalcType() {
        return prepmtVatCalcType;
    }

    public void setPrepmtVatCalcType(Integer prepmtVatCalcType) {
        this.prepmtVatCalcType = prepmtVatCalcType;
    }

    public String getPrepaymentVatIdentifier() {
        return prepaymentVatIdentifier;
    }

    public void setPrepaymentVatIdentifier(String prepaymentVatIdentifier) {
        this.prepaymentVatIdentifier = prepaymentVatIdentifier;
    }

    public String getPrepaymentTaxAreaCode() {
        return prepaymentTaxAreaCode;
    }

    public void setPrepaymentTaxAreaCode(String prepaymentTaxAreaCode) {
        this.prepaymentTaxAreaCode = prepaymentTaxAreaCode;
    }

    public Boolean getPrepaymentTaxLiable() {
        return prepaymentTaxLiable;
    }

    public void setPrepaymentTaxLiable(Boolean prepaymentTaxLiable) {
        this.prepaymentTaxLiable = prepaymentTaxLiable;
    }

    public String getPrepaymentTaxGroupCode() {
        return prepaymentTaxGroupCode;
    }

    public void setPrepaymentTaxGroupCode(String prepaymentTaxGroupCode) {
        this.prepaymentTaxGroupCode = prepaymentTaxGroupCode;
    }

    public Double getPrepmtAmtToDeduct() {
        return prepmtAmtToDeduct;
    }

    public void setPrepmtAmtToDeduct(Double prepmtAmtToDeduct) {
        this.prepmtAmtToDeduct = prepmtAmtToDeduct;
    }

    public Double getPrepmtAmtDeducted() {
        return prepmtAmtDeducted;
    }

    public void setPrepmtAmtDeducted(Double prepmtAmtDeducted) {
        this.prepmtAmtDeducted = prepmtAmtDeducted;
    }

    public Boolean getPrepaymentLine() {
        return prepaymentLine;
    }

    public void setPrepaymentLine(Boolean prepaymentLine) {
        this.prepaymentLine = prepaymentLine;
    }

    public Double getPrepmtAmountInvInclVat() {
        return prepmtAmountInvInclVat;
    }

    public void setPrepmtAmountInvInclVat(Double prepmtAmountInvInclVat) {
        this.prepmtAmountInvInclVat = prepmtAmountInvInclVat;
    }

    public String getIcPartnerCode() {
        return icPartnerCode;
    }

    public void setIcPartnerCode(String icPartnerCode) {
        this.icPartnerCode = icPartnerCode;
    }

    public String getJobTaskNo() {
        return jobTaskNo;
    }

    public void setJobTaskNo(String jobTaskNo) {
        this.jobTaskNo = jobTaskNo;
    }

    public Integer getJobContractEntryNo() {
        return jobContractEntryNo;
    }

    public void setJobContractEntryNo(Integer jobContractEntryNo) {
        this.jobContractEntryNo = jobContractEntryNo;
    }

    public String getVariantCode() {
        return variantCode;
    }

    public void setVariantCode(String variantCode) {
        this.variantCode = variantCode;
    }

    public String getBinCode() {
        return binCode;
    }

    public void setBinCode(String binCode) {
        this.binCode = binCode;
    }

    public Double getQtyPerUnitOfMeasure() {
        return qtyPerUnitOfMeasure;
    }

    public void setQtyPerUnitOfMeasure(Double qtyPerUnitOfMeasure) {
        this.qtyPerUnitOfMeasure = qtyPerUnitOfMeasure;
    }

    public Boolean getPlanned() {
        return planned;
    }

    public void setPlanned(Boolean planned) {
        this.planned = planned;
    }

    public String getUnitOfMeasureCode() {
        return unitOfMeasureCode;
    }

    public void setUnitOfMeasureCode(String unitOfMeasureCode) {
        this.unitOfMeasureCode = unitOfMeasureCode;
    }

    public Double getQuantityBase() {
        return quantityBase;
    }

    public void setQuantityBase(Double quantityBase) {
        this.quantityBase = quantityBase;
    }

    public Double getOutstandingQtyBase() {
        return outstandingQtyBase;
    }

    public void setOutstandingQtyBase(Double outstandingQtyBase) {
        this.outstandingQtyBase = outstandingQtyBase;
    }

    public Double getQtyToInvoiceBase() {
        return qtyToInvoiceBase;
    }

    public void setQtyToInvoiceBase(Double qtyToInvoiceBase) {
        this.qtyToInvoiceBase = qtyToInvoiceBase;
    }

    public Double getQtyToShipBase() {
        return qtyToShipBase;
    }

    public void setQtyToShipBase(Double qtyToShipBase) {
        this.qtyToShipBase = qtyToShipBase;
    }

    public Double getQtyShippedNotInvdBase() {
        return qtyShippedNotInvdBase;
    }

    public void setQtyShippedNotInvdBase(Double qtyShippedNotInvdBase) {
        this.qtyShippedNotInvdBase = qtyShippedNotInvdBase;
    }

    public Double getQtyShippedBase() {
        return qtyShippedBase;
    }

    public void setQtyShippedBase(Double qtyShippedBase) {
        this.qtyShippedBase = qtyShippedBase;
    }

    public Double getQtyInvoicedBase() {
        return qtyInvoicedBase;
    }

    public void setQtyInvoicedBase(Double qtyInvoicedBase) {
        this.qtyInvoicedBase = qtyInvoicedBase;
    }

    public LocalDateTime getFaPostingDate() {
        return faPostingDate;
    }

    public void setFaPostingDate(LocalDateTime faPostingDate) {
        this.faPostingDate = faPostingDate;
    }

    public String getDepreciationBookCode() {
        return depreciationBookCode;
    }

    public void setDepreciationBookCode(String depreciationBookCode) {
        this.depreciationBookCode = depreciationBookCode;
    }

    public Boolean getDeprUntilFaPostingDate() {
        return deprUntilFaPostingDate;
    }

    public void setDeprUntilFaPostingDate(Boolean deprUntilFaPostingDate) {
        this.deprUntilFaPostingDate = deprUntilFaPostingDate;
    }

    public String getDuplicateInDepreciationBook() {
        return duplicateInDepreciationBook;
    }

    public void setDuplicateInDepreciationBook(String duplicateInDepreciationBook) {
        this.duplicateInDepreciationBook = duplicateInDepreciationBook;
    }

    public Boolean getUseDuplicationList() {
        return useDuplicationList;
    }

    public void setUseDuplicationList(Boolean useDuplicationList) {
        this.useDuplicationList = useDuplicationList;
    }

    public String getResponsibilityCenter() {
        return responsibilityCenter;
    }

    public void setResponsibilityCenter(String responsibilityCenter) {
        this.responsibilityCenter = responsibilityCenter;
    }

    public Boolean getOutOfStockSubstitution() {
        return outOfStockSubstitution;
    }

    public void setOutOfStockSubstitution(Boolean outOfStockSubstitution) {
        this.outOfStockSubstitution = outOfStockSubstitution;
    }

    public String getOriginallyOrderedNo() {
        return originallyOrderedNo;
    }

    public void setOriginallyOrderedNo(String originallyOrderedNo) {
        this.originallyOrderedNo = originallyOrderedNo;
    }

    public String getOriginallyOrderedVarCode() {
        return originallyOrderedVarCode;
    }

    public void setOriginallyOrderedVarCode(String originallyOrderedVarCode) {
        this.originallyOrderedVarCode = originallyOrderedVarCode;
    }

    public String getCrossReferenceNo() {
        return crossReferenceNo;
    }

    public void setCrossReferenceNo(String crossReferenceNo) {
        this.crossReferenceNo = crossReferenceNo;
    }

    public String getUnitOfMeasureCrossRef() {
        return unitOfMeasureCrossRef;
    }

    public void setUnitOfMeasureCrossRef(String unitOfMeasureCrossRef) {
        this.unitOfMeasureCrossRef = unitOfMeasureCrossRef;
    }

    public Integer getCrossReferenceType() {
        return crossReferenceType;
    }

    public void setCrossReferenceType(Integer crossReferenceType) {
        this.crossReferenceType = crossReferenceType;
    }

    public String getCrossReferenceTypeNo() {
        return crossReferenceTypeNo;
    }

    public void setCrossReferenceTypeNo(String crossReferenceTypeNo) {
        this.crossReferenceTypeNo = crossReferenceTypeNo;
    }

    public String getItemCategoryCode() {
        return itemCategoryCode;
    }

    public void setItemCategoryCode(String itemCategoryCode) {
        this.itemCategoryCode = itemCategoryCode;
    }

    public Boolean getNonstock() {
        return nonstock;
    }

    public void setNonstock(Boolean nonstock) {
        this.nonstock = nonstock;
    }

    public String getPurchasingCode() {
        return purchasingCode;
    }

    public void setPurchasingCode(String purchasingCode) {
        this.purchasingCode = purchasingCode;
    }

    public String getProductGroupCode() {
        return productGroupCode;
    }

    public void setProductGroupCode(String productGroupCode) {
        this.productGroupCode = productGroupCode;
    }

    public Boolean getSpecialOrder() {
        return specialOrder;
    }

    public void setSpecialOrder(Boolean specialOrder) {
        this.specialOrder = specialOrder;
    }

    public String getSpecialOrderPurchaseNo() {
        return specialOrderPurchaseNo;
    }

    public void setSpecialOrderPurchaseNo(String specialOrderPurchaseNo) {
        this.specialOrderPurchaseNo = specialOrderPurchaseNo;
    }

    public Integer getSpecialOrderPurchLineNo() {
        return specialOrderPurchLineNo;
    }

    public void setSpecialOrderPurchLineNo(Integer specialOrderPurchLineNo) {
        this.specialOrderPurchLineNo = specialOrderPurchLineNo;
    }

    public Boolean getCompletelyShipped() {
        return completelyShipped;
    }

    public void setCompletelyShipped(Boolean completelyShipped) {
        this.completelyShipped = completelyShipped;
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

    public LocalDateTime getPlannedDeliveryDate() {
        return plannedDeliveryDate;
    }

    public void setPlannedDeliveryDate(LocalDateTime plannedDeliveryDate) {
        this.plannedDeliveryDate = plannedDeliveryDate;
    }

    public LocalDateTime getPlannedShipmentDate() {
        return plannedShipmentDate;
    }

    public void setPlannedShipmentDate(LocalDateTime plannedShipmentDate) {
        this.plannedShipmentDate = plannedShipmentDate;
    }

    public String getShippingAgentCode() {
        return shippingAgentCode;
    }

    public void setShippingAgentCode(String shippingAgentCode) {
        this.shippingAgentCode = shippingAgentCode;
    }

    public String getShippingAgentServiceCode() {
        return shippingAgentServiceCode;
    }

    public void setShippingAgentServiceCode(String shippingAgentServiceCode) {
        this.shippingAgentServiceCode = shippingAgentServiceCode;
    }

    public Boolean getAllowItemChargeAssignment() {
        return allowItemChargeAssignment;
    }

    public void setAllowItemChargeAssignment(Boolean allowItemChargeAssignment) {
        this.allowItemChargeAssignment = allowItemChargeAssignment;
    }

    public Double getReturnQtyToReceive() {
        return returnQtyToReceive;
    }

    public void setReturnQtyToReceive(Double returnQtyToReceive) {
        this.returnQtyToReceive = returnQtyToReceive;
    }

    public Double getReturnQtyToReceiveBase() {
        return returnQtyToReceiveBase;
    }

    public void setReturnQtyToReceiveBase(Double returnQtyToReceiveBase) {
        this.returnQtyToReceiveBase = returnQtyToReceiveBase;
    }

    public Double getReturnQtyRcdNotInvd() {
        return returnQtyRcdNotInvd;
    }

    public void setReturnQtyRcdNotInvd(Double returnQtyRcdNotInvd) {
        this.returnQtyRcdNotInvd = returnQtyRcdNotInvd;
    }

    public Double getRetQtyRcdNotInvdBase() {
        return retQtyRcdNotInvdBase;
    }

    public void setRetQtyRcdNotInvdBase(Double retQtyRcdNotInvdBase) {
        this.retQtyRcdNotInvdBase = retQtyRcdNotInvdBase;
    }

    public Double getReturnRcdNotInvd() {
        return returnRcdNotInvd;
    }

    public void setReturnRcdNotInvd(Double returnRcdNotInvd) {
        this.returnRcdNotInvd = returnRcdNotInvd;
    }

    public Double getReturnRcdNotInvdLcy() {
        return returnRcdNotInvdLcy;
    }

    public void setReturnRcdNotInvdLcy(Double returnRcdNotInvdLcy) {
        this.returnRcdNotInvdLcy = returnRcdNotInvdLcy;
    }

    public Double getReturnQtyReceived() {
        return returnQtyReceived;
    }

    public void setReturnQtyReceived(Double returnQtyReceived) {
        this.returnQtyReceived = returnQtyReceived;
    }

    public Double getReturnQtyReceivedBase() {
        return returnQtyReceivedBase;
    }

    public void setReturnQtyReceivedBase(Double returnQtyReceivedBase) {
        this.returnQtyReceivedBase = returnQtyReceivedBase;
    }

    public Integer getApplFromItemEntry() {
        return applFromItemEntry;
    }

    public void setApplFromItemEntry(Integer applFromItemEntry) {
        this.applFromItemEntry = applFromItemEntry;
    }

    public String getBomItemNo() {
        return bomItemNo;
    }

    public void setBomItemNo(String bomItemNo) {
        this.bomItemNo = bomItemNo;
    }

    public String getReturnReceiptNo() {
        return returnReceiptNo;
    }

    public void setReturnReceiptNo(String returnReceiptNo) {
        this.returnReceiptNo = returnReceiptNo;
    }

    public Integer getReturnReceiptLineNo() {
        return returnReceiptLineNo;
    }

    public void setReturnReceiptLineNo(Integer returnReceiptLineNo) {
        this.returnReceiptLineNo = returnReceiptLineNo;
    }

    public String getReturnReasonCode() {
        return returnReasonCode;
    }

    public void setReturnReasonCode(String returnReasonCode) {
        this.returnReasonCode = returnReasonCode;
    }

    public Boolean getAllowLineDisc() {
        return allowLineDisc;
    }

    public void setAllowLineDisc(Boolean allowLineDisc) {
        this.allowLineDisc = allowLineDisc;
    }

    public String getCustomerDiscGroup() {
        return customerDiscGroup;
    }

    public void setCustomerDiscGroup(String customerDiscGroup) {
        this.customerDiscGroup = customerDiscGroup;
    }

    public String getReference2() {
        return reference2;
    }

    public void setReference2(String reference2) {
        this.reference2 = reference2;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getChargeTypeCode() {
        return chargeTypeCode;
    }

    public void setChargeTypeCode(String chargeTypeCode) {
        this.chargeTypeCode = chargeTypeCode;
    }

    public String getChargeCode() {
        return chargeCode;
    }

    public void setChargeCode(String chargeCode) {
        this.chargeCode = chargeCode;
    }

    public String getRegDescription() {
        return regDescription;
    }

    public void setRegDescription(String regDescription) {
        this.regDescription = regDescription;
    }

    public Double getRegAmount() {
        return regAmount;
    }

    public void setRegAmount(Double regAmount) {
        this.regAmount = regAmount;
    }

    public String getGlAccountNo() {
        return glAccountNo;
    }

    public void setGlAccountNo(String glAccountNo) {
        this.glAccountNo = glAccountNo;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getIcao() {
        return icao;
    }

    public void setIcao(String icao) {
        this.icao = icao;
    }

    public Integer getMtow() {
        return mtow;
    }

    public void setMtow(Integer mtow) {
        this.mtow = mtow;
    }

    public String getWake() {
        return wake;
    }

    public void setWake(String wake) {
        this.wake = wake;
    }

    public Boolean getBands() {
        return bands;
    }

    public void setBands(Boolean bands) {
        this.bands = bands;
    }

    public String getChargeBands() {
        return chargeBands;
    }

    public void setChargeBands(String chargeBands) {
        this.chargeBands = chargeBands;
    }

    public Integer getBandNo() {
        return bandNo;
    }

    public void setBandNo(Integer bandNo) {
        this.bandNo = bandNo;
    }

    public Integer getChargeOption() {
        return chargeOption;
    }

    public void setChargeOption(Integer chargeOption) {
        this.chargeOption = chargeOption;
    }

    public Integer getMtom() {
        return mtom;
    }

    public void setMtom(Integer mtom) {
        this.mtom = mtom;
    }

    public String getBasedOnCharge() {
        return basedOnCharge;
    }

    public void setBasedOnCharge(String basedOnCharge) {
        this.basedOnCharge = basedOnCharge;
    }

    public String getPageDocument() {
        return pageDocument;
    }

    public void setPageDocument(String pageDocument) {
        this.pageDocument = pageDocument;
    }

    public String getBandName() {
        return bandName;
    }

    public void setBandName(String bandName) {
        this.bandName = bandName;
    }

    public String getRegMark() {
        return regMark;
    }

    public void setRegMark(String regMark) {
        this.regMark = regMark;
    }

    public Double getExaminersFee() {
        return examinersFee;
    }

    public void setExaminersFee(Double examinersFee) {
        this.examinersFee = examinersFee;
    }

    public Integer getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(Integer upperLimit) {
        this.upperLimit = upperLimit;
    }

    public Integer getDifference() {
        return difference;
    }

    public void setDifference(Integer difference) {
        this.difference = difference;
    }

    public Integer getAdditionalRate() {
        return additionalRate;
    }

    public void setAdditionalRate(Integer additionalRate) {
        this.additionalRate = additionalRate;
    }

    public Double getAmountRate() {
        return amountRate;
    }

    public void setAmountRate(Double amountRate) {
        this.amountRate = amountRate;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getTransactionNo() {
        return transactionNo;
    }

    public void setTransactionNo(Integer transactionNo) {
        this.transactionNo = transactionNo;
    }

    public Double getLeaseValue() {
        return leaseValue;
    }

    public void setLeaseValue(Double leaseValue) {
        this.leaseValue = leaseValue;
    }

    public Double getSparePartsValue() {
        return sparePartsValue;
    }

    public void setSparePartsValue(Double sparePartsValue) {
        this.sparePartsValue = sparePartsValue;
    }

    public Double getTbo() {
        return tbo;
    }

    public void setTbo(Double tbo) {
        this.tbo = tbo;
    }

    public Double getExtensionGranted() {
        return extensionGranted;
    }

    public void setExtensionGranted(Double extensionGranted) {
        this.extensionGranted = extensionGranted;
    }

    public Double getCurrentOperationHrs() {
        return currentOperationHrs;
    }

    public void setCurrentOperationHrs(Double currentOperationHrs) {
        this.currentOperationHrs = currentOperationHrs;
    }

    public Boolean getAatisPayment() {
        return aatisPayment;
    }

    public void setAatisPayment(Boolean aatisPayment) {
        this.aatisPayment = aatisPayment;
    }

    public LocalDateTime getCoaStartDate() {
        return coaStartDate;
    }

    public void setCoaStartDate(LocalDateTime coaStartDate) {
        this.coaStartDate = coaStartDate;
    }

    public LocalDateTime getCoaEndDate() {
        return coaEndDate;
    }

    public void setCoaEndDate(LocalDateTime coaEndDate) {
        this.coaEndDate = coaEndDate;
    }

    public Boolean getAnnualAnsCharge() {
        return annualAnsCharge;
    }

    public void setAnnualAnsCharge(Boolean annualAnsCharge) {
        this.annualAnsCharge = annualAnsCharge;
    }

    public Boolean getAttracted() {
        return attracted;
    }

    public void setAttracted(Boolean attracted) {
        this.attracted = attracted;
    }

    public Boolean getcOfACharge() {
        return cOfACharge;
    }

    public void setcOfACharge(Boolean cOfACharge) {
        this.cOfACharge = cOfACharge;
    }
}
