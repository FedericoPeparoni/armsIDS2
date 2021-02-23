package ca.ids.abms.modules.reports2.invoices.aviation;

import ca.ids.abms.config.error.*;
import ca.ids.abms.config.security.SecurityUtils;
import ca.ids.abms.modules.billingcenters.BillingCenter;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.FlightMovementMapper;
import ca.ids.abms.modules.flightmovements.FlightMovementService;
import ca.ids.abms.modules.flightmovements.FlightMovementViewModel;
import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementStatus;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementBuilderException;
import ca.ids.abms.modules.flightmovementsbuilder.utility.KCAAFlightUtility;
import ca.ids.abms.modules.reports2.common.*;
import ca.ids.abms.modules.usereventlogs.UserEventLogService;
import ca.ids.abms.modules.users.User;
import ca.ids.abms.modules.users.UserService;
import ca.ids.abms.plugins.kcaa.aatis.modules.permitnumber.KcaaAatisPermitNumber;
import ca.ids.abms.util.StringUtils;
import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Preconditions;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = {"api/reports2/aviation-invoice", "api/reports/aviation-invoice"})
@SuppressWarnings({"unused", "WeakerAccess"})
public class AviationInvoiceController extends ReportControllerBase {

    private static final Logger LOG = LoggerFactory.getLogger(AviationInvoiceController.class);

    private final AviationInvoiceService aviationInvoiceService;
    private final FlightMovementMapper flightMovementMapper;
    private final FlightMovementService flightMovementService;
    private final ReportHelper reportHelper;
    private final UserEventLogService userEventLogService;
    private final UserService userService;
    private final KCAAFlightUtility kcaaFlightUtility;

    public AviationInvoiceController(final AviationInvoiceService aviationInvoiceService,
                                     final FlightMovementMapper flightMovementMapper,
                                     final FlightMovementService flightMovementService,
                                     final ReportHelper reportHelper,
                                     final UserEventLogService userEventLogService,
                                     final UserService userService,
                                     final KCAAFlightUtility kcaaFlightUtility) {
        this.aviationInvoiceService = aviationInvoiceService;
        this.flightMovementMapper = flightMovementMapper;
        this.flightMovementService = flightMovementService;
        this.reportHelper = reportHelper;
        this.userEventLogService = userEventLogService;
        this.userService = userService;
        this.kcaaFlightUtility = kcaaFlightUtility;
    }

    /**
     *
     * Create aviation (non-IATA) invoice(s) for the given billing period.
     *
     * <H2>Usage</h2>
     *
     * <code><b>
     *      <pre>{GET,POST} /api/reports/aviation-invoice?param=value...</pre>
     * </b></code>
     *
     * <b>WARNING</b>: the GET method is deprecated, please use POST
     *
     * <p>
     * @param userBillingCenterOnly  - flights by user's billing centre or all flights (true/false)? optional
     * @param format                 - output format: pdf, xlsx, docx, csv, txt, xml, json; optional; defaults to pdf.
     * @param preview                - preview or commit (0/1)? optional; defaults to 0 (false). When preview is set:
     *                                  <ul>
     *                                          <li>the report will contain a "fake" invoice number (XXXXXX)
     *                                      <li>all database changes will be rolled back at the end
     *                                      <li>output will include records only from accounts that match accountIdList (see below)
     *                                  </ul>
     * @param aviationInvoicePayload - list of account IDs to be included in the report. This parameter is used <b>only</b> when
     *                                 <code>preview</code> is set to 1, and will be ignored otherwise, i.e., in the normal (non-preview)
     *                                 mode the report will include all relevant records.
     * @param billingInterval        - billing period monthly/weekly; optional
     * @param startDate              - start date for billing period; optional
     * @param endDateInclusive       - end date (inclusive) for billing period; optional
     *
     * @return the invoice document in the requested format. The response entity will have "Content-Type" and "Content-Disposition"
     *         (i.e., the suggested file name) headers set as necessary. The returned document may contain multiple invoices on separate
     *         pages (one invoice per account).
     */
    @Timed
    @RequestMapping (method = { RequestMethod.GET, RequestMethod.POST })
    @Transactional
    public ResponseEntity<ByteArrayResource> createAviationInvoice(
        @RequestParam final Boolean userBillingCenterOnly,
        @RequestParam final ReportFormat format,
        @RequestParam final Boolean preview,
        @RequestParam final Integer flightCategory,
        @RequestParam BillingInterval billingInterval,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime endDateInclusive,
        @RequestBody(required = false) final AviationInvoicePayload aviationInvoicePayload,
        HttpServletRequest request
    ) {
        LOG.debug("REST request to create Aviation Invoice");

        final String ipAddress = userEventLogService.getIpAddressFromRequest(request);
        final List<Integer> accountIdList = aviationInvoicePayload != null
            ? aviationInvoicePayload.getAccountIdList() : null;
        final User currentUser = userService.getUserByLogin (SecurityUtils.getCurrentUserLogin());
        if (userBillingCenterOnly==null) {
            throw new CustomParametrizedException("Please Select Flights");
        }
        return doCreateBinaryResponse(preview, isPreview -> aviationInvoiceService.createCombinedMonthlyInvoice(
            billingInterval, startDate, endDateInclusive, userBillingCenterOnly, format, accountIdList, isPreview,
            ipAddress, flightCategory, currentUser)
        );
    }

