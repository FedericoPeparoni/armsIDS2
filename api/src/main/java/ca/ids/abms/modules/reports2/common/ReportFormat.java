package ca.ids.abms.modules.reports2.common;

/**
 * Report formats
 */
public enum ReportFormat {
    json (".json", "application/json; charset=utf-8"),
    pdf  (".pdf",  "application/pdf"),
    csv  (".csv",  "text/csv; charset=utf-8"),
    txt  (".txt",  "text/plain; charset=utf-8"),
    docx (".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    xlsx (".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    xml  (".xml",  "application/xml; charset=utf-8"),
	zip  (".zip", "application/octet-stream");
    
    public static ReportFormat parse(String value) {
        try {
            return ReportFormat.valueOf(value);
            
        } catch (IllegalArgumentException ex) {
            return null;
        }        
    }
    
    public String fileNameSuffix() {
        return fileNameSuffix;
    }
    
    public String contentType() {
        return contentType;
    }

    // ---------------- private -------------------
    private final String fileNameSuffix;
    private final String contentType;
    
    private ReportFormat (final String fileNameSuffix, final String contentType) {
        this.fileNameSuffix = fileNameSuffix;
        this.contentType = contentType;
    }
    
    
}  