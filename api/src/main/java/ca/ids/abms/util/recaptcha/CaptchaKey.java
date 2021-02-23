package ca.ids.abms.util.recaptcha;

import java.util.Objects;

public class CaptchaKey {

    CaptchaKey(final CaptchaKeyType type, final String value) {
        this.type = type;
        this.value = value;
    }

    private CaptchaKeyType type;

    private String value;

    public CaptchaKeyType getType() {
        return type;
    }

    public void setType(CaptchaKeyType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CaptchaKey that = (CaptchaKey) o;
        return type == that.type &&
            Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value);
    }

    @Override
    public String toString() {
        return "CaptchaKey{" +
            "type=" + type +
            ", value='" + value + '\'' +
            '}';
    }
}
