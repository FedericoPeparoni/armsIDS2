package ca.ids.abms.modules.flightmovements;

import java.util.List;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementSource;
import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementStatus;
import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementType;
import ca.ids.abms.modules.flightmovements.enumerate.FlightmovementCategoryNationality;
import ca.ids.abms.modules.flightmovements.enumerate.FlightmovementCategoryScope;
import ca.ids.abms.modules.flightmovements.enumerate.FlightmovementCategoryType;
import ca.ids.abms.modules.routesegments.RouteSegment;
import ca.ids.abms.modules.routesegments.RouteSegmentViewModel;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class FlightMovementMapper {


    @Mapping(target = "associatedAccount", source = "account.name")
    @Mapping(target = "associatedAccountID", source = "account.id")
    @Mapping(target = "associatedAccountName", source = "account.name")
    @Mapping(target = "associatedAccountIataMember", source = "account.iataMember")
    @Mapping(target = "associatedAccountBlackListedIndicator", source = "account.blackListedIndicator")
    @Mapping(target = "associatedAccountBlackListedOverride", source = "account.blackListedOverride")
    @Mapping(target = "markedAsDuplicate", ignore = true)
    @Mapping(target = "markedAsMissingBeforeThis", ignore = true)
    @Mapping(target = "markedAsFirstMissing", ignore = true)
    @Mapping(target = "markedAsLastMissing", ignore = true)
    @Mapping(target = "flightmovementCategoryName", source = "flightmovementCategory.name")
    @Mapping(target = "enrouteResultCurrency", source = "flightmovementCategory.enrouteResultCurrency")
    @Mapping(target = "enrouteInvoiceCurrency", source = "flightmovementCategory.enrouteInvoiceCurrency")
    public abstract FlightMovementViewModel toViewModel(FlightMovement flightMovement);

    public abstract List<FlightMovementViewModel> toViewModel(Iterable<FlightMovement> flightMovements);

    @AfterMapping
    public void parseDateTime(final FlightMovementViewModel source, @MappingTarget FlightMovement result) {
        result.setDateOfFlight(source.getDateOfFlight().toLocalDate().atStartOfDay());
        result.setBillingDate(source.getDateOfFlight().toLocalDate().atStartOfDay());
    }

    @Mapping(target = "resolutionErrorsSet", ignore = true)
    @Mapping(target = "dateOfFlight", ignore = true)
    public abstract FlightMovement toModel(FlightMovementViewModel dto);

    @Mapping(target = "flightRecordId", source = "flightMovement.id")
    public abstract RouteSegmentViewModel toViewModel(RouteSegment routeSegment);


    @Mapping(target = "flightMovement", ignore = true)
    public abstract RouteSegment toModel(RouteSegmentViewModel dto);

    @AfterMapping
    public void resolveAccount (final FlightMovementViewModel source, @MappingTarget FlightMovement result) {
        Account account = new Account();
        account.setId(source.getAssociatedAccountID());
        result.setAccount(account);
    }

    public FlightMovementStatus mapFlightMovementStatus(final String flightMovementStatus) {
        return FlightMovementStatus.forValue(flightMovementStatus);
    }

    public String mapFlightMovementStatus(final FlightMovementStatus flightMovementStatus) {
        return flightMovementStatus != null ? flightMovementStatus.toValue() : null;
    }

    public FlightMovementType mapFlightMovementType(final String flightMovementType) {
        return FlightMovementType.forValue(flightMovementType);
    }

    public String mapFlightMovementType(final FlightMovementType flightMovementType) {
        return flightMovementType != null ? flightMovementType.toValue() : null;
    }


    public FlightMovementSource mapFlightMovementSource(final String flightMovementSource) {
        return FlightMovementSource.forValue(flightMovementSource);
    }

    public String mapFlightMovementSource(final FlightMovementSource flightMovementSource) {
        return flightMovementSource != null ? flightMovementSource.toValue() : null;
    }
    
    public String mapFlightmovementCategoryType(final FlightmovementCategoryType flightmovementCategoryType) {
        return flightmovementCategoryType != null ? flightmovementCategoryType.toValue() : null;
    }
    
    public FlightmovementCategoryType mapFlightmovementCategoryType(final String flightmovementCategoryType) {
        return flightmovementCategoryType != null ? FlightmovementCategoryType.forValue(flightmovementCategoryType) : null;
    }
    
    public String mapFlightmovementCategoryScope(final FlightmovementCategoryScope flightmovementCategoryScope) {
        return flightmovementCategoryScope != null ? flightmovementCategoryScope.toValue() : null;
    }
    
    public FlightmovementCategoryScope mapFlightmovementCategoryScope(final String flightmovementCategoryScope) {
        return flightmovementCategoryScope != null ? FlightmovementCategoryScope.forValue(flightmovementCategoryScope) : null;
    }
    
    
    public String mapFlightmovementCategoryNationality(final FlightmovementCategoryNationality flightmovementCategoryNationality) {
        return flightmovementCategoryNationality != null ? flightmovementCategoryNationality.toValue() : null;
    }
    
    public FlightmovementCategoryNationality mapFlightmovementCategoryNationality(final String flightmovementCategoryNationality) {
        return flightmovementCategoryNationality != null ? FlightmovementCategoryNationality.forValue(flightmovementCategoryNationality) : null;
    }
    
    @AfterMapping
    public void resolveMovementCategories(final FlightMovementViewModel source, @MappingTarget FlightMovement target) {
        target.setFlightCategoryType(FlightmovementCategoryType.forValue(source.getFlightCategoryType()));
        target.setFlightCategoryScope(FlightmovementCategoryScope.forValue(source.getFlightCategoryScope()));
        target.setFlightCategoryNationality(FlightmovementCategoryNationality.forValue(source.getFlightCategoryNationality()));
    }

    @AfterMapping
    void resolveFlightMovementValidatorIssue(final FlightMovement source, @MappingTarget FlightMovementCsvExportModel target) {
        target.setResolutionErrorsSet(source.getResolutionErrorsSet() != null ? source.getResolutionErrorsSet().toString() : null);
    }

    @Mapping(target = "associatedAccount", source = "account.name")
    @Mapping(target = "associatedAccountBlackListedIndicator", source = "account.blackListedIndicator")
    @Mapping(target = "associatedAccountBlackListedOverride", source = "account.blackListedOverride")
    @Mapping(target = "flightmovementCategoryName", source = "flightmovementCategory.name")
    @Mapping(target = "enrouteResultCurrency", source = "enrouteResultCurrency.currencyCode")
    @Mapping(target = "approachChargesCurrency", source = "approachChargesCurrency.currencyCode")
    @Mapping(target = "aerodromeChargesCurrency", source = "aerodromeChargesCurrency.currencyCode")
    @Mapping(target = "lateArrivalDepartureChargesCurrency", source = "lateArrivalDepartureChargesCurrency.currencyCode")
    @Mapping(target = "extendedHoursSurchargeCurrency", source = "extendedHoursSurchargeCurrency.currencyCode")
    @Mapping(target = "resolutionErrorsSet", ignore = true)
    public abstract FlightMovementCsvExportModel toCsvModel(FlightMovement flightMovement);

    public abstract List<FlightMovementCsvExportModel> toCsvModel(Iterable<FlightMovement> flightMovements);
}
