package ca.ids.abms.modules.amhsconfiguration;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.ErrorDTO;

@Service
public class AmhsConfigurationValidator {
    
    public void validate (final AmhsConfiguration x) {
        
        // descr
        x.setDescr(StringUtils.stripToNull(x.getDescr()));
        
        // protocol
        checkEnum ("protocol", "Protocol", x.getProtocol(), "P1", "P3");
        
        // rtseCheckpointSize
        checkGreater ("rtse_checkpoint_size", "RTSE Checkpoint Size", x.getRtseCheckpointSize(), 0);
        
        // rtseCheckpointSize
        checkGreater ("rtse_window_size", "RTSE Window Size", x.getRtseWindowSize(), 0);
        
        // maxConn
        checkBetween ("max_conn", "Max Connections", x.getMaxConn(), 1, 100);
        
        // pingDelay
        if (x.getPingEnabled()) {
            if (x.getPingDelay() == null) {
                x.setPingDelay (Math.max (x.getRemoteIdleTime() - 10, 10));
            }
            else {
                checkGreater ("ping_delay", "Ping Delay", x.getPingDelay(), 9);
            }
        }
        
        // networkDevice
        final String networkDevice = StringUtils.trimToNull (x.getNetworkDevice());
        if (networkDevice != null) {
            if (!RE_NETWORK_DEVICE.matcher (networkDevice).matches()) {
                throw error ("networkDevice", "Network Device", "Expecting letters, digits, ':', '.', '_' or '-'");
            }
        }
        x.setNetworkDevice (networkDevice);
        
        // localPasswd
        if (x.getLocalBindAuthenticated()) {
            checkString ("local_mta_passwd", "Local MTA Password", x.getLocalPasswd(), 1, 26);
        }
        else {
            x.setLocalPasswd("default");
        }
        
        // localHostname
        final String localHostname = StringUtils.trimToNull (x.getLocalHostname());
        if (localHostname != null) {
            checkString ("local_hostname", "Local MTA Name", localHostname, 1, 50);
            
            // localIpaddr
            final String localIpaddr = StringUtils.trimToNull (x.getLocalIpaddr());
            if (localIpaddr != null) {
                x.setLocalIpaddr (checkIpaddr ("local_ipaddr", "Local IP Address", localIpaddr));
            }
        }
        else {
            x.setLocalIpaddr (null);
        }
        
        // localPort
        checkBetween ("localPort", "Local Port", x.getLocalPort(), 1, 65535);

        // localTsapAddr
        x.setLocalTsapAddr (checkTsapAddr (x.getLocalTsapAddrIsHex(), "local_tsap_addr", "Local TSAP", x.getLocalTsapAddr()));
        
        // remoteHostname
        final String remoteHostname = x.getRemoteHostname();
        if (remoteHostname != null) {
            checkString ("remote_hostname", "Remote MTA Name", remoteHostname, 1, 50);
            // remoteIpaddr
            final String remoteIpaddr = StringUtils.trimToNull (x.getRemoteIpaddr());
            if (remoteIpaddr != null) {
                x.setRemoteIpaddr (checkIpaddr ("remote_ipaddr", "Remote IP Address", remoteIpaddr));
            }
        }
        else {
            x.setRemoteIpaddr (null);
        }
        
        // remotePasswd
        if (x.getRemoteBindAuthenticated()) {
            checkString ("remote_mta_passwd", "Remote MTA Password", x.getRemotePasswd(), 1, 26);
        }
        else {
            x.setRemotePasswd ("default");
        }
        
        // remoteTsapAddr
        x.setRemoteTsapAddr (checkTsapAddr (x.getRemoteTsapAddrIsHex(), "remote_tsap_addr", "Remote TSAP", x.getRemoteTsapAddr()));
    }
    
    private RuntimeException error (final String field, final String fieldName, final String fieldErr) {
        final ErrorDTO dto = new ErrorDTO.Builder()
                .setErrorMessage (String.format ("%s: %s", fieldName, fieldErr))
                .addInvalidField (AmhsConfiguration.class, field, fieldErr)
                .build();
        return new CustomParametrizedException (dto);
    }

