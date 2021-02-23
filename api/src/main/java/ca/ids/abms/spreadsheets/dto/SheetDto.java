package ca.ids.abms.spreadsheets.dto;

import java.util.ArrayList;
import java.util.List;

public class SheetDto {

    private List<List<CellTitleDto>> headers = new ArrayList<>();

    List<List<Double>> data = new ArrayList<>();

    public List<List<CellTitleDto>> getHeaders() {
        return headers;
    }

    public void addRowHeaders(List<CellTitleDto> rowHeader) {
        headers.add(rowHeader);
    }

    public List<CellTitleDto> addRowHeaders() {
        final ArrayList<CellTitleDto> rowHeaders = new ArrayList<>();
        this.headers.add(rowHeaders);
        return rowHeaders;
    }

    public SheetDto () {
        super();
    }

    public List<List<Double>> getData() {
        return data;
    }

    public void addRowData(List<Double> rowData) {
        data.add(rowData);
    }

    public List<Double> addRowData() {
        final ArrayList<Double> rowData = new ArrayList<>();
        data.add(rowData);
        return rowData;
    }
}
