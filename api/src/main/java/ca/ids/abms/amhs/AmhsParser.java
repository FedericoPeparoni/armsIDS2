package ca.ids.abms.amhs;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ca.ids.abms.amhs.parsers.amhs.RawAmhsMessage;
import ca.ids.abms.amhs.parsers.amhs.RawAmhsParser;
import ca.ids.abms.atnmsg.amhs.addr.AmhsAddressCreator;

@Component
class AmhsParser {

    @SuppressWarnings("squid:CommentedOutCodeLine")
    public AmhsMessage parse(final byte[] bytes, final String content, final String filename, final LocalDateTime filingDateTimeUpperLimit) {
        LOG.debug("parsing AMHS file {}", filename);

        final RawAmhsMessage r = new RawAmhsParser().parse(bytes);
//        final AmhsAddress origAddr = amhsAddressCreator.parseUbimexString(r.getOriginatorAddr());
//        final String aftnOrigAddr = origAddr.toAftnString();
//        final List<AmhsAddress> destAddrList = r.getDestAddrList() == null ? new ArrayList<>()
//                : r.getDestAddrList().stream().map(amhsAddressCreator::parseUbimexString).collect(Collectors.toList());
//        final List<String> aftnDestAddrList = destAddrList.stream().map(AmhsAddress::toAftnString)
//                .collect(Collectors.toList());
//		final LocalDateTime origDate = r.getCreationDate() == null
//				? null
//				: LocalDateTime.ofInstant (Instant.ofEpochMilli(r.getCreationDate().getTime()), ZoneOffset.UTC);

        final AmhsMessage m = new AmhsMessage();
        m.setBody(r.getBody());
//        m.setAftnOrigAddr(aftnOrigAddr);
        m.setFilingDateTime (createFilingDateTime (r.getCreationDate(), filingDateTimeUpperLimit));
//		m.setOrigDateTimeUtc (origDate);
//        m.setAftnDestAddrList(aftnDestAddrList);
        m.setFilename(filename);
        m.setRawMessageText(content);

        return m;
    }

    static LocalDateTime createFilingDateTime (final Date javaDate, final LocalDateTime base) {
        final LocalDateTime dflt = base == null ? base : LocalDateTime.now();
        if (javaDate == null) {
            return dflt;
        }
        return LocalDateTime.ofInstant (
                Instant.ofEpochMilli (javaDate.getTime()),
                ZoneOffset.UTC
        );
    }

    public AmhsParser(AmhsAddressCreator amhsAddressCreator) {
//        this.amhsAddressCreator = amhsAddressCreator;
    }

    private static final Logger LOG = LoggerFactory.getLogger(AmhsParser.class);
//    private final AmhsAddressCreator amhsAddressCreator;

}
