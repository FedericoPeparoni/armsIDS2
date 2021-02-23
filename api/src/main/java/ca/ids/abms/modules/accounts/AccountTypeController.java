package ca.ids.abms.modules.accounts;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/account-types")
public class AccountTypeController {

    private final Logger log = LoggerFactory.getLogger(AccountTypeController.class);
    private final AccountTypeService accountTypeService;
    private final AccountTypeMapper accountTypeMapper;

    public AccountTypeController(AccountTypeService accountTypeService, AccountTypeMapper accountTypeMapper) {
        this.accountTypeService = accountTypeService;
        this.accountTypeMapper = accountTypeMapper;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<AccountTypeViewModel>> getAllAccountTypes() {
        log.debug("REST request to get all types");
        List <AccountType> types = accountTypeService.findAll();

        return new ResponseEntity<>(accountTypeMapper.toViewModel(types), HttpStatus.OK);
    }
}
