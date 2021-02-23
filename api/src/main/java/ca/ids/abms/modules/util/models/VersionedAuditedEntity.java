package ca.ids.abms.modules.util.models;

import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;
import javax.persistence.*;

@MappedSuperclass
@OptimisticLocking(type = OptimisticLockType.VERSION)
public abstract class VersionedAuditedEntity extends AuditedEntity implements Versioned<Long> {

    private static final long serialVersionUID = 1L;
	@Version
    private Long version;

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
