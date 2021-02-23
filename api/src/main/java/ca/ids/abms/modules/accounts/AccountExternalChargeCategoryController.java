package ca.ids.abms.modules.accounts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Controller
@RequestMapping("/api/accounts-external-charge-categories")
public class AccountExternalChargeCategoryController {

    private static final Logger LOG = LoggerFactory.getLogger(AccountExternalChargeCategoryController.class);

    private final AccountExternalChargeCategoryService accountExternalChargeCategoryService;

    public AccountExternalChargeCategoryController(final AccountExternalChargeCategoryService accountExternalChargeCategoryService) {
        this.accountExternalChargeCategoryService = accountExternalChargeCategoryService;
    }

    @RequestMapping(value = "/for-account/{id}", method = RequestMethod.GET)
    public ResponseEntity<List<AccountExternalChargeCategory>> retrieveByAccount(
        @PathVariable(name = "id") Integer accountId,
        @RequestParam(required = false) Integer externalChargeCategoryId
    ) {
        LOG.debug("Request to get all external charge categories for accountId '{}'.", accountId);
        return ResponseEntity.ok(accountExternalChargeCategoryService.findByAccount(accountId, externalChargeCategoryId));
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<AccountExternalChargeCategory> create(
        @Valid @RequestBody AccountExternalChargeCategory accountExternalChargeCategory
    ) throws URISyntaxException {
        LOG.debug("Request to create account external charge category : '{}'", accountExternalChargeCategory);
        AccountExternalChargeCategory result = accountExternalChargeCategoryService.create(accountExternalChargeCategory);
        return ResponseEntity.created(new URI("/api/accounts-external-charge-categories/" + result.getId())).body(result);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<AccountExternalChargeCategory> update(
        @PathVariable Integer id,
        @Valid @RequestBody AccountExternalChargeCategory accountExternalChargeCategory
    ) {
        LOG.debug("Request to update account external charge category : '{}", accountExternalChargeCategory);
        if (accountExternalChargeCategory.getId() == null)
            accountExternalChargeCategory.setId(id);
        else if (!accountExternalChargeCategory.getId().equals(id))
            throw new IllegalArgumentException("Path variable identification does not match that of the request body.");
        return ResponseEntity.ok(accountExternalChargeCategoryService.update(accountExternalChargeCategory));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(
        @PathVariable Integer id
    ) {
        LOG.debug("Request to delete account external charge category with id '{}'", id);
        accountExternalChargeCategoryService.delete(id);
        return ResponseEntity.ok().build();
    }
}
