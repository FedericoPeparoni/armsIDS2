package ca.ids.abms.spreadsheets.dto;

public class WorkbookDto {

    public WorkbookDto() {
        super();
        this.data = new SheetDto();

    }

    private SheetDto data;

    public SheetDto getData() {
        return data;
    }

    public void setData(SheetDto data) {
        this.data = data;
    }
}
