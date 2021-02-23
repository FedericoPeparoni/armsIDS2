package ca.ids.abms.modules.transactions;

import ca.ids.abms.config.security.SecurityUtils;
import ca.ids.abms.modules.users.User;
import ca.ids.abms.modules.users.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/transaction-types")
public class TransactionTypeController {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionTypeController.class);
    private final TransactionTypeService transactionTypeService;
    private final UserService userService;

    public TransactionTypeController(TransactionTypeService transactionTypeService, UserService userService) {
        this.transactionTypeService = transactionTypeService;
        this.userService = userService;
    }

    @GetMapping
    public List<TransactionType> getAllTransactionTypes() {
        LOG.debug("REST request to get all transaction types");
        List<TransactionType> types = transactionTypeService.findAll();
        User currentUser = userService.getUserByLogin(SecurityUtils.getCurrentUserLogin());
        boolean canCreateAdjustment = currentUser.getPermissions().contains("transaction_adjustment_create");
        if (!canCreateAdjustment) {
            types.removeIf(TransactionType::isDebit);
        }

        return types;
    }
}
