package ca.ids.abms.modules.common.dto;

import ca.ids.abms.modules.util.models.VersionedViewModel;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.Size;

public abstract class EmbeddedFileDto extends VersionedViewModel {

    @JsonIgnore
    private byte[] documentContents;

    @Size(max = 128)
    private String documentMimeType;

    @Size(max = 128)
    private String documentFilename;

    public byte[] getDocumentContents() {
        return documentContents;
    }

    public void setDocumentContents(byte[] documentContents) {
        this.documentContents = documentContents;
    }

    public String getDocumentMimeType() {
        return documentMimeType;
    }

    public void setDocumentMimeType(String documentMimeType) {
        this.documentMimeType = documentMimeType;
    }

    public String getDocumentFilename() {
        return documentFilename;
    }

    public void setDocumentFilename(String documentFilename) {
        this.documentFilename = documentFilename;
    }
}
