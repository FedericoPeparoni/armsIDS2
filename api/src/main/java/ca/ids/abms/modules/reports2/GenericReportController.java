package ca.ids.abms.modules.reports2;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.modules.billings.BillingLedger;
import ca.ids.abms.modules.billings.BillingLedgerService;
import ca.ids.abms.modules.reports2.common.BirtReportCreator;
import ca.ids.abms.modules.reports2.common.ReportFormat;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.modules.transactions.Transaction;
import ca.ids.abms.modules.transactions.TransactionPayment;
import ca.ids.abms.modules.transactions.TransactionPaymentRepository;
import ca.ids.abms.modules.transactions.TransactionService;

import org.apache.commons.lang.StringUtils;
import org.eclipse.birt.report.model.api.DesignFileException;
import org.eclipse.birt.report.model.parser.DesignParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static ca.ids.abms.modules.util.models.DateTimeUtils.convertStringToLocalDateTime;

/**
 * Report controller.
 * <p>
 * An HTTP request such as <code>GET /api/reports/generic/SOME/NAME</code> will load
 * and execute a report definition from the file <code>/etc/abms/api/reports/SOME/NAME.rptdesign</code> in Linux
 * or <code>$PROJECT_DIR/reports/SOME/NAME.rptdesign</code> in Eclipse.
 * <p>
 * <b>WARNING</b>: This controller should be accessed via URL /api/reports/generic, not "/api/reports2/generic"!
 *                 Eventually this package will be moved to ca.ids.abms.modules.reports.
 *
 */
@RestController
@RequestMapping(value = {"api/reports2/generic", "api/reports/generic"})
public class GenericReportController {

    private static final String CONTROLLER_PATH = "/api/reports2/generic";

    private static final Logger LOG = LoggerFactory.getLogger(GenericReportController.class);

    private final BirtReportCreator birtReportCreator;

    private final SystemConfigurationService systemConfigurationService;
    private final TransactionService transactionService;
    private final BillingLedgerService billingLedgerService;
    private final TransactionPaymentRepository transactionPaymentRepository;


    public GenericReportController(final BirtReportCreator birtReportCreator,
    final TransactionService transactionService,
    final BillingLedgerService billingLedgerService,
    final TransactionPaymentRepository transactionPaymentRepository,
    final SystemConfigurationService systemConfigurationService ) {

        this.birtReportCreator = birtReportCreator;
        this.systemConfigurationService = systemConfigurationService;
        this.transactionService = transactionService;
        this.billingLedgerService = billingLedgerService;
        this.transactionPaymentRepository = transactionPaymentRepository;

    }

    // TODO: add permission annotations

    // This will match: /api/reports/generic/{{ organisation }}/path/suffix
    @RequestMapping (value = "/**", method = { RequestMethod.GET, RequestMethod.POST })
    public void getReportPDF (
        final @RequestParam(required = false) Map <String, Object> queryParams,
        final @RequestBody(required = false) Map <String, Object> bodyParams,
        final HttpServletRequest req, final HttpServletResponse response
    ) throws Exception {
        ReportFormat format=ReportFormat.pdf;  //default like before
        //Handle various output formats US65037
        if (queryParams != null && queryParams.containsKey("output_format")) {
            String outputFormat = queryParams.get("output_format").toString().toLowerCase();
            switch(outputFormat) {
                case "docx":
                    format = ReportFormat.docx;
                    break;
                case "xlsx":
                    format = ReportFormat.xlsx;
                    break;
                default:
                    format = ReportFormat.pdf;
                    break;
            }
        }

        // See here: http://stackoverflow.com/questions/3686808/spring-3-requestmapping-get-path-value
        final String partialName = ((String) req.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE))
            .substring(CONTROLLER_PATH.length())
            .replaceAll("^/+", "");

        if (StringUtils.isEmpty(partialName)) {
            throw new CustomParametrizedException("Invalid template name");
        }

        String fileName = Paths.get(partialName).getFileName().toString().concat(format.fileNameSuffix());

        final Map <String, Object> params = new HashMap<>();
        if (bodyParams != null) {
            params.putAll (bodyParams);
        }

