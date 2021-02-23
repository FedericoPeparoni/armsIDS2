package ca.ids.abms.modules.reports2.template;

import ca.ids.abms.util.csv.annotations.CsvProperty;

public class ReportTemplateCsvExportModel {

    @CsvProperty(value = "Report Name")
    private String reportTemplateName;

    @CsvProperty(value = "SQL Query")
    private String sqlQuery;

    private String parameters;

    public String getReportTemplateName() {
        return reportTemplateName;
    }

    public void setReportTemplateName(String reportTemplateName) {
        this.reportTemplateName = reportTemplateName;
    }

    public String getSqlQuery() {
        return sqlQuery;
    }

    public void setSqlQuery(String sqlQuery) {
        this.sqlQuery = sqlQuery;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }
}
