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

    private final AccountExternalChargeCategoryMapper accountExternalChargeCategoryMapper;

    public AccountExternalChargeCategoryController(final AccountExternalChargeCategoryService accountExternalChargeCategoryService, final AccountExternalChargeCategoryMapper accountExternalChargeCategoryMapper) {
        this.accountExternalChargeCategoryService = accountExternalChargeCategoryService;
        this.accountExternalChargeCategoryMapper = accountExternalChargeCategoryMapper;
    }

    @GetMapping(value = "/for-account/{id}")
    public ResponseEntity<List<AccountExternalChargeCategory>> retrieveByAccount(
        @PathVariable(name = "id") Integer accountId,
        @RequestParam(required = false) Integer externalChargeCategoryId
    ) {
        LOG.debug("Request to get all external charge categories for accountId '{}'.", accountId);
        return ResponseEntity.ok(accountExternalChargeCategoryService.findByAccount(accountId, externalChargeCategoryId));
    }

    @PostMapping
    public ResponseEntity<AccountExternalChargeCategoryViewModel> create(
        @Valid @RequestBody AccountExternalChargeCategoryViewModel accountExternalChargeCategoryDto
    ) throws URISyntaxException {
        LOG.debug("Request to create account external charge category : '{}'", accountExternalChargeCategoryDto);
        AccountExternalChargeCategory account = accountExternalChargeCategoryMapper.toModel(accountExternalChargeCategoryDto);

        AccountExternalChargeCategory result = accountExternalChargeCategoryService.create(account);

        final AccountExternalChargeCategoryViewModel resultDto = accountExternalChargeCategoryMapper.toViewModel(result);

        return ResponseEntity.created(new URI("/api/accounts-external-charge-categories/" + result.getId())).body(resultDto);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<AccountExternalChargeCategoryViewModel> update(
        @PathVariable Integer id,
        @Valid @RequestBody AccountExternalChargeCategoryViewModel accountExternalChargeCategoryDto
    ) {
        LOG.debug("Request to update account external charge category : '{}", accountExternalChargeCategoryDto);
        if (accountExternalChargeCategoryDto.getId() == null)
            accountExternalChargeCategoryDto.setId(id);
        else if (!accountExternalChargeCategoryDto.getId().equals(id))
            throw new IllegalArgumentException("Path variable identification does not match that of the request body.");

        AccountExternalChargeCategory updateAccountExternalChargeCategory = accountExternalChargeCategoryService.update(accountExternalChargeCategoryMapper.toModel(accountExternalChargeCategoryDto));
        AccountExternalChargeCategoryViewModel result = accountExternalChargeCategoryMapper.toViewModel(updateAccountExternalChargeCategory);

        return ResponseEntity.ok(result);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(
        @PathVariable Integer id
    ) {
        LOG.debug("Request to delete account external charge category with id '{}'", id);
        accountExternalChargeCategoryService.delete(id);
        return ResponseEntity.ok().build();
    }
}
