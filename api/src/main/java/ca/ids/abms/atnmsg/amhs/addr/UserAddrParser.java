package ca.ids.abms.atnmsg.amhs.addr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.common.base.Preconditions;

import static ca.ids.abms.atnmsg.amhs.addr.Utils.abbrev;
import static ca.ids.abms.atnmsg.amhs.addr.Utils.countChar;

/**
 * User-entered AMHS address parser
 *
 * See document "cronos-amhs-addressformat" from "aim-docs" project for parsing
 * requirements.
 * 
 * Copied/adapted from CRONOS
 */
@SuppressWarnings ({ "squid:RedundantThrowsDeclarationCheck", "squid:S1192", "squid:S1199", "squid:S3776" })
class UserAddrParser {

    public AmhsAddress parse(String s) throws InvalidAmhsAddressException {
        Preconditions.checkNotNull(s);

        // trim leading and trailing white space around the entire string
        final String trimmedAddrString = Utils.trim(s);
        // if the result is empty, bail out
        if (trimmedAddrString.isEmpty()) {
            throw ErrorCodes.EMPTY.error("Invalid empty AMHS address");
        }

        // Check for chars outside of the allowed char set (non-ASCII)
        {
            String bad = Utils.checkCharSet(trimmedAddrString);
            if (bad != null) {
                throw ErrorCodes.BAD_CHARS.error("AMHS address contains invalid characters \"{0}\"", abbrev(bad));
            }
        }

        // Check for prohibited character sequences
        {
            String bad = Utils.checkBadChars(trimmedAddrString);
            if (bad != null) {
                throw ErrorCodes.BAD_CHARS.error("AMHS address contains invalid characters \"{0}\"", abbrev(bad));
            }
        }

        // detect comma vs slash - separated format
        final String sep = countChar(trimmedAddrString, '/') > countChar(trimmedAddrString, ',') ? "/" : ",";

        // split on separators
        final Map<String, String> itemMap = new HashMap<>();
        final String[] partsArray = trimmedAddrString.split(sep);
        if (partsArray == null || partsArray.length <= 0) {
            throw ErrorCodes.BAD_CHARS.error("AMHS address contains invalid characters \"{0}\"",
                    abbrev(trimmedAddrString));
        }
        for (int i = 0; i < partsArray.length; ++i) {
            final String part = partsArray[i];
            final String trimmedPart = Utils.trim(part);
            if (trimmedPart.length() > 0) {
                final String ucPart = trimmedPart.toUpperCase(Locale.US);
                final int eqPos = ucPart.indexOf('=');
                if (eqPos < 0) {
                    throw ErrorCodes.MISSING_EQ.error("Missing \"=\" in AMHS address near \"{0}\"",
                            abbrev(trimmedPart));
                }

                // key
                final String key = ucPart.substring(0, eqPos);
                final String trimmedKey = Utils.trim(key);
                if (trimmedKey.isEmpty()) {
                    throw ErrorCodes.EMPTY_KEY.error("Invalid empty key in AMHS address near \"{0}\"",
                            abbrev(trimmedPart));
                }
                if (Utils.containsWhiteSpace(trimmedKey)) {
                    throw ErrorCodes.BAD_WS.error("Invalid whitespace in AMHS address near \"{0}\"",
                            abbrev(trimmedPart));
                }
                // make sure key is known
                final String knownKey = Utils.checkKey(trimmedKey);
                if (knownKey == null) {
                    throw ErrorCodes.UNKNOWN_KEY.error(
                            "AMHS address \"{0}\" contains unknown key \"{0}\" in AMHS address",
                            abbrev(trimmedAddrString), abbrev(trimmedKey));
                }

                // make sure key is unique
                if (itemMap.containsKey(knownKey)) {
                    throw ErrorCodes.DUP_KEY.error("AMHS address \"{0}\" contains multiple instances of key \"{0}\"",
                            abbrev(trimmedAddrString), abbrev(knownKey));
                }

                // value
                final String value = ucPart.substring(eqPos + 1);
                final String trimmedValue = Utils.trim(value);
                if (trimmedValue.isEmpty()) {
                    throw ErrorCodes.EMPTY_VALUE.error("Invalid empty value in AMHS address near \"{0}\"",
                            abbrev(trimmedPart));
                }
                if (Utils.containsWhiteSpace(trimmedValue)) {
                    throw ErrorCodes.BAD_WS.error("Invalid whitespace in AMHS address near \"{0}\"",
                            abbrev(trimmedPart));
                }
                if (trimmedValue.indexOf('=') >= 0) {
                    throw ErrorCodes.BAD_EQ.error("Invalid \"=\" in AMHS address near \"{0}\"", abbrev(trimmedPart));
                }

                // add to map
                itemMap.put(knownKey, trimmedValue);

            }
        }
        if (itemMap.isEmpty()) {
            throw ErrorCodes.EMPTY.error("Invalid empty AMHS address \"{0}\"", abbrev(trimmedAddrString));
        }

        // Check required keys
        for (final String rkey : Utils.REQ_KEYS) {
            if (!itemMap.containsKey(rkey)) {
                throw ErrorCodes.MISSING_KEY.error("Required key \"{0}\" is missing in AMHS address \"{0}\"", rkey,
                        abbrev(trimmedAddrString));
            }
        }

        // Construct AFTN address
        String aftnAddrKey = "CN";
        String aftnAddr = itemMap.get("CN");
        if (aftnAddr == null) {
            aftnAddrKey = "OU1";
            aftnAddr = itemMap.get("OU1");
        }
        if (!Utils.isAftnAddr(aftnAddr)) {
            throw ErrorCodes.BAD_AFTN_ADDR.error("Key \"{0}\" contains an invalid AFTN address \"{0}\"", aftnAddrKey,
                    abbrev(aftnAddr));
        }

        // Create a list of normalized key/value pairs in the right order
        final String ubimexString = do_createUbimexString(itemMap);

        // Make sure the normalized string is not too long
        if (ubimexString.length() > Utils.MAX_AMHS_ADDR_LEN) {
            throw ErrorCodes.ADDR_TOO_LONG.error("AMHS address is too long", abbrev(ubimexString));
        }

        // done
        return new AmhsAddressImpl(aftnAddr, ubimexString);
    }

