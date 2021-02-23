package ca.ids.abms.modules.statistics;

import ca.ids.abms.modules.util.models.ModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RevenueStatisticService {

    private final Logger log = LoggerFactory.getLogger(AirTrafficStatisticService.class);

    private RevenueStatisticRepository revenueStatisticRepository;

    public RevenueStatisticService(RevenueStatisticRepository revenueStatisticRepository) {
        this.revenueStatisticRepository = revenueStatisticRepository;
    }

    public RevenueStatistic save(RevenueStatistic revenueStatistic) {
        log.debug("Request to save revenueStatistic template : {}", revenueStatistic.getName());
        return revenueStatisticRepository.save(revenueStatistic);
    }

    public RevenueStatistic update(Integer id, RevenueStatistic revenueStatistic) {
        log.debug("Request to update revenueStatistic template: {}", revenueStatistic.getName());
        RevenueStatistic existingRevenueStatistic = revenueStatisticRepository.getOne(id);
        ModelUtils.mergeExcept(revenueStatistic, existingRevenueStatistic, "version", "createdAt", "createdBy", "updatedAt", "updatedBy");
        return revenueStatisticRepository.save(existingRevenueStatistic);
    }

    public void delete(Integer id) {
        log.debug("Request to delete revenueStatistic template : {}", id);
        revenueStatisticRepository.delete(id);
    }

    public List<String> getAllNames() {
        return revenueStatisticRepository.findAllNames();
    }

    @Transactional(readOnly = true)
    public RevenueStatistic getOneByName(String name) {
        log.debug("Request to get to get revenueStatistic by name {} ", name);
        return revenueStatisticRepository.findByName(name);
    }
}
