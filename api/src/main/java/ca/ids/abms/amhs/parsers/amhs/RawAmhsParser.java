package ca.ids.abms.amhs.parsers.amhs;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.ibm.icu.text.MessageFormat;

import ca.ids.abms.amhs.AmhsMessageParseError;

/**
 * Copied/adapted from CRONOS
 */
@SuppressWarnings ({
    "squid:S135",
    "squid:S1066",
    "squid:S1134",
    "squid:S3626",
    "squid:S3776",
    "squid:AssignmentInSubExpressionCheck",
    "squid:CommentedOutCodeLine",
})
public class RawAmhsParser {

    public RawAmhsMessage parse(final byte[] rawData) {
        this.rawData = rawData;
        this.ubimexMessage = null;
        this.amhsXmlText = null;
        this.amhsRawMessageText = null;
        this.amhsMessageBody = null;
        this.amhsMessageBodyClean = null;
        this.amhsDestAddrList = null;
        this.amhsOriginatorAddr = null;
        this.amhsMessageCreationDate = null;
        this.amhsPriority = null;

        final RawAmhsMessage msg = new RawAmhsMessage();

        try {
            do_createXmlText();
            do_parseXml();
            do_findPriority();
            do_findCreationDate();
            do_findOrignatorAddr();
            do_findDestAddrList();
            do_findMessageBody();
            do_stripBodyHeaders();
            do_findFilename();
            do_findPayloadType();
            do_logFields();
            do_createMessage(msg);
        } catch (Exception x) {
            throw new AmhsMessageParseError(x);
        }

        return msg;
    }

    // ------------------------- private ------------------------------------

    private interface ErrorHandler {
        public void handleError(final String badValue);
    }

    private void do_logFields() {
        if (LOG.isTraceEnabled()) {
            boolean ok = false;
            final StringBuilder buf = new StringBuilder();
            buf.append("\nFound AMHS message fields:\n");
            if (this.amhsPriority != null) {
                buf.append("  amhsPriority = [" + this.amhsPriority + "]\n");
                ok = true;
            }
            if (this.amhsMessageCreationDate != null) {
                buf.append("  amhsMessageCreationDate = [" + do_formatUtcDate(this.amhsMessageCreationDate) + "]\n");
                ok = true;
            }
            if (this.amhsOriginatorAddr != null) {
                buf.append("  amhsOriginatorAddr = [" + this.amhsOriginatorAddr + "]\n");
                ok = true;
            }
            if (this.amhsDestAddrList != null) {
                buf.append("  amhsDestAddrList = [" + do_join(";", this.amhsDestAddrList) + "]\n");
                ok = true;
            }
            if (this.amhsMessageBodyClean != null) {
                buf.append("-------------------------------\n");
                buf.append(amhsMessageBodyClean);
                buf.append("\n");
                ok = true;
            }
            if (ok) {
                LOG.trace("{}", buf);
            }
        }
    }

    private void do_findFilename() {
        if (this.ubimexMessage.AmhsMte != null && this.ubimexMessage.AmhsMte.OrgnlFlNm != null) {
            this.amhsFilename = this.ubimexMessage.AmhsMte.OrgnlFlNm;
        }
    }

    private void do_findPayloadType() {
//        if (this.amhsMessageBodyClean != null) {
//            this.amhsPayloadType = ParseUtils.findAtnMsgType (this.amhsMessageBodyClean);
//        } else {
//            this.amhsPayloadType = AtnPayloadType.OTHER;
//        }
    }

    private void do_createMessage(RawAmhsMessage msg) {
        msg.setBodyId(amhsMessageBodyId);
        msg.setBody(this.amhsMessageBodyClean);
        msg.setDestAddrList(this.amhsDestAddrList);
        msg.setOriginatorAddr(this.amhsOriginatorAddr);
        msg.setCreationDate(this.amhsMessageCreationDate);
        msg.setPriority(this.amhsPriority);
        msg.setRawMessageText(this.amhsRawMessageText);
        msg.setFilename(amhsFilename);
        // msg.setPayloadType (amhsPayloadType);
        msg.setResponsibleAccounts(this.activeAccount);
    }