    /**
     * Create aviation (non-IATA) invoice for the given account and a list of flights.
     *
     * <H2>Usage</h2>
     *
     * <code><b>
     *      <pre>{GET,POST} /api/reports/aviation-invoice?accountId=NNN,param=value...</pre>
     * </b></code>
     *
     * <b>WARNING</b>: the GET method is deprecated, please use POST
     *
     * <p>
     * @param format         - output format: pdf, xlsx, docx, csv, txt, xml, json; optional; defaults to pdf.
     * @param preview        - preview or commit (0/1)? optional; defaults to 0 (false). When preview is set:
     *                         <ul>
     *                           <li>the report will contain a "fake" invoice number (XXXXXX)
     *                           <li>all database changes will be rolled back at the end
     *                           <li>output will include records only from accounts that match accountIdList (see below)
     *                         </ul>
     * @param accountId       ID of the account for the invoice (mandatory).
     * @param flightIdList    list of flight IDs to be included in the report. If this parameter is omitted, then all
     *                        PENDING flights for the specified account ID will be included in the invoice. If a flight
     *                        ID list is provided (even an empty one), then the invoice will include only the flights
     *                        specified in the flight ID list, minus flights that don't exist, don't belong to the account,
     *                        or not in the pending state.
     * @param payload         List of newly created flight movements
     * @return the invoice document in the requested format. The response entity will have "Content-Type" and "Content-Disposition"
     *         (i.e., the suggested file name) headers set as necessary.
     */
    @Timed
    @RequestMapping(params = { "accountId" }, method = { RequestMethod.GET, RequestMethod.POST })
    @Transactional
    public ResponseEntity<ByteArrayResource> createPosOrFmInvoice(@RequestParam(required = false) final ReportFormat format,
                                                                  @RequestParam(required = false) final Boolean preview,
                                                                  @RequestParam final Integer accountId,
                                                                  @RequestParam(required = false) final Integer flightCategory,
                                                                  @RequestParam(required = false) final List <Integer> flightIdList,
                                                                  @RequestParam(required = false, defaultValue = "true") final Boolean pointOfSale,
                                                                  @RequestParam(required = false) final String invoiceCurrency,
                                                                  @RequestBody(required = false) final AviationInvoicePayload payload,
                                                                  HttpServletRequest request) {

        if (pointOfSale) {
            LOG.debug("REST request to create an aviation invoice from Point Of Sale");
        } else {
            LOG.debug("REST request to create an aviation invoice from Flight Movements");
        }

        // Get the IP address from the request, for invoice logging purposes
        String ipAddress = userEventLogService.getIpAddressFromRequest(request);

        return doCreateBinaryResponse(preview, isPreview -> doCreatePosOrFmInvoice(
            format, accountId, flightIdList, payload, isPreview, ipAddress, flightCategory, pointOfSale, invoiceCurrency));
    }

