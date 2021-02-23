package ca.ids.abms.modules.amhsconfiguration;

import ca.ids.abms.atnmsg.amhs.addr.AmhsAddress;
import ca.ids.abms.atnmsg.amhs.addr.AmhsAddressCreator;
import ca.ids.abms.atnmsg.amhs.addr.InvalidAmhsAddressException;
import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.modules.common.services.AbmsCrudService;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import javax.persistence.EntityNotFoundException;

@Service
public class AmhsAccountService extends AbmsCrudService<AmhsAccount, Integer> {

    @SuppressWarnings ("squid:S2068")
    private static final String DFLT_PASSWORD = "default";
    private static final Logger LOG = LoggerFactory.getLogger(AmhsAccountService.class);
    private final AmhsAccountRepository amhsAccountRepository;
    private final AmhsAddressCreator amhsAddressCreator;
    private final AmhsAgentConfigService amhsAgentConfigService;

    AmhsAccountService(
            final AmhsAccountRepository amhsAccountRepository,
            final AmhsAddressCreator amhsAddressCreator,
            final AmhsAgentConfigService amhsAgentConfigService) {
        super(amhsAccountRepository);
        this.amhsAccountRepository = amhsAccountRepository;
        this.amhsAddressCreator = amhsAddressCreator;
        this.amhsAgentConfigService = amhsAgentConfigService;
    }

    @Transactional(readOnly = true)
    public List<AmhsAccount> findAll() {
        return amhsAccountRepository.findAll();
    }
    
    @Override
    @Transactional
    public AmhsAccount create(final AmhsAccount entity) {
        validate(null, entity);
        final AmhsAccount res = super.create(entity);
        amhsAccountRepository.flush();
        amhsAgentConfigService.update();
        return res;
    }

    @Override
    @Transactional
    public AmhsAccount update(final Integer id, final AmhsAccount entity) {
        final AmhsAccount old = amhsAccountRepository.getOne (id);
        if (old == null) {
            throw ExceptionFactory.persistenceDataManagement(new EntityNotFoundException(),
                ErrorConstants.ERR_UPDATE_NO_LONGER_EXISTS);
        }
        if (entity.getVersion() == null) {
            entity.setVersion(old.getVersion());
        }
        validate(id, entity);
        final AmhsAccount res = super.update (id, entity);
        amhsAccountRepository.flush();
        amhsAgentConfigService.update();
        return res;
    }

    @Override
    @Transactional
    public void remove (final Integer id) {
        super.remove(id);
        amhsAccountRepository.flush();
        amhsAgentConfigService.update();
    }

    // ------------------------- private ----------------------

    private AmhsAccount findByAmhsAddr(final AmhsAddress addr) {
        return amhsAccountRepository.findAll().stream().filter(x -> {
            if (x.getAddr() != null) {
                try {
                    if (amhsAddressCreator.parseUbimexString(x.getAddr()).equals(addr)) {
                        return true;
                    }
                } catch (final InvalidAmhsAddressException xx) {
                    // ignore this exception
                }
            }
            return false;
        }).findFirst().orElse(null);
    }

    private void validate(final Integer id, AmhsAccount entity) {
        AmhsAccount other = null;
        // id

        // active

        // addr
        String addr = StringUtils.stripToNull(entity.getAddr());
        if (addr == null) {
            throw validationError("Address is required");
        }
        try {
            final AmhsAddress addrObject = amhsAddressCreator.parseUbimexString(addr);
            addr = addrObject.toUbimexString();
            other = findByAmhsAddr(addrObject);
            if (other != null && (id == null || !Objects.equals(id, other.getId()))) {
                throw validationError("Another account with this address already exists");
            }
            entity.setAddr(addr);
        } catch (final InvalidAmhsAddressException x) {
            throw validationError(x);
        }

        // passwd
        String passwd = entity.getPasswd();
        if (passwd == null || passwd.length() == 0) {
            passwd = DFLT_PASSWORD;
            entity.setPasswd(passwd);
        } else if (containsInvalidChars(passwd)) {
            throw validationError("Invalid characters in password");
        }

        // allowMtaConn

        // svcHoldForDelivery
    }

    private RuntimeException validationError(final Throwable cause) {
        return validationError(cause.getMessage(), cause);
    }

    private RuntimeException validationError(final String msg) {
        return validationError(msg, null);
    }

    private RuntimeException validationError(final String msg, final Throwable cause) {
        LOG.debug("{}", msg);
        final Exception x = cause == null ? new Exception(msg) : new Exception(msg, cause);
        throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION, x);
    }

    // For text fields, some characters are not allowed to avoid parsing problems
    // with "mts_users.conf"
    // - non-printable characters
    // - spaces, colons: because UBIATN uses these as delimiters in some contexts
    // - number signs: because UBIATN uses them to mark comments in some contexts
    // - any character outside of LATIN-1 (ISO 8859-1) character set. For example
    // the Cyrillic "Zhe", Unicode code U+0416 (decimal). In contrast, most Western
    // European accented characters & umlauts are allowed.
    //
    // look for the following prohibited chars (Unicode/Latin 1 codepoints):
    // Hex     Dec      Descr
    // ---------------------------------------------
    // 00-1F  000-031   non-printable chars and space
    // 20     032       space
    // 3A     058       colon :
    // 23     035       number sign #
    // 7f     127       <DEL>
    // 80-9F  128-159   undefined in Latin-1
    // A0     160       <NBSP>
    private static final Pattern RE_INVALID_CHARS = Pattern
            .compile("[\\x00-\\x1f\\x20\\x3a\\x23\\x7f\\x80-\\x9f\\xa0]");

    static boolean containsInvalidChars(final String s) {
        return RE_INVALID_CHARS.matcher(s).find();
    }
}
