package ca.ids.abms.modules.system.summary;

import java.util.List;

import ca.ids.abms.modules.system.summary.vo.FlightMovementCategoryInfoVO;
import ca.ids.abms.modules.system.summary.vo.FlightMovementVO;

public class SystemSummaryViewModel {

    private List<FlightMovementCategoryInfoVO> flightMovementCategories;

    private FlightMovementVO flightMovementAll;

    private FlightMovementVO flightMovementOutside;

    private FlightMovementVO flightMovementInside;

    private FlightMovementVO flightMovementParkingTimeInternationaArrivals;

    private FlightMovementVO flightMovementParkingTimeDomestic;

    private FlightMovementVO flightMovementParkingTimeTotal;

    private FlightMovementVO flightMovementAircraftType;

    private FlightMovementVO flightMovementUnknownAircraftType;

    private FlightMovementVO flightMovementBlacklistedAccount;

    private FlightMovementVO flightMovementBlacklistedMovement;

    private FlightMovementVO flightMovementInternationalActiveAccount;

    private FlightMovementVO flightMovementDomesticActiveAccount;

    private FlightMovementVO flightMovementRejected;

    private FlightMovementVO outstandingBill;

    private FlightMovementVO overdueBill;

    private FlightMovementVO flightMovementLatest;

    public SystemSummaryViewModel() {

    }

    public FlightMovementVO getFlightMovementAircraftType() {
        return flightMovementAircraftType;
    }

    public FlightMovementVO getFlightMovementAll() {
        return flightMovementAll;
    }

    public FlightMovementVO getFlightMovementBlacklistedAccount() {
        return flightMovementBlacklistedAccount;
    }

    public FlightMovementVO getFlightMovementBlacklistedMovement() {
        return flightMovementBlacklistedMovement;
    }

    public List<FlightMovementCategoryInfoVO> getFlightMovementCategories() {
        return flightMovementCategories;
    }

    public FlightMovementVO getFlightMovementDomesticActiveAccount() {
        return flightMovementDomesticActiveAccount;
    }

    public FlightMovementVO getFlightMovementInternationalActiveAccount() {
        return flightMovementInternationalActiveAccount;
    }

    public FlightMovementVO getFlightMovementLatest() {
        return flightMovementLatest;
    }

    public FlightMovementVO getFlightMovementOutside() {
        return flightMovementOutside;
    }

    public FlightMovementVO getFlightMovementInside() {
        return flightMovementInside;
    }

    public FlightMovementVO getFlightMovementParkingTimeDomestic() {
        return flightMovementParkingTimeDomestic;
    }

    public FlightMovementVO getFlightMovementParkingTimeInternationaArrivals() {
        return flightMovementParkingTimeInternationaArrivals;
    }

    public FlightMovementVO getFlightMovementParkingTimeTotal() {
        return flightMovementParkingTimeTotal;
    }

    public FlightMovementVO getFlightMovementRejected() {
        return flightMovementRejected;
    }

    public FlightMovementVO getFlightMovementUnknownAircraftType() {
        return flightMovementUnknownAircraftType;
    }

    public FlightMovementVO getOutstandingBill() {
        return outstandingBill;
    }

    public FlightMovementVO getOverdueBill() {
        return overdueBill;
    }

    public void setFlightMovementAircraftType(FlightMovementVO aFlightMovementAircraftType) {
        flightMovementAircraftType = aFlightMovementAircraftType;
    }

    public void setFlightMovementAll(FlightMovementVO aFlightMovementAll) {
        flightMovementAll = aFlightMovementAll;
    }

    public void setFlightMovementBlacklistedAccount(FlightMovementVO aFlightMovementBlacklistedAccount) {
        flightMovementBlacklistedAccount = aFlightMovementBlacklistedAccount;
    }

    public void setFlightMovementBlacklistedMovement(FlightMovementVO aFlightMovementBlacklistedMovement) {
        flightMovementBlacklistedMovement = aFlightMovementBlacklistedMovement;
    }

    public void setFlightMovementCategories(List<FlightMovementCategoryInfoVO> aFlightMovementCategories) {
        flightMovementCategories = aFlightMovementCategories;
    }

    public void setFlightMovementDomesticActiveAccount(FlightMovementVO aFlightMovementDomesticActiveAccount) {
        flightMovementDomesticActiveAccount = aFlightMovementDomesticActiveAccount;
    }

    public void setFlightMovementInternationalActiveAccount(FlightMovementVO aFlightMovementInternationalActiveAccount) {
        flightMovementInternationalActiveAccount = aFlightMovementInternationalActiveAccount;
    }

    public void setFlightMovementLatest(FlightMovementVO aFlightMovementLatest) {
        flightMovementLatest = aFlightMovementLatest;
    }

    public void setFlightMovementOutside(FlightMovementVO aFlightMovementOutside) {
        flightMovementOutside = aFlightMovementOutside;
    }

    public void setFlightMovementInside(FlightMovementVO flightMovementInside) {
        this.flightMovementInside = flightMovementInside;
    }

    public void setFlightMovementParkingTimeDomestic(FlightMovementVO aFlightMovementParkingTimeDomestic) {
        flightMovementParkingTimeDomestic = aFlightMovementParkingTimeDomestic;
    }

    public void setFlightMovementParkingTimeInternationaArrivals(
            FlightMovementVO aFlightMovementParkingTimeInternationaArrivals) {
        flightMovementParkingTimeInternationaArrivals = aFlightMovementParkingTimeInternationaArrivals;
    }

    public void setFlightMovementParkingTimeTotal(FlightMovementVO aFlightMovementParkingTimeTotal) {
        flightMovementParkingTimeTotal = aFlightMovementParkingTimeTotal;
    }

    public void setFlightMovementRejected(FlightMovementVO aFlightMovementRejected) {
        flightMovementRejected = aFlightMovementRejected;
    }

    public void setFlightMovementUnknownAircraftType(FlightMovementVO aFlightMovementUnknownAircraftType) {
        flightMovementUnknownAircraftType = aFlightMovementUnknownAircraftType;
    }

    public void setOutstandingBill(FlightMovementVO aOutstandingBill) {
        outstandingBill = aOutstandingBill;
    }

    public void setOverdueBill(FlightMovementVO aOverdueBill) {
        overdueBill = aOverdueBill;
    }
}
