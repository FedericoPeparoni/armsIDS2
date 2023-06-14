package ca.ids.abms.modules.amhsconfiguration;

import ca.ids.abms.modules.util.models.VersionedViewModel;

import javax.validation.constraints.NotNull;

public class AmhsAccountViewModel extends VersionedViewModel {
    private Integer id;

    @NotNull
    private Boolean active;

    @NotNull
    private String addr;

    private String descr;

    @NotNull
    private String passwd;

    @NotNull
    private Boolean allowMtaConn = true;

    @NotNull
    private Boolean svcHoldForDelivery = true;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public Boolean getAllowMtaConn() {
        return allowMtaConn;
    }

    public void setAllowMtaConn(Boolean allowMtaConn) {
        this.allowMtaConn = allowMtaConn;
    }

    public Boolean getSvcHoldForDelivery() {
        return svcHoldForDelivery;
    }

    public void setSvcHoldForDelivery(Boolean svcHoldForDelivery) {
        this.svcHoldForDelivery = svcHoldForDelivery;
    }

    @Override
    public String toString() {
        return "AmhsAccountViewModel{" +
            "id=" + id +
            ", active=" + active +
            ", addr='" + addr + '\'' +
            ", descr='" + descr + '\'' +
            ", passwd='" + passwd + '\'' +
            ", allowMtaConn=" + allowMtaConn +
            ", svcHoldForDelivery=" + svcHoldForDelivery +
            '}';
    }
}
