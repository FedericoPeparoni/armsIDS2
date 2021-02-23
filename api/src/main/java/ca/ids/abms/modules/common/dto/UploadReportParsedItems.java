package ca.ids.abms.modules.common.dto;

import java.util.List;

public class UploadReportParsedItems<T> {

    private final List<T> processed;
    private final List<UploadReportViewModel> rejected;

    private UploadReportParsedItems(final List<T> processed, final List<UploadReportViewModel> rejected) {
        this.processed = processed;
        this.rejected = rejected;
    }

    public static <T> UploadReportParsedItems<T> createInstance(final List<T> processed, final List<UploadReportViewModel> rejected){
        return new UploadReportParsedItems<>(processed, rejected);
    }

    public List<T> getProcessed() {
        return processed;
    }

    public List<UploadReportViewModel> getRejected() {
        return rejected;
    }
}
