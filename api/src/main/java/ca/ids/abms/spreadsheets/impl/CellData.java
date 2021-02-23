package ca.ids.abms.spreadsheets.impl;

import org.apache.poi.ss.usermodel.Cell;

class CellData <V> {
    public final V value;
    public final Cell cell;
    public static <U> CellData <U> create (final U value, final Cell cell) {
        return new CellData <U> (value, cell);
    }
    private CellData (final V value, final Cell cell) {
        this.value = value;
        this.cell = cell;
    }

}
