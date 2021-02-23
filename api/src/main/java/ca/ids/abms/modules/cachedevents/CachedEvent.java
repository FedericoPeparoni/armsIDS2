package ca.ids.abms.modules.cachedevents;

import ca.ids.abms.config.db.SearchableEntity;
import ca.ids.abms.config.db.SearchableText;
import ca.ids.abms.modules.util.models.AuditedEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "cached_events")
public class CachedEvent extends AuditedEntity {

    private static final long serialVersionUID = 1L;

	@Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "target")
    @SearchableText
    private String target;

    @Column(name = "method_name")
    @SearchableText
    private String methodName;

    @CollectionTable(name = "cached_event_param_types", joinColumns = @JoinColumn(name = "cached_event_id"))
    @Column(name = "type")
    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    @OrderColumn(name = "sequence")
    private List<String> paramTypes;

    @CollectionTable(name = "cached_event_arguments", joinColumns = @JoinColumn(name = "cached_event_id"))
    @Column(name = "argument")
    @ElementCollection
    @JsonIgnore
    @LazyCollection(LazyCollectionOption.FALSE)
    @OrderColumn(name = "sequence")
    private List<byte[]> args;

    @CollectionTable(name = "cached_event_exceptions", joinColumns = @JoinColumn(name = "cached_event_id"))
    @Column(name = "exception")
    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<String> exceptions;

    @CollectionTable(name = "cached_event_caches", joinColumns = @JoinColumn(name = "cached_event_id"))
    @Column(name = "cache_name")
    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<String> caches;

    @CollectionTable(name = "cached_event_results", joinColumns = @JoinColumn(name = "cached_event_id"))
    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    @OrderColumn(name = "sequence")
    @SearchableEntity
    private List<CachedEventResult> results;

    @CollectionTable(name = "cached_event_metadata", joinColumns = @JoinColumn(name = "cached_event_id"))
    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    @OrderColumn(name = "sequence")
    @SearchableEntity
    private List<CachedEventMetadata> metadata;

    @Column(name = "retry")
    private Boolean retry;

    @Column(name = "exceptions_inverted", nullable = false)
    private Boolean exceptionsInverted;
    
    @Column(name = "retry_count", nullable = false)
    private int retryCount = 0;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public List<String> getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(List<String> paramTypes) {
        this.paramTypes = paramTypes;
    }

    public List<byte[]> getArgs() {
        return args;
    }

    public void setArgs(List<byte[]> args) {
        this.args = args;
    }

    public List<String> getExceptions() {
        return exceptions;
    }

    public void setExceptions(List<String> exceptions) {
        this.exceptions = exceptions;
    }

    public List<String> getCaches() {
        return caches;
    }

    public void setCaches(List<String> caches) {
        this.caches = caches;
    }

    public List<CachedEventResult> getResults() {
        return results;
    }

    public void setResults(List<CachedEventResult> results) {
        this.results = results;
    }

    public List<CachedEventMetadata> getMetadata() {
        return metadata;
    }

    public void setMetadata(List<CachedEventMetadata> metadata) {
        this.metadata = metadata;
    }

    public Boolean getRetry() {
        return retry;
    }

    public void setRetry(Boolean retry) {
        this.retry = retry;
    }

    public Boolean getExceptionsInverted() {
        return exceptionsInverted;
    }

    public void setExceptionsInverted(Boolean exceptionsInverted) {
        this.exceptionsInverted = exceptionsInverted;
    }
    
    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    @JsonProperty("last_attempt")
    public LocalDateTime getLastAttempt() {
        return super.getUpdatedAt() != null ? super.getUpdatedAt() : super.getCreatedAt();
    }

    public void addThrownResult(Throwable throwable) {
        if (throwable != null) {
            CachedEventResult cachedEventResult = new CachedEventResult();
            cachedEventResult.setClazz(throwable.getClass().getName());
            cachedEventResult.setResult(throwable.getLocalizedMessage());
            this.results.add(cachedEventResult);
        }
    }

    @Override
    @PrePersist
    public void onCreate() {
        super.onCreate();
        for (CachedEventResult cachedEventResult : this.results) {
            cachedEventResult.setCreatedAt(this.getCreatedAt());
            cachedEventResult.setCreatedBy(this.getCreatedBy());
        }
    }

    @Override
    @PreUpdate
    public void onPersist() {
        super.onPersist();
        for (CachedEventResult cachedEventResult : this.results) {
            if (cachedEventResult.getCreatedAt() == null || cachedEventResult.getCreatedBy() == null) {
                cachedEventResult.setCreatedAt(this.getUpdatedAt());
                cachedEventResult.setCreatedBy(this.getUpdatedBy());
            }
        }
    }

    @Override
    public String toString() {
        return "CachedEvent [id=" + id + ", target=" + target + ", methodName=" + methodName
            + ", paramTypes=" + paramTypes + ", retry=" + retry + ", retryCount=" + retryCount + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        CachedEvent that = (CachedEvent) o;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
