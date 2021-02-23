package ca.ids.abms.modules.usereventlogs;

import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.accounts.AccountService;
import ca.ids.abms.modules.unspecifiedaircraft.UnspecifiedAircraftTypeService;
import ca.ids.abms.modules.users.User;
import ca.ids.abms.modules.users.UserService;
import ca.ids.abms.modules.util.models.ModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.StringTokenizer;

@Service
@Transactional
public class UserEventLogService {

    private UserEventLogRepository userEventLogRepository;
    private UserService userService;
    private AccountService accountService;
    private static final Logger LOG = LoggerFactory.getLogger(UnspecifiedAircraftTypeService.class);

    public UserEventLogService(final UserEventLogRepository userEventLogRepository,
                               @Lazy final UserService userService,
                               final AccountService accountService){
        this.userEventLogRepository = userEventLogRepository;
        this.userService = userService;
        this.accountService = accountService;
    }

    @Transactional(readOnly = true)
    public Page<UserEventLog> findAllEventLogsByFilters (final String textSearch, final Pageable pageable) {

        final FiltersSpecification.Builder filterBuilder = new FiltersSpecification.Builder().lookFor(textSearch);

        LOG.debug("Attempting to find user event logs by filters. Search: {}", textSearch);
        return userEventLogRepository.findAll(filterBuilder.build(), pageable);
    }

    public UserEventLog save(UserEventLog userEventLog) {
        String recordPrimaryKey = userEventLog.getRecordPrimaryKey();
        String identifier = userEventLog.getUniqueRecordId();
        String event = userEventLog.getEventType();
        String ipAddress = userEventLog.getIpAddress();
        String userName = userEventLog.getUserName();
        User authUser = null;
        if (userName == null) {
            authUser = userService.getAuthenticatedUser();
            userName = authUser != null ? authUser.getLogin() : "system";
        }

        userEventLog.setUserName(userName);
        userEventLog.setId(null);
        userEventLog.setDateTime(LocalDateTime.now());
        userEventLog.setModifiedColumnNamesValues(""); // todo: implement in later user story

        LOG.debug("Request to save userEventLog : Username: {}, IP: {}, Event: {}, PrimaryKey: {}, Identifier: {}", userName, ipAddress, event, recordPrimaryKey, identifier);

        if (recordPrimaryKey != null && identifier != null && event != null &&
            userName != null && ipAddress != null) {

            // update user accounts last activity date/time for Whitelisting
            updateUserAccountLastActivityDateTimeForWhitelisting(userEventLog, authUser);

            return userEventLogRepository.save(userEventLog);
        }


        return userEventLog;
    }

    public UserEventLog create(UserEventLog userEventLog) {
        return userEventLogRepository.save(userEventLog);
    }

    public UserEventLog update(Integer id, UserEventLog userEventLog) {
        LOG.debug("Request to update user event log : {}", userEventLog);
        UserEventLog existingUserEventLog = userEventLogRepository.getOne(id);
        ModelUtils.merge(userEventLog, existingUserEventLog);
        return userEventLogRepository.save(existingUserEventLog);
    }

    public void delete(Integer id) {
        LOG.debug("Request to delete user event log : {}", id);
        userEventLogRepository.delete(id);
    }

    public String getIpAddressFromRequest(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader == null) {
            return request.getRemoteAddr();
        } else {
            return new StringTokenizer(xForwardedForHeader, ",").nextToken().trim();
        }
    }

    public UserEventLog createInvoiceUserEventLog(String ipAddress, String primaryKey) {
        return createInvoiceUserEventLogAsync(ipAddress, primaryKey, null);
    }

    public UserEventLog createInvoiceUserEventLogAsync(final String ipAddress, final String key, final User currentUser) {
        final UserEventLog userEventLog = new UserEventLog();

        userEventLog.setEventType("invoice creation");
        userEventLog.setUniqueRecordId("invoices");
        userEventLog.setIpAddress(ipAddress);
        userEventLog.setRecordPrimaryKey(key);
        if (currentUser != null) {
            userEventLog.setUserName(currentUser.getName());
        }
        return save(userEventLog);
    }

    public long countAll() {
        return userEventLogRepository.count();
    }

    /**
     * Account activity updated by following:
     * -	payments being made;
     * -    flights occurring; and
     * -	users logging in.
     * @param userEventLog UserEventLog
     * @param user logged in user
     */
    private void updateUserAccountLastActivityDateTimeForWhitelisting(UserEventLog userEventLog, User user) {
        if (user == null) {
            return;
        }

        if (!userEventLog.getEventType().equalsIgnoreCase("login")) {
            return;
        }

        if (userEventLog.getUniqueRecordId() != null && userEventLog.getUniqueRecordId().equalsIgnoreCase("failed")) {
            return;
        }

        if (!user.getIsSelfcareUser()) {
            return;
        }

        List<Account> userAccounts = accountService.findByUserId(user.getId());
        for (Account account: userAccounts) {
            accountService.updateAccountLastActivityDateTimeForWhitelisting(account);
        }
    }
}
