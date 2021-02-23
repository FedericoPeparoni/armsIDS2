package ca.ids.abms.spreadsheets.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import org.apache.commons.lang.StringUtils;

public class CellTitleDto extends CellDto<String> {

    public CellTitleDto() {
        super(StringUtils.EMPTY, 1);
    }

    public CellTitleDto(final String title) {
        super(title, 1);
    }

    public CellTitleDto(final String title, final int width) {
        super(title, width);
    }

    @JsonGetter
    public String getTitle() {
        return super.getContent();
    }

    public void setTitle(String title) {
        super.setContent(title);
    }
}