    /**
     * Create aviation (non-IATA) invoice for the given account and a list of flights; and simultaneously pay for it.
     *
     * <H2>Usage</h2>
     *
     * <code><b><pre>
     *      POST /api/reports/aviation-invoice?pay=1&accountId=NNN,param=value...
     *      {
     *          amount: 123.45,                          // required; invoice amount
     *
     *          currency: { ... },                       // optional; currency of the account
     *
     *          paymentMechanism: "cash",                // required; picked by user from drop-down, one of:
     *                                                   //   cash
     *                                                   //   credit
     *                                                   //   debit
     *                                                   //   cheque
     *                                                   //   wire
     *
     *          description: "some description",         // required; entered by user
     *
     *          paymentReferenceNumber: "some ref num",  // required; entered by user
     *      }
     * </pre></b></code>
     *
     * Request body should contain payment information; see class {@link InvoicePaymentParameters}
     *
     * <p>
     * @param accountId      - ID of the account for the invoice (mandatory).
     * @param format         - output format: pdf, xlsx, docx, csv, txt, xml, json; optional; defaults to pdf.
     * @param preview        - preview or commit (0/1)? optional; defaults to 0 (false). When preview is set:
     *                         <ul>
     *                           <li>the report will contain a "fake" invoice number (XXXXXX)
     *                           <li>all database changes will be rolled back at the end
     *                         </ul>
     * @param flightIdList    - list of flight IDs to be included in the report. If this parameter is omitted, then all
     *                        PENDING flights for the specified account ID will be included in the invoice. If a flight
     *                        ID list is provided (even an empty one), then the invoice will include only the flights
     *                        specified in the flight ID list, minus flights that don't exist, don't belong to the account,
     *                        or not in the pending state.
     * @param payload         - list of newly created Flight movements and the payment information
     *
     * @return the invoice document in the requested format. The response entity will have "Content-Type" and "Content-Disposition"
     *         (i.e., the suggested file name) headers set as necessary.
     */
    @Timed
    @PostMapping(params = { "accountId", "pay=1" })
    @SuppressWarnings("squid:S00107")
    @Transactional
    public ResponseEntity<ByteArrayResource> createPosInvoiceAndPay(@RequestParam(required = false) final ReportFormat format,
                                                                    @RequestParam(required = false) final Boolean preview,
                                                                    @RequestParam final Integer accountId,
                                                                    @RequestParam(required = false) final Integer flightCategory,
                                                                    @RequestParam(required = false) final List <Integer> flightIdList,
                                                                    @RequestBody(required = false) final AviationInvoicePayload payload,
                                                                    @RequestParam(required = false) final String invoiceCurrency,
                                                                    HttpServletRequest request) {

        LOG.debug("REST request to generate and pay an invoice from Point Of Sale");

        // get the IP address from the request, for invoice logging purposes
        String ipAddress = userEventLogService.getIpAddressFromRequest(request);

        // this should never happen from abms web application
        Preconditions.checkArgument(payload != null, "Payload is null");

        return doCreateBinaryResponse(preview, isPreview -> doCreatePosOrFmInvoice(
            format, accountId, flightIdList, payload, isPreview, ipAddress, flightCategory, true, invoiceCurrency));
    }

    /**
     * Get All non-invoiced flights for the given account which belong to the Billing Center associated with the user
     *
     * <H2>Usage</h2>
     *
     * <code><b>
     *      <pre>GET /api/reports/aviation-invoice/non-invoiced-flights-for-account?accountId=NNN,page=NNN...</pre>
     * </b></code>
     *
     * <p>
     * These are ALL non-invoiced flights including PENDING flights that could be included in the invoice created by {@link #createAviationInvoice}
     * and INCOMPLETE and CANCELED flights.
     *
     * The UI should call this when displaying flights for selection in POS aviation invoice form and similar.
     * <p>
     * This API supports paging.
     *
     * @param accountId  - the account
     */
    @Timed
    @GetMapping(value = "/non-invoiced-flights-for-account")
    @Transactional
    public ResponseEntity<Page<FlightMovementViewModel>> getNonInvoicedFlights(
        @RequestParam final Integer accountId,
        final boolean userBillingCenterOnly,
        final Integer flightCategory,
        @SortDefault.SortDefaults({
            @SortDefault(sort = {"billingDate"}, direction=Sort.Direction.DESC),
            @SortDefault(sort = {"flightId"}, direction = Sort.Direction.DESC),
            @SortDefault(sort = {"id"}, direction = Sort.Direction.DESC)
        }) final Pageable pageable
    ) {
        LOG.debug("REST request to get non invoiced flight movements : accountId={}, categoryId={}, userBillingCenterOnly={}",
            accountId, flightCategory, userBillingCenterOnly);
        return do_createResponse (
            ()->{
                final Page <FlightMovement> rawPage =
                    aviationInvoiceService.getAllFlightMovementsForAccountInvoice(accountId, userBillingCenterOnly, flightCategory, pageable);

                List<FlightMovementViewModel> flightMovements = flightMovementMapper.toViewModel(rawPage);

                // only used when organization is KCAA
                kcaaFlightUtility.setAdhocChargeRequiredForFlightMovements(flightMovements);

                final PageImpl <FlightMovementViewModel> page = new PageImpl <> (flightMovements, pageable, rawPage.getTotalElements());
                page.getSort();
                return page;
            }
        );
    }

