package ca.ids.abms.modules.reports2;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.modules.accounts.Account;
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
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
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
    private final BillingLedgerService billingLedgerService;



    public GenericReportController(final BirtReportCreator birtReportCreator,
    final TransactionService transactionService,
    final BillingLedgerService billingLedgerService,
    final TransactionPaymentRepository transactionPaymentRepository,
    final SystemConfigurationService systemConfigurationService ) {

        this.birtReportCreator = birtReportCreator;
        this.systemConfigurationService = systemConfigurationService;
        this.billingLedgerService = billingLedgerService;

    }

     DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd").withZone( ZoneId.of("UTC"));

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
        if (bodyParams != null) {
            if( partialName.equals("unified_tax") && bodyParams.get("account_id") != null){

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                Date dFrom =  sdf.parse(queryParams.get("fromdate").toString());
                Date dTo =  sdf.parse(queryParams.get("todate").toString());

                final List<String> accountIdListToCheck = (Arrays.asList(((String)bodyParams.get("account_id")).replace("{", "").replace("}", "").split(",")));

                List<BillingLedger> blList = billingLedgerService.findIssuedInvoicesAccountsIdsByTypeAndDateClear("unified-tax",dFrom,dTo);

                final List<Integer>  idValidAccountsPossible = blList.stream().map(BillingLedger::getAccount).map(Account::getId).collect(Collectors.toList());
                final List<String>  idValidAccounts = new ArrayList<>();

                for(String idAccount: accountIdListToCheck)
                    if(idValidAccountsPossible.contains(Integer.parseInt(idAccount)))
                        idValidAccounts.add(idAccount);

                bodyParams.put("account_id", (Object) idValidAccounts.toString().replace("[", "{").replace("]", "}"));
                params.putAll (bodyParams);
            }else{
                params.putAll (bodyParams);
            }
        }

        try {
        	//EANA:When downloading the report, the file name is in english (Unified Tax.pdf)
        	//EANA has asksed to have it in Spanish
        	final String organisationName = systemConfigurationService.getString(SystemConfigurationItemName.ORGANISATION_NAME, null);
        	if(organisationName.equals("EANA") && partialName.equals("unified_tax")){
        	String dataGeneration =	DATE_TIME_FORMATTER.format(new Date().toInstant());
                fileName = "Reporte_TU-".concat(dataGeneration).concat(format.fileNameSuffix());
              //
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
