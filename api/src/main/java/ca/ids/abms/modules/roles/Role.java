package ca.ids.abms.modules.roles;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ca.ids.abms.config.db.SearchableText;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import ca.ids.abms.modules.permissions.Permission;
import ca.ids.abms.modules.users.UserRole;

@Entity
public class Role extends VersionedAuditedEntity {

    private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Size(max = 25)
    @SearchableText
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "role", fetch = FetchType.EAGER)
    private Set<RolePermission> rolePermissions = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "role")
    private Set<UserRole> userRoles = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "parentRoleId")
    private Set<RoleOwnership> roleOwnership = new HashSet<>();

    @NotNull
    private Double maxCreditNoteAmountApprovalLimit;

    @NotNull
    private Double maxDebitNoteAmountApprovalLimit;

    @Size(max = 50)
    private String notificationMechanism;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<UserRole> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(Set<UserRole> userRoles) {
        this.userRoles = userRoles;
    }

    public Set<RolePermission> getRolePermissions() {
        return rolePermissions;
    }

    public void setRolePermissions(Set<RolePermission> rolePermissions) {
        this.rolePermissions = rolePermissions;
    }

    public Double getMaxCreditNoteAmountApprovalLimit() {
        return maxCreditNoteAmountApprovalLimit;
    }

    public void setMaxCreditNoteAmountApprovalLimit(Double maxCreditNoteAmountApprovalLimit) {
        this.maxCreditNoteAmountApprovalLimit = maxCreditNoteAmountApprovalLimit;
    }

    public Double getMaxDebitNoteAmountApprovalLimit() {
        return maxDebitNoteAmountApprovalLimit;
    }

    public void setMaxDebitNoteAmountApprovalLimit(Double maxDebitNoteAmountApprovalLimit) {
        this.maxDebitNoteAmountApprovalLimit = maxDebitNoteAmountApprovalLimit;
    }

    public String getNotificationMechanism() {
        return notificationMechanism;
    }

    public void setNotificationMechanism(String notificationMechanism) {
        this.notificationMechanism = notificationMechanism;
    }

    @Transient
    public Collection<Permission> getPermissions() {
        return getRolePermissions().stream()
            .map(RolePermission::getPermission)
            .collect(Collectors.toList());
    }

    @Transient
    public Collection<Role> getOwnedRoles() {
        return getRoleOwnership().stream()
            .map(RoleOwnership::getOwnedRoleId)
            .collect(Collectors.toList());
    }


    public Set<RoleOwnership> getRoleOwnership() {
        return roleOwnership;
    }

    public void setRoleOwnership(Set<RoleOwnership> roleOwnership) {
        this.roleOwnership = roleOwnership;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result
                + ((maxCreditNoteAmountApprovalLimit == null) ? 0 : maxCreditNoteAmountApprovalLimit.hashCode());
        result = prime * result
                + ((maxDebitNoteAmountApprovalLimit == null) ? 0 : maxDebitNoteAmountApprovalLimit.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((notificationMechanism == null) ? 0 : notificationMechanism.hashCode());
        result = prime * result + ((rolePermissions == null) ? 0 : rolePermissions.hashCode());
        result = prime * result + ((userRoles == null) ? 0 : userRoles.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Role other = (Role) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (maxCreditNoteAmountApprovalLimit == null) {
            if (other.maxCreditNoteAmountApprovalLimit != null)
                return false;
        } else if (!maxCreditNoteAmountApprovalLimit.equals(other.maxCreditNoteAmountApprovalLimit))
            return false;
        if (maxDebitNoteAmountApprovalLimit == null) {
            if (other.maxDebitNoteAmountApprovalLimit != null)
                return false;
        } else if (!maxDebitNoteAmountApprovalLimit.equals(other.maxDebitNoteAmountApprovalLimit))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (notificationMechanism == null) {
            if (other.notificationMechanism != null)
                return false;
        } else if (!notificationMechanism.equals(other.notificationMechanism))
            return false;
        if (rolePermissions == null) {
            if (other.rolePermissions != null)
                return false;
        } else if (!rolePermissions.equals(other.rolePermissions))
            return false;
        if (userRoles == null) {
            if (other.userRoles != null)
                return false;
        } else if (!userRoles.equals(other.userRoles))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Role{" +
            "id=" + id +
            ", name='" + name + '\'' +
            '}';
    }
}
