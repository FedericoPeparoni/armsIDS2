package ca.ids.abms.spreadsheets.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class CellDto<T> {

    @JsonIgnore
    private T content;

    private int width;

    protected CellDto() {
        super();
        this.content = null;
        this.width = 1;
    }

    protected CellDto(final T content) {
        super();
        this.content = content;
        this.width = 1;
    }

    protected CellDto(final T content, final int width) {
        super();
        this.content = content;
        this.width = width;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    @JsonGetter
    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
