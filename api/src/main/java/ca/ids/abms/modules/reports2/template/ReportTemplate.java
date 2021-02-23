package ca.ids.abms.modules.reports2.template;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ca.ids.abms.config.db.validators.UniqueKey;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;

@Entity
@UniqueKey(columnNames = "reportTemplateName")
public class ReportTemplate extends VersionedAuditedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Size(max = 50)
    private String reportTemplateName;

    @NotNull
    @Size(max = 200)
    private String sqlQuery;

    private byte[] templateDocument;

    private String mimeType;

    @NotNull
    @Size(max = 200)
    private String parameters;

    @Size(max = 128)
    @NotNull
    private String reportFilename;

    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ReportTemplate that = (ReportTemplate) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    public Integer getId() {
        return id;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getParameters() {
        return parameters;
    }

    public String getReportFilename() {
        return reportFilename;
    }

    public String getReportTemplateName() {
        return reportTemplateName;
    }

    public String getSqlQuery() {
        return sqlQuery;
    }

    public byte[] getTemplateDocument() {
        return templateDocument;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public void setId(Integer aId) {
        id = aId;
    }

    public void setMimeType(String aMimeType) {
        mimeType = aMimeType;
    }

    public void setParameters(String aParameters) {
        parameters = aParameters;
    }

    public void setReportFilename(String aReportFilename) {
        reportFilename = aReportFilename;
    }

    public void setReportTemplateName(String aReportTemplateName) {
        reportTemplateName = aReportTemplateName;
    }

    public void setSqlQuery(String aSqlQuery) {
        sqlQuery = aSqlQuery;
    }

    public void setTemplateDocument(byte[] aTemplateDocument) {
        templateDocument = aTemplateDocument;
    }

    @Override
    public String toString() {
        return "ReportTemplate [id=" + id + ", reportTemplateName=" + reportTemplateName + ", sqlQuery=" + sqlQuery
                + ", mimeType=" + mimeType + ", parameters=" + parameters + ", reportFilename=" + reportFilename + "]";
    }
}
