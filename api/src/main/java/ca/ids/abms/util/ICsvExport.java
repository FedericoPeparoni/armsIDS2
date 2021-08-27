package ca.ids.abms.util;


import java.util.List;

public interface ICsvExport<TData, TCsvExportModel> {
    List<TCsvExportModel> toCsvModel(Iterable<TData> data);
}
