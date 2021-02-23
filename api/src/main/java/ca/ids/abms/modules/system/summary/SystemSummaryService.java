package ca.ids.abms.modules.system.summary;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import ca.ids.abms.modules.flightmovements.category.FlightmovementCategory;
import ca.ids.abms.modules.flightmovements.category.FlightmovementCategoryService;
import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementType;
import ca.ids.abms.modules.flightmovements.enumerate.FlightmovementCategoryScope;
import ca.ids.abms.modules.rejected.RejectedItemType;
import ca.ids.abms.modules.system.summary.vo.FlightMovementCategoryInfoVO;
import ca.ids.abms.modules.system.summary.vo.FlightMovementVO;
import ca.ids.abms.util.jdbc.AutoRowMapper;

@Service
public class SystemSummaryService {

    private final Logger log = LoggerFactory.getLogger(SystemSummaryService.class);

    private JdbcTemplate jdbcTemplate;

    private FlightmovementCategoryService flightmovementCategoryService;

    private static final String GET_ALL_FLIGHT_MOVEMENT = "select count(*) as total, (select count(*) from flight_movements where status = 'PENDING' and flight_category_id in (:ids)) as val,"
                + "((select count(*) from flight_movements where status = 'PENDING' and flight_category_id in (:ids)) * 100)::numeric / CASE count(*) WHEN 0 THEN 1 ELSE count(*) END as percent "
                + "from flight_movements where flight_category_id in (:ids)";

    private static final String GET_ALL_FLIGHT_MOVEMENT_CATEGORIES = "select count(*) as total, (select count(*) from flight_movements where status = 'PENDING' and flight_category_id = ?) as val,"
                    + "((select count(*) from flight_movements where status = 'PENDING' and flight_category_id = ?) * 100)::numeric / CASE count(*) WHEN 0 THEN 1 ELSE count(*) END as percent "
                    + "from flight_movements  where flight_category_id = ?";

    private static final String GET_ALL_FLIGHT_MOVEMENT_OUTSIDE = "select count(*) as total,  (select count(*) from flight_movements fm where fm.flight_category_id = ? ) as val,"
                + " ((select count(*) from flight_movements fm where fm.flight_category_id = ? ) * 100)::numeric /"
                + " ((select CASE WHEN count(*)=0 THEN 1 ELSE count(*) END from flight_movements  )) as percent from flight_movements fm where fm.flight_category_id = ?";

    private static final String GET_ALL_FLIGHT_MOVEMENT_INSIDE = "select count(*) as total,  (select count(*) from flight_movements fm where fm.flight_category_id != ? ) as val,"
                + " ((select count(*) from flight_movements fm where fm.flight_category_id != ? ) * 100)::numeric /"
                + " ((select CASE WHEN count(*)=0 THEN 1 ELSE count(*) END from flight_movements  )) as percent from flight_movements fm where fm.flight_category_id != ?";

    private static final String GET_ALL_FLIGHT_MOVEMENT_WITH_PARKING_TIME = "select count(*) as total, "
                    + "(select count(*) from flight_movements fm, flightmovement_category_attributes fmca where fm.flight_category_id = fmca.id and fmca.flight_scope in (?) and parking_time > 0 ) as val, "
                    + "((select count(*) from flight_movements fm, flightmovement_category_attributes fmca where fm.flight_category_id = fmca.id and fmca.flight_scope in (?) and parking_time > 0 ) * 100)::numeric / CASE count(*) WHEN 0 THEN 1 ELSE count(*) END as percent "
                    + "from flight_movements fm, flightmovement_category_attributes fmca where fm.flight_category_id = fmca.id and fmca.flight_scope in (?)";

    public SystemSummaryService(DataSource primaryDataSource, FlightmovementCategoryService aFlightmovementCategoryService) {
        jdbcTemplate = new JdbcTemplate(primaryDataSource);
        flightmovementCategoryService = aFlightmovementCategoryService;
    }

