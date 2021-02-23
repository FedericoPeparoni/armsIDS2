package ca.ids.abms.modules.cachedevents;

import ca.ids.abms.config.db.SearchableText;
import ca.ids.abms.modules.cachedevents.enumerators.CachedEventAction;
import ca.ids.abms.modules.cachedevents.enumerators.CachedEventType;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CachedEventMetadata implements Serializable {

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    @SearchableText
    private CachedEventType type;

    @Column(name = "action")
    @Enumerated(EnumType.STRING)
    @SearchableText
    private CachedEventAction action;

    @Column(name = "resource")
    @SearchableText
    private String resource;

    @Column(name = "statement")
    @SearchableText
    private String statement;

    CachedEventMetadata() {
        this(null, null, null, null);
    }

    public CachedEventMetadata(CachedEventType type, CachedEventAction action, String resource, String statement) {
        this.type = type;
        this.action = action;
        this.resource = resource;
        this.statement = statement;
    }

    public CachedEventType getType() {
        return type;
    }

    public void setType(CachedEventType type) {
        this.type = type;
    }

    public CachedEventAction getAction() {
        return action;
    }

    public void setAction(CachedEventAction action) {
        this.action = action;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    @Override
    public String toString() {
        return "CachedEventMetadata [type=" + type + ", action=" + action + ", resource=" + resource
            + ", statement=" + statement + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        CachedEventMetadata that = (CachedEventMetadata) o;
        return (type != null ? type.equals(that.type) : that.type == null)
            && (action != null ? action.equals(that.action) : that.action == null)
            && (resource != null ? resource.equals(that.resource) : that.resource == null)
            && (statement != null ? statement.equals(that.statement) : that.statement == null);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, action, resource, statement);
    }
}
