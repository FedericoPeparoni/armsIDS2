package ca.ids.abms.modules.amhsconfiguration;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import ca.ids.abms.config.db.SearchableText;
import ca.ids.abms.config.db.validators.UniqueKey;
import ca.ids.abms.modules.common.entities.AbmsCrudEntity;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;

@SuppressWarnings("serial")
@Entity
@Table(name = "amhs_accounts")
@UniqueKey(columnNames = { "addr" })
public class AmhsAccount extends VersionedAuditedEntity implements AbmsCrudEntity<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    private Boolean active;

    @NotNull
    @SearchableText
    private String addr;
    
    @SearchableText
    private String descr;

    @NotNull
    private String passwd;

    @NotNull
    private Boolean allowMtaConn = true;

    @NotNull
    private Boolean svcHoldForDelivery = true;

    @Override
    public String toString() {
        return "AmhsAccount [" + (getId() != null ? "id=" + getId() + ", " : "")
                + (getActive() != null ? "active=" + getActive() + ", " : "")
                + (getAddr() != null ? "addr=" + getAddr() + ", " : "")
                + (getDescr() != null ? "addr=" + getDescr() + ", " : "")
                + (getAllowMtaConn() != null ? "allowMtaConn=" + getAllowMtaConn() + ", " : "")
                + (getSvcHoldForDelivery() != null ? "svcHoldForDelivery=" + getSvcHoldForDelivery() + ", " : "")
                + (getCreatedAt() != null ? "createdAt=" + getCreatedAt() + ", " : "")
                + (getCreatedBy() != null ? "createdBy=" + getCreatedBy() + ", " : "")
                + (getUpdatedAt() != null ? "updatedAt=" + getUpdatedAt() + ", " : "")
                + (getUpdatedBy() != null ? "updatedBy=" + getUpdatedBy() : "") + "]";
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof AmhsAccount) {
            final AmhsAccount other = (AmhsAccount) o;
            return Objects.equals(id, other.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, addr);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public Boolean getAllowMtaConn() {
        return allowMtaConn;
    }

    public void setAllowMtaConn(Boolean allowMtaConn) {
        this.allowMtaConn = allowMtaConn;
    }

    public Boolean getSvcHoldForDelivery() {
        return svcHoldForDelivery;
    }

    public void setSvcHoldForDelivery(Boolean svcHoldForDelivery) {
        this.svcHoldForDelivery = svcHoldForDelivery;
    }

}
