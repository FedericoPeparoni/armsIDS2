package ca.ids.abms.modules.usereventlogs;

import ca.ids.abms.modules.translation.Translation;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper
public interface UserEventLogMapper {

    List<UserEventLogViewModel> toViewModel(Iterable<UserEventLog> userEventLogs);

    @Mapping(target = "eventType", ignore = true)
    @Mapping(target = "uniqueRecordId", ignore = true)
    UserEventLogViewModel toViewModel(UserEventLog userEventLog);

    @AfterMapping
    default void translate(final UserEventLog source, @MappingTarget UserEventLogViewModel result) {
        result.setEventType(Translation.getLangByToken(source.getEventType()));
        result.setUniqueRecordId(Translation.getLangByToken(source.getUniqueRecordId()));
    }

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    UserEventLog toModel(UserEventLogViewModel dto);

    UserEventLogCsvExportModel toCsvModel(UserEventLog item);

    List<UserEventLogCsvExportModel> toCsvModel(Iterable<UserEventLog> items);
}
