package ca.ids.abms.modules.airspaces;

import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.modules.util.models.ModelUtils;
import ca.ids.abms.util.billingcontext.BillingContext;
import ca.ids.abms.modules.flightmovementsbuilder.vo.RPAirspaceVO;
import ca.ids.abms.modules.util.models.NavDBUtils;
import ca.ids.abms.util.billingcontext.BillingContextKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AirspaceService {

    private static final Logger LOG = LoggerFactory.getLogger(AirspaceService.class);

    private AirspaceRepository airspaceRepository;

    private NavDBUtils navDBUtils;

    public AirspaceService(AirspaceRepository airspaceRepository, NavDBUtils navDBUtils) {
        this.airspaceRepository = airspaceRepository;
        this.navDBUtils=navDBUtils;
    }

    @Transactional(readOnly = true)
    public List<Airspace> getAllFromNavDb(Pageable pageable) {
        LOG.debug("Request to createAirspaceFromNavDbByID airspaces");

        List<Airspace> airspaceLst = navDBUtils.getAllAirspacesNavdb();
        LOG.debug("There are {} airspace(s) in navdb", airspaceLst.size());
        return airspaceLst;
    }

    public Airspace createAirspaceFromNavDbByID(Integer id) {
        LOG.debug("Request to createAirspaceFromNavDbByID Airspace : {}", id);
        Airspace airspace = navDBUtils.getAirspaceNavdbById(id);
        LOG.debug("Airspace from navdb : {}", airspace);

        airspaceRepository.save(airspace);

        return airspace;
    }

    public Airspace update(Integer id, Airspace airspace) {
        LOG.debug("Request to update Airspace : {}", airspace);
        try {
            Airspace existingAirspace = airspaceRepository.getOne(id);
            ModelUtils.merge(airspace, existingAirspace);
            return airspaceRepository.save(existingAirspace);
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e, ErrorConstants.ERR_UPDATE_NO_LONGER_EXISTS);
        }
    }


    @Transactional(readOnly = true)
    public Page<Airspace> findAll(Pageable pageable, String searchFilter) {
        LOG.debug("Request to getAll airspaces from Billing DB");
        final FiltersSpecification.Builder filterBuilder = new FiltersSpecification.Builder().lookFor(searchFilter);
        Page<Airspace> airspaceLst = airspaceRepository.findAll(filterBuilder.build(), pageable);
        LOG.debug("There are {} airspaces on billing DB", airspaceLst.getTotalElements());
        return airspaceLst;
    }

    /**
     * Get the list of billable airspaces.
     * <p>
     * This function will join all FIRs and FIR_Ps into a single polygon, if possible,
     * forming a single pseudo-airspace of type FIR.
     * See comments in {@link AirspaceRepository#getDistinctFirPolygonsWKT}.
     */
    @Transactional(readOnly = true)
    public List<RPAirspaceVO> getBillableAirspacesForRouteParser() {

        if (BillingContext.get(BillingContextKey.BILLABLE_AIRSPACE) != null) {
            return BillingContext.get(BillingContextKey.BILLABLE_AIRSPACE);
        }

        // Get the minimal list of disjoint polygons from the FIR-like
        // airspaces within our area of responsibility and pretend that each
        // polygon is an FIR. See also TFS 90189 and the comments in
        // AirspaceRepository.getDistinctFirPolygonsWKT
        final List <RPAirspaceVO> firList =
                    // get the polygons
                    getDistinctFirPolygonsWKT()
                    // convert them to RPAirspaceVO objects with type = FIR
                    .stream().map (wkt->new RPAirspaceVO ("FIR", wkt))
                    .collect (Collectors.toList());

        // Get the list of non-FIR airspaces and convert them to RPAirspaceVO objects
        final List <RPAirspaceVO> nonFirList =
                    // get all non-FIR airspaces
                    getNonFirAirspace()
                    // convert them to RPAirspaceVO objects
                    .stream().map (x->new RPAirspaceVO (x.getAirspaceType(), x.getAirspaceBoundary().toText()))
                    .collect (Collectors.toList());

        // Concatenate the results and return
        final List <RPAirspaceVO> result = new ArrayList<> (firList);
        result.addAll (nonFirList);
        return result;
    }
    /**
     * Get the list of billable airspaces excluding TMAs
     * <p>
     * This function will join all FIRs and FIR_Ps into a single polygon, if possible,
     * forming a single pseudo-airspace of type FIR.
     * See comments in {@link AirspaceRepository#getDistinctFirPolygonsWKT}.
     */
    @Transactional(readOnly = true)
    public List<RPAirspaceVO> getBillableAirspacesForRouteParserExcludeTmas(double flightLevel) {

        List <RPAirspaceVO> result = null;
        
        if (BillingContext.get(BillingContextKey.BILLABLE_AIRSPACE) != null) {
            return BillingContext.get(BillingContextKey.BILLABLE_AIRSPACE);
        }
       List<String> difList = airspaceRepository.getDifference(flightLevel);
       if(difList == null || difList.isEmpty()|| difList.get(0)== null) {
            result = getBillableAirspacesForRouteParser();
        } else {
            final List <RPAirspaceVO> list = difList.stream().map (wkt->new RPAirspaceVO ("FIR", wkt))
                .collect (Collectors.toList());
            result = new ArrayList<> (list);
        }
        return result;
    }
    
    public void deleteAirspace(Integer id) {
        LOG.debug("Request to delete Airspace : {}", id);
        if(id!=null) {
            airspaceRepository.delete(id);
        }
    }

    @Transactional(readOnly = true)
    public List<String> getAllAirspaceGeometry(){
        LOG.debug("Request to getAllAirspaceGeometry");
        return getBillableAirspacesForRouteParser().stream().map(RPAirspaceVO::getAirspceWkt).collect (Collectors.toList());
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("S1319")
    public LinkedList<RPAirspaceVO> getLinkedListAllAirspaceGeometry(){
        LOG.debug("Request to getLinkedListAllAirspaceGeometry");
        return new LinkedList<> (getBillableAirspacesForRouteParser());
    }

    @Transactional(readOnly = true)
    public List<String> getAllAirspaceGeometryExcludeTmas(Double flightLevel){
        LOG.debug("Request to getAllAirspaceGeometryExcludeTmas");
        return getBillableAirspacesForRouteParserExcludeTmas(flightLevel).stream().map(RPAirspaceVO::getAirspceWkt).collect (Collectors.toList());
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("S1319")
    public LinkedList<RPAirspaceVO> getLinkedListAllAirspaceGeometryExcludeTmas(Double flightLevel){
        LOG.debug("Request to getLinkedListAllAirspaceGeometryExcludeTmas");
        return new LinkedList<> (getBillableAirspacesForRouteParserExcludeTmas(flightLevel));       
    }

    private List<String> getDistinctFirPolygonsWKT() {
        return this.airspaceRepository.getDistinctFirPolygonsWKT();
    }
    
    private List<Airspace> getNonFirAirspace() {
        return this.airspaceRepository.getNonFirAirspaces();
    }

    public long countAll() {
        return airspaceRepository.count();
    }
    
}
