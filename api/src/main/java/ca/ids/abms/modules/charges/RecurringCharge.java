package ca.ids.abms.modules.charges;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import ca.ids.abms.config.db.SearchableEntity;
import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;

@Entity
public class RecurringCharge extends VersionedAuditedEntity {

    private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @NotNull
    @SearchableEntity
    private ServiceChargeCatalogue serviceChargeCatalogue;

    @ManyToOne
    @NotNull
    @SearchableEntity
    private Account account;

    @NotNull
    private LocalDateTime startDate;

    @NotNull
    private LocalDateTime endDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ServiceChargeCatalogue getServiceChargeCatalogue() {
        return serviceChargeCatalogue;
    }

    public void setServiceChargeCatalogue(ServiceChargeCatalogue serviceChargeCatalogue) {
        this.serviceChargeCatalogue = serviceChargeCatalogue;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o != null) {
            try {
                RecurringCharge other = (RecurringCharge) o;
                if (getId() == null || other.getId() == null)
                    return false;
                if (!getId().equals(other.getId()))
                    return false;
                return true;
            }
            catch (final ClassCastException x) {
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (serviceChargeCatalogue != null ? serviceChargeCatalogue.hashCode() : 0);
        result = 31 * result + (account != null ? account.hashCode() : 0);
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RecurringCharge{" +
            "id=" + id +
            ", serviceChargeCatalogue=" + serviceChargeCatalogue +
            ", account=" + account +
            ", startDate=" + startDate +
            ", endDate=" + endDate +
            '}';
    }
}