    public SystemSummaryViewModel getSystemSummary() {

        List<FlightmovementCategory> fmCategories = flightmovementCategoryService.findAllByOrderBySortOrderAsc();

        log.debug("Request to get system summary");
        SystemSummaryViewModel ss = new SystemSummaryViewModel();

        List<FlightMovementCategoryInfoVO> flightMovementCategories = getFlightMovementCategories(fmCategories);
        ss.setFlightMovementCategories(flightMovementCategories);


        FlightMovementVO flightMovementAll = getAllFlightMovement(fmCategories);
        flightMovementAll.setName(SystemSummaryName.TOTAL_FLIGHTS.getValue());
        ss.setFlightMovementAll(flightMovementAll);

        FlightMovementVO flightMovementOutside = getFlightMovementOutside(fmCategories);
        flightMovementOutside.setName(SystemSummaryName.OUTSIDE_BILLING_AREA.getValue());
        ss.setFlightMovementOutside(flightMovementOutside);

        FlightMovementVO flightMovementInside = getFlightMovementInside(fmCategories);
        flightMovementInside.setName(SystemSummaryName.INSIDE_BILLING_AREA.getValue());
        ss.setFlightMovementInside(flightMovementInside);

        FlightMovementVO flightMovementParkingTimeInternationaArrivals = getFlightMovementWithParking(FlightmovementCategoryScope.INTERNATIONAL.getCode());
        flightMovementParkingTimeInternationaArrivals.setName(FlightmovementCategoryScope.INTERNATIONAL.getMtowFactorClass());
        ss.setFlightMovementParkingTimeInternationaArrivals(flightMovementParkingTimeInternationaArrivals);
        FlightMovementVO flightMovementParkingTimeDomestics = getFlightMovementWithParking(FlightmovementCategoryScope.DOMESTIC.getCode());
        flightMovementParkingTimeDomestics.setName(FlightmovementCategoryScope.DOMESTIC.getMtowFactorClass());
        ss.setFlightMovementParkingTimeDomestic(flightMovementParkingTimeDomestics);
        String allFlightParkingTime = FlightmovementCategoryScope.INTERNATIONAL.getCode() + "," + FlightmovementCategoryScope.DOMESTIC.getCode();
        FlightMovementVO flightMovementParkingTimeTotal = getFlightMovementWithParking(allFlightParkingTime);
        flightMovementParkingTimeTotal.setName(SystemSummaryName.TOTAL.getValue());
        ss.setFlightMovementParkingTimeTotal(flightMovementParkingTimeTotal);


        FlightMovementVO flightMovementAircraftType = getFlightMovement("true",
                SystemSummaryConst.QUERY_TYPE_FLIGHT_MOVEMENT_AIRCRAFT_TYPE);
        flightMovementAircraftType.setName(SystemSummaryName.ALL_FLIGHT.getValue());
        ss.setFlightMovementAircraftType(flightMovementAircraftType);
        FlightMovementVO flightMovementUnknownAircraftType = getFlightMovement("false",
                SystemSummaryConst.QUERY_TYPE_FLIGHT_MOVEMENT_AIRCRAFT_TYPE);
        flightMovementUnknownAircraftType.setName(SystemSummaryName.UNKNOWN_AIRCRAFT_TYPE.getValue());
        ss.setFlightMovementUnknownAircraftType(flightMovementUnknownAircraftType);

        FlightMovementVO flightMovementBlacklistedAccount = getFlightMovement("",
                SystemSummaryConst.QUERY_TYPE_BLACKLISTED_ACCOUNT);
        flightMovementBlacklistedAccount.setName(SystemSummaryName.BLACKLISTED_ACCOUNT.getValue());
        ss.setFlightMovementBlacklistedAccount(flightMovementBlacklistedAccount);
        FlightMovementVO flightMovementBlacklisted = getFlightMovement("",
                SystemSummaryConst.QUERY_TYPE_FLIGHT_MOVEMENT_BLACKLISTED);
        flightMovementBlacklisted.setName(SystemSummaryName.BLACKLISTED_MOVEMENT.getValue());
        ss.setFlightMovementBlacklistedMovement(flightMovementBlacklisted);

        String internationalFlightsString = getAllInternationalFlightsString();
        FlightMovementVO flightMovementInternationalActiveAccount = getFlightMovement(internationalFlightsString,
                SystemSummaryConst.QUERY_TYPE_FLIGHT_MOVEMENT_ACTIVE_ACCOUNT);
        flightMovementInternationalActiveAccount.setName(SystemSummaryName.INTERNATIONAL_ACCOUNT.getValue());
        ss.setFlightMovementInternationalActiveAccount(flightMovementInternationalActiveAccount);

        FlightMovementVO flightMovementDomesticActiveAccount = getFlightMovement(FlightMovementType.DOMESTIC.toValue(),
                SystemSummaryConst.QUERY_TYPE_FLIGHT_MOVEMENT_ACTIVE_ACCOUNT);
        flightMovementDomesticActiveAccount.setName(SystemSummaryName.DOMESTIC_ACCOUNT.getValue());
        ss.setFlightMovementDomesticActiveAccount(flightMovementDomesticActiveAccount);

        FlightMovementVO flightMovementRejected = getFlightMovement(RejectedItemType.FLIGHT_MOVEMENTS.getValue(),
                SystemSummaryConst.QUERY_TYPE_FLIGHT_MOVEMENT_REJECTED);
        flightMovementRejected.setName(SystemSummaryName.REJECTED.getValue());
        ss.setFlightMovementRejected(flightMovementRejected);

        FlightMovementVO flightMovementOutstandingBill = getFlightMovement("",
                SystemSummaryConst.QUERY_TYPE_OUTSTANDING_BILL);
        flightMovementOutstandingBill.setName(SystemSummaryName.OUSTANDING_BILL.getValue());
        ss.setOutstandingBill(flightMovementOutstandingBill);

        FlightMovementVO flightMovementOverdueBill = getFlightMovement("", SystemSummaryConst.QUERY_TYPE_OVERDUE_BILL);
        flightMovementOverdueBill.setName(SystemSummaryName.OVERDUE_BILL.getValue());
        ss.setOverdueBill(flightMovementOverdueBill);

        FlightMovementVO flightMovementLatest = getFlightMovementLatest();
        flightMovementLatest.setName(SystemSummaryName.LATEST_FLIGHT.getValue());
        ss.setFlightMovementLatest(flightMovementLatest);

        return ss;
    }

