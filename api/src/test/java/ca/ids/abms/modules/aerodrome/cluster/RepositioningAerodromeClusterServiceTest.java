package ca.ids.abms.modules.aerodrome.cluster;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.ids.abms.config.db.FiltersSpecification;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import ca.ids.abms.modules.aerodromes.cluster.RepositioningAerodromeCluster;
import ca.ids.abms.modules.aerodromes.cluster.RepositioningAerodromeClusterRepository;
import ca.ids.abms.modules.aerodromes.cluster.RepositioningAerodromeClusterService;
import ca.ids.abms.modules.aerodromes.cluster.RepositioningAssignedAerodromeCluster;
import ca.ids.abms.modules.aerodromes.cluster.RepositioningAssignedAerodromeClusterRepository;

public class RepositioningAerodromeClusterServiceTest {

    private RepositioningAerodromeClusterRepository repositioningAerodromeClusterRepository;
    private RepositioningAssignedAerodromeClusterRepository repositioningAssignedAerodromeClusterRepository;
    private RepositioningAerodromeClusterService repositioningAerodromeClusterService;

    @Test
    public void createRepositioningAerodromeCluster() {
        RepositioningAerodromeCluster repositioningAerodromeCluster = new RepositioningAerodromeCluster();
        repositioningAerodromeCluster.setRepositioningAerodromeClusterName("name");

        when(repositioningAerodromeClusterRepository.save(any(RepositioningAerodromeCluster.class)))
                .thenReturn(repositioningAerodromeCluster);

        List<RepositioningAssignedAerodromeCluster> clusters = new ArrayList<RepositioningAssignedAerodromeCluster>();
        RepositioningAssignedAerodromeCluster assigned = new RepositioningAssignedAerodromeCluster();
        assigned.setAerodromeIdentifier("identifier");
        clusters.add(assigned);
        RepositioningAerodromeCluster result = repositioningAerodromeClusterService.save(repositioningAerodromeCluster,
                clusters);
        assertThat(result.getRepositioningAerodromeClusterName())
                .isEqualTo(repositioningAerodromeCluster.getRepositioningAerodromeClusterName());
    }

    @Test
    public void deleteRepositioningAerodromeCluster() {
        RepositioningAerodromeCluster repositioningAerodromeCluster = new RepositioningAerodromeCluster();
        repositioningAerodromeCluster.setId(1);
        when(repositioningAerodromeClusterRepository.getOne(any())).thenReturn(repositioningAerodromeCluster);

        repositioningAerodromeClusterService.delete(1);
        verify(repositioningAerodromeClusterRepository).delete(any(Integer.class));
    }

    @Test
    public void getAllAerodromecategories() {
        List<RepositioningAerodromeCluster> repositioningAerodromeClusters = Collections
                .singletonList(new RepositioningAerodromeCluster());

        when(repositioningAerodromeClusterRepository
                .findAll(any(FiltersSpecification.class), any(Pageable.class)))
                        .thenReturn(new PageImpl<>(repositioningAerodromeClusters));

        Page<RepositioningAerodromeCluster> results = repositioningAerodromeClusterService
                .findAll(mock(Pageable.class), "");

        assertThat(results.getTotalElements()).isEqualTo(repositioningAerodromeClusters.size());
    }

    @Test
    public void getRepositioningAerodromeClusterById() {
        RepositioningAerodromeCluster repositioningAerodromeCluster = new RepositioningAerodromeCluster();
        repositioningAerodromeCluster.setId(1);

        when(repositioningAerodromeClusterRepository.getOne(any())).thenReturn(repositioningAerodromeCluster);

        RepositioningAerodromeCluster result = repositioningAerodromeClusterService.getOne(1);
        assertThat(result).isEqualTo(repositioningAerodromeCluster);
    }

    @Before
    public void setup() {
        repositioningAerodromeClusterRepository = mock(RepositioningAerodromeClusterRepository.class);
        repositioningAssignedAerodromeClusterRepository = mock(RepositioningAssignedAerodromeClusterRepository.class);
        repositioningAerodromeClusterService = new RepositioningAerodromeClusterService(
                repositioningAerodromeClusterRepository, repositioningAssignedAerodromeClusterRepository);
    }

    @Test
    public void updateRepositioningAerodromeCluster() {
        RepositioningAerodromeCluster existingRepositioningAerodromeCluster = new RepositioningAerodromeCluster();
        existingRepositioningAerodromeCluster.setRepositioningAerodromeClusterName("name");

        RepositioningAerodromeCluster repositioningAerodromeCluster = new RepositioningAerodromeCluster();
        repositioningAerodromeCluster.setRepositioningAerodromeClusterName("new name");
        List<RepositioningAssignedAerodromeCluster> clusters = new ArrayList<RepositioningAssignedAerodromeCluster>();
        RepositioningAssignedAerodromeCluster assigned = new RepositioningAssignedAerodromeCluster();
        assigned.setAerodromeIdentifier("identifier");
        clusters.add(assigned);

        when(repositioningAerodromeClusterRepository.getOne(any())).thenReturn(existingRepositioningAerodromeCluster);

        when(repositioningAerodromeClusterRepository.save(any(RepositioningAerodromeCluster.class)))
                .thenReturn(existingRepositioningAerodromeCluster);

        repositioningAerodromeClusterService.update(1, repositioningAerodromeCluster, clusters);

        RepositioningAerodromeCluster result = repositioningAerodromeClusterRepository.getOne(1);
        assertThat(result.getRepositioningAerodromeClusterName()).isEqualTo("new name");
    }
}
