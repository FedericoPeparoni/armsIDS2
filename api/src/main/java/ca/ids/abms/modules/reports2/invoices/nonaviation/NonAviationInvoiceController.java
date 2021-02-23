package ca.ids.abms.modules.reports2.invoices.nonaviation;

import java.util.List;

import ca.ids.abms.config.error.ErrorDTO;
import ca.ids.abms.config.error.RejectedReasons;
import com.codahale.metrics.annotation.Timed;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.ids.abms.modules.billings.InvoiceLineItemViewModel;
import ca.ids.abms.modules.charges.ServiceChargeCatalogueViewModel;
import ca.ids.abms.modules.reports2.common.InvoicePaymentParameters;
import ca.ids.abms.modules.reports2.common.ReportControllerBase;
import ca.ids.abms.modules.reports2.common.ReportFormat;
import ca.ids.abms.modules.usereventlogs.UserEventLogService;

@RestController
@RequestMapping(value = {"api/reports2/non-aviation-invoice", "api/reports/non-aviation-invoice"})
@SuppressWarnings({"unused", "WeakerAccess"})
public class NonAviationInvoiceController extends ReportControllerBase {

    private static final Logger LOG = LoggerFactory.getLogger(NonAviationInvoiceController.class);

    private final NonAviationInvoiceService nonAviationInvoiceService;
    private final UserEventLogService userEventLogService;

    public NonAviationInvoiceController(final NonAviationInvoiceService nonAviationInvoiceService, final UserEventLogService userEventLogService) {
        this.nonAviationInvoiceService = nonAviationInvoiceService;
        this.userEventLogService = userEventLogService;
    }

    /**
     * Create non-aviation invoice for the given account and a list of line items.
     *
     * <H2>Usage</h2>
     *
     * <code><b>
     *      <pre>
     *            POST /api/reports/non-aviation-invoice/monthly?accountId=NNN&year=2017&month=3&param=value...
     *            [
     *              {
     *                aerodrome: { ... },                    // aerodrome model (picked by user from list)
     *                recurring_charge: { ... },             // recurring charge model (picked by user from list)
     *                amount: 123.45,                        // charge amount -- calculated by front-end
     *              },
     *              ...
     *            ]
     *      </pre>
     * </b></code>
     *
     * <p>
     * @param accountId      - ID of the account for the invoice (mandatory).
     * @param year           - Year of the invoice
     * @param month          - Month of the invoice
     * @param format         - output format: pdf, xlsx, docx, csv, txt, xml, json; optional; defaults to pdf.
     * @param preview        - preview or commit (0/1)? optional; defaults to 0 (false). When preview is set:
     *                         <ul>
     *                           <li>the report will contain a "fake" invoice number (XXXXXX)
     *                           <li>all database changes will be rolled back at the end
     *                         </ul>
     *
     * <p>Request body should contain the list of line items (see {@link InvoiceLineItemViewModel}).
     *
     * @return the invoice document in the requested format. The response entity will have "Content-Type" and "Content-Disposition"
     *         (i.e., the suggested file name) headers set as necessary.
     */
    @Timed
    @PostMapping(value = "/monthly")
    @Transactional
    public ResponseEntity<ByteArrayResource> createMonthlyInvoice(
        @RequestParam() final Integer accountId,
        @RequestParam() final Integer year,
        @RequestParam() final Integer month,
        @RequestParam(required = false) final ReportFormat format,
        @RequestParam(required = false) final Boolean preview,
        @RequestBody final List <InvoiceLineItemViewModel> invoiceLineItems,
        HttpServletRequest request) {

        String ipAddress = userEventLogService.getIpAddressFromRequest(request);

        if (CollectionUtils.isEmpty(invoiceLineItems)) {
            throw new ErrorDTO.Builder("No charges found for category")
                .appendDetails("There are no charges found for this account using the selected category. " +
                    "Please choose a different charge category or ensure the account is eligible for the " +
                    "selected category.")
                .addRejectedReason(RejectedReasons.NOT_FOUND)
                .buildInvalidDataException();
        }

        return doCreateBinaryResponse(preview, isPreview -> nonAviationInvoiceService.createMonthlyInvoice(
            year, month, format, accountId, invoiceLineItems, isPreview, ipAddress));
    }

