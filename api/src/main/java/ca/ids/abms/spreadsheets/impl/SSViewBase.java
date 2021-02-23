package ca.ids.abms.spreadsheets.impl;

import java.io.IOException;
import java.io.OutputStream;

import ca.ids.abms.spreadsheets.dto.WorkbookDto;
import com.google.common.base.Preconditions;

import ca.ids.abms.spreadsheets.SSException;
import ca.ids.abms.spreadsheets.SSView;

class SSViewBase implements SSView {

    @Override
    public void save(final OutputStream stream) {
        try {
            stream.write(rawData);
        } catch (final IOException x) {
            throw new SSException(x);
        }
    }

    @Override
    public String baseName() {
        return baseName;
    }

    @Override
    public String mimeType() {
        return mimeType;
    }

    @Override
    public int contentLength() {
        return rawData.length;
    }

    public String sheetName() {
        return sheetName;
    }

    @Override
    public String toHtml() {
        return "<!-- Not implemented -->";
    }

    @Override
    public WorkbookDto toModel() { return new WorkbookDto(); }

    protected SSViewBase(final byte[] rawData, final String baseName, final String sheetName, final String mimeType) {
        Preconditions.checkNotNull(rawData);
        Preconditions.checkNotNull(mimeType);
        this.rawData = rawData;
        this.baseName = baseName;
        this.sheetName = sheetName;
        this.mimeType = mimeType;
    }

    protected static final String TABLE_OPEN_1 = "<table class=\"";
    protected static final String TABLE_OPEN_2 = "\">";
    protected static final String TABLE_CLOSE = "</table>";
    protected static final String CAPTION_OPEN = "<caption>";
    protected static final String CAPTION_CLOSE = "</caption>";
    protected static final String ROW_OPEN = "<tr>";
    protected static final String ROW_CLOSE = "</tr>";
    protected static final String HEAD_OPEN = "<th>";
    protected static final String HEAD_SPAN_OPEN = "<th colspan=\"";
    protected static final String HEAD_SPAN_CLOSE = "\">";
    protected static final String HEAD_CLOSE = "</th>";
    protected static final String CELL_OPEN = "<td>";
    protected static final String CELL_CLOSE = "</td>";

    private final byte[] rawData;
    private final String baseName;
    private final String sheetName;
    private final String mimeType;
}