        LocalDateTime endDate  = null;
        LocalDateTime startDate = null;


        if (queryParams != null) {

            if (queryParams.containsKey("todate") && queryParams.containsKey("fromdate")){
                endDate = convertStringToLocalDateTime(queryParams.get("todate").toString());
                startDate = convertStringToLocalDateTime(queryParams.get("fromdate").toString());
                if (endDate.isAfter(startDate)) {
                    params.putAll (queryParams);
                }
                else {
                    LOG.debug("Bad request: expiring date is overlapped starting date");
                    throw new CustomParametrizedException(ErrorConstants.ERR_START_END_DATE,
                        new Exception("Start date: " + startDate + "; expiry date:" + endDate));
                }
            }
            else {
                params.putAll (queryParams);
            }
        }

        //new block for checking if evry account has almost one u.t. b4 passing his id to the template
        if(partialName.equals("unified_tax")){
            final List<String> accountIdListToCheck = (Arrays.asList(((String)bodyParams.get("account_id")).replace("{", "").replace("}", "").split(",")));

            //Frist part of sub query t
            final List<Integer> accountIdListChecked = new ArrayList<>();

            Page<Transaction> trPage = transactionService.findAllTransactionsForSelfCareAccounts(null, null,
            accountIdListToCheck , startDate.toLocalDate(), endDate.toLocalDate());

            for(Transaction tr : trPage.getContent()){
                BillingLedger bl = billingLedgerService.findByInvoiceNumber(tr.getPaymentReferenceNumber());

                //with all accounts, start date 2021-04-01, end date 2021-05-31, accountIdListChecked is empty
                if(bl != null && bl.getInvoiceType() != null && bl.getInvoiceType().equals("unified-tax")){
                    accountIdListChecked.add(tr.getAccount().getId());
                }
            }

            //Second part of sub query t
            List<BillingLedger> blList = billingLedgerService.findAllByAccountIds((accountIdListToCheck.stream().map(Integer::parseInt).collect(Collectors.toList())));

            blList.forEach(bl ->{


                    final List<TransactionPayment> tpList = transactionPaymentRepository.findAllByBillingLedgerId(bl.getId());
                    //with all accounts, start date 2021-04-01, end date 2021-05-31, trList is empty
                    final List<Transaction>  trList = tpList.stream().map(TransactionPayment::getTransaction).collect(Collectors.toList());

                     //TODO: missing steps:
                     /*
                        1) check all the Transaction found, and filter each with the params startDate and endDate
                        2) if an account has at least one valid Transaction, add it to the valid account list
                     */

            });

            //TODO: missing steps:
            /*
            1) get a set of unique acoount's ids that have passed the filters
            2) set account_id propriety of bodyParams with the ids found

            */

        }


        try {
        	//EANA:When downloading the report, the file name is in english (Unified Tax.pdf)
        	//EANA has asksed to have it in Spanish
        	final String organisationName = systemConfigurationService.getString(SystemConfigurationItemName.ORGANISATION_NAME, null);
        	if(organisationName.equals("EANA") && partialName.equals("unified_tax")){
                fileName = "Report_TU".concat(format.fileNameSuffix());
            }

            LOG.debug("Trying to generate the report {} with parameters: {}", partialName, params);


            final byte[] document = birtReportCreator.createOtherReport(partialName, params, format);

            if (document != null && document.length > 0) {
                response.addHeader("Content-disposition", "attachment;filename=" + fileName);
                response.setContentType(format.contentType());
                response.getOutputStream().write(document);
                response.flushBuffer();
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (org.eclipse.birt.report.engine.api.impl.ParameterValidationException pve) {
            throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION, pve);
        } catch (org.eclipse.birt.report.engine.api.EngineException ee) {
            LOG.error("Cannot process the file {}", partialName, ee);
            throw ee;
        } catch (DesignFileException dfe) {
            LOG.error("Cannot open the file {}", partialName, dfe);
            throw dfe;
        } catch (DesignParserException dpe) {
            LOG.error("Cannot parse the file {}", partialName, dpe);
            throw dpe;
        }
        LOG.debug("The report {} has been processed.", partialName);
    }
}
