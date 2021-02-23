package ca.ids.abms.modules.users;

import java.util.Objects;

public class ChangePassword {

    private String oldPassword;
    private String newPassword;

    String getOldPassword() {
        return oldPassword;
    }

    void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    String getNewPassword() {
        return newPassword;
    }

    void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChangePassword that = (ChangePassword) o;
        return Objects.equals(oldPassword, that.oldPassword) &&
            Objects.equals(newPassword, that.newPassword);
    }

    @Override
    public int hashCode() {

        return Objects.hash(oldPassword, newPassword);
    }

    @Override
    public final String toString() {
        return super.toString();
    }
}
