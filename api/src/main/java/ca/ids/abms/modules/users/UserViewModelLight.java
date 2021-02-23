package ca.ids.abms.modules.users;

import javax.validation.constraints.Size;

import ca.ids.abms.modules.util.models.VersionedViewModel;

public class UserViewModelLight extends VersionedViewModel {
    private Integer id;
   
    @Size(max = 100)
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
    
    @Override
    public String toString() {
        return "UserViewModelLight{" + "id=" + id  + ", name='" + name + "'}";
    }
}
