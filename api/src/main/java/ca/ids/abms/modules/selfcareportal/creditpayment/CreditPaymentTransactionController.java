package ca.ids.abms.modules.selfcareportal.creditpayment;

import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.accounts.AccountService;
import ca.ids.abms.modules.common.controllers.AbmsViewController;
import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.currencies.CurrencyService;
import ca.ids.abms.modules.reports2.common.ReportDocumentCreator;
import ca.ids.abms.modules.reports2.common.ReportHelper;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;

import ca.ids.abms.modules.transactions.*;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Scanner;

@RestController
@RequestMapping(CreditPaymentTransactionController.ENDPOINT)
public class CreditPaymentTransactionController extends AbmsViewController<CreditPaymentTransaction, CreditPaymentTransactionViewModel, CreditPaymentTransactionCsvExportModel, Integer> {

    static final String ENDPOINT = "/api/sc-credit-payment";
    private static final String CXPAY = "cxpay";
    private static final String OKAY = "okay";
    private static final String FAIL = "fail";
    private static final String USD = "USD";
    private static final String CREDIT = "credit";
    private String accountId;
    private String amount;
    private String apiKey;
    private String url;

    private static final Logger LOG = LoggerFactory.getLogger(CreditPaymentTransactionController.class);

    private final SystemConfigurationService systemConfigurationService;
    private final TransactionTypeService transactionTypeService;
    private final TransactionService transactionService;
    private final CurrencyService currencyService;
    private final AccountService accountService;

    @SuppressWarnings("squid:S00107")
    CreditPaymentTransactionController(final SystemConfigurationService systemConfigurationService,
                                       final CreditPaymentTransactionMapper creditPaymentTransactionMapper,
                                       final CreditPaymentTransactionService creditPaymentTransactionService,
                                       final ReportDocumentCreator reportDocumentCreator,
                                       final AccountService accountService,
                                       final TransactionTypeService transactionTypeService,
                                       final CurrencyService currencyService,
                                       final TransactionService transactionService) {
        super(ENDPOINT, creditPaymentTransactionMapper, creditPaymentTransactionService, reportDocumentCreator, "Credit_Payments",
            CreditPaymentTransactionCsvExportModel.class);
        this.systemConfigurationService = systemConfigurationService;
        this.accountService = accountService;
        this.transactionTypeService = transactionTypeService;
        this.currencyService = currencyService;
        this.transactionService = transactionService;
    }

    @Override
    @GetMapping
    @PreAuthorize("hasAnyAuthority('self_care_admin','self_care_access')")
    public ResponseEntity<?> getPage(@RequestParam(required = false) final String search,
                                     @SortDefault.SortDefaults({@SortDefault(sort = { "transactionTime" }, direction = Sort.Direction.DESC)}) final Pageable pageable,
                                     @RequestParam(name = "csvExport", required = false) Boolean csvExport) {
        LOG.debug("REST request to get all credit payment transactions with search '{}' for page '{}'", search, pageable);
        return super.doGetPage(search, pageable, csvExport);
    }

    @Override
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('self_care_admin','self_care_access')")
    public ResponseEntity<CreditPaymentTransactionViewModel> getOne(@PathVariable final Integer id) {
        LOG.debug("REST request to a credit payment transaction with id '{}'", id);
        return super.doGetOne(id);
    }

