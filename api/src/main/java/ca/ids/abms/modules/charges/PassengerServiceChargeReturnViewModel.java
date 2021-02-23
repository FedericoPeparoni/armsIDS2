package ca.ids.abms.modules.charges;

import javax.validation.constraints.Size;

import ca.ids.abms.modules.accounts.AccountViewModel;
import com.opencsv.bean.CsvBindByName;

import ca.ids.abms.modules.common.dto.EmbeddedFileDto;
import ca.ids.abms.modules.common.mappers.Time4Digits;

public class PassengerServiceChargeReturnViewModel extends EmbeddedFileDto {

    private Integer id;

    @CsvBindByName
    @Size(max = 16)
    private String flightId;

    @CsvBindByName
    @Size(max = 32)
    private String dayOfFlight;

    @Time4Digits
    @CsvBindByName
    private String departureTime;

    @CsvBindByName
    private Integer transitPassengers;

    @CsvBindByName
    private Integer joiningPassengers;

    @CsvBindByName
    private Integer children;

    @CsvBindByName
    private Integer chargeableItlPassengers;

    @CsvBindByName
    private Integer chargeableDomesticPassengers;

    @CsvBindByName
    private byte[] documentContents;

    private Boolean hasImage;

    private Integer arrivingPaxDomesticAirport;

    private Integer landingPaxDomesticAirport;

    private Integer transferPaxDomesticAirport;

    private Integer departingPaxDomesticAirport;

    private Integer arrivingChildDomesticAirport;

    private Integer landingChildDomesticAirport;

    private Integer transferChildDomesticAirport;

    private Integer departingChildDomesticAirport;

    private Integer exemptArrivingPaxDomesticAirport;

    private Integer exemptLandingPaxDomesticAirport;

    private Integer exemptTransferPaxDomesticAirport;

    private Integer exemptDepartingPaxDomesticAirport;

    private Double loadedGoods;

    private Double dischargedGoods;

    private Double loadedMail;

    private Double dischargedMail;

    private AccountViewModel account;

    private Boolean createdBySelfCare;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFlightId() {
        return flightId;
    }

    public void setFlightId(String flightId) {
        this.flightId = flightId;
    }

    public String getDayOfFlight() {
        return dayOfFlight;
    }

