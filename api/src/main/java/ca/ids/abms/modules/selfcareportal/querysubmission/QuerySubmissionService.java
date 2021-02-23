package ca.ids.abms.modules.selfcareportal.querysubmission;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.users.UserService;
import org.apache.commons.validator.EmailValidator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class QuerySubmissionService {

    private static final Logger LOG = LoggerFactory.getLogger(QuerySubmissionService.class);

    @SuppressWarnings("squid:S2068")
    private static final String EMAIL_PASSWORD = "Email password";
    private static final String EMAIL_USERNAME = "Email username";
    private static final String SERVER_PORT = "Email server port";
    private static final String SERVER_NAME = "Email server name";
    private static final String EMAIL_FROM_ADDRESS = "Email from address";
    private static final String SSL_INDICATOR = "Email SSL indicator";

    private SystemConfigurationService systemConfigurationService;
    private UserService userService;

    public QuerySubmissionService(SystemConfigurationService systemConfigurationService, UserService userService) {
        this.systemConfigurationService = systemConfigurationService;
        this.userService = userService;
    }

    public Boolean send(String subject, String messageText, List<String> to, boolean throwQuerySubmissionException) {
        return send(null, subject, messageText, to, throwQuerySubmissionException);
    }

    /**
     * Send email message to list of recipients.
     *
     * @param senderEmail senders email address
     * @param subject email message subject
     * @param messageText email message body
     * @param to list of recipients' email addresses
     * @return true if successfully sent email message
     *
     * @throws QuerySubmissionException Caused when issues arise when attempting to send email message email. Exception
     * with reason of `ErrorConstant.ERR_EMAIL_SERVICE_MISCONFIGURATION` is the result of missing system configuration
     * settings well reason of `ErrorConstant.ERR_EMAIL_SERVICE_UNAVAILABLE` is the result of error during transmission.
     */
    public Boolean send(String senderEmail, String subject, String messageText, List<String> to, boolean throwQuerySubmissionException) {
        LOG.debug("Request to send an email");

        if (to == null) {
            to = userService.getSelfCarePortalAdminAddress();
        }

        String userName = systemConfigurationService.getCurrentValue(EMAIL_USERNAME);
        String password = systemConfigurationService.getCurrentValue(EMAIL_PASSWORD);
        String serverName = systemConfigurationService.getCurrentValue(SERVER_NAME);
        String serverPort = systemConfigurationService.getCurrentValue(SERVER_PORT);
        String fromAddress = systemConfigurationService.getCurrentValue(EMAIL_FROM_ADDRESS);
        String tlsIndicator = systemConfigurationService.getCurrentValue(SSL_INDICATOR).equals("t") ? "true" : "false";


        Properties properties = System.getProperties();
        try {
            properties.put("mail.smtp.starttls.enable", tlsIndicator);
            properties.put("mail.smtp.host", serverName);
            properties.put("mail.smtp.user", userName);
            properties.put("mail.smtp.password", password);
            properties.put("mail.smtp.port", serverPort);
            properties.put("mail.smtp.sendpartial", true); // send to valid email addresses, even if one is invalid

        } catch (NullPointerException ex) {
            LOG.debug("Email has not been sent, because a system configuration value is null " +
                "or configured incorrectly");
            return throwQuerySubmissionException(throwQuerySubmissionException, ErrorConstants.ERR_EMAIL_SERVICE_MISCONFIGURATION, ex);
        }

        Session session = Session.getDefaultInstance(properties);

        try{
            MimeMessage message = setMimeMessage(session, fromAddress, senderEmail, to, subject, messageText);

            Transport transport = session.getTransport("smtp");
            transport.connect(serverName, userName, password);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            LOG.debug("Sent email successfully");
            return true;
        } catch (SendFailedException ex) {
            Address[] validAddresses = ex.getValidSentAddresses();

            LOG.debug("One or more emails is invalid: {}", ex.getInvalidAddresses());
            LOG.debug("Sent to the following valid emails: {}", validAddresses);

            if (validAddresses != null && validAddresses.length > 0) {
                return true;
            }

            return throwQuerySubmissionException(throwQuerySubmissionException, ErrorConstants.ERR_EMAIL_SERVICE_UNAVAILABLE, ex);

        } catch (MessagingException ex) {
            LOG.debug("Email has not been sent, because: {}", ex.getMessage());
            return throwQuerySubmissionException(throwQuerySubmissionException, ErrorConstants.ERR_EMAIL_SERVICE_UNAVAILABLE, ex);
        }
    }

    private MimeMessage setMimeMessage(final Session session,
                                       final String fromAddress,
                                       final String senderEmail,
                                       final List<String> to,
                                       final String subject,
                                       final String messageText) throws MessagingException {

        MimeMessage message = new MimeMessage(session);

        message.setFrom(fromAddress);

        if (senderEmail != null) {
            message.setReplyTo(new Address[] {
                new InternetAddress(senderEmail)
            });
        }

        InternetAddress[] toAddress = new InternetAddress[to.size()];

        EmailValidator emailValidator = EmailValidator.getInstance();

        for( int i = 0; i < to.size(); i++ ) {
            if (emailValidator.isValid(to.get(i))) {
                toAddress[i] = new InternetAddress(to.get(i));
                message.addRecipient(Message.RecipientType.TO, toAddress[i]);
            }
        }

        if (subject != null) {
            message.setSubject(subject);
        }

        if (messageText != null) {
            message.setText(messageText);
        }

        return message;
    }

    private Boolean throwQuerySubmissionException(final boolean throwQuerySubmissionException, ErrorConstants errorConstants, Exception ex) {
        if (throwQuerySubmissionException) {
            throw new QuerySubmissionException(errorConstants, ex);
        } else {
            return false;
        }
    }
}
