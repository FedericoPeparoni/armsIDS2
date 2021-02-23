package ca.ids.abms.modules.aerodromes.cluster;

import java.util.*;
import java.util.stream.Collectors;

import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.modules.exemptions.ExemptionType;
import ca.ids.abms.modules.exemptions.ExemptionTypeProvider;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import com.google.common.base.Preconditions;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.ids.abms.modules.util.models.ModelUtils;

@Service
@Transactional
public class RepositioningAerodromeClusterService implements ExemptionTypeProvider {

    private static final Logger LOG = LoggerFactory.getLogger(RepositioningAerodromeClusterService.class);

    private final RepositioningAerodromeClusterRepository repositioningAerodromeClusterRepository;
    private final RepositioningAssignedAerodromeClusterRepository repositioningAssignedAerodromeClusterRepository;

    public RepositioningAerodromeClusterService(
        final RepositioningAerodromeClusterRepository repositioningAerodromeClusterRepository,
        final RepositioningAssignedAerodromeClusterRepository repositioningAssignedAerodromeClusterRepository
    ) {
        this.repositioningAerodromeClusterRepository = repositioningAerodromeClusterRepository;
        this.repositioningAssignedAerodromeClusterRepository = repositioningAssignedAerodromeClusterRepository;
    }

    public void delete(Integer id) {
        LOG.debug("Request to delete RepositioningAerodromeCluster : {}", id);
        final RepositioningAerodromeCluster existingRepositioningAerodromeCluster = getOne(id);
        final Set<RepositioningAssignedAerodromeCluster> assigns = existingRepositioningAerodromeCluster
                .getAerodromeIdentifiers();
        if (CollectionUtils.isNotEmpty(assigns)) {
            repositioningAssignedAerodromeClusterRepository.delete(assigns);
            repositioningAssignedAerodromeClusterRepository.flush();
        }
        repositioningAerodromeClusterRepository.delete(id);
        repositioningAerodromeClusterRepository.flush();
    }

    @Transactional(readOnly = true)
    public List<RepositioningAerodromeCluster> findAll() {
        LOG.debug("Request to get all RepositioningAerodromeCluster");

        return repositioningAerodromeClusterRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<RepositioningAerodromeCluster> findAll(Pageable pageable, final String textSearch) {
        LOG.debug("Request to get all RepositioningAerodromeCluster by text filter: {}", textSearch);
        final FiltersSpecification.Builder filterBuilder = new FiltersSpecification.Builder().lookFor(textSearch);
        return repositioningAerodromeClusterRepository.findAll(filterBuilder.build(), pageable);
    }

    @Transactional(readOnly = true)
    public RepositioningAerodromeCluster getOne(Integer id) {
        LOG.debug("Request to get RepositioningAerodromeCluster : {}", id);
        return repositioningAerodromeClusterRepository.getOne(id);
    }

    public RepositioningAerodromeCluster save(RepositioningAerodromeCluster repositioningAerodromeCluster,
            List<RepositioningAssignedAerodromeCluster> clusters) {
        LOG.debug("Request to save RepositioningAerodromeCluster : {}", repositioningAerodromeCluster);
        Set<RepositioningAssignedAerodromeCluster> assigns = repositioningAerodromeCluster.getAerodromeIdentifiers();
        RepositioningAerodromeCluster saved = repositioningAerodromeClusterRepository
                .save(repositioningAerodromeCluster);
        for (RepositioningAssignedAerodromeCluster cluster : clusters) {
            assigns.add(cluster);
            cluster.setRepositioningAerodromeCluster(saved);
            repositioningAssignedAerodromeClusterRepository.save(cluster);
        }
        repositioningAerodromeClusterRepository.save(saved);
        return saved;
    }

    public void update(Integer id, RepositioningAerodromeCluster repositioningAerodromeCluster,
            List<RepositioningAssignedAerodromeCluster> aerodromeClusters) {
        LOG.debug("Request to update AerodromeCategory : {}", repositioningAerodromeCluster);
        RepositioningAerodromeCluster existingRepositioningAerodromeCluster = repositioningAerodromeClusterRepository
                .getOne(id);

        ModelUtils.merge(repositioningAerodromeCluster, existingRepositioningAerodromeCluster, "aerodromeIdentifiers");
        repositioningAerodromeClusterRepository.save(existingRepositioningAerodromeCluster);

        final Map<String, RepositioningAssignedAerodromeCluster> mapCurrentAerodromeClusters = existingRepositioningAerodromeCluster
                .getAerodromeIdentifiers().stream()
                .collect(Collectors.toMap(RepositioningAssignedAerodromeCluster::getAerodromeIdentifier, ro -> ro));

        final Collection<String> aerodromeIdentifiers = aerodromeClusters.stream()
                .map(RepositioningAssignedAerodromeCluster::getAerodromeIdentifier).collect(Collectors.toList());

        final Map<String, RepositioningAssignedAerodromeCluster> mapAerodromeClusters = aerodromeClusters.stream()
                .collect(Collectors.toMap(RepositioningAssignedAerodromeCluster::getAerodromeIdentifier, ro -> ro));

        final List<RepositioningAssignedAerodromeCluster> aerodromeToAddCluster = aerodromeIdentifiers.stream()
                .filter(i -> !mapCurrentAerodromeClusters.containsKey(i))
                .map(i -> setCluster(mapAerodromeClusters.get(i), existingRepositioningAerodromeCluster))
                .collect(Collectors.toList());

        final List<RepositioningAssignedAerodromeCluster> aerodromeToDeleteCluster = mapCurrentAerodromeClusters
                .entrySet().stream().filter(e -> !aerodromeIdentifiers.contains(e.getKey())).map(Map.Entry::getValue)
                .collect(Collectors.toList());

        repositioningAssignedAerodromeClusterRepository.save(aerodromeToAddCluster);
        repositioningAssignedAerodromeClusterRepository.delete(aerodromeToDeleteCluster);
        repositioningAssignedAerodromeClusterRepository.flush();
        repositioningAerodromeClusterRepository.refresh(existingRepositioningAerodromeCluster);

    }

    /**
     * Return applicable RepositioningAerodromeCluster exemptions by provided flight movement.
     */
    @Override
    @Transactional(readOnly = true)
    public Collection<ExemptionType> findApplicableExemptions(FlightMovement flightMovement) {
        Preconditions.checkArgument(flightMovement != null);
        return findApplicableExemptions(flightMovement.getDepAd(), flightMovement.getDestAd());
    }

    /**
     * Return applicable RepositioningAerodromeCluster exemptions by provided departure and destination aerodromes.
     */
    @Transactional(readOnly = true)
    public Collection<ExemptionType> findApplicableExemptions(final String depAd, final String destAd) {
        Collection<ExemptionType> exemptions = new ArrayList<>();

        if (StringUtils.isNotBlank(depAd) && StringUtils.isNotBlank(destAd)) {
            exemptions.addAll(repositioningAerodromeClusterRepository.findAllByDepAdAndDestAd(depAd, destAd));
        }

        return exemptions;
    }

    private RepositioningAssignedAerodromeCluster setCluster(RepositioningAssignedAerodromeCluster aerodrome,
                                                             RepositioningAerodromeCluster cluster) {
        aerodrome.setRepositioningAerodromeCluster(cluster);
        return aerodrome;
    }

    public long countAll() {
        return repositioningAerodromeClusterRepository.count();
    }
}
