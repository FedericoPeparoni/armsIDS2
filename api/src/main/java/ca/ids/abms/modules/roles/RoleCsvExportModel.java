package ca.ids.abms.modules.roles;

import ca.ids.abms.util.csv.annotations.CsvProperty;

public class RoleCsvExportModel {

    @CsvProperty(value = "Group Name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