    /**
     * Prepare non-aviation invoice line items applicable for the given account and period.
     * This line items should be filled out (with unit amounts, etc), and then re-submitted
     * {@link #createMonthlyInvoice}.
     *
     * <H2>Usage</h2>
     *
     * <code><b>
     *      <pre>
     *            POST /api/reports/non-aviation-invoice/monthly/prepare-line-items?accountId=NNN&param=value...
     *      </pre>
     * </b></code>
     *
     * <p>
     * @param accountId                 - ID of the account for the invoice (mandatory).
     * @param year                      - Year of the invoice
     * @param month                     - Month of the invoice
     * @param externalChargeCategoryId  - ID of the external charge category for the invoice (mandatory)
     */
    @Timed
    @PostMapping(value = "/monthly/prepare-line-items")
    @Transactional
    public ResponseEntity<List<InvoiceLineItemViewModel>> prepareMonthlyLineItems(
        @RequestParam final Integer accountId,
        @RequestParam final Integer year,
        @RequestParam final Integer month,
        @RequestParam(required = false) final Integer externalChargeCategoryId) {

        return do_createResponse(() -> nonAviationInvoiceService.prepareMonthlyLineItems(
            accountId, year, month, externalChargeCategoryId));
    }

    @Timed
    @PostMapping(value = "/monthly/validate-line-item")
    @Transactional
    public ResponseEntity<InvoiceLineItemViewModel> validateMonthlyLineItem(
            @RequestParam final Integer accountId,
            @RequestParam final Integer year,
            @RequestParam final Integer month,
            @RequestBody final InvoiceLineItemViewModel lineItem) {
        return this.do_createResponse(() -> nonAviationInvoiceService.validateMonthlyLineItem(
            accountId, year, month, lineItem));
    }

    /**
     * Create non-aviation invoice for the given account and a list of line items.
     *
     * <H2>Usage</h2>
     *
     * <code><b>
     *      <pre>
     *            POST /api/reports/non-aviation-invoice/pos?accountId=NNN,param=value...
     *            [
     *              {
     *                aerodrome: { ... },                    // aerodrome model (picked by user from list)
     *                service_charge_catalogue: { ... },     // service charge model (picked by user from list)
     *                recurring_charge: { ... },             // recurring charge model (picked by user from list)
     *                amount: 123.45,                        // charge amount -- calculated by front-end
     *              },
     *              ...
     *            ]
     *      </pre>
     * </b></code>
     *
     * <p>
     * @param accountId      - ID of the account for the invoice (mandatory).
     * @param format         - output format: pdf, xlsx, docx, csv, txt, xml, json; optional; defaults to pdf.
     * @param preview        - preview or commit (0/1)? optional; defaults to 0 (false). When preview is set:
     *                         <ul>
     *                           <li>the report will contain a "fake" invoice number (XXXXXX)
     *                           <li>all database changes will be rolled back at the end
     *                         </ul>
     *
     * <p>Request body should contain the list of line items (see {@link InvoiceLineItemViewModel}).
     *
     * @return the invoice document in the requested format. The response entity will have "Content-Type" and "Content-Disposition"
     *         (i.e., the suggested file name) headers set as necessary.
     */
    @Timed
    @PostMapping(value = "/pos")
    @Transactional
    public ResponseEntity<ByteArrayResource> createPosInvoice(@RequestParam(required = false) final ReportFormat format,
                                                              @RequestParam(required = false) final Boolean preview,
                                                              @RequestParam(required = false, defaultValue = "false") final Boolean proforma,
                                                              @RequestParam final Integer accountId,
                                                              @RequestParam(required = false) final String invoiceCurrency,
                                                              @RequestBody NonAviationInvoicePayload payload,
                                                              HttpServletRequest request) {
        LOG.debug("REST request to create a{} non-aviation invoice from Point Of Sale", proforma ? " proforma" : "");

        final String ipAddress = userEventLogService.getIpAddressFromRequest(request);
        return doCreateBinaryResponse(preview, isPreview -> nonAviationInvoiceService.createPosInvoice(
            format, accountId, invoiceCurrency, payload.getLineItems(), null, isPreview, ipAddress, payload.getPermitNumbers(),
            payload.getRequisitionNumbers(), proforma)
        );
    }