    public AmhsAddress create(final Map<String, String> data) {
        final Map<String, String> itemMap = new HashMap<>();
        Preconditions.checkNotNull(data);
        for (final Map.Entry <String, String> entry: itemMap.entrySet()) {
            final String key = entry.getKey();
            final String value = entry.getValue();
            Preconditions.checkArgument(key != null);
            Preconditions.checkArgument(value != null);

            final String trimmedKey = Utils.trim(key).toUpperCase(Locale.US);
            Preconditions.checkArgument(!trimmedKey.isEmpty());
            Preconditions.checkArgument(Utils.checkCharSet(trimmedKey) == null);
            Preconditions.checkArgument(Utils.checkBadChars(trimmedKey) == null);
            Preconditions.checkArgument(!Utils.containsWhiteSpace(trimmedKey));
            final String knownKey = Utils.checkKey(trimmedKey);
            Preconditions.checkArgument(knownKey != null);

            final String trimmedValue = Utils.trim(value).toUpperCase(Locale.US);
            Preconditions.checkArgument(!trimmedValue.isEmpty());
            Preconditions.checkArgument(Utils.checkCharSet(trimmedValue) == null);
            Preconditions.checkArgument(Utils.checkBadChars(trimmedValue) == null);
            Preconditions.checkArgument(!Utils.containsWhiteSpace(trimmedValue));

            itemMap.put(knownKey, trimmedValue);
        }

        // Make sure address is not empty
        Preconditions.checkArgument(!itemMap.isEmpty());

        // Check required keys
        for (final String rkey : Utils.REQ_KEYS) {
            Preconditions.checkArgument(itemMap.containsKey(rkey));
        }

        // Construct AFTN address
        String aftnAddr = itemMap.get("CN");
        if (aftnAddr == null) {
            aftnAddr = itemMap.get("OU1");
        }
        Preconditions.checkArgument(aftnAddr != null);

        // comma-separated X.400 address
        final String ubimexString = do_createUbimexString(itemMap);

        // done
        return new AmhsAddressImpl(aftnAddr, ubimexString);
    }

    private static String do_createUbimexString(final Map<String, String> itemMap) {
        // Create a list of normalized key/value pairs in the right order
        final List<String> itemStringList = new ArrayList<>(6);
        for (final String rkey : Utils.ALL_KEYS) {
            final String value = itemMap.get(rkey);
            if (value != null) {
                itemStringList.add(rkey + "=" + value);
            }
        }

        // Create a comma-separated and a slash-separated string
        return String.join(",", itemStringList);
    }

}
