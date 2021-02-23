package ca.ids.abms.modules.selfcareportal.userregistration;

import ca.ids.abms.modules.users.UserViewModel;

public class SCUserRegistrationViewModel extends UserViewModel {

    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
