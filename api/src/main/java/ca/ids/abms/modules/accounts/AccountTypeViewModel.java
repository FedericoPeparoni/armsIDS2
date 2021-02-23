package ca.ids.abms.modules.accounts;

import ca.ids.abms.modules.translation.Translation;
import org.apache.commons.lang.StringUtils;

public class AccountTypeViewModel {

    private Integer id;

    private String name;

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
}