    private GregorianCalendar do_createCalendar() {
        return new GregorianCalendar(UTC_TIMEZONE);
    }

    private static String do_join(final String glue, final Collection<String> parts) {
        final StringBuilder buf = new StringBuilder();
        String sep = "";
        for (final String part : parts) {
            if (part != null) {
                buf.append(sep).append(part);
                sep = glue;
            }
        }
        return buf.toString();
    }

    private Date do_parseDate(final String str, final ErrorHandler errorHandler) {
        // 2014 12 25 00 00 00 (without spaces)
        final Matcher m = Pattern.compile("^(\\d{4})(\\d{2})(\\d{2})(\\d{2})(\\d{2})(\\d{2})").matcher(str);
        if (!m.matches()) {
            errorHandler.handleError(str);
            return null;
        }
        final int year = Integer.parseInt (m.group(1));
        final int month = Integer.parseInt(m.group(2)) - 1;
        final int day = Integer.parseInt (m.group(3));
        final int hour = Integer.parseInt (m.group(4));
        final int minute = Integer.parseInt (m.group(5));
        final int second = Integer.parseInt (m.group(6));
        final GregorianCalendar cal = do_createCalendar();
        cal.set(year, month, day, hour, minute, second);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    // Create a String from raw data for further XML parsing
    private static final Pattern RE_BODY_START_TAG = Pattern.compile("<BdyPrt>.*?<Cntnt>", Pattern.DOTALL);
    private static final Pattern RE_BODY_END_TAG = Pattern.compile("</Cntnt>");

    private void do_createXmlText() throws UnsupportedEncodingException {
        this.amhsRawMessageText = new String(this.rawData, encoding);
        this.amhsXmlText = amhsRawMessageText;
        this.amhsMessageBody = null;

        // FIXME: remove this after bug 11103 is fixed.
        //
        // Here we try to extract the body directly bypassing the XML parser. This is
        // because
        // UBIATN doesn't escape element contents properly, which causes Java XML parser
        // exceptions.
        //
        // Instead we find all text between <Cntnt>...</Cntnt> tags directly by
        // searching the original text and empty-out the contents of that tag.
        //
        // Once bug 11103 is fixed remove the rest of this function please.

        // find <BdyPrt> tag followed by a <Cntnt> tag
        final Matcher startMatcher = RE_BODY_START_TAG.matcher(amhsRawMessageText);
        if (startMatcher.find()) {
            int bodyStart = startMatcher.end();
            final String suffix = amhsRawMessageText.substring(bodyStart);
            final Matcher endMatcher = RE_BODY_END_TAG.matcher(suffix);
            if (endMatcher.find()) {
                final int bodyEnd = bodyStart + endMatcher.start();
                this.amhsXmlText = amhsRawMessageText.substring(0, bodyStart) + amhsRawMessageText.substring(bodyEnd);
                this.amhsMessageBody = amhsRawMessageText.substring(bodyStart, bodyEnd);
                return;
            }
        }
    }

    private void do_parseXml() throws UnsupportedEncodingException, JAXBException {
        final byte[] xmlBytes = this.amhsXmlText.getBytes(encoding);
        final ByteArrayInputStream stream = new ByteArrayInputStream(xmlBytes);
        final JAXBContext context = JAXBContext.newInstance(UbimexMessage.class);
        final Unmarshaller unmarshaller = context.createUnmarshaller();
        final UbimexMessage message = (UbimexMessage) unmarshaller.unmarshal(stream);
        this.ubimexMessage = message;
    }

    private void do_findPriority() {
        if (ubimexMessage.AmhsMte != null && ubimexMessage.AmhsMte.AftnPri != null) {
            final String value = ubimexMessage.AmhsMte.AftnPri.trim().toUpperCase(Locale.US);
            if (!value.matches("^[A-Z]{2}$")) {
                throw error("invalid priority \"{0}\"", value);
            }
            this.amhsPriority = value;
            return;
        }
        if (ubimexMessage.AmhsIpm != null && ubimexMessage.AmhsIpm.ImprtncId != null) {
            final String code = ubimexMessage.AmhsIpm.ImprtncId.trim();
            if (code.equals("2")) {
                this.amhsPriority = "SS";
            } else if (code.equals("1")) {
                this.amhsPriority = "GG";
            } else if (code.equals("0")) {
                this.amhsPriority = "FF";
            } else {
                this.amhsPriority = "GG";
            }
            return;
        }
        throw error("message priority not found");
    }

    private void do_findCreationDate() {
        if (ubimexMessage.AmhsMte != null && ubimexMessage.AmhsMte.CrtnTm != null) {
            final String valueString = ubimexMessage.AmhsMte.CrtnTm.trim();
            if (!valueString.isEmpty()) {
                this.amhsMessageCreationDate = do_parseDate(valueString,
                        badValue->{ throw error("invalid message creation time {0}", badValue); }
                );
            }
        }
        if (this.amhsMessageCreationDate == null) {
            throw error("message creation time not found");
        }
    }

    private void do_findOrignatorAddr() {

        String origAddr = null;

        // First try /AmhsMsg/AmhsMte/Orgntr
        if (ubimexMessage.AmhsMte != null && ubimexMessage.AmhsMte.Orgntr != null) {
            final String addr = ubimexMessage.AmhsMte.Orgntr.trim();
            if (!addr.isEmpty()) {
                origAddr = ubimexMessage.AmhsMte.Orgntr.trim();
            }
        }

        // Next try /AmhsMsg/AmhsIpm/IpmId/Orgntr
        if (origAddr == null &&
                ubimexMessage.AmhsIpm != null &&
                ubimexMessage.AmhsIpm.IpmId != null &&
                ubimexMessage.AmhsIpm.IpmId.Orgntr != null) {
            final String addr = ubimexMessage.AmhsIpm.IpmId.Orgntr.trim();
            if (!addr.isEmpty()) {
                origAddr = addr;
            }
        }

        // originator not found
        if (origAddr == null) {
            throw error("Originator address not found");
        }

        // Parse/validate/normalize
        final String normalizedAddr = this.do_normalizeAddress(origAddr, badAddr->{
            throw error("Invalid originator address \"{0}\"", badAddr);
        });

        // done
        this.amhsOriginatorAddr = normalizedAddr;
    }

    private void do_findDestAddrList() {
        if (ubimexMessage.AmhsIpm != null) {
            this.amhsDestAddrList = new ArrayList<>(4);
            // "TO" recipient addresses
            this.amhsDestAddrList.addAll(do_normalizeIpmAddressList(ubimexMessage.AmhsIpm.ToRcpt, badAddr->{
                throw error("invalid \"To\" recepient address \"{0}", badAddr);
            }));

            // "CC" recipient addresses
            this.amhsDestAddrList.addAll(do_normalizeIpmAddressList(ubimexMessage.AmhsIpm.CcRcpt, badAddr->{
                throw error("invalid \"CC\" recepient address \"{0}", badAddr);
            }));

            // "BCC" recipient addresses
            this.amhsDestAddrList.addAll(do_normalizeIpmAddressList(ubimexMessage.AmhsIpm.BccRcpt, badAddr->{
                throw error("invalid \"BCC\" recepient address \"{0}", badAddr);
            }));

            // AllRcpt addresses in the AmhsMte section
            this.amhsDestAddrList.addAll(do_normalizeMteRcptList(ubimexMessage.AmhsMte.AllRcpt, badAddr->{
                throw error("invalid \"AllRcpt\" recepient address \"{0}", badAddr);
            }));

            // remove duplicates
            this.amhsDestAddrList = this.amhsDestAddrList.stream().distinct().collect(Collectors.toList());
        }
        if (this.amhsDestAddrList == null || this.amhsDestAddrList.isEmpty()) {
            this.amhsDestAddrList = null;
            throw error("no recepient addresses found");
        }
    }

    private void do_findMessageBody() {
        if (this.amhsMessageBody != null)
            return;
        if (ubimexMessage.AmhsIpm != null && ubimexMessage.AmhsIpm.BdyPrt != null
                && !ubimexMessage.AmhsIpm.BdyPrt.isEmpty()) {
            final Map<Integer, String> bodyPartMap = new HashMap<>();
            final Map<Integer, String> bodyPartIdMap = new HashMap<>();
            int count = 1;
            for (final UbimexMessage.AmhsIpmType.BdyPrtType rec : ubimexMessage.AmhsIpm.BdyPrt) {
                if (rec != null && rec.Cntnt != null) {
                    int formatCode = 0;
                    int seqNum = count++;
                    // body part format code, look for "0" (basic text)
                    if (rec.Frmt != null) {
                        final String valueString = rec.Frmt.trim();
                        if (!valueString.isEmpty()) {
                            try {
                                formatCode = Integer.valueOf(valueString);
                            } catch (NumberFormatException x) {
                                throw error("invalid body part format \"{0}\", expecting an integer", valueString);
                            }
                        }
                    }

                    // sequence number
                    if (rec.Nbr != null) {
                        final String valueString = rec.Nbr.trim();
                        if (!valueString.isEmpty()) {
                            try {
                                seqNum = Integer.valueOf(valueString);
                                if (bodyPartMap.containsKey(seqNum)) {
                                    throw error("multiple body parts with sequence number \"{0}\" found", seqNum);
                                }
                            } catch (NumberFormatException x) {
                                throw error("invalid body part sequence number \"{0}\", expecting an integer",
                                        valueString);
                            }
                        }
                    }

                    if (formatCode != 0)
                        continue;

                    // add this body part to the map
                    bodyPartMap.put(seqNum, rec.Cntnt);

                    // add body id to the map
                    String id = rec.MsgId;
                    if (id != null) {
                        if ((id = id.trim()).isEmpty())
                            id = null;
                    }
                    bodyPartIdMap.put(seqNum, id);
                }
            }
            final List<Integer> keyList = new ArrayList<>(bodyPartMap.keySet());
            Collections.sort(keyList);

            if (bodyPartMap.isEmpty()) {
                throw error("no text attachments found");
            }
            if (bodyPartMap.size() > 0) {
                if (bodyPartMap.size() > 1) {
                    LOG.warn("multiple text attachments found, using the first one as message body");
                }
                this.amhsMessageBody = bodyPartMap.get(keyList.get(0)).trim();
                this.amhsMessageBodyId = bodyPartIdMap.get(keyList.get(0));
                return;
            }
        }
        throw error("no text attachments found");
    }

    /*
     * UBIATN adds AFTN-like headers in the beginning of message bodies, like so:
     *
     * <Cntnt>^APRI: GG^M FT: 271931^M OHI: ^M ^BACTUAL MESSAGE BODY ... </Cntnt>
     *
     * i.e., it adds "headers" with SOH,STH,STX delimiters
     *
     * Strip it out
     */
    private void do_stripBodyHeaders() {
        if (this.amhsMessageBody != null) {
            // Look for ^B, ASCII STX, code 2
            int index = amhsMessageBody.indexOf('\u0002');
            if (index >= 0) {
                for (; index < amhsMessageBody.length(); ++index) {
                    if (amhsMessageBody.charAt(index) != '\u0002')
                        break;
                }
                final String tmp = amhsMessageBody.substring(index);
                this.amhsMessageBodyClean = do_stripWhiteSpace(tmp);
                return;
            }
            this.amhsMessageBodyClean = do_stripWhiteSpace(this.amhsMessageBody);
        }
    }

    private static String do_stripWhiteSpace(final String s) {
        int start = 0;
        int end;
        for (; start < s.length(); ++start) {
            final char c = s.charAt(start);
            final int C = (int) c;
            // TAB NL VT FF CR SP
            if (C == 0x09 || C == 0x0a || C == 0x0b || C == 0x0c || C == 0x0d || C == 0x20) {
                continue;
            }
            break;
        }
        for (end = s.length() - 1; end >= 0; --end) {
            final char c = s.charAt(end);
            final int C = (int) c;
            // TAB NL VT FF CR SP
            if (C == 0x09 || C == 0x0a || C == 0x0b || C == 0x0c || C == 0x0d || C == 0x20) {
                continue;
            }
            break;
        }
        if (start < end) {
            return s.substring(start, end + 1);
        }
        return "";
    }

    private String do_formatUtcDate(final Date date) {
        final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        f.setTimeZone(UTC_TIMEZONE);
        return f.format(date) + "Z";
    }

    private List<String> do_normalizeIpmAddressList(final List<UbimexMessage.AmhsIpmType.RcpntType> list,
            final ErrorHandler errorHandler) {
        final ArrayList<String> result = new ArrayList<>();
        if (list != null) {
            result.ensureCapacity(list.size());
            for (final UbimexMessage.AmhsIpmType.RcpntType rec : list) {
                if (rec.Rcpt != null) {
                    final String addr = rec.Rcpt.trim();
                    if (!addr.isEmpty()) {
                        final String normalizedAddr = do_normalizeAddress(addr, errorHandler);
                        if (normalizedAddr != null)
                            result.add(normalizedAddr);
                    }
                }
            }
        }
        return result;
    }

    private List<String> do_normalizeMteRcptList(final List<UbimexMessage.AmhsMteType.AllRcptType> list,
            final ErrorHandler errorHandler) {
        final ArrayList<String> result = new ArrayList<>();
        if (list != null) {
            result.ensureCapacity(list.size());
            for (final UbimexMessage.AmhsMteType.AllRcptType rec : list) {
                if (rec.Rcpt != null) {
                    String rspnsblty = rec.Rspnsblty;
                    final String addr = rec.Rcpt.trim();
                    if (!addr.isEmpty()) {
                        final String normalizedAddr = do_normalizeAddress(addr, errorHandler);
                        if (normalizedAddr != null) {
                            result.add(normalizedAddr);
                            if (!Strings.isNullOrEmpty(rspnsblty) && rspnsblty.equals("1")) {
                                if (!this.activeAccount.contains(normalizedAddr)) {
                                    this.activeAccount.add(normalizedAddr);
                                }

                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    private String do_normalizeAddress(final String addr0, final ErrorHandler errorHandler) {
        final String addr = addr0.trim();
        final Matcher m = Pattern.compile("(?:CN|OU|OU1)\\s*=\\s*[A-Z0-9]{8}").matcher(addr);
        if (!m.find()) {
            errorHandler.handleError(addr);
            return null;
        }
        return addr;
    }

    private AmhsMessageParseError error(final String format, final Object... args) {
        return new AmhsMessageParseError (MessageFormat.format (format, args));
    }

    private static final Logger LOG = LoggerFactory.getLogger(RawAmhsParser.class);
    private byte[] rawData;
    private UbimexMessage ubimexMessage;
    private String amhsXmlText;
    private String amhsRawMessageText;
    private String amhsMessageBody;
    private String amhsMessageBodyClean;
    private String amhsMessageBodyId;
    private String amhsOriginatorAddr;
    private String amhsPriority;
    private String amhsFilename;
    // private AtnPayloadType amhsPayloadType;
    private Date amhsMessageCreationDate;
    private List<String> amhsDestAddrList;
    private List<String> activeAccount = new ArrayList<>();

    private String encoding = "ISO-8859-1";

    private static final TimeZone UTC_TIMEZONE = TimeZone.getTimeZone("UTC");
}
