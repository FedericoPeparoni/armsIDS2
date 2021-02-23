package ca.ids.abms.modules.util.models;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.ids.abms.config.security.SecurityUtils;
import ca.ids.abms.util.MiscUtils;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditedEntity implements Serializable {

    private static final long serialVersionUID = 1L;

	@JsonIgnore
    @CreatedDate
    private LocalDateTime createdAt;

    @JsonIgnore
    @Size(min = 4, max = 50)
    @CreatedBy
    private String createdBy;

    @JsonIgnore
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @JsonIgnore
    @Size(min = 4, max = 50)
    @LastModifiedBy
    private String updatedBy;

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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.createdBy = MiscUtils.nvl(SecurityUtils.getCurrentUserLogin(),"system");
    }

    @PreUpdate
    public void onPersist() {
        if (this.createdAt == null || this.createdBy == null) {
            this.createdAt = LocalDateTime.now();
            this.createdBy = MiscUtils.nvl(SecurityUtils.getCurrentUserLogin(),"system");
        }
        this.updatedAt =  LocalDateTime.now();
        this.updatedBy = MiscUtils.nvl(SecurityUtils.getCurrentUserLogin(),"system");
    }

}
