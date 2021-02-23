package ca.ids.abms.spreadsheets;

import ca.ids.abms.spreadsheets.dto.WorkbookDto;

import java.io.OutputStream;

/**
 * Base interface for all spreadsheet-related read-only views.
 *
 */
public interface SSView {

    /**
     * Save this spreadsheet to the given stream (i.e., as XLSX data).
     */
    public void save (final OutputStream stream);

    /**
     * Get the base name of the file used to create this spreadsheet, if known.
     */
    public String baseName();

    /**
     * Get the MIME type of this spreadsheet.
     */
    public String mimeType();

    /**
     * Return the number of bytes that would be produced by @{link #save}
     */
    public int contentLength();

    /**
     * Return an HTML representation of this spreadsheet.
     */
    public String toHtml();

    /**
     * Return a model easy to browse that represents this spreadsheet.
     */
    public WorkbookDto toModel();

}
