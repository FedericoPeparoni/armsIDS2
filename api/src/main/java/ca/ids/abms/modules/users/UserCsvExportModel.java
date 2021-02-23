package ca.ids.abms.modules.users;

import ca.ids.abms.util.csv.annotations.CsvProperty;

public class UserCsvExportModel {

    @CsvProperty(value = "User Name")
    private String name;

    @CsvProperty(value = "User Login")
    private String login;

    private String jobTitle;

    @CsvProperty(value = "Email Address")
    private String email;

    private String groups;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGroups() {
        return groups;
    }

    public void setGroups(String groups) {
        this.groups = groups;
    }
}
