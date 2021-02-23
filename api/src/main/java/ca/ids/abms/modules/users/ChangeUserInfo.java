package ca.ids.abms.modules.users;

public class ChangeUserInfo extends ChangePassword {

    private String email;
    private String smsNumber;

    String getEmail() {
        return email;
    }
    String getSmsNumber() {
        return smsNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSmsNumber(String smsNumber) {
        this.smsNumber = smsNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ChangeUserInfo that = (ChangeUserInfo) o;

        if (!email.equals(that.email)) return false;
        return smsNumber.equals(that.smsNumber);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + email.hashCode();
        result = 31 * result + smsNumber.hashCode();
        return result;
    }
}
