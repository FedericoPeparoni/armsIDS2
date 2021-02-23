package ca.ids.abms.modules.reports2.template;

import ca.ids.abms.modules.common.dto.EmbeddedFileDto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ReportTemplateViewModel extends EmbeddedFileDto {

    private Integer id;

    @NotNull
    @Size(max = 50)
    private String reportTemplateName;

    @NotNull
    @Size(max = 200)
    private String parameters;
    
    @NotNull
    @Size(max = 200)
    private String sqlQuery;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReportTemplateViewModel that = (ReportTemplateViewModel) o;

        return id != null ? id.equals(that.id) : that.id == null;

    }

    public Integer getId() {
        return id;
    }

    public String getParameters() {
        return parameters;
    }

    public String getReportTemplateName() {
        return reportTemplateName;
    }

    public String getSqlQuery() {
        return sqlQuery;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public void setId(Integer aId) {
        id = aId;
    }

    public void setParameters(String aParameters) {
        parameters = aParameters;
    }

    public void setReportTemplateName(String aReportTemplateName) {
        reportTemplateName = aReportTemplateName;
    }

    public void setSqlQuery(String aSqlQuery) {
        sqlQuery = aSqlQuery;
    }

    @Override
    public String toString() {
        return "ReportTemplateViewModel [id=" + id + ", reportTemplateName=" + reportTemplateName + ", parameters="
                + parameters + ", sqlQuery=" + sqlQuery + "]";
    }

    
}