    private FlightMovementVO getAllFlightMovement(List<FlightmovementCategory> aFmCategories) {
        FlightMovementVO vo = null;
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate.getDataSource());
        Set<Integer> ids = new HashSet<>();
        for (FlightmovementCategory aFmCategory : aFmCategories) {
            ids.add(aFmCategory.getId());
        }
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("ids", ids);
        List<FlightMovementVO> voLst = namedParameterJdbcTemplate.query(GET_ALL_FLIGHT_MOVEMENT, parameters, new AutoRowMapper<>(FlightMovementVO.class));
        if (voLst != null && !voLst.isEmpty()) {
            vo = voLst.get(0);
        }
        return vo;
    }

    private String getAllInternationalFlightsString() {
            StringBuffer allFlight = new StringBuffer();
            allFlight.append(FlightMovementType.INT_ARRIVAL.toValue());
            allFlight.append("','");
            allFlight.append(FlightMovementType.INT_DEPARTURE.toValue());
            allFlight.append("','");
            allFlight.append(FlightMovementType.INT_OVERFLIGHT.toValue());
            return allFlight.toString();
        }

    private FlightMovementVO getFlightMovement(String aType, Integer aQueryType) {
        Object[] params = { aType, aQueryType };
        String query = "select * from flight_movements_summary(?, ?)";
        return jdbcTemplate.queryForObject(query, params, new AutoRowMapper<>(FlightMovementVO.class));
    }

   private List<FlightMovementCategoryInfoVO> getFlightMovementCategories(List<FlightmovementCategory> aFmCategories) {
    List<FlightMovementCategoryInfoVO> flightMovementCategoryVOLst = new ArrayList<>();
    for (FlightmovementCategory aFmCategory : aFmCategories) {
        if (!aFmCategory.getName().equals("OTHER")) {
            Integer catId = aFmCategory.getId();
            Object[] params = { catId, catId, catId };
            FlightMovementVO fmVO = jdbcTemplate.queryForObject(GET_ALL_FLIGHT_MOVEMENT_CATEGORIES, params, new AutoRowMapper<>(FlightMovementVO.class));
            FlightMovementCategoryInfoVO fmCatVO = new FlightMovementCategoryInfoVO();
            fmCatVO.setCategoryName(aFmCategory.getName());
            fmCatVO.setFlightMovementVO(fmVO);
            flightMovementCategoryVOLst.add(fmCatVO);
        }
    }
    return flightMovementCategoryVOLst;
}

    private FlightMovementVO getFlightMovementLatest() {
        FlightMovementVO fm = new FlightMovementVO();
        String query = "select max(date_of_flight) as latest_flight from flight_movements";
        List<Map<String, Object>> fmRows = jdbcTemplate.queryForList(query);
        if (!fmRows.isEmpty()) {
            Timestamp date = (Timestamp) fmRows.get(0).get("latest_flight");
            fm.setDate(date);
        }
        return fm;
    }

    private FlightMovementVO getFlightMovementOutside(List<FlightmovementCategory> aFmCategories) {

        Integer otherId = null;
        for (FlightmovementCategory aFmCategory : aFmCategories) {
            if (aFmCategory.getName().equals("OTHER")) {
                otherId = aFmCategory.getId();
                break;
            }
        }
        Object[] params = { otherId, otherId, otherId };
        return jdbcTemplate.queryForObject(GET_ALL_FLIGHT_MOVEMENT_OUTSIDE, params, new AutoRowMapper<>(FlightMovementVO.class));
    }

    private FlightMovementVO getFlightMovementInside(List<FlightmovementCategory> aFmCategories) {

        Integer otherId = null;
        for (FlightmovementCategory aFmCategory : aFmCategories) {
            if (aFmCategory.getName().equals("OTHER")) {
                otherId = aFmCategory.getId();
                break;
            }
        }
        Object[] params = { otherId, otherId, otherId };
        return jdbcTemplate.queryForObject(GET_ALL_FLIGHT_MOVEMENT_INSIDE, params, new AutoRowMapper<>(FlightMovementVO.class));
    }

    private FlightMovementVO getFlightMovementWithParking(String category) {
        Object[] params = { category, category, category };
        return jdbcTemplate.queryForObject(GET_ALL_FLIGHT_MOVEMENT_WITH_PARKING_TIME, params, new AutoRowMapper<>(FlightMovementVO.class));
    }
}
