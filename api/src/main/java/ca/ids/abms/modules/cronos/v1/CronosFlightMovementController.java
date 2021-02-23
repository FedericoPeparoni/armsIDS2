package ca.ids.abms.modules.cronos.v1;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.ids.abms.modules.flightmovements.FlightMovementService;

@RestController
@RequestMapping("/api/cronos/v1/flights")
public class CronosFlightMovementController {
    
    // Get flights by Day-of-flight and either registration number or flight id
    // Get only Flight records with spatiaFplObjectId == null && status in (PAID,INVOICED)
    @RequestMapping(method = RequestMethod.GET)
    public Page<CronosFlightMovementViewModel> getFlightsByDOF(
            @SortDefault.SortDefaults ({
                @SortDefault (sort = {"dateOfFlight"}, direction = Sort.Direction.DESC),
                @SortDefault (sort = {"depTime"}, direction = Sort.Direction.DESC),
                @SortDefault (sort = {"id"}, direction = Sort.Direction.DESC),
            }) final Pageable pageable,
            @RequestParam(value = "dateOfFlight", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate dateOfFlight,
            @RequestParam(value = "flightId", required = false) final String flightId,
            @RequestParam(value = "aircraftReg", required = false) final String aircraftReg) {
        return flightMovementService
                    .findForCronosByDateOfFlight (pageable, dateOfFlight, flightId, aircraftReg)
                    .map (cronosFlightMovementMapper::toViewModel);
    }
    
    // Get flights by invoice number
    // Get only Flight records with spatiaFplObjectId == null && status in (PAID,INVOICED)
    @RequestMapping(method = RequestMethod.GET, params = { "invoiceNum" })
    public Page<CronosFlightMovementViewModel> getFlightsByInvoiceNum(
            @SortDefault.SortDefaults ({
                @SortDefault (sort = {"dateOfFlight"}, direction = Sort.Direction.DESC),
                @SortDefault (sort = {"depTime"}, direction = Sort.Direction.DESC),
                @SortDefault (sort = {"id"}, direction = Sort.Direction.DESC),
            }) final Pageable pageable,
            @RequestParam(value = "invoiceNum", required = true) final String invoiceNum) {
        return flightMovementService
                    .findForCronosByInvoiceNum(pageable, invoiceNum)
                    .map (cronosFlightMovementMapper::toViewModel);
    }

    // ---------------------- private -------------------
    public CronosFlightMovementController (
            final FlightMovementService flightMovementService,
            final CronosFlightMovementMapper cronosFlightMovementMapper) {
        this.flightMovementService = flightMovementService;
        this.cronosFlightMovementMapper = cronosFlightMovementMapper;
    }
    
    private final FlightMovementService flightMovementService;
    private final CronosFlightMovementMapper cronosFlightMovementMapper;
}
