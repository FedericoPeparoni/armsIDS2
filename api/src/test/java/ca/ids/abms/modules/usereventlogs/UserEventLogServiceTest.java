package ca.ids.abms.modules.usereventlogs;

import ca.ids.abms.modules.accounts.AccountService;
import ca.ids.abms.modules.users.UserService;
import org.junit.Before;
import org.junit.Test;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserEventLogServiceTest {

    private UserEventLogRepository userEventLogRepository;
    private UserEventLogService userEventLogService;

    @Before
    public void setup() {
        userEventLogRepository = mock(UserEventLogRepository.class);
        UserService userService = mock(UserService.class);
        AccountService accountService = mock(AccountService.class);
        userEventLogService = new UserEventLogService(userEventLogRepository, userService, accountService);
    }

    @Test
    public void createUserEventLog() {
        UserEventLog userEventLog = mockUserEventLog();

        when(userEventLogRepository.save(any(UserEventLog.class)))
            .thenReturn(userEventLog);

        UserEventLog result = userEventLogService.save(userEventLog);
        assertThat(userEventLog.getUserName()).isEqualTo(result.getUserName());
    }

    @Test
    public void updateUserEventLog() {
        UserEventLog existingUserEventLog = new UserEventLog();
        existingUserEventLog.setUserName("test username");

        UserEventLog updateUserEventLog = new UserEventLog();
        updateUserEventLog.setUserName("test username");

        when(userEventLogRepository.getOne(1)).thenReturn(existingUserEventLog);

        when(userEventLogRepository.save(any(UserEventLog.class))).thenReturn(existingUserEventLog);

        UserEventLog result = userEventLogService.update(1, updateUserEventLog);

        assertThat(result.getUserName()).isEqualTo("test username");
    }

    @Test
    public void deleteUserEventLog() {
        userEventLogService.delete(1);
        verify(userEventLogRepository).delete(any(Integer.class));
    }

    private UserEventLog mockUserEventLog() {
        UserEventLog userEventLog = new UserEventLog();
        userEventLog.setUserName("Test");
        userEventLog.setDateTime(LocalDateTime.now());
        userEventLog.setRecordPrimaryKey("500");
        userEventLog.setModifiedColumnNamesValues("");
        userEventLog.setEventType("add");
        userEventLog.setIpAddress("0:0:0:0:0:0:1");

        return  userEventLog;
    }

}
