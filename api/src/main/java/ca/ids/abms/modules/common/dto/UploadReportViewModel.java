package ca.ids.abms.modules.common.dto;

import static ca.ids.abms.util.StringUtils.abbrev;

public class UploadReportViewModel {

    private String id;

    private String details;

    private String errorMessages;
    
    private String filename;
    
    private String rawText;

    public String getDetails() {
        return details;
    }

	public String getErrorMessages() {
        return errorMessages;
    }

	public String getFilename() {
        return filename;
    }

	public String getId() {
        return id;
    }

	public String getRawText() {
		return rawText;
	}

    public void setDetails(String aDetails) {
        details = aDetails;
    }

    public void setErrorMessages(String aErrorMessages) {
        errorMessages = aErrorMessages;
    }

    public void setFilename(String aFilename) {
        filename = aFilename;
    }

    public void setId(String aId) {
        id = aId;
    }

    public void setRawText(String aRawText) {
		rawText = aRawText;
	}

    @Override
	public String toString() {
		return "UploadReportViewModel [id=" + id + ", details=" + details + ", errorMessages=" + errorMessages
				+ ", filename=" + filename + ", rawText=\"" + abbrev (rawText) + "\"]";
	}
}
