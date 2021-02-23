package ca.ids.abms.modules.rejected;

import java.util.List;

import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ca.ids.abms.config.error.ExceptionFactory;

@Component
@SuppressWarnings("WeakerAccess")
public class RejectedItemHandlerChain {

    private final List<AbstractRejectedItemHandler> handlers;
    private final RejectedItemRepository rejectedItemRepository;

    RejectedItemHandlerChain(
        final List<AbstractRejectedItemHandler> handlers,
        final RejectedItemRepository rejectedItemRepository
    ) {
        this.handlers = handlers;
        this.rejectedItemRepository = rejectedItemRepository;
    }

    @Transactional
    public RejectedItem fixRejectedItem (final Integer id, final RejectedItemViewModel itemDto) {
        if (StringUtils.isNotEmpty(itemDto.getRawText()))
            return fixRejectedItemByRawText(id, itemDto.getRawText());
        else if (itemDto.getJsonText() != null && itemDto.getJsonText().length > 0)
            return fixRejectedItemByJsonText(id, itemDto.getJsonText());
        else
            throw ExceptionFactory.getInvalidDataException("Empty fields for rejected item " + id,
                RejectedItem.class, "jsonText", "rawText");
    }

    @Transactional
    public RejectedItem fixRejectedItemByRawText (final Integer id, final String rawText) {
        Preconditions.checkArgument(id != null);
        Preconditions.checkArgument(rawText != null);

        final RejectedItem existingItem = rejectedItemRepository.getOne(id);
        if (existingItem.getStatus().equals(RejectedItemStatus.CORRECTED.toValue())) {
            throw ExceptionFactory.getInvalidDataException("Already fixed", RejectedItem.class, "status");
        }
        existingItem.setRawText(rawText);
        handleItemToFix(existingItem, true);
        return rejectedItemRepository.save(existingItem);
    }

    @Transactional
    public RejectedItem fixRejectedItemByJsonText (final Integer id, final byte[] jsonText) {
        Preconditions.checkArgument(id != null);
        Preconditions.checkArgument(jsonText != null);

        final RejectedItem existingItem = rejectedItemRepository.getOne(id);
        if (existingItem.getStatus().equals(RejectedItemStatus.CORRECTED.toValue())) {
            throw ExceptionFactory.getInvalidDataException("Already fixed", RejectedItem.class, "status");
        }
        existingItem.setJsonText(jsonText);
        handleItemToFix(existingItem, false);
        return rejectedItemRepository.save(existingItem);
    }

    @Transactional(readOnly = true)
    public byte[] getJsonText (final Integer rejectedItemId, final String rawText) {
        Preconditions.checkArgument(rejectedItemId != null);

        if (StringUtils.isEmpty(rawText)) {
            throw ExceptionFactory.getInvalidDataException("Empty field for rejected item" + " " + rejectedItemId,
                AbstractRejectedItemHandler.class, "rawText");
        }
        final RejectedItem existingItem = rejectedItemRepository.getOne(rejectedItemId);
        final RejectedItemType recordType = RejectedItemType.valueOf(existingItem.getRecordType());

        return handleItemToParse(recordType, rawText);
    }

    private void handleItemToFix (final RejectedItem rejectedItem, boolean isRawText) {
        for (AbstractRejectedItemHandler handler : handlers) {
            if (handler.resposibleForRecordType().toValue().equals(rejectedItem.getRecordType())) {
                if (isRawText) {
                    handler.fixItemWithRawText(rejectedItem);
                } else {
                    handler.fixItemWithJsonText(rejectedItem);
                }
                return;
            }
        }
        throw new RuntimeException("Handler not found to manage the rejected item of type" + " "
            + rejectedItem.getRecordType());
    }

    private byte[] handleItemToParse (final RejectedItemType rejectedItemType, final String rawText) {
        for (AbstractRejectedItemHandler handler : handlers) {
            if (handler.resposibleForRecordType().equals(rejectedItemType)) {
                return handler.convertRawTextIntoJsonText(rawText);
            }
        }
        throw new RuntimeException("Handler not found to manage the rejected item of type" + " "
            + rejectedItemType);
    }
}
