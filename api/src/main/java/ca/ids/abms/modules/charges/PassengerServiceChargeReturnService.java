package ca.ids.abms.modules.charges;

import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.config.db.JoinFilter;
import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.accounts.AccountService;
import ca.ids.abms.modules.common.dto.ItemLoaderObserver;
import ca.ids.abms.modules.flightmovements.FlightMovementService;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementBuilderException;
import ca.ids.abms.modules.util.models.DateTimeUtils;
import ca.ids.abms.modules.util.models.ModelUtils;
import ca.ids.abms.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class PassengerServiceChargeReturnService {

    private static final Logger LOG = LoggerFactory.getLogger(PassengerServiceChargeReturnService.class);

    private PassengerServiceChargeReturnRepository passengerServiceChargeReturnRepository;

    private FlightMovementService flightMovementService;
    private AccountService accountService;

    public PassengerServiceChargeReturnService(PassengerServiceChargeReturnRepository passengerServiceChargeReturnRepository,
                                               FlightMovementService flightMovementService,
                                               AccountService accountService) {
        this.passengerServiceChargeReturnRepository = passengerServiceChargeReturnRepository;
        this.flightMovementService=flightMovementService;
        this.accountService=accountService;
    }

    public PassengerServiceChargeReturn create(PassengerServiceChargeReturn item) throws FlightMovementBuilderException {
        return this.create(item, null);
    }

    public PassengerServiceChargeReturn create(PassengerServiceChargeReturn item, ItemLoaderObserver o) throws FlightMovementBuilderException {
        updateFlightMovement(item, false, o);
        return passengerServiceChargeReturnRepository.save(item);
    }

    public PassengerServiceChargeReturn createOrUpdate(@Valid PassengerServiceChargeReturn item) throws FlightMovementBuilderException {
        return this.createOrUpdate(item, null);
    }

    public PassengerServiceChargeReturn createOrUpdate(@Valid PassengerServiceChargeReturn item, ItemLoaderObserver o) throws FlightMovementBuilderException {
        if(item == null) {
    	   return null;
        }
    	updateFlightMovement(item, true, o);
        PassengerServiceChargeReturn storedItem;
        final PassengerServiceChargeReturn existingItem = this.findByUniqueKey(item.getFlightId(), item.getDayOfFlight(), item.getDepartureTime());
        if (existingItem != null) {
            final Integer id = existingItem.getId();
            LOG.debug("Updating the PSCR item #{}", id);
            ModelUtils.merge(item, existingItem, "id");
            storedItem = passengerServiceChargeReturnRepository.save(existingItem);
        } else {
            LOG.debug("Creating a new PSCR with flightId:{} and dayOfFlight:{}", item.getFlightId(), item.getDayOfFlight());
            storedItem = passengerServiceChargeReturnRepository.save(item);
        }
        return storedItem;
    }

    @Transactional(readOnly = true)
    public PassengerServiceChargeReturn getOne(Integer id) {
        return passengerServiceChargeReturnRepository.getOne(id);
    }

    @Transactional(readOnly = true)
    public Page<PassengerServiceChargeReturn> findAll(Pageable pageable, boolean filtered, String textSearch, final Integer userId) {
        final FiltersSpecification.Builder filterBuilder = new FiltersSpecification.Builder().lookFor(textSearch);
        List<Integer> idLists = new ArrayList<>();

        if (userId != null) {
            filterBuilder.restrictOn(JoinFilter.equal("account", "accountUsers", "id", userId));
        }

        if (filtered) {
            idLists = passengerServiceChargeReturnRepository.getOrphanPassengerServiceChargeReturns();
        }
        return passengerServiceChargeReturnRepository.findAll(new OrphanPassengerServiceChargeReturnFilter(filterBuilder, idLists, filtered), pageable);
    }

    private void updateFlightMovement(final PassengerServiceChargeReturn item, boolean isUploaded, ItemLoaderObserver o)  throws FlightMovementBuilderException {
        assert item != null;
        if(StringUtils.isNotBlank(item.getFlightId()) && item.getDayOfFlight() != null){
            flightMovementService.updateFlightMovementFromPassengerServiceCharge(item, isUploaded, o);
        }
    }

    public PassengerServiceChargeReturn update(Integer id, Map<String, String> params, final MultipartFile file) throws IOException, FlightMovementBuilderException {
        PassengerServiceChargeReturn pscr = getOne(id);
        PassengerServiceChargeReturn result = passengerServiceChargeReturnRepository.save(updatePscr(pscr, file, params, true));
        this.updateFlightMovement(result, false, null);
        return result;
    }

    public void delete(Integer id) {
        try {
            passengerServiceChargeReturnRepository.delete(id);
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e,ErrorConstants.ERR_DELETE_NO_LONGER_EXISTS);
        }
    }

    @Transactional(readOnly = true)
    public PassengerServiceChargeReturn findByUniqueKey(String flightId, LocalDateTime dayOfFlight, String departureTime){
        LOG.debug("Find PassengerServiceChargeReturn by UniqueKey flightId '{}', dayOfFlight '{}', depTime '{}'",
            flightId,dayOfFlight,departureTime);

        // require at least flightId and dayOfFlight to match
        if(StringUtils.isNotBlank(flightId) && dayOfFlight != null) {

            // try specific targeted search with departure time if exists
            List<PassengerServiceChargeReturn> psList = null;
            if(departureTime != null && !departureTime.isEmpty())
                psList = passengerServiceChargeReturnRepository.findByFlightIdAndDayOfFlightAndDepartureTime(
                    flightId, dayOfFlight, departureTime);

            // try more wide search if none found
            if(psList == null || psList.isEmpty())
                psList = passengerServiceChargeReturnRepository.findByFlightIdAndDayOfFlight(
                    flightId, dayOfFlight);

            // return if one and only one found
            if(psList != null && psList.size() == 1)
                return psList.get(0);
        }

        // return null indicating none or multiple found
        LOG.warn("Found none or multiple PassengerServiceChargeReturn by UniqueKey flightId '{}', dayOfFlight '{}', depTime '{}'",
            flightId,dayOfFlight,departureTime);
        return null;
    }

    public PassengerServiceChargeReturn uploadPscrWithImage(final MultipartFile file, Map<String, String> params) throws IOException, FlightMovementBuilderException {
        PassengerServiceChargeReturn pscr = new PassengerServiceChargeReturn();

        PassengerServiceChargeReturn result = passengerServiceChargeReturnRepository.saveAndFlush(updatePscr(pscr, file, params, false));
        this.updateFlightMovement(result, false, null);
        return result;
    }

    private PassengerServiceChargeReturn updatePscr(PassengerServiceChargeReturn pscr, MultipartFile file, Map<String, String> params, boolean update) throws IOException {

        Integer accountId = returnInteger(params.get("account_id"));
        if (accountId != null) {
            Account account = accountService.getOne(accountId);
            pscr.setAccount(account);
        }

        Boolean isSelfCareCreate = Boolean.parseBoolean(params.get("self_care_created"));
        pscr.setCreatedBySelfCare(isSelfCareCreate);

        if (!isSelfCareCreate && accountId == null) {
            pscr.setAccount(null);
        }

        if (file != null) {
            final byte[] bytes = file.getBytes();

            pscr.setHasImage(true);
            pscr.setDocumentContents(bytes);
            pscr.setDocumentMimeType(file.getContentType());
            pscr.setDocumentFilename(file.getOriginalFilename());
        } else {
            pscr.setHasImage(false);
        }

        String depTime = params.get("departure_time");

        if (depTime!= null && !depTime.equalsIgnoreCase("null")) {
            pscr.setDepartureTime(depTime);
        }

        pscr.setFlightId(params.get("flight_id"));
        pscr.setDayOfFlight(DateTimeUtils.convertStringToLocalDateTime(params.get("day_of_flight")));
        pscr.setChildren(returnInteger(params.get("children")));
        pscr.setTransitPassengers(returnInteger(params.get("transit_passengers")));
        pscr.setJoiningPassengers(returnInteger(params.get("joining_passengers")));
        pscr.setChargeableItlPassengers(returnInteger(params.get("chargeable_itl_passengers")));
        pscr.setChargeableDomesticPassengers(returnInteger(params.get("chargeable_domestic_passengers")));
        pscr.setArrivingPaxDomesticAirport(returnInteger(params.get("arriving_pax_domestic_airport")));
        pscr.setLandingPaxDomesticAirport(returnInteger(params.get("landing_pax_domestic_airport")));
        pscr.setTransferPaxDomesticAirport(returnInteger(params.get("transfer_pax_domestic_airport")));
        pscr.setDepartingPaxDomesticAirport(returnInteger(params.get("departing_pax_domestic_airport")));
        pscr.setArrivingChildDomesticAirport(returnInteger(params.get("arriving_child_domestic_airport")));
        pscr.setLandingChildDomesticAirport(returnInteger(params.get("landing_child_domestic_airport")));
        pscr.setTransferChildDomesticAirport(returnInteger(params.get("transfer_child_domestic_airport")));
        pscr.setDepartingChildDomesticAirport(returnInteger(params.get("departing_child_domestic_airport")));
        pscr.setExemptArrivingPaxDomesticAirport(returnInteger(params.get("exempt_arriving_pax_domestic_airport")));
        pscr.setExemptLandingPaxDomesticAirport(returnInteger(params.get("exempt_landing_pax_domestic_airport")));
        pscr.setExemptTransferPaxDomesticAirport(returnInteger(params.get("exempt_transfer_pax_domestic_airport")));
        pscr.setExemptDepartingPaxDomesticAirport(returnInteger(params.get("exempt_departing_pax_domestic_airport")));
        pscr.setLoadedGoods(returnDouble(params.get("loaded_goods")));
        pscr.setDischargedGoods(returnDouble(params.get("discharged_goods")));
        pscr.setLoadedMail(returnDouble(params.get("loaded_mail")));
        pscr.setDischargedMail(returnDouble(params.get("discharged_mail")));


        if (!update) {
            final PassengerServiceChargeReturn existingItem = this.findByUniqueKey(pscr.getFlightId(), pscr.getDayOfFlight(), pscr.getDepartureTime());
            if (existingItem != null) {
                LOG.debug("Cannot create PSCR with flight id {} and day of flight {} because it is already in the database ", existingItem.getId(), existingItem.getDayOfFlight());
                throw new DuplicateKeyException("This PSCR has already been created");
            }
        }

        return pscr;
    }

    private Integer returnInteger(String data) {
        if (data != null && !data.equalsIgnoreCase("null")) {
            return Integer.parseInt(data);
        } else {
            return null;
        }
    }

    private Double returnDouble(String data) {
        if (data != null && !data.equalsIgnoreCase("null")) {
            return Double.parseDouble(data);
        } else {
            return null;
        }
    }

    public Integer getMovementsWithoutPassengerCharges(String startDate, String endDate) {
        return passengerServiceChargeReturnRepository.getMovementsWithoutPassengerCharges(startDate, endDate);
    }

    public Integer getPassengerChargesWithoutMovements(String startDate, String endDate) {
        return passengerServiceChargeReturnRepository.getPassengerChargesWithoutMovements(startDate, endDate);
    }

    public Double totalDomesticFees(String startDate, String endDate) {
        return passengerServiceChargeReturnRepository.totalDomesticPassengerFee(startDate, endDate);
    }

    public Double totalInternationalFees(String startDate, String endDate) {
        return passengerServiceChargeReturnRepository.totalInternationalPassengerFee(startDate, endDate);
    }

    public long countAll() {
        return passengerServiceChargeReturnRepository.count();
    }

    public long countAllPassengerServiceChargeReturnsForSelfCareUser(Integer id) {
        return passengerServiceChargeReturnRepository.countAllPassengerServiceChargeReturnsForSelfCareUser(id);
    }
}
