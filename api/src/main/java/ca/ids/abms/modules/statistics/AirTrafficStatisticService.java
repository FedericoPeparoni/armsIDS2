package ca.ids.abms.modules.statistics;

import ca.ids.abms.modules.util.models.ModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AirTrafficStatisticService {

    private final Logger log = LoggerFactory.getLogger(AirTrafficStatisticService.class);

    private AirTrafficStatisticRepository airTrafficStatisticRepository;

    public AirTrafficStatisticService(AirTrafficStatisticRepository airTrafficStatisticRepository) {
        this.airTrafficStatisticRepository = airTrafficStatisticRepository;
    }

    public AirTrafficStatistic save(AirTrafficStatistic airTrafficStatistic) {
        log.debug("Request to save airTrafficStatistics template : {}", airTrafficStatistic.getName());
        return airTrafficStatisticRepository.save(airTrafficStatistic);
    }

    public AirTrafficStatistic update(Integer id, AirTrafficStatistic airTrafficStatistic) {
        log.debug("Request to update airTrafficStatistics template: {}", airTrafficStatistic.getName());
        AirTrafficStatistic existingAirTrafficStatistic = airTrafficStatisticRepository.getOne(id);
        ModelUtils.mergeExcept(airTrafficStatistic, existingAirTrafficStatistic, "version", "createdAt", "createdBy", "updatedAt", "updatedBy");
        return airTrafficStatisticRepository.save(existingAirTrafficStatistic);
    }

    public void delete(Integer id) {
        log.debug("Request to delete airTrafficStatistics template : {}", id);
        airTrafficStatisticRepository.delete(id);
    }

    public List<String> getAllNames() {
        return airTrafficStatisticRepository.findAllNames();
    }

    @Transactional(readOnly = true)
    public AirTrafficStatistic getOneByName(String name) {
        log.debug("Request to get to get AirTrafficStatistic by name {} ", name);
        return airTrafficStatisticRepository.findByName(name);
    }
}
