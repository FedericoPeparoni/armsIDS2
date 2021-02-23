package ca.ids.abms.modules.reports2.common;

import ca.ids.abms.modules.common.dto.MediaDocument;

/**
 * Report document file content, name and MIME type
 */
public class ReportDocument extends MediaDocument {

    public ReportDocument (final String fileName, final String contentType, final byte[] data) {
        super(fileName, contentType, data);
    }
}