    private String checkTsapAddr (final Boolean isHex, final String field, final String fieldName, final String addr) {
        final String addrValue = StringUtils.trimToEmpty (addr);
        // hex
        if (isHex != null && isHex) {
            if (!isHex (addrValue)) {
                throw error (field, fieldName, "Expecting a hex string");
            }
            checkString (field, fieldName, addrValue, 2, 40);
            if (addr.length() % 2 != 0) {
                throw error (field, fieldName, "Expecting a hex string of an even length [2..40]");
            }
            return addrValue.toUpperCase (Locale.US);
        }
        checkString (field, fieldName, addrValue, 1, 20);
        return addrValue;
    }
    
    private <T> void checkEnum (final String field, final String fieldName, final T v, @SuppressWarnings("unchecked") final T... allowed) {
        boolean found = false;
        for (final T x: allowed) {
            if (Objects.equals(v, x)) {
                found = true;
                break;
            }
        }
        if (!found) {
            throw error (field, fieldName, String.format ("Expecting one of %s", Arrays.asList (allowed)));
        }
    }
    
    private <T extends Comparable <T>> void checkGreater (final String field, final String fieldName, final T v, final T threshold) {
        if (v.compareTo (threshold) < 1) {
            throw error (field, fieldName, String.format ("Expecting a value greater than %s", threshold.toString()));
        }
    }
    
    private <T extends Comparable <T>> void checkBetween (final String field, final String fieldName, final T v, final T min, final T max) {
        if (v.compareTo (min) < 1 || v.compareTo (max) >= 1) {
            throw error (field, fieldName, String.format ("Expecting a value between %s and %s",
                    min.toString(), max.toString()));
        }
    }
    
    private void checkString (final String field, final String fieldName, final String value, final Integer minLen, final Integer maxLen) {
        if (containsInvalidChars (value)) {
            throw error (field, fieldName, "Field contains invalid characters");
        }
        if (value.length() < minLen) {
            throw error (field, fieldName, String.format ("Expecting >= %d character(s)", minLen));
        }
        if (value.length() > maxLen) {
            throw error (field, fieldName, String.format ("Expecting <= %d character(s)", minLen));
        }
    }
    
    private String checkIpaddr (final String field, final String fieldName, final String s) {
        final String s1 = StringUtils.trimToEmpty (s);
        if (!RE_IPADDR.matcher(s1).matches ()) {
            throw error (field, fieldName, "Invalid IP address");
        }
        return s1;
    }
    private static final Pattern RE_IPADDR = Pattern.compile(
            "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
    
    private static boolean isHex (final String s) {
        return RE_HEX.matcher(s).matches();
    }
    private static final Pattern RE_HEX = Pattern.compile ("^[0-9a-fA-F]+$");

    // Determine whether the given string contains invalid characters.
    // Some characters are used as delimiters or have special meaning to
    // various configuration files, so we don't allow them:
    // - double-quotes, backslashes, newlines, carriage returns: their presense will break the syntax of property files
    // - colons, spaces: used as delimiters in various UBIMEX config files
    // - non-printable chars: no reason to allow those
    // - chars outside of Latin 1 encoding: UBIMX config files must be saved in Latin-1 encoding, because
    //   that's how UBIATN treats them
    //
    // look for the following prohibited chars (Unicode/Latin 1 codepoints):
    // Hex     Dec      Descr
    // ---------------------------------------------
    // 00-1F   000-031  non-printable chars and space
    // 20      032      space
    // 22      034      double-quote "
    // 5C      092      backslash \
    // 3A      058      colon :
    // 23      035      number sign #
    // 24      036      dollar sign $
    // 7f      127      <DEL>
    // 80-9F   128-159  undefined in Latin-1
    // A0      160      <NBSP>
    private static final Pattern RE_INVALID_CHARS = Pattern
            .compile("[\\x00-\\x1f\\x20\\x22\\x5c\\x3a\\x23\\x24\\x7f\\x80-\\x9f\\xa0]");
    static boolean containsInvalidChars(final String s) {
        return RE_INVALID_CHARS.matcher(s).find();
    }
    
    private static final Pattern RE_NETWORK_DEVICE = Pattern.compile ("^[a-zA-Z0-9:._-]+$");
    
}