    public void setDayOfFlight(String dayOfFlight) {
        this.dayOfFlight = dayOfFlight;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public Integer getTransitPassengers() {
        return transitPassengers;
    }

    public void setTransitPassengers(Integer transitPassengers) {
        this.transitPassengers = transitPassengers;
    }

    public Integer getJoiningPassengers() {
        return joiningPassengers;
    }

    public void setJoiningPassengers(Integer joiningPassengers) {
        this.joiningPassengers = joiningPassengers;
    }

    public Integer getChildren() {
        return children;
    }

    public void setChildren(Integer children) {
        this.children = children;
    }

    public Integer getChargeableItlPassengers() {
        return chargeableItlPassengers;
    }

    public void setChargeableItlPassengers(Integer chargeableItlPassengers) {
        this.chargeableItlPassengers = chargeableItlPassengers;
    }

    public Integer getChargeableDomesticPassengers() {
        return chargeableDomesticPassengers;
    }

    public void setChargeableDomesticPassengers(Integer chargeableDomesticPassengers) {
        this.chargeableDomesticPassengers = chargeableDomesticPassengers;
    }

    public byte[] getDocumentContents() {
        return documentContents;
    }

    public void setDocumentContents(byte[] documentContents) {
        this.documentContents = documentContents;
    }

    public Boolean getHasImage() {
        return hasImage;
    }

    public void setHasImage(Boolean hasImage) {
        this.hasImage = hasImage;
    }

    public Integer getArrivingPaxDomesticAirport() {
        return arrivingPaxDomesticAirport;
    }

    public void setArrivingPaxDomesticAirport(Integer arrivingPaxDomesticAirport) {
        this.arrivingPaxDomesticAirport = arrivingPaxDomesticAirport;
    }

    public Integer getLandingPaxDomesticAirport() {
        return landingPaxDomesticAirport;
    }

    public void setLandingPaxDomesticAirport(Integer landingPaxDomesticAirport) {
        this.landingPaxDomesticAirport = landingPaxDomesticAirport;
    }

    public Integer getTransferPaxDomesticAirport() {
        return transferPaxDomesticAirport;
    }

    public void setTransferPaxDomesticAirport(Integer transferPaxDomesticAirport) {
        this.transferPaxDomesticAirport = transferPaxDomesticAirport;
    }

    public Integer getDepartingPaxDomesticAirport() {
        return departingPaxDomesticAirport;
    }

    public void setDepartingPaxDomesticAirport(Integer departingPaxDomesticAirport) {
        this.departingPaxDomesticAirport = departingPaxDomesticAirport;
    }

    public Integer getArrivingChildDomesticAirport() {
        return arrivingChildDomesticAirport;
    }

    public void setArrivingChildDomesticAirport(Integer arrivingChildDomesticAirport) {
        this.arrivingChildDomesticAirport = arrivingChildDomesticAirport;
    }

    public Integer getLandingChildDomesticAirport() {
        return landingChildDomesticAirport;
    }

    public void setLandingChildDomesticAirport(Integer landingChildDomesticAirport) {
        this.landingChildDomesticAirport = landingChildDomesticAirport;
    }

    public Integer getTransferChildDomesticAirport() {
        return transferChildDomesticAirport;
    }

    public void setTransferChildDomesticAirport(Integer transferChildDomesticAirport) {
        this.transferChildDomesticAirport = transferChildDomesticAirport;
    }

    public Integer getDepartingChildDomesticAirport() {
        return departingChildDomesticAirport;
    }

    public void setDepartingChildDomesticAirport(Integer departingChildDomesticAirport) {
        this.departingChildDomesticAirport = departingChildDomesticAirport;
    }

    public Integer getExemptArrivingPaxDomesticAirport() {
        return exemptArrivingPaxDomesticAirport;
    }

    public void setExemptArrivingPaxDomesticAirport(Integer exemptArrivingPaxDomesticAirport) {
        this.exemptArrivingPaxDomesticAirport = exemptArrivingPaxDomesticAirport;
    }

    public Integer getExemptLandingPaxDomesticAirport() {
        return exemptLandingPaxDomesticAirport;
    }

    public void setExemptLandingPaxDomesticAirport(Integer exemptLandingPaxDomesticAirport) {
        this.exemptLandingPaxDomesticAirport = exemptLandingPaxDomesticAirport;
    }

    public Integer getExemptTransferPaxDomesticAirport() {
        return exemptTransferPaxDomesticAirport;
    }

    public void setExemptTransferPaxDomesticAirport(Integer exemptTransferPaxDomesticAirport) {
        this.exemptTransferPaxDomesticAirport = exemptTransferPaxDomesticAirport;
    }

    public Integer getExemptDepartingPaxDomesticAirport() {
        return exemptDepartingPaxDomesticAirport;
    }

    public void setExemptDepartingPaxDomesticAirport(Integer exemptDepartingPaxDomesticAirport) {
        this.exemptDepartingPaxDomesticAirport = exemptDepartingPaxDomesticAirport;
    }

    public Double getLoadedGoods() {
        return loadedGoods;
    }

    public void setLoadedGoods(Double loadedGoods) {
        this.loadedGoods = loadedGoods;
    }

    public Double getDischargedGoods() {
        return dischargedGoods;
    }

    public void setDischargedGoods(Double dischargedGoods) {
        this.dischargedGoods = dischargedGoods;
    }

    public Double getLoadedMail() {
        return loadedMail;
    }

    public void setLoadedMail(Double loadedMail) {
        this.loadedMail = loadedMail;
    }

    public Double getDischargedMail() {
        return dischargedMail;
    }

    public void setDischargedMail(Double dischargedMail) {
        this.dischargedMail = dischargedMail;
    }

    public AccountViewModel getAccount() {
        return account;
    }

    public void setAccount(AccountViewModel account) {
        this.account = account;
    }

    public Boolean getCreatedBySelfCare() {
        return createdBySelfCare;
    }

    public void setCreatedBySelfCare(Boolean createdBySelfCare) {
        this.createdBySelfCare = createdBySelfCare;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PassengerServiceChargeReturnViewModel that = (PassengerServiceChargeReturnViewModel) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

}