    /**
     * Validate single newly created Flight Movement
     * Object is validated and the user gets back the result including resolution errors
     * Transaction is rolled-back, so the record is never saved in the DB.
     *
     * @param invoiceFlights contains temporary flights and current POS flight
     * @param accountId flight movement account
     *
     * @return FlightMovementViewModel of created flight movement
     *
     * <H2>Usage</h2>
     *
     * <code><b>
     *      <pre>POST /api/aviation-invoice/validate-flight?forInvoice=true&accountId=2</pre>
     *      {
     *        // FlightMovementViewModel
     *        "id": null,
     *        "actual_departure_time": null,
     *        "actual_mtow": 62.28,
     *         "aircraft_type": "A012",
     *         "arrival_ad": "FBFT",
     *         ................
     *
     *     }
     * </pre></b></code>
     */
    @PostMapping(value = "/validate-flight")
    @Transactional
    public ResponseEntity<FlightMovementViewModel> validateNewFlight(@RequestBody AviationInvoiceValidate invoiceFlights,
                                                                     @RequestParam(value = "accountId") final Integer accountId) {
        FlightMovementViewModel flight = invoiceFlights.getFlight();
        FlightMovementViewModel[] temporaryFlights = invoiceFlights.getTemporaryFlights();

        LOG.debug("REST request to validate a new flight from Point Of Sale");

        // records are always rolled back
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

        if (flight == null) {
            return ResponseEntity.badRequest().body(null);
        }

        if (temporaryFlights != null && temporaryFlights.length > 0) {
            for(FlightMovementViewModel tempFlight : temporaryFlights) {
                if(flightMovementService.isDuplicate(flight,tempFlight)) {
                        throw new CustomParametrizedException(
                                String.format("Flight %s from %s to %s not created because it already exists",
                                        flight.getFlightId(), flight.getDestAd(), flight.getDepAd()));
                    }
            }
        }

        FlightMovementViewModel flightMovementViewModel = this.doCreateNewFlight(flight);

        // only used when organization is KCAA
        kcaaFlightUtility.setAdhocChargeRequiredForFlightMovements(flightMovementViewModel);

        return ResponseEntity.ok().body(flightMovementViewModel);
    }


    // ----------------- private -------------------

    private void updateFlightMovementWithTaspCharge(List<KcaaAatisPermitNumber> invoicePermits) {
        if (invoicePermits == null || invoicePermits.isEmpty())
            return;

        for (KcaaAatisPermitNumber permit: invoicePermits) {
            FlightMovement flightMovement = flightMovementService.findFlightMovementById(permit.getFlightMovement().getId());
            flightMovement.setTaspCharge(permit.getAdhocTotalFeePaymentAmount());
            flightMovementService.doPersistFlightMovement(flightMovement);
        }
    }

    private void checkFlightMovementInInvoicePermits(List<KcaaAatisPermitNumber> invoicePermits, Integer oldId, Integer newId) {
        if (invoicePermits == null || invoicePermits.isEmpty() || oldId == null || newId == null)
            return;

        invoicePermits.stream().filter(p -> p.getFlightMovement().getId().equals(oldId)).findFirst().
            ifPresent(permit -> permit.setFlightMovement(flightMovementService.findFlightMovementById(newId)));
    }

