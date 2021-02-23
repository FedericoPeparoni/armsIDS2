package ca.ids.abms.modules.roles;

import ca.ids.abms.modules.util.models.VersionedViewModel;

import java.time.LocalDateTime;
import java.util.Collection;

public class RoleViewModel extends VersionedViewModel {

    private Integer id;
    private String name;
    private Collection<RolePermissionViewModel> permissions;
    private Collection<RoleOwnershipViewModel> ownedRoles;
    private Double maxCreditNoteAmountApprovalLimit;
    private Double maxDebitNoteAmountApprovalLimit;
    private String notificationMechanism;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;

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

    public Collection<RolePermissionViewModel> getPermissions() {
        return permissions;
    }

    public void setPermissions(Collection<RolePermissionViewModel> permissions) {
        this.permissions = permissions;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
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

    public Collection<RoleOwnershipViewModel> getOwnedRoles() {
        return ownedRoles;
    }

    public void setOwnedRoles(Collection<RoleOwnershipViewModel> ownedRoles) {
        this.ownedRoles = ownedRoles;
    }

    @Override
    public String toString() {
        return "RoleViewModel{" +
            "id=" + id +
            ", name='" + name + '\'' +
            '}';
    }
}