    @PreAuthorize("hasAnyAuthority('self_care_admin','self_care_access')")
    @PostMapping
    public ResponseEntity<String> sendNonSensitiveDataToCreditGateway(@RequestParam final String amount,
                                                                      @RequestParam final String accountId,
                                                                      final HttpServletRequest httpServletRequest) throws IOException {
        LOG.debug("Attempting to send non sensitive data to cxPay");
        this.accountId = accountId;
        this.amount = amount;
        String origin = httpServletRequest.getHeader("origin");

        validateCreditCardConfigurations();

        // build the xml string with minimum 3 values
        // api-key, redirect-url, and amount
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<credit>");
        xmlBuilder.append("<api-key>").append(apiKey);
        xmlBuilder.append("</api-key>");
        xmlBuilder.append("<redirect-url>").append(origin).append("/#/sc-credit-payment");
        xmlBuilder.append("</redirect-url>");
        xmlBuilder.append("<amount>").append(amount);
        xmlBuilder.append("</amount>");
        xmlBuilder.append("</credit>");
        String xmlString = xmlBuilder.toString();

        // post xml string and obtain result
        String xmlStringResponse = postXmlString(xmlString);
        LOG.debug("The returned cxPay formUrl: , {}", xmlStringResponse);
        JSONObject formUrlJson = XML.toJSONObject(xmlStringResponse);

        return new ResponseEntity(formUrlJson.toString(), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('self_care_admin','self_care_access')")
    @PostMapping("/token")
    public ResponseEntity<String> sendTokenIdToCreditGateway(@RequestParam final String token,
                                                             final HttpServletRequest httpServletRequest) throws IOException, URISyntaxException {

        // This function is responsible for Step 3. of the
        // three-step redirect integration of cxPay.
        // It sends the final token-id to the credit gateway

        LOG.debug("Attempting to complete the credit payment transaction with token-id: {}", token);

        validateCreditCardConfigurations();

        // build the xml string with minimum 3 values
        // api-key, redirect-url, and amount
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<complete-action>");
        xmlBuilder.append("<api-key>").append(apiKey);
        xmlBuilder.append("</api-key>");
        xmlBuilder.append("<token-id>").append(token);
        xmlBuilder.append("</token-id>");
        xmlBuilder.append("</complete-action>");
        String xmlString = xmlBuilder.toString();

        // post xml string and obtain result
        String xmlStringResponse = postXmlString(xmlString);
        LOG.debug("Response from cxPay gateway: {}", xmlStringResponse);
        JSONObject jsonResponse = XML.toJSONObject(xmlStringResponse);

        super.doCreate(buildCreditPaymentTransaction(jsonResponse, httpServletRequest));
        return new ResponseEntity(jsonResponse.toString(), HttpStatus.OK);
    }

    private void validateCreditCardConfigurations() {
        boolean configured = systemConfigurationService.getBoolean(SystemConfigurationItemName.CC_PROCESSOR_CONFIGURED);
        String creditCardProcessor = systemConfigurationService.getValue(SystemConfigurationItemName.CC_PROCESSOR);
        url = systemConfigurationService.getValue(SystemConfigurationItemName.CC_PROCESSOR_URL);
        apiKey = systemConfigurationService.getValue(SystemConfigurationItemName.CC_PROCESSOR_PUBLIC_KEY);

        // make sure that credit card system configurations are properly set
        if (!configured || url == null || apiKey == null || !creditCardProcessor.equals(CXPAY)) {
            LOG.debug("Missing or invalid credit card configuration. Configured: {}, Processor: {}, URL {}, Public Key: {}", configured, creditCardProcessor, url, apiKey);
            throw new CreditPaymentTransactionException(ErrorConstants.ERR_CREDIT_PAYMENT_MISCONFIGURATION, new RuntimeException());
        }
    }

    private String postXmlString(String xmlString) throws IOException {
        URL obj = new URL(url);
        URLConnection con = obj.openConnection();

        // allows response
        con.setDoInput(true);
        // sets POST request
        con.setDoOutput(true);
        // cxPay needs XML string
        con.setRequestProperty("Content-Type", "text/xml");

        // make the POST request
        OutputStream os = con.getOutputStream();
        os.write( xmlString.getBytes("utf-8") );
        os.close();

        // convert the input stream result into a string to be returned
        Scanner s = new java.util.Scanner(con.getInputStream()).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    private CreditPaymentTransactionViewModel buildCreditPaymentTransaction(JSONObject responseFromXML,
                                                                            HttpServletRequest originalRequest){

        // get transactionTime
        final ZonedDateTime zdtNow = ZonedDateTime.now (ReportHelper.UTC_ZONE_ID);
        final LocalDateTime ldtNow = zdtNow.toLocalDateTime();

        // get status
        JSONObject responseInJson = responseFromXML.getJSONObject("response");
        String status = responseInJson.getInt("result") == 1 ? OKAY : FAIL;

        // get IP address
        String ipAddress = originalRequest.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = originalRequest.getRemoteAddr();
        }

        // get request
        Account accountFromId = accountService.getOne(Integer.parseInt(accountId));
        String request = "amount: " + amount + ", account: \"" + accountFromId.getName() + "\", apiKey: \"" + apiKey + "\"";

        // build credit payment transaction
        CreditPaymentTransactionViewModel creditPaymentTransactionViewModel  = new CreditPaymentTransactionViewModel();
        creditPaymentTransactionViewModel.setResponse(responseInJson.toString());
        creditPaymentTransactionViewModel.setTransactionTime(ldtNow);
        creditPaymentTransactionViewModel.setRequestorIp(ipAddress);
        creditPaymentTransactionViewModel.setResponseStatus(status);
        creditPaymentTransactionViewModel.setRequest(request);
        creditPaymentTransactionViewModel.setAccount(accountFromId);


        // if the credit payment response is returned as
        // successful, we create a transaction to update the account balance
        if (status.equals(OKAY)) {
            TransactionType transactionType = transactionTypeService.findOneByName(CREDIT);
            Currency usdCurrency = currencyService.findByCurrencyCode(USD);
            String transactionId = Long.toString((responseInJson.getLong("transaction-id")));
            Transaction transaction = new Transaction();
            transaction.setAmount(-Double.parseDouble(amount));
            transaction.setPaymentAmount(-Double.parseDouble((amount)));
            transaction.setAccount(accountFromId);
            transaction.setTransactionDateTime(ldtNow);
            transaction.setDescription("Credit Card Whitelist Payment");
            transaction.setTransactionType(transactionType);
            transaction.setExported(false);
            transaction.setPaymentMechanism(TransactionPaymentMechanism.credit);
            transaction.setCurrency(usdCurrency);
            transaction.setPaymentCurrency(usdCurrency);
            transaction.setPaymentReferenceNumber(transactionId);
            transaction.setPaymentExchangeRate(1d);
            Transaction createdTransaction = transactionService.createCreditTransactionByPayments(transaction);
            if (createdTransaction != null) {
                transactionService.checkWhitelistingRetroactivePayments(accountFromId, null);
            }
        }

        return creditPaymentTransactionViewModel;
    }
}


