package ca.ids.abms.modules.utilities.schedules;

import ca.ids.abms.config.db.ABMSRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UtilitiesScheduleRepository extends ABMSRepository<UtilitiesSchedule, Integer> {

    UtilitiesSchedule getUtilitiesScheduleByScheduleIdAndScheduleType (Integer scheduleId, ScheduleType scheduleType);

    Page<UtilitiesSchedule> findAllByScheduleType(ScheduleType scheduleType, Pageable pageable);

}
