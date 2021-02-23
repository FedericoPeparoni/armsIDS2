package ca.ids.abms.modules.notifications;

import ca.ids.abms.config.db.ABMSRepository;

import java.util.Collection;

public interface NotificationEventTypeRepository extends ABMSRepository<NotificationEventType, Integer> {

    Collection<NotificationEventType> findAllByIdIn(Collection<Integer> ids);

    NotificationEventType findById(int id);
}