    private FlightMovement doInsertNewFlight(FlightMovementViewModel flightViewModel) {

        FlightMovement result = null;

        if(flightViewModel != null){
            final FlightMovement flightMovement = flightMovementMapper.toModel(flightViewModel);

            try {
                result = flightMovementService.createFlightMovementFromUI(flightMovement, Boolean.TRUE);
            } catch (FlightMovementBuilderException e) {
                if (StringUtils.isBlank(flightMovement.getEstimatedElapsedTime())){
                    throw ExceptionFactory.getInvalidDataException(ErrorConstants.ERR_CIRCULAR_ROUTE, FlightMovement.class );
                }

                if (StringUtils.isBlank(flightMovement.getCruisingSpeedOrMachNumber())){
                    throw ExceptionFactory.getInvalidDataException(ErrorConstants.ERR_CIRCULAR_ROUTE, FlightMovement.class);
                }
                throw new CustomParametrizedException(e.getFlightMovementBuilderIssue().toValue(), e.getLocalizedMessage());
            }
        }
        return result;
    }

    private FlightMovementViewModel doCreateNewFlight(FlightMovementViewModel flight) {

        if (flight == null) {
            return null;
        }

        final FlightMovement result = this.doInsertNewFlight(flight);

        if (result == null) {
            throw new CustomParametrizedException(
                String.format("Flight %s from %s to %s not created",
                    flight.getFlightId(), flight.getDestAd(), flight.getDepAd()));
        } else if (result.getStatus() != FlightMovementStatus.PENDING) {
            throw new CustomParametrizedException(
                String.format("Flight %s from %s to %s has status [%s]. Resolution errors: %s",
                    result.getFlightId(), result.getDestAd(), result.getDepAd(),result.getStatus().toValue(),
                    result.getResolutionErrors()));
        }

        // flight must belong to the Billing Center associated with the current user
        BillingCenter userBC = reportHelper.getBillingCenterOfCurrentUser();

        // get the view model for the flight movement
        FlightMovementViewModel fmViewModel = flightMovementMapper.toViewModel(result);

        if (userBC != null) {
            final BillingCenter resultBillingCenter = result.getBillingCenter();
            if (!result.isOTHER() && resultBillingCenter != null && !(resultBillingCenter.getId().equals(userBC.getId()))) {
                LOG.debug("Flight {} from {} to {} doesn't belong to the Billing Centre {}", result.getFlightId(), result.getDepAd(), result.getDestAd(), userBC.getName());
            }
        }

        return fmViewModel;
    }

    @SuppressWarnings("squid:S00107")
    private ReportDocument doCreatePosOrFmInvoice(final ReportFormat format,
                                                  final Integer accountId,
                                                  final List<Integer> flightIdList,
                                                  final AviationInvoicePayload payload,
                                                  final Boolean isPreview,
                                                  final String ipAddress,
                                                  final Integer flightCategory,
                                                  final Boolean pointOfSale,
                                                  final String invoiceCurrency) {

        final List<Integer> idList = flightIdList == null ? new ArrayList<>() : flightIdList;
        final List<FlightMovementViewModel> flights = payload == null ? null : payload.getFlightItems();
        final List<KcaaAatisPermitNumber> permits = payload == null ? null : payload.getInvoicePermits();
        final InvoicePaymentParameters payment = payload == null ? null : payload.getPayment();

        // validate and insert new flights
        if (flights != null && !flights.isEmpty()) {
            for (FlightMovementViewModel flight : flights) {

                try {
                    // insert flight movement and get the id
                    Integer id = flight.getId();
                    FlightMovementViewModel result = this.doCreateNewFlight(flight);
                    if (result != null && result.getId() != null) {
                        idList.add(result.getId());
                        checkFlightMovementInInvoicePermits(permits, id, result.getId());
                    }
                } catch (CustomParametrizedException e) {
                    throw e;
                } catch (final Exception e) {
                    throw new RejectedException(ExceptionFactory.resolveManagedErrors(e));
                }
            }
        }

        // update tasp charge for flight movements with AATIS Permit Number
        updateFlightMovementWithTaspCharge(permits);

        return aviationInvoiceService.createPosOrFmInvoice(format, accountId, idList, payment, isPreview, ipAddress,
            flightCategory, permits, pointOfSale, invoiceCurrency);
    }     
}
