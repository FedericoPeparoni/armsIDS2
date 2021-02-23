package ca.ids.abms.modules.util.models;

public class TestModel implements Versioned<Long> {

    private String firstName;
    private String lastName;
    private int    age;
    private Object empty;
    private Long   version;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Object getEmpty() {
        return empty;
    }

    public void setEmpty(Object empty) {
        this.empty = empty;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public Long getVersion() {
        return version;
    }
}
