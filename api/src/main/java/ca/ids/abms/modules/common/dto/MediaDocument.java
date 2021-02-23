package ca.ids.abms.modules.common.dto;

/**
 * Media document file content, name and MIME type
 */
public class MediaDocument {

    private final String fileName;

    private final String contentType;

    private final byte[] data;

    public MediaDocument (final String fileName, final String contentType, final byte[] data) {
        this.contentType = contentType;
        this.fileName = fileName;
        this.data = data;
    }

    public String contentType() {
        return contentType;
    }

    public int contentLength() {
        return data != null ? data.length : 0;
    }

    public String fileName() {
        return fileName;
    }

    public byte[] data() {
        return data;
    }
}
