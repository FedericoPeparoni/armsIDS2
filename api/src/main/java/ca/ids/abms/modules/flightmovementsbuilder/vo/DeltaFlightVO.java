package ca.ids.abms.modules.flightmovementsbuilder.vo;

/**
 * Created by c.talpa on 10/02/2017.
 */
public class DeltaFlightVO {

    private String ident;

    private String arrivaAt;

    private String departAt;

    public String getIdent() {
        return ident;
    }

    public void setIdent(String ident) {
        this.ident = ident;
    }

    public String getArrivaAt() {
        return arrivaAt;
    }

    public void setArrivaAt(String arrivaAt) {
        this.arrivaAt = arrivaAt;
    }

    public String getDepartAt() {
        return departAt;
    }

    public void setDepartAt(String departAt) {
        this.departAt = departAt;
    }

    @Override
    public String toString() {
        return "DeltaFlightVO{" +
            "ident='" + ident + '\'' +
            ", arrivaAt='" + arrivaAt + '\'' +
            ", departAt='" + departAt + '\'' +
            '}';
    }
}
