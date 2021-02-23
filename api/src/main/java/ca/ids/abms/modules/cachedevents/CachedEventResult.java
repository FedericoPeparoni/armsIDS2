package ca.ids.abms.modules.cachedevents;

import ca.ids.abms.config.db.SearchableText;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Embeddable
public class CachedEventResult implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "class")
    @SearchableText
    private String clazz;

    @Column(name = "result")
    @SearchableText
    private String result;

    @CreatedDate
    private LocalDateTime createdAt;

    @Size(min = 4, max = 50)
    @CreatedBy
    private String createdBy;

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String toString() {
        return "CachedEventResult [clazz=" + clazz + ", result=" + result
            + ", createdAt=" + createdAt + ", createdBy=" + createdBy + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        CachedEventResult that = (CachedEventResult) o;
        return (clazz != null ? clazz.equals(that.clazz) : that.clazz == null)
            && (result != null ? result.equals(that.result) : that.result == null)
            && (createdAt != null ? createdAt.equals(that.createdAt) : that.createdAt == null)
            && (createdBy != null ? createdBy.equals(that.createdBy) : that.createdBy == null);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz, result, createdAt, createdBy);
    }
}
