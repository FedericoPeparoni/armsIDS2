package ca.ids.abms.modules.selfcareportal.usermanagement;

import ca.ids.abms.modules.users.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SCUserManagementService {

    private final UserService userService;

    public SCUserManagementService(UserService userService) {
        this.userService = userService;
    }

    @Scheduled(fixedRate=3600000) // 1 hour
    public void checkActivationKeyExpiration() {
        userService.deleteExpiredUsers();
    }

    @Scheduled(fixedRate=3600000) // 1 hour
    public void checkExpiredTempPasswords() {
        userService.checkExpiredTempPasswords();
    }
}
