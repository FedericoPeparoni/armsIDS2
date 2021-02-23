package ca.ids.abms.amhs.parsers.amhs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressWarnings ("squid:CommentedOutCodeLine")
public class RawAmhsMessage {

    public boolean getSkipTransmit() {
        return skipTransmit;
    }

    public void setSkipTransmit(boolean skipTransmit) {
        this.skipTransmit = skipTransmit;
    }

    public String getOriginatorAddr() {
        return originatorAddr;
    }

    public void setOriginatorAddr(String originatorAddr) {
        this.originatorAddr = originatorAddr;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public List<String> getDestAddrList() {
        return destAddrList;
    }

    public void setDestAddrList(List<String> destAddrList) {
        this.destAddrList = destAddrList;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getRawMessageText() {
        return rawMessageText;
    }

    public void setRawMessageText(String rawMessageText) {
        this.rawMessageText = rawMessageText;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    /*
     * public AtnPayloadType getPayloadType () { return payloadType; } public void
     * setPayloadType (AtnPayloadType payloadType) { this.payloadType = payloadType;
     * }
     */

    public String getBodyId() {
        return bodyId;
    }

    public void setBodyId(String bodyId) {
        this.bodyId = bodyId;
    }

    public void setResponsibleAccounts(List<String> responsibleAccounts) {
        this.responsibleAccounts = responsibleAccounts;
    }

    public List<String> getResponsibleAccounts() {
        return this.responsibleAccounts;
    }

    public String getOptionalHeaderInformation() {
        return optionalHeaderInformation;
    }

    public void setOptionalHeaderInformation(String optionalHeaderInformation) {
        this.optionalHeaderInformation = optionalHeaderInformation;
    }

    private boolean skipTransmit = false;
    private String originatorAddr;
    private String optionalHeaderInformation;
    private String priority;
    private List<String> destAddrList;
    private Date creationDate;
    private String body;
    private String rawMessageText;
    private String filename;
    // private AtnPayloadType payloadType;
    private String bodyId;
    private List<String> responsibleAccounts = new ArrayList<>();

}
