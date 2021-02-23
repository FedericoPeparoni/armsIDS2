package ca.ids.abms.modules.permissions;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ca.ids.abms.modules.roles.RolePermission;
import ca.ids.abms.modules.util.models.AuditedEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Permission extends AuditedEntity {

    private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue
    private Integer id;

    @NotNull
    @Size(max = 100)
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "permission")
    private Set<RolePermission> rolePermissions = new HashSet<>();

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

    public Set<RolePermission> getRolePermissions() {
        return rolePermissions;
    }

    public void setRolePermissions(Set<RolePermission> rolePermissions) {
        this.rolePermissions = rolePermissions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Permission that = (Permission) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Permission{" +
            "id=" + id +
            ", name='" + name + '\'' +
            '}';
    }
}