    /**
     * Create non-aviation invoice for the given account and a list of line items.
     *
     * <H2>Usage</h2>
     *
     * <code><b>
     *      <pre>POST /api/reports/non-aviation-invoice/pos?pay=1&accountId=NNN,param=value...</pre>
     *      {
     *        // List of line items
     *        line_items: [
     *          {
     *            aerodrome: { ... },                    // aerodrome model (picked by user from list)
     *            service_charge_catalogue: { ... },     // service charge model (picked by user from list)
     *            recurring_charge: { ... },             // recurring charge model (picked by user from list)
     *            amount: 123.45,                        // charge amount -- calculated by front-end
     *          },
     *          ...
     *        ],
     *
     *        // Payment information (same as in aviation invoice)
     *        payment: {
     *          amount: 123.45,                          // required; invoice amount
     *          currency: { ... },                       // optional; currency of the account
     *          payment_mechanism: "cash",               // required; picked by user from drop-down, one of:
     *                                                   //   cash
     *                                                   //   credit
     *                                                   //   debit
     *                                                   //   cheque
     *                                                   //   wire
     *          description: "some description",         // required; entered by user
     *          payment_reference_number: "some ref num",  // required; entered by user
     *       }
     *     }
     * </b></code>
     *
     * Request body should contain 2 keys, the list of line items ({@link InvoiceLineItemViewModel})./
     * and payment information {@link InvoicePaymentParameters}
     *
     * <p>
     * @param accountId      - ID of the account for the invoice (mandatory).
     * @param format         - output format: pdf, xlsx, docx, csv, txt, xml, json; optional; defaults to pdf.
     * @param preview        - preview or commit (0/1)? optional; defaults to 0 (false). When preview is set:
     *                         <ul>
     *                           <li>the report will contain a "fake" invoice number (XXXXXX)
     *                           <li>all database changes will be rolled back at the end
     *                         </ul>
     *
     * @return the invoice document in the requested format. The response entity will have "Content-Type" and "Content-Disposition"
     *         (i.e., the suggested file name) headers set as necessary.
     */
    @Timed
    @PostMapping(value = "/pos", params = { "pay=1" })
    @Transactional
    public ResponseEntity<ByteArrayResource> createPosInvoiceAndPay(@RequestParam(required = false) final ReportFormat format,
                                                                    @RequestParam final Boolean preview,
                                                                    @RequestParam(required = false, defaultValue = "false") final Boolean proforma,
                                                                    @RequestParam final Integer accountId,
                                                                    @RequestParam(required = false) final String invoiceCurrency,
                                                                    @RequestBody NonAviationInvoicePayload payload,
                                                                    HttpServletRequest request) {
        LOG.debug("REST request to generate and pay a{} non-aviation invoice from Point Of Sale",  proforma ? " proforma" : "");

        final String ipAddress = userEventLogService.getIpAddressFromRequest(request);
        return doCreateBinaryResponse(preview, isPreview -> nonAviationInvoiceService.createPosInvoice(
            format, accountId, invoiceCurrency, payload.getLineItems(), payload.getPayment(), isPreview, ipAddress,
            payload.getPermitNumbers(), payload.getRequisitionNumbers(), proforma));
    }

    @Timed
    @PostMapping(value = "/pos/validate-line-item")
    @Transactional
    public ResponseEntity<InvoiceLineItemViewModel> validatePosLineItem(@RequestParam final Integer accountId,
                                                                        @RequestParam(required = false) final String invoiceCurrency,
                                                                        @RequestBody final InvoiceLineItemViewModel lineItem) {
        LOG.debug("REST request to validate Line Item from Point Of Sale for an account {} and lineItem {}", accountId, lineItem);
        return this.do_createResponse(() -> this.nonAviationInvoiceService.validatePosLineItem(
            accountId, lineItem, invoiceCurrency));
    }

    /**
     * Returns the list of service charges applicable to the POS non-aviation invoice.
     * The return values may be used to allow users to pick a service charge when creating a line item.
     *
     * <H2>Usage</h2>
     *
     * <code><b>
     *      <pre>
     *            GET /api/reports/non-aviation-invoice/pos/service-charges
     *      </pre>
     * </b></code>
     *
     */
    @Timed
    @GetMapping(value = "/pos/service-charges")
    @Transactional
    public ResponseEntity<List<ServiceChargeCatalogueViewModel>> getApplicablePosServiceCharges() {
        return do_createResponse(nonAviationInvoiceService::getApplicablePosServiceCharges);
    }
}
