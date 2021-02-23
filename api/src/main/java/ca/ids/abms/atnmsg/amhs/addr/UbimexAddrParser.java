package ca.ids.abms.atnmsg.amhs.addr;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ca.ids.abms.atnmsg.amhs.addr.Utils.abbrev;

/**
 * UBIATN-generated AMHS address parser
 *
 * See document "cronos-amhs-addressformat" from "aim-docs" project for parsing
 * requirements.
 */
@SuppressWarnings ({ "squid:RedundantThrowsDeclarationCheck", "squid:S1199" })
class UbimexAddrParser {

    public AmhsAddress parse(String s) throws InvalidAmhsAddressException {
        try {
            return (new UserAddrParser().parse(s));
        } catch (final InvalidAmhsAddressException x) {
            return do_parseRelaxed(s);
        }
    }

    private AmhsAddress do_parseRelaxed(String s) throws InvalidAmhsAddressException {

        // Trim it; check whether it's empty
        final String trimmedAddrString = Utils.trim(s);
        if (trimmedAddrString.isEmpty()) {
            throw ErrorCodes.EMPTY.error("Invalid empty AMHS address");
        }

        // Make sure it's not too long
        if (trimmedAddrString.length() > Utils.MAX_AMHS_ADDR_LEN) {
            throw ErrorCodes.ADDR_TOO_LONG.error("AMHS address \"{0}\" is too long", abbrev(trimmedAddrString));
        }

        // Check for allowed characters
        {
            String bad = Utils.checkCharSet(trimmedAddrString);
            if (bad != null) {
                throw ErrorCodes.BAD_CHARS.error("AMHS address contains invalid characters \"{0}\"", abbrev(bad));
            }
        }

        // convert to upper case
        final String ucAddrString = trimmedAddrString.toUpperCase(Locale.US);

        // Find AFTN address at (last of) CN=... or OU=... or OU1=...
        String aftnAddr = null;
        for (final Pattern p : new Pattern[] { RE_CN, RE_OU1 }) {
            final Matcher m = p.matcher(ucAddrString);
            if (m.find()) {
                do {
                    aftnAddr = m.group(1);
                } while (m.find(m.end()));
                break;
            }
        }
        if (aftnAddr == null) {
            throw ErrorCodes.MISSING_KEY.error(
                    "AMHS address \"{0}\" is invalid, expecting \"CN=\", \"OU=\" or \"OU1=\"",
                    abbrev(trimmedAddrString));
        }

        // done
        return new AmhsAddressImpl(aftnAddr, ucAddrString);
    }

    private static final Pattern RE_CN = Pattern.compile("\\bCN\\s*=\\s*([A-Z0-9]{8})\\b");
    private static final Pattern RE_OU1 = Pattern.compile("\\bOU1?\\s*=\\s*([A-Z0-9]{8})\\b");
}
