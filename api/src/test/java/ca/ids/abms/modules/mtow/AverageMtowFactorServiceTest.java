package ca.ids.abms.modules.mtow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import ca.ids.abms.modules.system.SystemConfigurationService;

public class AverageMtowFactorServiceTest {

    private AverageMtowFactorRepository averageMtowFactorRepository;
    private AverageMtowFactorService averageMtowFactorService;
    private SystemConfigurationService systemConfigurationService;

    @Test
    public void createAverageMtowFactor() throws Exception {
        AverageMtowFactor averageMtowFactor = new AverageMtowFactor();
        averageMtowFactor.setAverageMtowFactor(3.4d);

        when(averageMtowFactorRepository.save(any(AverageMtowFactor.class))).thenReturn(averageMtowFactor);

        AverageMtowFactor result = averageMtowFactorService.save(averageMtowFactor);
        assertThat(result.getAverageMtowFactor()).isEqualTo(averageMtowFactor.getAverageMtowFactor());
    }

    @Test
    public void deleteAverageMtowFactor() throws Exception {
        averageMtowFactorService.delete(1);
        verify(averageMtowFactorRepository).delete(any(Integer.class));
    }

    @Test
    public void getAverageMtowFactorById() throws Exception {
        AverageMtowFactor averageMtowFactor = new AverageMtowFactor();
        averageMtowFactor.setId(1);

        when(averageMtowFactorRepository.getOne(any())).thenReturn(averageMtowFactor);

        AverageMtowFactor result = averageMtowFactorService.getOne(1);
        assertThat(result).isEqualTo(averageMtowFactor);
    }

    @Before
    public void setup() {
        averageMtowFactorRepository = mock(AverageMtowFactorRepository.class);
        averageMtowFactorService = new AverageMtowFactorService(averageMtowFactorRepository, systemConfigurationService);
    }

    @Test
    public void updateAverageMtowFactor() throws Exception {
        AverageMtowFactor existingAverageMtowFactor = new AverageMtowFactor();
        existingAverageMtowFactor.setAverageMtowFactor(3.4d);

        AverageMtowFactor averageMtowFactor = new AverageMtowFactor();
        averageMtowFactor.setAverageMtowFactor(4.5d);

        when(averageMtowFactorRepository.getOne(any())).thenReturn(existingAverageMtowFactor);

        when(averageMtowFactorRepository.save(any(AverageMtowFactor.class))).thenReturn(existingAverageMtowFactor);

        AverageMtowFactor result = averageMtowFactorService.update(1, averageMtowFactor);

        assertThat(result.getAverageMtowFactor()).isEqualTo(4.5d);
    }
}
