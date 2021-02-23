package ca.ids.abms.modules.selfcareportal;

import ca.ids.abms.modules.selfcareportal.querysubmission.QuerySubmission;
import ca.ids.abms.modules.users.UserService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class QuerySubmissionServiceTest {

    private UserService userService;

    @Before
    public void setup() {
        userService = mock(UserService.class);
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void send() throws MessagingException {

        QuerySubmission querySubmission = new QuerySubmission();
        querySubmission.setSubject("Subject");
        querySubmission.setSenderEmail("blabla@gmail.com");

        List<String> list = new ArrayList<>();
        list.add("admin@gmail.com");

        userService.getSelfCarePortalAdminAddress();

        when(userService.getSelfCarePortalAdminAddress())
            .thenReturn(list);

        Properties properties = System.getProperties();
        Session session = Session.getDefaultInstance(properties);
        MimeMessage message = new MimeMessage(session);

        InternetAddress[] toAddress = new InternetAddress[list.size()];

        for( int i = 0; i < list.size(); i++ ) {
            toAddress[i] = new InternetAddress(list.get(i));
            message.addRecipient(Message.RecipientType.TO, toAddress[i]);
        }

        message.setReplyTo(new Address[] { new InternetAddress(querySubmission.getSenderEmail()) });
        message.setSubject(querySubmission.getSubject());

        exception.expect(MessagingException.class);
        Transport.send(message);

        assertThat(message.getSubject()).isEqualTo(querySubmission.getSubject());
        assertThat(message.getReplyTo()).isEqualTo(new Address[] {new InternetAddress(querySubmission.getSenderEmail())});
        assertThat(message.getAllRecipients()).isEqualTo(new Address[] {new InternetAddress("admin@gmail.com")});
    }
}
