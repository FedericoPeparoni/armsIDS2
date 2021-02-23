package ca.ids.abms.amhs;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ca.ids.abms.amhs.parsers.aftn.RawAftnMessage;
import ca.ids.abms.amhs.parsers.aftn.RawAftnParser;
import ca.ids.abms.util.DateUtils;

@Component
class AftnParser {

    public AmhsMessage parse(final @SuppressWarnings ("squid:S1172") byte[] bytes, final String content, final String filename, final LocalDateTime filingDateTimeUpperLimit) {
        LOG.debug("parsing AFTN file {}", filename);

        final RawAftnMessage r = new RawAftnParser().parseMsg(content);

        final AmhsMessage m = new AmhsMessage();
        m.setBody(r.getMsgBody());
//        m.setAftnOrigAddr(r.getOriginator());
//        m.setAftnDestAddrList(r.getDestAddrList());
        m.setFilingDateTime (createFilingDateTime (r.getFilingDatetime(), filingDateTimeUpperLimit));
        m.setFilename(filename);
        m.setRawMessageText(content);

        return m;
    }

    static LocalDateTime createFilingDateTime (final String ddhhmm, final LocalDateTime base) {
        final LocalDateTime dflt = LocalDateTime.of (
                base.toLocalDate(),
                LocalTime.of (base.toLocalTime().getHour(), base.toLocalTime().getMinute())
        ).truncatedTo (ChronoUnit.MINUTES);
        if (ddhhmm == null) {
            return dflt;
        }
        // Make sure date/time string is 6 digits
        final Matcher m = RE_DDHHMM.matcher(ddhhmm);
        if (!m.matches()) {
            return dflt;
        }
        // Get the individual components
        final int day = Integer.parseInt (m.group(1));
        final int hour = Integer.parseInt (m.group(2));
        final int minute = Integer.parseInt (m.group(3));
        if (day < 1 || day > 31 || hour > 23 || minute > 59) {
            return dflt;
        }
        // find nearest date <= base that matches the day of month
        LocalDate d = base.toLocalDate();
        final LocalTime t = LocalTime.of (hour, minute);
        for (int i = 0; i < 3; ++i) {
            if (day <= DateUtils.getLastDayOfMonth (d.getYear(), d.getMonth().getValue())) {
                final LocalDateTime filingDateTime = LocalDateTime.of (
                        LocalDate.of (d.getYear(), d.getMonth().getValue(), day),
                        t);
                if (!filingDateTime.isAfter (dflt)) {
                    return filingDateTime;
                }
            }
            d = d.minusMonths(1);
        }
        return dflt;
    }

    private static final Pattern RE_DDHHMM = Pattern.compile ("^\\s*(\\d{2})(\\d{2})(\\d{2})\\s*$");
    private static final Logger LOG = LoggerFactory.getLogger(AftnParser.class);
}
