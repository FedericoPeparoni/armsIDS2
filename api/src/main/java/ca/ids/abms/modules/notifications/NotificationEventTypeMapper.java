package ca.ids.abms.modules.notifications;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collection;
import java.util.List;

@Mapper
public interface NotificationEventTypeMapper {

    Collection<NotificationEventTypeViewModel> toViewModel(Collection<NotificationEventType> notifications);

    List<NotificationEventTypeViewModel> toViewModel(Iterable<NotificationEventType> items);

    NotificationEventTypeViewModel toViewModel(NotificationEventType notificationEventType);

    @Mapping(target = "accountEventMaps", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    NotificationEventType toModel(NotificationEventTypeViewModel dto);
}
